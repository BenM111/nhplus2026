package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse PdfExportUtil ermöglicht den Export sämtlicher zu einer Person
 * gespeicherter Daten in eine PDF-Datei.
 *
 * <p>
 * Sie wurde zur Umsetzung der User Story „Auskunft personenbezogener Daten“
 * eingeführt. Exportiert werden sowohl Stammdaten des Patienten als auch
 * alle zugehörigen Behandlungsdaten.
 * </p>
 *
 * <p>
 * Die Klasse folgt dem Single-Responsibility-Prinzip (SRP), da sie
 * ausschließlich für die Erstellung und Formatierung von PDF-Dokumenten
 * verantwortlich ist.
 * </p>
 *
 * <p>
 * Der eigentliche Datenzugriff erfolgt weiterhin über die DAO-Schicht,
 * wodurch eine klare Trennung zwischen Datenhaltung und Exportlogik
 * gewährleistet wird.
 * </p>
 *
 * @author Ben
 */
public class PdfExportUtil {
    private static final float MARGIN = 70f;
    private static final int FONT_SIZE = 12;
    private static final int FONT_SIZE_LARGE = 18;
    private static final float LINE_HEIGHT = 14f;

    private static final PDType1Font FONT_NORMAL =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static float pageWidth = 0;
    private static final float HEADER_Y_START = 700f;
    private static final float FOOTER_Y_START = 50;

    private static float currYPos = 0f;

    private PdfExportUtil() {}

    /**
     * Öffnet einen Datei-Speicherdialog zur Auswahl des Speicherorts der PDF-Datei.
     *
     * <p>
     * Die Methode kapselt die Benutzerinteraktion zur Dateiauswahl und stellt sicher,
     * dass der Export nur nach expliziter Nutzerentscheidung durchgeführt wird.
     * </p>
     *
     * @return die vom Benutzer ausgewählte Datei oder {@code null}, falls der Dialog abgebrochen wurde
     */
    private static File setUpDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pdf Speichern");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));

        return fileChooser.showSaveDialog(null);
    }

    /**
     * Exportiert sämtliche zu einem Patienten gespeicherten Daten in eine PDF-Datei.
     *
     * <p>
     * Die Methode bildet den zentralen Einstiegspunkt der PDF-Exportfunktion.
     * Sie lädt alle benötigten Daten, erzeugt das Dokument, fügt Inhalte ein
     * und speichert die fertige Datei am vom Benutzer gewählten Speicherort.
     * </p>
     *
     * <p>
     * Im Rahmen der User Story wird bewusst nur ein einzelner Patient exportiert,
     * um eine gezielte Datenauskunft gemäß Datenschutzanforderungen zu ermöglichen.
     * </p>
     *
     * @param patient der zu exportierende Patient
     *
     * @throws IOException falls beim Erstellen oder Speichern des PDF-Dokuments
     *         ein Fehler auftritt
     */
    public static void exportPatient(Patient patient) throws IOException {
        File file = setUpDocument();
        if(file == null) {
            return;
        }

        PDDocument pdDocument = new PDDocument();
        PDPage page = new PDPage();
        pdDocument.addPage(page);

        PDPageContentStream currentContentStream =
                new PDPageContentStream(pdDocument, page);

        pageWidth = page.getMediaBox().getWidth();

        List<Treatment> treatments =
                loadTreatments(patient.getPid());

        currYPos = HEADER_Y_START;

        writeHeader(currentContentStream);
        writePatientData(currentContentStream, patient);
        writeTreatments(currentContentStream, treatments);
        writeFooter(currentContentStream);

        currentContentStream.close();

        pdDocument.save(file);
        pdDocument.close();

    }

    /**
     * Schreibt den Kopfbereich des PDF-Dokuments.
     *
     * <p>
     * Der Header enthält den Titel des Exports und dient der strukturellen
     * Einordnung des Dokuments als personenbezogene Datenauskunft.
     * </p>
     *
     * @param currentContentStream aktiver PDF-Inhalt-Stream
     * @throws IOException falls beim Schreiben in das PDF ein Fehler auftritt
     */
    private static void writeHeader(PDPageContentStream currentContentStream) throws IOException {
        String text = "AUSKUNFT PERSONENBEZOGENER DATEN";
        float width =
                new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(text) / 1000 * FONT_SIZE_LARGE;
        float xPos = (pageWidth - width) / 2;
        currentContentStream.setFont(FONT_NORMAL, FONT_SIZE_LARGE);
        currentContentStream.beginText();
        currentContentStream.newLineAtOffset(xPos, currYPos);
        currentContentStream.showText(text);
        currentContentStream.endText();
        currYPos -= 35;
    }

    /**
     * Schreibt sämtliche Stammdaten eines Patienten in das PDF-Dokument.
     *
     * <p>
     * Exportiert werden alle personenbezogenen Informationen, die in der
     * Patiententabelle gespeichert sind.
     * </p>
     *
     * @param currentContentStream aktiver PDF-Stream
     * @param patient zu exportierender Patient
     *
     * @throws IOException falls beim Schreiben des Dokuments ein Fehler auftritt
     */
    private static void writePatientData(PDPageContentStream currentContentStream,Patient patient) throws IOException {
        currYPos -= 35;

        writeText(currentContentStream, 0, currYPos, "Patientendaten", FONT_BOLD);
        currYPos -= 25;

        writeText(currentContentStream, 20, currYPos, "Vorname: " + patient.getFirstName(), FONT_NORMAL);
        currYPos -= LINE_HEIGHT;

        writeText(currentContentStream, 20, currYPos, "Nachname: " + patient.getSurname(), FONT_NORMAL);
        currYPos -= LINE_HEIGHT;

        writeText(currentContentStream, 20, currYPos, "Geburtsdatum: " + patient.getDateOfBirth(), FONT_NORMAL);
        currYPos -= LINE_HEIGHT;

        writeText(currentContentStream, 20, currYPos, "Pflegegrad: " + patient.getCareLevel(), FONT_NORMAL);
        currYPos -= LINE_HEIGHT;

        writeText(currentContentStream, 20, currYPos, "Zimmernummer: " + patient.getRoomNumber(), FONT_NORMAL);
        currYPos -= LINE_HEIGHT;

        writeText(currentContentStream, 20, currYPos, "Vermögensstand: " + patient.getAssets(), FONT_NORMAL);

        currYPos -= 40;
    }

    /**
     * Schreibt sämtliche Behandlungsdaten eines Patienten in das PDF-Dokument.
     *
     * <p>
     * Die Methode ergänzt die exportierten Stammdaten um alle gespeicherten
     * Behandlungseinträge und ermöglicht dadurch eine vollständige
     * Datenauskunft.
     * </p>
     *
     * @param currentContentStream aktiver PDF-Stream
     * @param treatments Liste aller Behandlungen des Patienten
     *
     * @throws IOException falls beim Schreiben des Dokuments ein Fehler auftritt
     */
    private static void writeTreatments(PDPageContentStream currentContentStream, List<Treatment> treatments) throws IOException {

        writeText(currentContentStream, 0, currYPos, "Behandlungen", FONT_BOLD);
        currYPos -= 25;

        if (treatments.isEmpty()) {

            writeText(currentContentStream, 20, currYPos,
                    "Keine Behandlungen gespeichert.", FONT_NORMAL);
            currYPos -= 25;

            return;
        }

        for (Treatment treatment : treatments) {

            writeText(currentContentStream, 0, currYPos,
                    "Identifikationsnummer #" + treatment.getTid(), FONT_BOLD);
            currYPos -= 20;

            writeText(currentContentStream, 20, currYPos,
                    "Datum: " + treatment.getDate(), FONT_NORMAL);
            currYPos -= LINE_HEIGHT;

            writeText(currentContentStream, 20, currYPos,
                    "Beginn: " + treatment.getBegin(), FONT_NORMAL);
            currYPos -= LINE_HEIGHT;

            writeText(currentContentStream, 20, currYPos,
                    "Ende: " + treatment.getEnd(), FONT_NORMAL);
            currYPos -= LINE_HEIGHT;

            writeText(currentContentStream, 20, currYPos,
                    "Beschreibung:", FONT_NORMAL);
            currYPos -= LINE_HEIGHT;

            writeText(currentContentStream, 40, currYPos,
                    treatment.getDescription(), FONT_NORMAL);
            currYPos -= 10;

            writeText(currentContentStream, 20, currYPos,
                    "Bemerkungen:", FONT_NORMAL);
            currYPos -= LINE_HEIGHT;

            writeText(currentContentStream, 40, currYPos,
                    treatment.getRemarks(), FONT_NORMAL);

            currYPos -= 25;
        }
    }

    /**
     * Lädt alle Behandlungen eines Patienten aus der Datenbank.
     *
     * <p>
     * Die Methode dient der Trennung zwischen Datenbeschaffung und
     * PDF-Erzeugung. Dadurch bleibt die Exportlogik unabhängig von der
     * konkreten Datenbankimplementierung.
     * </p>
     *
     * @param pid eindeutige Patienten-ID
     * @return Liste aller zugehörigen Behandlungen
     */
    private static List<Treatment> loadTreatments(long pid) {
        try {
            return DaoFactory.getDaoFactory()
                    .createTreatmentDao()
                    .readTreatmentsByPid(pid);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Fügt eine Fußzeile mit dem Erstellungsdatum in das PDF ein.
     *
     * <p>
     * Dies dient der Nachvollziehbarkeit des Exportzeitpunkts und ist relevant
     * für Datenschutz- und Dokumentationsanforderungen.
     * </p>
     *
     * @param currentContentStream aktiver PDF-Inhalt-Stream
     * @throws IOException falls beim Schreiben in das PDF ein Fehler auftritt
     */
    private static void writeFooter(PDPageContentStream currentContentStream) throws IOException {
        currYPos = FOOTER_Y_START;
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = currentDate.format(formatter);

        writeText(currentContentStream, 315, currYPos,"Auszug erstellt am: " + formattedDate, FONT_NORMAL);
    }

    /**
     * Schreibt formatierten Text an eine bestimmte Position im PDF-Dokument.
     *
     * <p>
     * Die Methode kapselt die wiederholte PDFBox-Logik zur Textausgabe und
     * unterstützt Zeilenumbrüche innerhalb eines Textblocks.
     * </p>
     *
     * <p>
     * Dadurch wird die Exportlogik in Teile gekleinert (SRP) und die
     * Wartbarkeit verbessert.
     * </p>
     *
     * @param currentContentStream aktiver PDF-Inhalt-Stream
     * @param displacement horizontale Verschiebung relativ zum linken Rand
     * @param yPos vertikale Startposition
     * @param text auszugebender Text
     * @param font verwendete Schriftart
     * @throws IOException falls beim Schreiben in das PDF ein Fehler auftritt
     */
    private static void writeText(PDPageContentStream currentContentStream, float displacement, float yPos, String text,PDType1Font font) throws IOException {

        currentContentStream.setFont(font, FONT_SIZE);

        String[] lines = text.split("\n");

        float currentY = yPos;

        for (String line : lines) {

            currentContentStream.beginText();
            currentContentStream.newLineAtOffset(MARGIN + displacement, currentY);
            currentContentStream.showText(line);
            currentContentStream.endText();

            currentY -= LINE_HEIGHT;
        }
        currYPos = currentY;
    }

}
