package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import validator.Validator;

import java.util.Optional;

/**
 * clasa de repository cu database ce implementeaza clasa generica repository
 */
public class FriendshipDbRepository implements Repository<Tuple<Integer, Integer>, Friendship>{
    private Validator<Friendship> validator;
    private String url;
    private String user;
    private String password;

    /**
     * constructorul clasei
     * @param validator validatorul datelor
     * @param url adresa url a bazei de date
     * @param user utiliatorul
     * @param password parola
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Integer, Integer> integerIntegerTuple) {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Iterable<Friendship> findAll() {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Optional<Friendship> delete(Tuple<Integer, Integer> integerIntegerTuple) {
        throw new UnsupportedOperationException("lab 4");
    }
}
