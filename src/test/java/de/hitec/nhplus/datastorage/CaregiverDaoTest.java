package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.SetUpDB;
import de.hitec.nhplus.utils.DateConverter;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CaregiverDaoTest {

    private static CaregiverDao dao;
    private static Connection connection;

    @BeforeAll
    static void setup() {
        // DB komplett neu aufsetzen
        SetUpDB.setUpDb();

        connection = ConnectionBuilder.getConnection();
        dao = new CaregiverDao(connection);
    }

    @Test
    @Order(1)
    void testCreateCaregiver() throws SQLException {
        Caregiver caregiver = new Caregiver(
                "Test",
                "User",
                LocalDate.of(1990, 1, 1),
                "Nurse",
                "12345"
        );

        dao.create(caregiver);

        List<Caregiver> all = dao.readAll();

        boolean found = all.stream()
                .anyMatch(c -> c.getFirstName().equals("Test") &&
                        c.getSurname().equals("User"));

        assertTrue(found, "Caregiver wurde nicht gespeichert");
    }

    @Test
    @Order(2)
    void testReadAll() throws SQLException {
        List<Caregiver> all = dao.readAll();

        assertNotNull(all);
        assertTrue(all.size() > 0, "Es sollten Testdaten vorhanden sein");
    }

    @Test
    @Order(3)
    void testUpdateCaregiver() throws SQLException {
        List<Caregiver> all = dao.readAll();
        Caregiver c = all.get(0);

        c.setJobTitle("UpdatedJob");

        dao.update(c);

        Caregiver updated = dao.read(c.getCaregiverId());

        assertEquals("UpdatedJob", updated.getJobTitle());
    }

    @Test
    @Order(4)
    void testDeleteCaregiver() throws SQLException {
        Caregiver caregiver = new Caregiver(
                "Delete",
                "Me",
                LocalDate.of(1995, 5, 5),
                "Temp",
                "999"
        );

        dao.create(caregiver);

        List<Caregiver> before = dao.readAll();

        Caregiver toDelete = before.stream()
                .filter(c -> c.getFirstName().equals("Delete"))
                .findFirst()
                .orElseThrow();

        dao.deleteById(toDelete.getCaregiverId());

        List<Caregiver> after = dao.readAll();

        boolean exists = after.stream()
                .anyMatch(c -> c.getFirstName().equals("Delete"));

        assertFalse(exists, "Caregiver wurde nicht gelöscht");
    }
}