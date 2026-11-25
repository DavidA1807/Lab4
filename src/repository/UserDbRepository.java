package repository;

import domain.User;
import util.paging.Page;
import util.paging.Pageable;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * clasa de repository cu baza de date, cu functii CRUD si paginare
 */
public class UserDbRepository implements PagingRepository<Integer, User>{
    private Validator<User> validator;
    private String url;
    private String user;
    private String password;

    /**
     * constructorul clasei
     * @param validator validatorul utilizatorului
     * @param url adresa url a bazei de date
     * @param user userul
     * @param password parola
     */
    public UserDbRepository(Validator<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private RepositoryException handleSqlException(SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null) {
            if (sqlState.startsWith("08")) {
                return new RepositoryException(
                        "Eroare de conexiune: Serverul PostgreSQL este oprit sau inaccesibil!"
                );
            }
            if ("28P01".equals(sqlState)) {
                return new RepositoryException(
                        "Eroare de autentificare: Utilizatorul sau parola din config.properties sunt incorecte!"
                );
            }
            if ("3D000".equals(sqlState)) {
                return new RepositoryException(
                        "Eroare baza de date: Baza de date specificata in config.properties nu exista!"
                );
            }
            if ("23505".equals(sqlState)) {
                return new RepositoryException(
                        "Eroare: Exista deja un utilizator cu aceste date unice (ex. email duplicat)!"
                );
            }
        }
        return new RepositoryException("Eroare la baza de date: " + e.getMessage());
    }

    private User extractUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nume = rs.getString("nume");
        String prenume = rs.getString("prenume");
        int varsta = rs.getInt("varsta");
        String email = rs.getString("email");
        String parola = rs.getString("parola");

        User u = new User(nume, prenume, varsta, email, parola);
        u.setId(id);
        return u;
    }

    @Override
    public Optional<User> findOne(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        String sql = "SELECT id, nume, prenume, varsta, email, parola FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUser(rs));
                }
            }
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, nume, prenume, varsta, email, parola FROM users ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi nula!");
        }
        validator.validate(entity);

        String sql = "INSERT INTO users (nume, prenume, varsta, email, parola) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, entity.getNume());
            ps.setString(2, entity.getPrenume());
            ps.setInt(3, entity.getVarsta());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getParola());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
            {
                return Optional.of(entity);
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    entity.setId(generatedKeys.getInt(1));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException(e);
            }
    }

    @Override
    public Optional<User> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate fi null!");
        }
        Optional<User> existing = findOne(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return existing;
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
    }
    @Override
    public Optional<User> update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi nula!");
        }
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Id-ul utilizatorului nu poate fi null!");
        }
        validator.validate(entity);

        String sql = "UPDATE users SET nume = ?, prenume = ?, varsta = ?, email = ?, parola = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getNume());
            ps.setString(2, entity.getPrenume());
            ps.setInt(3, entity.getVarsta());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getParola());
            ps.setInt(6, entity.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException(e);
            }
    }
    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable nu poate fi null!");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Numarul paginii trebuie sa fie >= 0, iar dimensiunea > 0!");
        }

        List<User> usersOnPage = new ArrayList<>();
        String countSql = "SELECT COUNT(*) AS total FROM users";
        String pageSql = "SELECT id, nume, prenume, varsta, email, parola FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = getConnection()) {
            int totalElements = 0;
            try (PreparedStatement countPs = conn.prepareStatement(countSql);
                 ResultSet countRs = countPs.executeQuery()) {
                if (countRs.next()) {
                    totalElements = countRs.getInt("total");
                }
            }

            try (PreparedStatement pagePs = conn.prepareStatement(pageSql)) {
                pagePs.setInt(1, pageable.getPageSize());
                pagePs.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

                try (ResultSet pageRs = pagePs.executeQuery()) {
                    while (pageRs.next()) {
                        usersOnPage.add(extractUser(pageRs));
                    }
                }
            }

            return new Page<>(usersOnPage, totalElements);
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
    }
}
