package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.PagingRepository;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * clasa ce implmenteaza functiile crud pentru utilizatorii din memorie
 */
public class UserService {
    private Repository<Integer, User> userRepository;
    private Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository;
    /**
     * constructorul clasei UserService
     * @param userRepository lista de utilizatori din memorie
     */
    public UserService(Repository<Integer, User> userRepository, Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Metodă ajutătoare privată pentru conversia de la Iterable la Stream.
     */
    private <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
    /**
     * functie ce returneaza toti utilizatorii din memorie
     * @return toti utilizatorii
     */
    public Iterable<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    /**
     * functie ce returneaza utilizatorul cu un anumit id
     * @param id id-ul utilizatorului cautat
     * @return utilizatorul cu id-ul dorit
     */
    public User findUser(Integer id)
    {
        return userRepository.findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));
    }

    /**
     * functie ce adauga un utilizator in memorie sau arunca exceptie daca exista deja
     * @param nume numele utilizatorului
     * @param prenume prenumele utilizatorului
     * @param varsta varsta utilizatorului
     * @param email emailul utilizatorului
     * @param parola parola utilizatorului
     */
    public User addUser(String nume, String prenume, int varsta, String email, String parola) {
        User utilizator = new User(nume, prenume, varsta, email, parola);
        if (userRepository instanceof repository.InMemoryRepository) {
            int newId = toStream(userRepository.findAll())
                    .mapToInt(User::getId)
                    .max()
                    .orElse(0) + 1;
            utilizator.setId(newId);
        }
        userRepository.save(utilizator)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Userul exista deja!");
                });
        return utilizator;
    }
    /**
     * functie ce sterge un utilizator din memorie in functie de id, sau arunca exceptie daca acesta nu exista
     * @param id id-ul utilizatorului ce trebuie sters
     */
    public void deleteUser(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        if (userRepository.findOne(id).isEmpty()) {
            throw new IllegalArgumentException("Userul nu exista!");
        }
        List<Tuple<Integer, Integer>> prieteniiDeSters = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll()) {
            if (f.getId().getVal1().equals(id) || f.getId().getVal2().equals(id)) {
                prieteniiDeSters.add(f.getId());
            }
        }
        for (Tuple<Integer, Integer> friendshipId : prieteniiDeSters) {
            friendshipRepository.delete(friendshipId);
        }

        userRepository.delete(id)
                .orElseThrow(() -> new IllegalArgumentException("Userul nu exista!"));
    }
    /**
     * functie ce filtreaza utilizatorii in functie de un criteriu
     * @param filter filtrarea
     * @return lista utilizatorilor filtrati
     */
    public List<User> filterUsers(Predicate<User> filter) {
        return userRepository.findAll(filter);
    }

    /**
     * functie ce da update la un utilizator de indice id
     * @param id id-ul userului ce trebuie modificat
     * @param nume nume nou
     * @param prenume prenume nou
     * @param varsta varsta moua
     * @param email email nou
     * @param parola parola noua
     */
    public void updateUser(Integer id, String nume, String prenume, int varsta, String email, String parola) {
        User utilizator = new User(nume, prenume, varsta, email, parola);
        utilizator.setId(id);
        userRepository.update(utilizator)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Userul cu id-ul " + id + " nu exista!");
                });
    }

    /**
     * returneaza o pagina de utilizatori
     * @param pageable
     * @return
     */
    public Page<User> getUsersOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable nu poate fi null!");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Numarul paginii trebuie sa fie >= 0, iar dimensiunea paginii > 0!");
        }
        if (userRepository instanceof PagingRepository<Integer, User> pagingRepo) {
            return pagingRepo.findAllOnPage(pageable);
        }
        throw new UnsupportedOperationException("Paginarea este disponibila doar pentru repository.type=DATABASE!");
    }
}
