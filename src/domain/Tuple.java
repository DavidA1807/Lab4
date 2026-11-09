package domain;

import java.util.Objects;

/**
 * clasa generica pentru tuplu, tip de data ce retine 2 obiecte
 * @param <E1> tipul primului obiect din tuplu
 * @param <E2> tipul celui de al doilea obiect din tuplu
 */
public class Tuple<E1, E2>{
    private E1 val1;
    private E2 val2;

    /**
     * constructorul tuplului
     * @param val1 primul obiect al tuplului
     * @param val2 al doilea obiect din tuplu
     */
    public Tuple(E1 val1, E2 val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    /**
     * functie ce returneaza primul obiect
     * @return primul obiect al tuplului
     */
    public E1 getVal1() {
        return val1;
    }

    /**
     * functie ce seteaza primul obiect cu o valoarea noua
     * @param val1 noul obiect
     */
    public void setVal1(E1 val1) {
        this.val1 = val1;
    }

    /**
     * functie ce returneaza al doilea obiect
     * @return al doilea obiect al tuplului
     */
    public E2 getVal2() {
        return val2;
    }

    /**
     * functie ce seteaza al doilea obiect cu o valoare noua
     * @param val2 noul obiect
     */
    public void setVal2(E2 val2) {
        this.val2 = val2;
    }

    /**
     * functie ce suprascrie egalitatea dintre 2 tupluri
     * @param o   the reference object with which to compare.
     * @return true daca sunt egale, false altfel
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple<?, ?> tuple)) return false;
        return Objects.equals(getVal1(), tuple.getVal1()) && Objects.equals(getVal2(), tuple.getVal2());
    }

    /**
     * functie ce suprascrie hashcodeul tuplului
     * @return hashcodeul tuplului
     */
    @Override
    public int hashCode() {
        return Objects.hash(getVal1(), getVal2());
    }

    /**
     * suprascrie modul de afisare al unui tuplu
     * @return noul mod de afisare
     */
    @Override
    public String toString() {
        return "Tuple{" +
                "val1=" + val1 +
                ", val2=" + val2 +
                '}';
    }
}
