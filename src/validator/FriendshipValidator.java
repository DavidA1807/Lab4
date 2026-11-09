package validator;

import domain.Friendship;

/**
 * clasa ce implementeaza validarea prieteniei cu ajutorul interfetei validator
 */
public class FriendshipValidator implements Validator<Friendship>{
    /**
     * functie ce valideaza prietenia
     * @param entity prietenia validata
     * @throws ValidatorException daca cel putin un id e null sau daca idurile sunt egale
     */
    @Override
    public void validate(Friendship entity) {
        if(entity.getId().getVal1() == null || entity.getId().getVal2() == null)
            throw new ValidatorException("ID-urile nu pot fi nule\n");
        if(entity.getId().getVal1().equals(entity.getId().getVal2()))
            throw new ValidatorException("ID-urile nu pot fi egale(un utilizator nu poate fi prieten cu el insusi)\n");
    }
}
