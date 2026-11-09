package repository;

import domain.User;

import java.util.function.Predicate;

public class CriteriiUtilizator {
    public static Predicate<User> areNumele(String nume){
        return user -> user.getNume().equalsIgnoreCase(nume);
    }
    public static Predicate<User> areVarstaMinima(int varsta){
        return user -> user.getVarsta() >= varsta;
    }
    public static boolean esteMajor(User user){
        return user.getVarsta() >= 18;
    }
    public static Predicate<User> numeIncepeCu(String litera) {
        return user -> user.getNume() != null && user.getNume().startsWith(litera);
    }
}
