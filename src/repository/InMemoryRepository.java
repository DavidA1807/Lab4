package repository;

import domain.Entity;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * clasa generica de repo ce implementeaza clasa Repository si suprascrie functiile ei
 * @param <ID> tipul de id al entitatii
 * @param <E> tipul de entitate
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E>{
    private Map<ID, E> entities;
    private Validator<E> validator;

    /**
     * constructorul clasei InMemoryRepository
     * @param validator validatorul entitatii
     */
    public InMemoryRepository(Validator<E> validator) {
        this.entities = new HashMap<>();
        this.validator = validator;
    }
    @Override
    public Optional<E> findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Id-ul nu poate fi null!");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi nula");
        validator.validate(entity);
        if(entities.containsKey(entity.getId()))
            return Optional.of(entity);
        entities.put(entity.getId(), entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi null");
        return Optional.ofNullable(entities.remove(id));
    }
}
