package repository;

import domain.Entity;
import validator.Validator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * clasa abstracta de prelucrare a datelor in fisiere
 * @param <ID> tipul id-ului
 * @param <E> tipul entitatii
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{
    private String filename;

    /**
     * constructorul clasei
     * @param validator validatorul entitatii
     * @param filename numele fisierului in care se afla datele
     */
    public AbstractFileRepository(Validator<E> validator, String filename) {
        super(validator);
        this.filename = filename;
        loadData();
    }

    /**
     * clasa abstracta de preluare a datelor din fisier
     * @param line linia de pe care se citeste
     * @return entitatea
     */
    protected abstract E extractEntity(String line);

    /**
     * clasa abstracta se formateaza tipul de scriere a entitatii in fisier
     * @param entity entitatea ce trebuie scrisa in fisier
     * @return modul de scriere
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * functie ce ia datele dintr-un fisier si ce salveaza entitatile in memorie
     */
    private void loadData() {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<String> lines = Files.lines(path)) {
            lines.filter(line -> !line.trim().isEmpty())
                    .forEach(line -> {
                        try {
                            E entity = extractEntity(line);
                            super.save(entity);
                        } catch (Exception e) {
                            System.err.println("Eroare la citirea din fișierul " + filename + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fișierului " + filename, e);
        }
    }

    /**
     * functie ce scrie entitatile in fisier
     */
    private void writeToFile()
    {
        Path path = Paths.get(filename);

        List<String> linii = StreamSupport.stream(super.findAll().spliterator(), false)
                .map(this::createEntityAsString)
                .collect(Collectors.toList());
        try {
            Files.write(path, linii);
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scrierea în fișierul " + filename, e);
        }
    }

    /**
     * sucrascrierea functiei de salvare a entitatii pentru a actualiza datele fisierului cu noua entitate
     * @param entity entitatea ce trebuie salvata
     * @return entitatea adaugata
     */
    @Override
    public Optional<E> save(E entity) {
        Optional<E> saved = super.save(entity);
        if(saved.isEmpty())
            writeToFile();
        return saved;
    }

    /**
     * suprascrierea functiei de stergere astfel incat sa se elimine si din fisier
     * @param id idul entitatii ce trebuie stearsa
     * @return entitatea stearsa
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deleted = super.delete(id);
        if(deleted.isPresent())
            writeToFile();
        return deleted;
    }
}
