package service;

import domain.User;
import repository.Repository;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * clasa ce implmenteaza functiile crud pentru utilizatorii din memorie
 */
public class UserService {
    private Repository<Integer, User> userRepository;

    /**
     * constructorul clasei UserService
     * @param userRepository lista de utilizatori din memorie
     */
    public UserService(Repository<Integer, User> userRepository) {
        this.userRepository = userRepository;
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
    public void addUser(String nume, String prenume, int varsta, String email, String parola)
    {
        int newId = toStream(userRepository.findAll())
                .mapToInt(User::getId)
                .max()
                .orElse(0) + 1;
        User utilizator = new User(nume, prenume, varsta, email, parola);
        utilizator.setId(newId);
        userRepository.save(utilizator)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Userul exista deja!");
                });
    }
    /**
     * functie ce sterge un utilizator din memorie in functie de id, sau arunca exceptie daca acesta nu exista
     * @param id id-ul utilizatorului ce trebuie sters
     */
    public void deleteUser(Integer id)
    {
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
}
