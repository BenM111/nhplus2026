package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Das Data Access Object (DAO) für die Verwaltung von {@link User}-Objekten in der Datenbank.
 * Erweitert die abstrakte Basisklasse {@link DaoImp} und implementiert die spezifischen
 * CRUD-Operationen sowie zusätzliche Abfragen für die Benutzerverwaltung.
 *
 * @author Luca Bullwinkel
 */
public class UserDao extends DaoImp<User> {

    /**
     * Erzeugt eine neue Instanz des UserDao mit einer bestehenden Datenbankverbindung.
     *
     * @param connection Die aktive SQL-Verbindung zur Datenbank.
     */
    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Sucht einen Benutzer anhand seines eindeutigen Benutzernamens in der Datenbank.
     *
     * @param username Der gesuchte Benutzername.
     * @return Das entsprechende {@link User}-Objekt, oder {@code null}, wenn kein Benutzer gefunden wurde.
     * @throws SQLException Wenn bei der Durchführung der Datenbankabfrage ein Fehler auftritt.
     */
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

    /**
     * Erstellt das PreparedStatement für das Auslesen eines Benutzers über den Benutzernamen.
     *
     * @param username Der gesuchte Benutzername.
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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

    /**
     * Erzeugt ein {@link User}-Objekt aus den Daten der aktuellen Zeile eines {@link ResultSet}.
     * Falls die Datumsfelder in der Datenbank leer oder ungültig sind, wird das aktuelle Tagesdatum verwendet.
     *
     * @param set Das ResultSet, das die Benutzerdaten enthält.
     * @return Das vollständig instanziierte {@link User}-Objekt.
     * @throws SQLException Wenn beim Zugriff auf die Spalten des ResultSets ein Fehler auftritt.
     */
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

    /**
     * Transformiert ein vollständiges {@link ResultSet} in eine Liste von {@link User}-Objekten.
     *
     * @param set Das ResultSet, das die Ergebnismenge der Abfrage enthält.
     * @return Eine {@link ArrayList} mit allen ausgelesenen Benutzern.
     * @throws SQLException Wenn beim Durchlaufen des ResultSets ein Fehler auftritt.
     */
    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (set.next()) {
            list.add(getInstanceFromResultSet(set));
        }
        return list;
    }

    /**
     * Erstellt das PreparedStatement für das Auslesen eines Benutzers über die ID.
     *
     * @param key Die ID (Primärschlüssel) des Benutzers.
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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

    /**
     * Erstellt das PreparedStatement für das Auslesen aller Benutzer aus der Tabelle.
     *
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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

    /**
     * Erstellt das PreparedStatement für das Aktualisieren der Sicherheitsdaten und des letzten Logins eines Benutzers.
     * Die Identifikation erfolgt über den Benutzernamen.
     *
     * @param user Das {@link User}-Objekt mit den aktualisierten Daten.
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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

    /**
     * Erstellt das PreparedStatement für das Einfügen (Registrieren) eines neuen Benutzers in die Datenbank.
     *
     * @param user Das neu anzulegende {@link User}-Objekt.
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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

    /**
     * Erstellt das PreparedStatement für das Löschen eines Benutzers anhand seiner ID.
     *
     * @param key Die ID des zu löschenden Benutzers.
     * @return Ein konfiguriertes {@link PreparedStatement} oder {@code null}, falls eine Exception auftritt.
     */
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