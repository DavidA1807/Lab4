package domain;

import java.util.Objects;

/**
 * clasa generica entitate ce retine id-ul
 * @param <ID> tipul idului unei entitati
 */
public class Entity<ID>{
    private ID id;

    /**
     * functie ce returneaza id-ul entitatii
     * @return id-ul entitatii
     */
    public ID getId() {
        return id;
    }

    /**
     * functie ce seteaza noul id al entitatii
     * @param id id-ul entitatii
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * functie ce suprascrie egalitatea dintre 2 entitati
     * @param o   the reference object with which to compare.
     * @return true daca entitatile sunt egale, fals altfel
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity<?> entity)) return false;
        return Objects.equals(getId(), entity.getId());
    }

    /**
     * functie ce suprascrie hashcodeul entitatii
     * @return hashcodeul entitatii
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * functie ce suprascrie afisarea entitatii
     * @return noul mod de afisare
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
