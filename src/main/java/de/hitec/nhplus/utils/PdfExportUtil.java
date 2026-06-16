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

    private static File setUpDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pdf Speichern");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));

        return fileChooser.showSaveDialog(null);
    }


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

    private static void writeFooter(PDPageContentStream currentContentStream) throws IOException {
        currYPos = FOOTER_Y_START;
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = currentDate.format(formatter);

        writeText(currentContentStream, 315, currYPos,"Auszug erstellt am: " + formattedDate, FONT_NORMAL);
    }

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
