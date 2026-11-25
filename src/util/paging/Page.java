package util.paging;

/**
 * clasa ce reprezintaa o pagina de elemente si nr de elemente disponibile
 * @param <E> tipul elementelor din pagina
 */
public class Page<E>{
    private Iterable<E> elementsOnPage;
    private int totalNumberOfElements;

    /**
     * constructor pagina de elemente
     * @param elementsOnPage colectia de elemente de pe pagina
     * @param totalNumberOfElements nr total de elemente de pe pagina
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * returneaza elementele de pe pagina curenta
     * @return elementele paginii curente
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * returneaza nr total de elemente existente
     * @return nr total de elemente
     */

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}
