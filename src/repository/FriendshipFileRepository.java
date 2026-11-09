package repository;

import domain.Friendship;
import domain.Tuple;
import validator.Validator;

import java.time.LocalDateTime;

/**
 * clasa ce prelucreaza prieteniile in fisier si extinde clasa abstracta abstractfilerepository
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Integer, Integer>, Friendship>{
    /**
     * constructorul clasei
     * @param validator calidatorul prieteniei
     * @param filename numele fisierului in care se prelucreaza datele
     */
    public FriendshipFileRepository(Validator<Friendship> validator, String filename) {
        super(validator, filename);
    }

    /**
     * functia ce extrage prieteniile din fisier
     * @param line linia de pe care se citeste
     * @return prietenia de pe o linie
     */
    @Override
    protected Friendship extractEntity(String line) {
        String[] attributes = line.split(";", -1);
        Integer id1 = Integer.parseInt(attributes[0]);
        Integer id2 = Integer.parseInt(attributes[1]);
        LocalDateTime date = LocalDateTime.parse(attributes[2]);

        Tuple<Integer, Integer> id = new Tuple<>(id1, id2);
        Friendship prietenie = new Friendship(id);
        prietenie.setFriendshipDate(date);

        return prietenie;
    }

    /**
     * clasa ce transforma prietenia in string pentru a putea fi scrisa in fisier
     * @param entity entitatea ce trebuie scrisa in fisier
     * @return tranformarea in string a unei prietenii
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getVal1() + ";" +
                entity.getId().getVal2() + ";" +
                entity.getFriendshipDate().toString();
    }
}
