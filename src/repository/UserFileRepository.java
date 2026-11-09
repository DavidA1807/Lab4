package repository;

import domain.User;
import validator.Validator;

/**
 * clasa ce prelucreaza utilizatorii in fisier si extinde clasa abstractfilerepository
 */
public class UserFileRepository extends AbstractFileRepository<Integer, User>{
    /**
     * constructorul clasei
     * @param validator validatorul uzerului
     * @param filename numele fisierului in care se prelucreaza utilizatorii
     */
    public UserFileRepository(Validator<User> validator, String filename)
    {
        super(validator, filename);
    }

    /**
     * functia ce extrage un utilizator de pe o linie din fisier
     * @param line linia de pe care se citeste
     * @return utilizatorul de pe linie
     */
    @Override
    protected User extractEntity(String line) {
        String[] attributes = line.split(";", -1);

        Integer id = Integer.parseInt(attributes[0]);
        String nume = attributes[1];
        String prenume = attributes[2];
        Integer varsta = Integer.parseInt(attributes[3]);
        String email = attributes[4];
        String parola = attributes[5];

        User user = new User(nume, prenume, varsta, email, parola);
        user.setId(id);

        return user;
    }

    /**
     * functie ce transforma utilizatorul in string pentru a putea fiv scris in fisier
     * @param entity entitatea ce trebuie scrisa in fisier
     * @return modul de scriere al utilizatorului
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" +
                entity.getNume() + ";" +
                entity.getPrenume() + ";" +
                entity.getVarsta() + ";" +
                entity.getEmail() + ";" +
                entity.getParola();
    }
}
