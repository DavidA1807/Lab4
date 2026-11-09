package validator;

import domain.User;

/**
 * clasa ce implementeaza validarea userului cu ajutorul interfetei validator
 */
public class UserValidator implements Validator<User>{
    /**
     * functie ce valideaza un utilizator
     * utilizatorul trebuie sa nu aiba date necompletate, cifre in nume si prenume, varsta >= 14, emailul trebuie sa se termine intr-un anumit mod,
     * iar parola trebuie sa aiba cel putin 8 caractere din care cel putin o litera mica, o litera mare, o cifra si un caracter special
     * @param entity userul verificat
     * @throws ValidatorException erorile userului
     */
    @Override
    public void validate(User entity) {
        String errors = "";
        if(entity.getNume() == null || entity.getPrenume() == null || entity.getVarsta() == null
            ||entity.getEmail() == null || entity.getParola() == null)
            throw new ValidatorException("Utilizatorul nu poate avea date necompletate\n");
        if(entity.getNume().matches(".*\\d.*") || entity.getPrenume().matches(".*\\d.*"))
            errors += "Numele utilizatorului nu poate contine cifre\n";
        if(!entity.getEmail().endsWith("@gmail.com") && !entity.getEmail().endsWith("@yahoo.com")
            && !entity.getEmail().endsWith("@ubbcluj.ro") && !entity.getEmail().endsWith("@outlook.com"))
            errors += "Emailul utilizatorului trebuie sa se termine in una dintre : @email.com, @yahoo.com, @outlook.com, @ubbcluj.ro\n";
        if(entity.getParola().length() <= 7 || (!entity.getParola().matches(".*[a-z].*") || !entity.getParola().matches(".*[A-Z].*") || !entity.getParola().matches(".*\\d.*") || !entity.getParola().matches(".*[^a-zA-Z0-9].*")))
            errors += "Parola trebuie sa aiba cel putin 8 caractere si cel putin o litera mare, o litera mica, o cifra si un caracter special\n";
        if(!errors.isEmpty())
            throw new ValidatorException(errors);

    }
}
