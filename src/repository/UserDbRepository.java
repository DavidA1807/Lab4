package repository;

import domain.User;
import validator.Validator;

import java.util.Optional;

/**
 * clasa de repository cu baza de date
 */
public class UserDbRepository implements Repository<Integer, User>{
    private Validator<User> validator;
    private String url;
    private String user;
    private String password;

    /**
     * constructorul clasei
     * @param validator validatorul utilizatorului
     * @param url adresa url a bazei de date
     * @param user userul
     * @param password parola
     */
    public UserDbRepository(Validator<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Integer integer) {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Iterable<User> findAll() {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Optional<User> save(User entity) {
        throw new UnsupportedOperationException("lab 4");
    }

    @Override
    public Optional<User> delete(Integer integer) {
        throw new UnsupportedOperationException("lab 4");
    }
}
