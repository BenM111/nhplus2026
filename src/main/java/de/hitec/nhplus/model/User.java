package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class User{
    private SimpleLongProperty id;
    private final SimpleStringProperty username;
    private SimpleStringProperty password_hash;
    private SimpleStringProperty salt;
    private final SimpleStringProperty created_at;
    private final SimpleStringProperty role;
    private final SimpleStringProperty last_login;

    public User(Long id, String username, String password_hash, String salt, LocalDate created_at, String role, LocalDate last_login) {
        this.id = new SimpleLongProperty(id != null ? id : 0);
        this.username = new SimpleStringProperty(username);
        this.password_hash = new SimpleStringProperty(password_hash);
        this.salt = new SimpleStringProperty(salt);
        this.created_at = new SimpleStringProperty(DateConverter.convertLocalDateToString(created_at));
        this.role = new SimpleStringProperty(role);
        this.last_login = new SimpleStringProperty(DateConverter.convertLocalDateToString(last_login));
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPassword_hash() {
        return password_hash.get();
    }

    public SimpleStringProperty password_hashProperty() {
        return password_hash;
    }

    public String getSalt() {
        return salt.get();
    }

    public SimpleStringProperty saltProperty() {
        return salt;
    }

    public String getCreated_at() {
        return created_at.get();
    }

    public SimpleStringProperty created_atProperty() {
        return created_at;
    }

    public String getRole() {
        return role.get();
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public String getLast_login() {
        return last_login.get();
    }

    public SimpleStringProperty last_loginProperty() {
        return last_login;
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash.set(password_hash);
    }

    public void setSalt(String salt) {
        this.salt.set(salt);
    }

    public void setLast_login(String last_login) {
        this.last_login.set(last_login);
    }
}
