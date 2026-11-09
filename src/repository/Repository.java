package repository;

import domain.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Interfața generică a repository-ului cu operații CRUD și filtrare Java 8.
 * @param <ID> tipul id-ului
 * @param <E> tipul entității
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Caută o entitate în funcție de id.
     * @param id id-ul entității căutate
     * @return entitatea sau null dacă nu există
     */
    Optional<E> findOne(ID id);

    /**
     * Returnează toate entitățile.
     * @return toate entitățile
     */
    Iterable<E> findAll();

    /**
     * Returnează entitățile care respectă un filtru (Predicate).
     * @param filter condiția de filtrare
     * @return lista entităților filtrate
     */
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Salvează entitatea.
     * @param entity entitatea ce trebuie salvată
     * @return entitatea dacă există deja, null dacă e adăugată cu succes
     */
    Optional<E> save(E entity);

    /**
     * Șterge o entitate.
     * @param id id-ul entității ce trebuie ștearsă
     * @return entitatea dacă e ștearsă, null dacă nu există
     */
    Optional<E> delete(ID id);
}