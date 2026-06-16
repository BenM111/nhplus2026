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

/**
 * Der Controller für das Login-Fenster (LoginView).
 * Handhabt die Authentifizierung von Benutzern, die Verifizierung von Passwörtern
 * sowie den Prozess der initialen Passwortvergabe (Erstanmeldung) für neu angelegte Accounts.
 *
 * @author Luca Bullwinkel
 */
public class LoginController {

    /**
     * Das Data Access Object für den Zugriff auf die Benutzerdatenbank.
     */
    private UserDao dao;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelError;

    /**
     * Initialisiert den Controller nach dem Laden der FXML-Sicht.
     * Erstellt die DAO-Instanz und fügt dem Benutzernamen-Feld einen Focus-Listener hinzu,
     * um beim Verlassen des Feldes direkt zu prüfen, ob es sich um eine Erstanmeldung handelt.
     */
    public void initialize() {
        this.dao = DaoFactory.getDaoFactory().createUserDao();
        usernameField.focusedProperty().addListener((observable, oldFocus, newFocus) -> {
            if (!newFocus) {
                checkIfNewUser();
            }
        });
    }

    /**
     * Prüft, ob der eingegebene Benutzer existiert und noch kein Passwort besitzt.
     * Ist dies der Fall, wird ein Hinweistext für die Erstanmeldung in der GUI eingeblendet.
     */
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

    /**
     * Event-Handler für den Login-Button.
     * Steuert den gesamten Authentifizierungsprozess:
     * <ul>
     * <li>Prüft, ob der Benutzer existiert.</li>
     * <li>Falls der Benutzer noch kein Passwort hat, wird das eingegebene Passwort gehasht,
     * gesalzen und als neues Passwort in der Datenbank hinterlegt (Erstanmeldung).</li>
     * <li>Falls bereits ein Passwort existiert, wird die Eingabe mit dem gespeicherten Hash verifiziert.</li>
     * </ul>
     * Bei erfolgreicher Anmeldung wird das Login-Datum aktualisiert, der Benutzer in die {@link Session}
     * eingetragen und das Hauptfenster der Anwendung geöffnet.
     */
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