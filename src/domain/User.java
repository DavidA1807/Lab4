package domain;

import java.util.Objects;

/**
 * clasa ce implementeaza noul tip de date user, ce extine clasa entitate
 */
public class User extends Entity<Integer>{
    private String nume;
    private String prenume;
    private Integer varsta;
    private boolean adult;
    private String email;
    private String parola;

    /**
     * constructorul clasei user
     * @param nume numele userului
     * @param prenume prenumele userului
     * @param varsta varsta userului
     * @param email emailul userului
     * @param parola parola userului
     */
    public User(String nume, String prenume, Integer varsta, String email, String parola) {
        this.nume = nume;
        this.prenume = prenume;
        this.varsta = varsta;
        this.adult = this.varsta >= 18;
        this.email = email;
        this.parola = parola;
    }

    /**
     * functie ce returneaza numele
     * @return numele
     */
    public String getNume() {
        return nume;
    }

    /**
     * functie ce returneaza prenumele
     * @return prenume
     */
    public String getPrenume() {
        return prenume;
    }

    /**
     * functie ce returneaza varsta
     * @return varsta
     */
    public Integer getVarsta() {
        return varsta;
    }

    /**
     * functie ce returneaza daca userul e sau nu adult
     * @return true daca e adult, false altfel
     */
    public boolean isAdult() {
        return adult;
    }

    /**
     * functie ce returneaza emailul
     * @return emailul
     */
    public String getEmail() {
        return email;
    }

    /**
     * functie ce returneaza parola
     * @return parola
     */
    public String getParola() {
        return parola;
    }

    /**
     * functie ce seteaza noul nume
     * @param nume noul nume
     */
    public void setNume(String nume) {
        this.nume = nume;
    }

    /**
     * functie ce seteaza noul prenume
     * @param prenume noul prenume
     */
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    /**
     * functie ce seteaza noua varsta
     * @param varsta noua varsta
     */
    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
        setAdult(getVarsta() >= 18);
    }

    /**
     * functie privata, seteaza daca e sau nu adult, folosita in setVarsta
     * @param adult noua valoare
     */
    private void setAdult(boolean adult){
        this.adult = adult;
    }

    /**
     * functie ce seteaza noul mail
     * @param email noul mail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * functie ce seteaza noua parola
     * @param parola noua parola
     */
    public void setParola(String parola) {
        this.parola = parola;
    }

    /**
     * functie ce suprascrie egaliteatea dintre 2 useri
     * @param o   the reference object with which to compare.
     * @return true daca sunt egale, false altfel
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User that)) return false;
        if (!super.equals(o)) return false;
        return isAdult() == that.isAdult() && Objects.equals(getNume(), that.getNume()) && Objects.equals(getPrenume(), that.getPrenume()) && Objects.equals(getVarsta(), that.getVarsta()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getParola(), that.getParola());
    }

    /**
     * functie ce suprascrie hashcodeul unui user
     * @return hashcodeul userului
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getNume(), getPrenume(), getVarsta(), isAdult(), getEmail(), getParola());
    }

    /**
     * functie ce suprascrie modul de afisare al unui user
     * @return noul mod de afisare a unui user
     */
    @Override
    public String toString() {
        return "Utilizator{" +
                "id=" + super.getId() +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", varsta=" + varsta +
                ", adult=" + adult +
                ", email='" + email + '\'' +
                ", parola='" + parola + '\'' +
                '}';
    }
}
