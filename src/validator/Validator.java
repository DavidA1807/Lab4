package validator;

@FunctionalInterface
public interface Validator<T>{
    void validate(T entity) throws ValidatorException;

    default Validator<T> andThen(Validator<T> other){
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }

}
