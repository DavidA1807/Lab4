package domain;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 * clasa ce implementeaza un obiect de tip prietenie si extinde clasa entitate
 * e format din id-ul mostenit de la clasa entitate si data la care se formeaza o prietenie
 */
public class Friendship extends Entity<Tuple<Integer, Integer>>{
    private LocalDateTime friendshipDate;

    /**
     * constructor-ul prieteniei
     * ne asiguram ca id-urile sa fie puse in ordine crescatoare
     * @param friendshipID id-ul prieteniei
     */
    public Friendship(Tuple<Integer, Integer> friendshipID) {
        Integer id1 = min(friendshipID.getVal1(), friendshipID.getVal2());
        Integer id2 = max(friendshipID.getVal1(), friendshipID.getVal2());
        this.setId(new Tuple<>(id1, id2));
        this.friendshipDate = LocalDateTime.now();
    }

    /**
     * functie ce returneaza data
     * @return data prieteniei
     */
    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    /**
     * functie ce seteaza noua valoare a datei
     * @param friendshipDate data prieteniei
     */
    public void setFriendshipDate(LocalDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    /**
     * fucntie ce suprascrie egalitatea dintre prietenii
     * @param o   the reference object with which to compare.
     * @return true daca sunt egale, false altfel
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Friendship that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getFriendshipDate(), that.getFriendshipDate());
    }

    /**
     * functie ce suprascrie hashcodeul prieteniei
     * @return hashcodeul prieteniei
     */
    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getFriendshipDate());
    }

    /**
     * functie ce suprascrie modul de afisare al unei prietenii
     * @return afisarea prieteniei
     */
    @Override
    public String toString() {
        return "Friendship{" +
                "FriendshipID=" + super.getId() +
                ", friendshipDate=" + friendshipDate +
                '}';
    }
}
