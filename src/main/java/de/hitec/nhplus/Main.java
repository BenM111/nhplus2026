package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.ConnectionBuilder;

import de.hitec.nhplus.utils.PasswordUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;
    private static Main instance;

    /**
     * Gibt die aktuelle, laufende Instanz der Hauptanwendung (Singleton-Muster) zurück.
     * Ermöglicht anderen Controllern und Klassen den Zugriff auf zentrale Steuerungsfunktionen
     * wie den Fensterwechsel.
     *
     * @return Die statische Instanz der {@link Main}-Klasse.
     */
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Instanz speichern
        this.primaryStage = primaryStage;
        openLoginWindow();
    }

    /**
     * Lädt und öffnet das Login-Fenster der Anwendung.
     * <p>
     * Diese Methode initialisiert die Benutzeroberfläche für den Login, indem sie die
     * entsprechende FXML-Datei ({@code LoginView.fxml}) lädt, die Szene auf der primären
     * Bühne ({@code primaryStage}) platziert und wichtige Fenster-Eigenschaften wie den
     * Titel, das Anwendungs-Icon (Favicon) sowie die feste Fenstergröße (nicht skalierbar) definiert.
     * Zudem wird ein Event-Handler registriert, der beim Schließen des Fensters die
     * gesamte Anwendung sauber beendet.
     * </p>
     * * @see javafx.fxml.FXMLLoader
     * @see javafx.stage.Stage
     */
    public void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox vBox = loader.load();

            Scene scene = new Scene(vBox);
            this.primaryStage.setTitle("Login");
            this.primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/de/hitec/nhplus/images/favicon-32x32.png"))));
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> closeApplication());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();

            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/de/hitec/nhplus/images/favicon-32x32.png"))));
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> {
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void closeApplication() {
        ConnectionBuilder.closeConnection();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}