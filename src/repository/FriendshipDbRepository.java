package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import util.paging.Page;
import util.paging.Pageable;
import validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDbRepository implements Repository<Tuple<Integer, Integer>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    public FriendshipDbRepository(Validator<Friendship> validator, String url, String user, String password) {
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
            if ("23503".equals(sqlState)) {
                return new RepositoryException(
                        "Eroare: Unul dintre utilizatorii specificati nu exista in baza de date!"
                );
            }
            if ("23505".equals(sqlState)) {
                return new RepositoryException(
                        "Eroare: Aceasta prietenie exista deja!"
                );
            }
        }
        return new RepositoryException("Eroare la baza de date: " + e.getMessage());
    }

    private Tuple<Integer, Integer> normalizeId(Tuple<Integer, Integer> id) {
        if (id.getVal1() == null || id.getVal2() == null) {
            throw new IllegalArgumentException("ID-urile din tuplu nu pot fi nule!");
        }
        return new Tuple<>(
                Math.min(id.getVal1(), id.getVal2()),
                Math.max(id.getVal1(), id.getVal2())
        );
    }

    private Friendship extractFriendship(ResultSet rs) throws SQLException {
        int id1 = rs.getInt("id1");
        int id2 = rs.getInt("id2");
        LocalDateTime friendsFrom = rs.getTimestamp("friends_from").toLocalDateTime();

        Friendship friendship = new Friendship(new Tuple<>(id1, id2));
        friendship.setFriendshipDate(friendsFrom);
        return friendship;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Integer, Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul prieteniei nu poate fi null!");
        }
        Tuple<Integer, Integer> normId = normalizeId(id);

        String sql = "SELECT id1, id2, friends_from FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, normId.getVal1());
            ps.setInt(2, normId.getVal2());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractFriendship(rs));
                }
            }
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT id1, id2, friends_from FROM friendships ORDER BY id1, id2";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                friendships.add(extractFriendship(rs));
            }
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi nula!");
        }
        validator.validate(entity);

        Tuple<Integer, Integer> normId = normalizeId(entity.getId());
        entity.setId(normId);

        if (findOne(normId).isPresent()) {
            return Optional.of(entity);
        }

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, normId.getVal1());
            ps.setInt(2, normId.getVal2());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getFriendshipDate()));

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
    public Optional<Friendship> delete(Tuple<Integer, Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul prieteniei nu poate fi null!");
        }
        Tuple<Integer, Integer> normId = normalizeId(id);
        Optional<Friendship> existing = findOne(normId);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, normId.getVal1());
            ps.setInt(2, normId.getVal2());
            ps.executeUpdate();
            return existing;
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi nula!");
        }
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Id-ul prieteniei nu poate fi null!");
        }
        validator.validate(entity);

        Tuple<Integer, Integer> normId = normalizeId(entity.getId());
        entity.setId(normId);

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(entity.getFriendshipDate()));
            ps.setInt(2, normId.getVal1());
            ps.setInt(3, normId.getVal2());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
    }

    public Page<User> findFriendsOnPage(Integer userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("ID-ul utilizatorului nu poate fi null!");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable nu poate fi null!");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Numarul paginii trebuie sa fie >= 0, iar dimensiunea > 0!");
        }

        List<User> friendsOnPage = new ArrayList<>();
        int totalElements = 0;

        String sql = """
            SELECT u.id, u.nume, u.prenume, u.varsta, u.email, u.parola, COUNT(*) OVER() AS total_count
            FROM friendships f
            JOIN users u ON (f.id1 = u.id OR f.id2 = u.id)
            WHERE (f.id1 = ? OR f.id2 = ?) AND u.id <> ?
            ORDER BY u.id
            LIMIT ? OFFSET ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, pageable.getPageSize());
            ps.setInt(5, pageable.getPageNumber() * pageable.getPageSize());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totalElements = rs.getInt("total_count");
                    User friend = new User(
                            rs.getString("nume"),
                            rs.getString("prenume"),
                            rs.getInt("varsta"),
                            rs.getString("email"),
                            rs.getString("parola")
                    );
                    friend.setId(rs.getInt("id"));
                    friendsOnPage.add(friend);
                }
            }

            return new Page<>(friendsOnPage, totalElements);
        } catch (SQLException e) {
            throw handleSqlException(e);
        }
    }
}