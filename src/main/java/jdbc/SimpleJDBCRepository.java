package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement ps;
    private Statement st;

    private static final String CREATE_USER_SQL = "INSERT INTO users (firstname, lastname, age) VALUES(?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE users SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM users WHERE name = ?";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM users";

    private static final String USER_NOT_EXISTS = "user does not exist";

    public Long createUser(User user) {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(CREATE_USER_SQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());

            return (long) ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public User findUserById(Long userId) {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(FIND_USER_BY_ID_SQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            else {
                throw new SQLException(USER_NOT_EXISTS);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public User findUserByName(String userName) {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(FIND_USER_BY_NAME_SQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            else {
                throw new SQLException(USER_NOT_EXISTS);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public List<User> findAllUser() {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(FIND_ALL_USER_SQL);
            ResultSet rs = ps.executeQuery();
            List<User> userList = new ArrayList<>();

            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                userList.add(user);
            }

            return userList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public User updateUser(User user) {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(UPDATE_USER_SQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());



            if (ps.executeUpdate() > 0) {
                return user;
            }
            else {
                throw new SQLException(USER_NOT_EXISTS);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void deleteUser(Long userId) {
        try {
            dataSource = CustomDataSource.getInstance();
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(DELETE_USER_SQL);
            ps.setLong(1, userId);

            if (ps.executeUpdate() == 0) {
                throw new SQLException(USER_NOT_EXISTS);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("firstname");
        String surname = resultSet.getString("lastName");
        int age = resultSet.getInt("age");

        return new User(id, name, surname, age);
    }
}
