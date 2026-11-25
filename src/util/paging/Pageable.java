package util.paging;

/**
 * clasa ce reprezinta param de paginare: number si size
 */
public class Pageable {
    private int pageNumber;
    private int pageSize;

    /**
     * constructor pageable
     * @param pageNumber nr paginii
     * @param pageSize marimea paginii
     */
    public Pageable(int pageNumber, int pageSize)
    {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * returneaza nr paginii curente
     * @return indexul paginii
     */
    public int getPageNumber()
    {
        return pageNumber;
    }

    /**
     * returneaza dimensiunea paginii
     * @return nr max de elem de pe o pagina
     */
    public int getPageSize()
    {
        return pageSize;
    }

}
