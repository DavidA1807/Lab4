package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import validator.Validator;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class RepositoryFactory {
    private static RepositoryFactory instance;
    private Properties properties = new Properties();

    /**
     * constructorul functiei
     * ia tipul de repository dorit din config.properties
     */
    private RepositoryFactory() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RepositoryException("Fișierul config.properties nu a fost găsit în src!");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RepositoryException("Eroare la încărcarea configurării: " + e.getMessage(), e);
        }
    }

    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    /**
     * functie ce creeaza repo-ul de utilizator in functie de proprietatea aleasa
     * @param validator validatorul de utilizator
     * @return repository-ul dorit
     */
    public Repository<Integer, User> createUserRepository(Validator<User> validator) {
        String typeStr = properties.getProperty("repository.type");
        if (typeStr == null) {
            throw new RepositoryException("Tipul de repository nu este specificat în config.properties!");
        }
        RepositoryType type = RepositoryType.valueOf(typeStr.toUpperCase());

        Map<RepositoryType, Function<Validator<User>, Repository<Integer, User>>> creators = new EnumMap<>(RepositoryType.class);

        creators.put(RepositoryType.MEMORY, InMemoryRepository::new);

        creators.put(RepositoryType.FILE, val -> {
            String filename = properties.getProperty("file.users");
            return new UserFileRepository(val, filename);
        });

        creators.put(RepositoryType.DATABASE, val -> new UserDbRepository(
                val,
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        ));

        Function<Validator<User>, Repository<Integer, User>> creator = creators.get(type);
        if (creator == null) {
            throw new RepositoryException("Tip de repository necunoscut: " + typeStr);
        }

        return creator.apply(validator);
    }

    /**
     * functie ce creeaza repo-ul de prietenie in functie de proprietatea aleasa
     * @param validator validatorul prieteniei
     * @return repository-ul dorit
     */
    public Repository<Tuple<Integer, Integer>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        String typeStr = properties.getProperty("repository.type");
        if (typeStr == null) {
            throw new RepositoryException("Tipul de repository nu este specificat în config.properties!");
        }

        RepositoryType type = RepositoryType.valueOf(typeStr.toUpperCase());

        Map<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Integer, Integer>, Friendship>>> creators = new EnumMap<>(RepositoryType.class);

        creators.put(RepositoryType.MEMORY, InMemoryRepository::new);

        creators.put(RepositoryType.FILE, val -> {
            String filename = properties.getProperty("file.friendships");
            return new FriendshipFileRepository(val, filename);
        });

        creators.put(RepositoryType.DATABASE, val -> new FriendshipDbRepository(
                val,
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        ));

        Function<Validator<Friendship>, Repository<Tuple<Integer, Integer>, Friendship>> creator = creators.get(type);
        if (creator == null) {
            throw new RepositoryException("Tip de repository necunoscut: " + typeStr);
        }

        return creator.apply(validator);
    }
}