package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;

public class UserDao extends DaoImp<User> {

    public UserDao(Connection connection) {
        super(connection);
    }

    public User readByUsername(String username) throws SQLException {
        User user = null;
        try (PreparedStatement statement = getReadByUsernameStatement(username);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                user = getInstanceFromResultSet(result);
            }
        }
        return user;
    }

    protected PreparedStatement getReadByUsernameStatement(String username) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected User getInstanceFromResultSet(ResultSet set) throws SQLException {
        String createdAtStr = set.getString("created_at");
        String lastLoginStr = set.getString("last_login");

        LocalDate createdAt = (createdAtStr != null && !createdAtStr.isBlank())
                ? DateConverter.convertStringToLocalDate(createdAtStr)
                : LocalDate.now();

        LocalDate lastLogin = (lastLoginStr != null && !lastLoginStr.isBlank())
                ? DateConverter.convertStringToLocalDate(lastLoginStr)
                : LocalDate.now();

        return new User(
                set.getLong("id"),
                set.getString("username"),
                set.getString("password_hash"),
                set.getString("salt"),
                createdAt,
                set.getString("role"),
                lastLogin
        );
    }

    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (set.next()) {
            list.add(getInstanceFromResultSet(set));
        }
        return list;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE user SET password_hash = ?, salt = ?, last_login = ? WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(SQL);

            preparedStatement.setString(1, user.getPassword_hash());
            preparedStatement.setString(2, user.getSalt());
            preparedStatement.setString(3, user.getLast_login());
            preparedStatement.setString(4, user.getUsername());

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getCreateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO user (username, password_hash, salt, created_at, role, last_login) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword_hash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setString(4, user.getCreated_at());
            preparedStatement.setString(5, user.getRole());
            preparedStatement.setString(6, user.getLast_login());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }


    public void UpdateNewUserStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE user SET password_hash = ?, salt = ?, last_login = ? WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(2, user.getPassword_hash());
            preparedStatement.setString(2, user.getSalt());
            preparedStatement.setString(1, DateConverter.convertLocalDateToString(LocalDate.now()));
            preparedStatement.setString(2, user.getUsername());
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void UpdateLastLoginStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE user SET last_login = ? WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, DateConverter.convertLocalDateToString(LocalDate.now()));
            preparedStatement.setString(2, user.getUsername());
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }
}