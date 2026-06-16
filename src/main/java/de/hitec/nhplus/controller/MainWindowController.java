package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void handleShowAllPatient(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllPatientView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowAllTreatments(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllTreatmentView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Lädt die Ansicht zur Anzeige aller Pfleger und setzt sie in die Center-Region
     * des Main BorderPanes.
     *
     * <p>Die View wird aus der FXML-Datei {@code AllCaregiverView.fxml}
     * geladen und dynamisch in die Hauptoberfläche eingebettet.</p>
     *
     * @param event ActionEvent, ausgelöst durch den Button zum Öffnen der Pfleger-Übersicht
     */

    @FXML
    private void handleShowAllCaregiver(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Button in der Navigation, der die Sicht zur Verwaltung aller Benutzer aufruft.
     * Sichtbar ausschließlich für Benutzer mit Administrator-Rechten.
     */
    @FXML
    private Button btnAllUser;

    /**
     * Initialisiert den Controller nach dem Laden der FXML-Datei.
     * Prüft über die aktuelle {@link Session}, ob der angemeldete Benutzer ein Administrator ist,
     * und schaltet den Button für die Benutzerverwaltung entsprechend sichtbar oder unsichtbar.
     */
    @FXML
    public void initialize() {
        btnAllUser.setVisible(Session.isAdmin());
    }

    /**
     * Event-Handler für den Klick auf den "Benutzerverwaltung"-Button.
     * Lädt die FXML-Sicht {@code AllUserView.fxml} dynamisch über einen {@link FXMLLoader}
     * und bettet sie im Zentrum des Haupt-Layouts ({@code mainBorderPane}) ein.
     *
     * @param event Das ausgelöste ActionEvent des Buttons.
     */
    @FXML
    private void handleShowAllUser(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllUserView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
