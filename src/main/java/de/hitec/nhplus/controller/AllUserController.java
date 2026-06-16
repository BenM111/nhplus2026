package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.PasswordUtil;
import de.hitec.nhplus.utils.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Der Controller für die Benutzerverwaltungssicht (AllUserView).
 * Verantwortlich für das Anzeigen aller registrierten Benutzer in einer Tabelle,
 * das Hinzufügen neuer Benutzer mit gehashten Passwörtern sowie das Löschen von Benutzern.
 *
 * @author Luca Bullwinkel
 */
public class AllUserController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Long> columnId;

    @FXML
    private TableColumn<User, String> columnUsername;

    @FXML
    private TableColumn<User, String> columnRole;

    @FXML
    private TableColumn<User, String> columnCreatedAt;

    @FXML
    private TableColumn<User, String> columnLastLogin;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label labelError;

    /**
     * Die interne Liste aller Benutzer, die an die TableView gebunden ist.
     */
    private ObservableList<User> users;

    /**
     * Das Data Access Object für den Datenbankzugriff auf die Benutzertabelle.
     */
    private UserDao dao;

    /**
     * Initialisiert den Controller nach dem Laden der FXML-Datei.
     * Richtet das DAO, die Auswahloptionen der Rollen-ComboBox sowie die CellValueFactorys
     * für die Tabellenspalten ein. Lädt anschließend die vorhandenen Benutzer.
     */
    @FXML
    public void initialize() {
        dao = DaoFactory.getDaoFactory().createUserDao();

        roleComboBox.getItems().addAll("admin", "user");
        roleComboBox.setValue("user");

        columnId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        columnUsername.setCellValueFactory(data -> data.getValue().usernameProperty());
        columnRole.setCellValueFactory(data -> data.getValue().roleProperty());
        columnCreatedAt.setCellValueFactory(data -> data.getValue().created_atProperty());
        columnLastLogin.setCellValueFactory(data -> data.getValue().last_loginProperty());

        loadUsers();
    }

    /**
     * Lädt alle Benutzer aus der Datenbank und aktualisiert die Anzeige der TableView.
     * Fängt eventuelle SQLExceptions ab und gibt den Stacktrace aus.
     */
    private void loadUsers() {
        try {
            List<User> all = dao.readAll();
            users = FXCollections.observableArrayList(all);
            tableView.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event-Handler für den Button "Hinzufügen".
     * Validiert die Eingabefelder, überprüft die Einzigartigkeit des Benutzernamens,
     * generiert ein Salt sowie den Passwort-Hash und speichert den neuen Benutzer in der Datenbank.
     * Setzt bei Erfolg die Eingabemasken zurück.
     */
    @FXML
    private void handleAdd() {
        labelError.setVisible(false);

        String username = textFieldUsername.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Bitte Benutzername und Passwort eingeben!");
            return;
        }

        if (role == null) {
            showError("Bitte eine Rolle wählen!");
            return;
        }

        try {
            if (dao.readByUsername(username) != null) {
                showError("Benutzername bereits vergeben!");
                return;
            }

            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hash(password, salt);

            User newUser = new User(
                    null,
                    username,
                    hash,
                    salt,
                    LocalDate.now(),
                    role,
                    LocalDate.parse("")
            );

            dao.create(newUser);
            loadUsers();

            textFieldUsername.clear();
            passwordField.clear();
            roleComboBox.setValue("user");

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Datenbankfehler beim Anlegen!");
        }
    }

    /**
     * Event-Handler für den Button "Löschen".
     * Ermittelt den in der Tabelle selektierten Benutzer und löscht diesen aus der Datenbank.
     * Verhindert das Löschen des aktuell angemeldeten Benutzers (Selbstlöschungsschutz).
     */
    @FXML
    private void handleDelete() {
        labelError.setVisible(false);
        User selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Bitte einen Benutzer auswählen!");
            return;
        }

        if (selected.getUsername().equals(Session.getCurrentUser().getUsername())) {
            showError("Du kannst deinen eigenen Account nicht löschen!");
            return;
        }

        try {
            dao.deleteById(selected.getId());
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Datenbankfehler beim Löschen!");
        }
    }

    /**
     * Setzt den übergebenen Text in das Fehler-Label und macht dieses in der GUI sichtbar.
     *
     * @param message Die Fehlermeldung, die dem Benutzer angezeigt werden soll.
     */
    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }
}