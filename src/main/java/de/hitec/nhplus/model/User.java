package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * Repräsentiert ein Modellobjekt für einen Benutzer (User) im System.
 * Die Klasse verwendet JavaFX-Properties ({@link SimpleStringProperty} und {@link SimpleLongProperty}),
 * um eine dynamische Datenbindung (Data Binding) innerhalb der Benutzeroberfläche (GUI) zu ermöglichen.
 *
 * @author Luca Bullwinkel
 */
public class User {
    private SimpleLongProperty id;
    private final SimpleStringProperty username;
    private SimpleStringProperty password_hash;
    private SimpleStringProperty salt;
    private final SimpleStringProperty created_at;
    private final SimpleStringProperty role;
    private final SimpleStringProperty last_login;

    /**
     * Konstruiert ein neues User-Objekt und initialisiert alle JavaFX-Properties.
     * Lokale Datumswerte ({@link LocalDate}) werden automatisch über den {@link DateConverter}
     * in ihre String-Repräsentation umgewandelt.
     *
     * @param id            Die eindeutige ID des Benutzers (falls {@code null}, wird sie auf 0 gesetzt).
     * @param username      Der eindeutige Benutzername.
     * @param password_hash Der kryptographische Hash des Benutzerpassworts.
     * @param salt          Das zufällige Salt, welches für das Hashen des Passworts verwendet wurde.
     * @param created_at    Das Erstelldatum des Benutzerkontos.
     * @param role          Die Rolle des Benutzers (z. B. "admin" oder "user").
     * @param last_login    Das Datum des letzten erfolgreichen Logins.
     */
    public User(Long id, String username, String password_hash, String salt, LocalDate created_at, String role, LocalDate last_login) {
        this.id = new SimpleLongProperty(id != null ? id : 0);
        this.username = new SimpleStringProperty(username);
        this.password_hash = new SimpleStringProperty(password_hash);
        this.salt = new SimpleStringProperty(salt);
        this.created_at = new SimpleStringProperty(DateConverter.convertLocalDateToString(created_at));
        this.role = new SimpleStringProperty(role);
        this.last_login = new SimpleStringProperty(DateConverter.convertLocalDateToString(last_login));
    }

    /**
     * Gibt den Benutzernamen als Standard-String zurück.
     *
     * @return Der Benutzername.
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Gibt die JavaFX-Property für den Benutzernamen zurück.
     * Wird für das UI-Binding (z. B. in TableViews) benötigt.
     *
     * @return Die Username-Property.
     */
    public SimpleStringProperty usernameProperty() {
        return username;
    }

    /**
     * Gibt den Passwort-Hash als Standard-String zurück.
     *
     * @return Der Passwort-Hash.
     */
    public String getPassword_hash() {
        return password_hash.get();
    }

    /**
     * Gibt die JavaFX-Property für den Passwort-Hash zurück.
     *
     * @return Die Password-Hash-Property.
     */
    public SimpleStringProperty password_hashProperty() {
        return password_hash;
    }

    /**
     * Gibt das Salt als Standard-String zurück.
     *
     * @return Das Salt.
     */
    public String getSalt() {
        return salt.get();
    }

    /**
     * Gibt die JavaFX-Property für das Salt zurück.
     *
     * @return Die Salt-Property.
     */
    public SimpleStringProperty saltProperty() {
        return salt;
    }

    /**
     * Gibt das Erstelldatum des Benutzers als formatierten String zurück.
     *
     * @return Das Erstelldatum.
     */
    public String getCreated_at() {
        return created_at.get();
    }

    /**
     * Gibt die JavaFX-Property für das Erstelldatum zurück.
     *
     * @return Die Created-At-Property.
     */
    public SimpleStringProperty created_atProperty() {
        return created_at;
    }

    /**
     * Gibt die Rolle des Benutzers als Standard-String zurück.
     *
     * @return Die Benutzerrolle.
     */
    public String getRole() {
        return role.get();
    }

    /**
     * Gibt die JavaFX-Property für die Benutzerrolle zurück.
     *
     * @return Die Role-Property.
     */
    public SimpleStringProperty roleProperty() {
        return role;
    }

    /**
     * Gibt das Datum des letzten Logins als formatierten String zurück.
     *
     * @return Das Datum des letzten Logins.
     */
    public String getLast_login() {
        return last_login.get();
    }

    /**
     * Gibt die JavaFX-Property für das Datum des letzten Logins zurück.
     *
     * @return Die Last-Login-Property.
     */
    public SimpleStringProperty last_loginProperty() {
        return last_login;
    }

    /**
     * Gibt die eindeutige ID des Benutzers als primitiven long-Wert zurück.
     *
     * @return Die ID des Benutzers.
     */
    public long getId() {
        return id.get();
    }

    /**
     * Gibt die JavaFX-Property für die Benutzer-ID zurück.
     *
     * @return Die ID-Property.
     */
    public SimpleLongProperty idProperty() {
        return id;
    }

    /**
     * Setzt die ID des Benutzers.
     *
     * @param id Die neue ID.
     */
    public void setId(long id) {
        this.id.set(id);
    }

    /**
     * Aktualisiert den Passwort-Hash des Benutzers.
     *
     * @param password_hash Der neue Passwort-Hash.
     */
    public void setPassword_hash(String password_hash) {
        this.password_hash.set(password_hash);
    }

    /**
     * Aktualisiert das Salt des Benutzers.
     *
     * @param salt Das neue Salt.
     */
    public void setSalt(String salt) {
        this.salt.set(salt);
    }

    /**
     * Aktualisiert das Datum des letzten Logins.
     *
     * @param last_login Das neue Login-Datum als formatierter String.
     */
    public void setLast_login(String last_login) {
        this.last_login.set(last_login);
    }
}