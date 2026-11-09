package service;

import domain.Entity;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

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
}
