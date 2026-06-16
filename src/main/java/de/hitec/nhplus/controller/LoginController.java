package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.utils.PasswordUtil;
import de.hitec.nhplus.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    private UserDao dao;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label labelError;

    public void initialize() {
        this.dao = DaoFactory.getDaoFactory().createUserDao();
        usernameField.focusedProperty().addListener((observable, oldFocus, newFocus) -> {
            if (!newFocus) {
                checkIfNewUser();
            }
        });
    }

    private void checkIfNewUser() {
        String username = usernameField.getText();
        if (username.isEmpty()) return;

        try {
            de.hitec.nhplus.model.User user = this.dao.readByUsername(username);
            if (user != null && (user.getPassword_hash() == null || user.getPassword_hash().isEmpty())) {
                labelError.setVisible(true);
                labelError.setText("Hallo! Bitte lege dein Passwort für die Erstanmeldung fest.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            de.hitec.nhplus.model.User user = this.dao.readByUsername(username);

            if (user != null) {
                if (user.getPassword_hash() == null || user.getPassword_hash().isEmpty()) {

                    String newSalt = PasswordUtil.generateSalt();
                    String newHash = PasswordUtil.hash(password, newSalt);
                    String today = de.hitec.nhplus.utils.DateConverter.convertLocalDateToString(java.time.LocalDate.now());

                    user.setPassword_hash(newHash);
                    user.setSalt(newSalt);
                    user.setLast_login(today);

                    this.dao.update(user);

                    Session.setCurrentUser(user);

                    labelError.setText("Passwort erfolgreich gesetzt! Du wirst eingeloggt...");
                    labelError.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    Main.getInstance().mainWindow();

                } else {
                    boolean passwordMatch = PasswordUtil.verify(password, user.getSalt(), user.getPassword_hash());
                    if (passwordMatch) {

                        String today = de.hitec.nhplus.utils.DateConverter.convertLocalDateToString(java.time.LocalDate.now());
                        user.setLast_login(today);
                        this.dao.update(user);

                        Session.setCurrentUser(user);
                        Main.getInstance().mainWindow();
                    } else {
                        labelError.setVisible(true);
                        labelError.setText("Falsches Passwort!");
                    }
                }
            }
            else {
                labelError.setVisible(true);
                labelError.setText("Den Benutzer gibt es nicht!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}