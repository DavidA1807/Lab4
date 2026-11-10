package validator;

/**
 * interfata functionala de validare a unei entitati
 * @param <T>
 */
@FunctionalInterface
public interface Validator<T>{
    void validate(T entity) throws ValidatorException;

    /**
     * functie ce adauga o noua validare entitatii
     * @param other noua validare
     * @return entitatiile validate
     */
    default Validator<T> andThen(Validator<T> other){
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}
