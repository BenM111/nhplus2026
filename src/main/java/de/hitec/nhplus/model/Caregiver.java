package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
/**
 * Repräsentiert einen Pfleger (Caregiver) im NHPlus-System.
 *
 * <p>Die Klasse erweitert die Klasse {@link Person} und enthält
 * zusätzliche Informationen eines Pflegers wie Geburtsdatum,
 * Berufsbezeichnung und Telefonnummer.</p>
 *
 * <p>Die Daten werden mit JavaFX Properties gespeichert,
 * damit sie direkt mit UI-Elementen (z.B. TableView) verbunden
 * werden können.</p>
 *
 * @author Amir
 * @version 1.0
 */
public class Caregiver extends Person{
    private SimpleLongProperty caregiverId;
    private final SimpleStringProperty dateOfBirth;
    private final SimpleStringProperty jobTitle;
    private final SimpleStringProperty phoneNumber;

    /**
     * Erstellt einen neuen Pfleger ohne ID.
     *
     * @param firstName Vorname des Pflegers
     * @param surname Nachname des Pflegers
     * @param dateOfBirth Geburtsdatum des Pflegers
     * @param jobTitle Berufsbezeichnung des Pflegers
     * @param phoneNumber Telefonnummer des Pflegers
     */

    public Caregiver(String firstName, String surname, LocalDate dateOfBirth, String jobTitle, String phoneNumber) {
        super(firstName, surname);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.jobTitle = new SimpleStringProperty(jobTitle);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    /**
     * Erstellt einen Pfleger mit vorhandener ID.
     *
     * @param caregiverId eindeutige ID des Pflegers
     * @param firstName Vorname des Pflegers
     * @param surname Nachname des Pflegers
     * @param dateOfBirth Geburtsdatum des Pflegers
     * @param jobTitle Berufsbezeichnung des Pflegers
     * @param phoneNumber Telefonnummer des Pflegers
     */

    public Caregiver(long caregiverId, String firstName, String surname, LocalDate dateOfBirth, String jobTitle, String phoneNumber) {
        super(firstName, surname);
        this.caregiverId = new SimpleLongProperty(caregiverId);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.jobTitle = new SimpleStringProperty(jobTitle);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    /**
     * Gibt die ID des Pflegers zurück.
     *
     * @return caregiverId des Pflegers
     */

    public long getCaregiverId() {
        return caregiverId.get();
    }

    public SimpleLongProperty caregiverIdProperty() {
        return caregiverId;
    }

    public void setCaregiverId(long caregiverId) {
        this.caregiverId.set(caregiverId);
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public SimpleStringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    public String getJobTitle() {
        return jobTitle.get();
    }

    public SimpleStringProperty jobTitleProperty() {
        return jobTitle;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return "Caregiver{" +
                "\ncaregiverId=" + caregiverId +
                ",\ndateOfBirth=" + dateOfBirth +
                ",\njobTitle=" + jobTitle +
                ",\nphoneNumber=" + phoneNumber +
                '}';
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle.set(jobTitle);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }
}
