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

    private DataSource dataSource = CustomDataSource.getInstance();
    private Connection connection;
    private PreparedStatement ps;
    private Statement st;

    private static final String createUserSQL = "INSERT INTO users (firstname, lastname, age) VALUES(?, ?, ?)";
    private static final String updateUserSQL = "UPDATE users SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM users WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM users WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM users WHERE name = ?";
    private static final String findAllUserSQL = "SELECT * FROM users";

    public Long createUser(User user) {
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(createUserSQL);
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
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("firstname");
                String surname = rs.getString("lastName");
                int age = rs.getInt("age");

                return new User(id, name, surname, age);
            }
            else {
                throw new SQLException("user with id = " + userId + " not found");
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
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("firstname");
                String surname = rs.getString("lastName");
                int age = rs.getInt("age");

                return new User(id, name, surname, age);
            }
            else {
                throw new SQLException("user with name " + userName + " not found");
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
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet rs = ps.executeQuery();
            List<User> userList = new ArrayList<>();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("firstname");
                String surname = rs.getString("lastName");
                int age = rs.getInt("age");

                User user = new User(id, name, surname, age);
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
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());



            if (ps.executeUpdate() > 0) {
                return user;
            }
            else {
                throw new SQLException("user with id " + user.getId() + " not found");
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

    private void deleteUser(Long userId) {
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("user with id " + userId + " not found");
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
}
