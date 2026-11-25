package repository;

import domain.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * clasa ce extinde clasa Repository, ce ajuta la paginarea entitatii
 * @param <ID>
 * @param <E>
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E>{
    /**
     * returneaza o pagina de entitati
     * @param pageable parametrii de paginare
     * @return un obiect ce contine elementele paginii si nr total al elementelor
     */
    Page<E> findAllOnPage(Pageable pageable);
}
