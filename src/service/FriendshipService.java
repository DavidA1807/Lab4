package service;

import domain.Entity;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.FriendshipDbRepository;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * clasa ce implementeaza functiile CRUD pentru prietenii + functie ce elimina toate prieteniile unui utilizator
 */
public class FriendshipService {
    private final Repository<Integer, User> UserRepository;
    private final Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository;

    /**
     * constructor pentru clasa FriendshipService
     * @param userRepository repo-ul de utilizatori
     * @param friendshipRepository repo-ul de prietenii
     */
    public FriendshipService(Repository<Integer, User> userRepository, Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository) {
        this.UserRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Metodă ajutătoare pentru conversia la Stream.
     */
    private <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * functie ce returneaza toate prieteniile
     * @return toate prieteniile
     */
    public Iterable<Friendship> getAllFriendships()
    {
        return friendshipRepository.findAll();
    }

    /**
     * functie ce cauta o prietenie
     * @param id id-ul prieteniei
     * @return prieteniea cautata
     */
    public Friendship findFriendship(Tuple<Integer, Integer> id)
    {
        return friendshipRepository.findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("Prietenia nu a fost gasita!"));
    }

    /**
     * functie ce adauga o prietenie in memorie
     * arunca exceptie daca idurile utilizatorilor nu exista sau daca prietenia exista deja
     * @param id1 id-ul primului utilizator
     * @param id2 id-ul celui de al doilea utilizator
     */
    public void addFriendship(Integer id1, Integer id2)
    {
        UserRepository.findOne(id1)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 1 nu exista!"));
        UserRepository.findOne(id2)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 2 nu exista!"));

        Tuple<Integer, Integer> id = new Tuple<>(id1, id2);
        Friendship friendship = new Friendship(id);

        friendshipRepository.save(friendship)
                .ifPresent(f -> {
                    throw new IllegalArgumentException("Prietenia exista deja!");
                });
    }

    /**
     * functie ce sterge o prietenie din memorie
     * @param id1 id-ul primului utilizator
     * @param id2 id-ul celui de al doilea utilizator
     */
    public void deleteFriendship(Integer id1, Integer id2)
    {
        Tuple<Integer, Integer> id = new Tuple<>(id1, id2);
        friendshipRepository.delete(id)
                .orElseThrow(() -> new IllegalArgumentException("Prietenia nu exista!"));
    }

    /**
     * functie ce sterge toate prieteniile unui utilizator
     * nu mai e folosita
     * @param userID id-ul utilizatorului
     */
    public void removeAllFriendshipsOfUser(Integer userID)
    {
        toStream(friendshipRepository.findAll())
                .filter(f -> f.getId().getVal1().equals(userID) || f.getId().getVal2().equals(userID))
                .map(Entity::getId)
                .collect(Collectors.toList())
                .forEach(friendshipRepository::delete);
    }

    /**
     * returneaza pagina cu prietenii unui utilizator dat
     * @param userId id-ul utilizatorului al carui prieteni sunt afisati
     * @param pageable parametrii de paginare
     * @return un obiect cu prietenii pe pagina data
     */
    public Page<User> getFriendsOnPage(Integer userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("ID-ul utilizatorului nu poate fi null!");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable nu poate fi null!");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Numarul paginii trebuie sa fie >= 0, iar dimensiunea paginii > 0!");
        }
        if (UserRepository.findOne(userId).isEmpty()) {
            throw new IllegalArgumentException("Utilizatorul cu id-ul " + userId + " nu exista!");
        }
        if (friendshipRepository instanceof FriendshipDbRepository friendshipDbRepo) {
            return friendshipDbRepo.findFriendsOnPage(userId, pageable);
        }
        throw new UnsupportedOperationException("Paginarea prietenilor este disponibila doar pentru repository.type=DATABASE!");
    }
}
