package de.hitec.nhplus.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonExportUtil {

    private JsonExportUtil() {
    }

    private static File setUpDocument() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("JSON speichern");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "JSON-Dateien",
                        "*.json"
                )
        );

        return fileChooser.showSaveDialog(null);
    }

    public static void exportPatient(Patient patient)
            throws IOException {

        File file = setUpDocument();

        if (file == null) {
            return;
        }

        List<Treatment> treatments =
                loadTreatments(patient.getPid());

        Map<String, Object> exportData = new LinkedHashMap<>();

        exportData.put("patient", patient);
        exportData.put("treatments", treatments);

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        mapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );

        mapper.writeValue(file, exportData);
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
}