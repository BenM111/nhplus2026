package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller für die Pfleger-Übersicht (AllCaregiverView).
 *
 * <p>Verantwortlich für die Anzeige, Erstellung, Bearbeitung
 * und Löschung von Caregiver-Daten in der Benutzeroberfläche.</p>
 *
 * <p>Die Daten werden über ein CaregiverDao aus der Datenbank geladen
 * und in einer JavaFX TableView dargestellt.</p>
 *
 * @author Amir
 * @version 1.0
 */
public class AllCaregiverController {


        @FXML
        private TableView<Caregiver> tableView;

        @FXML
        private TableColumn<Caregiver, Integer> columnId;

        @FXML
        private TableColumn<Caregiver, String> columnFirstName;

        @FXML
        private TableColumn<Caregiver, String> columnLastName;

        @FXML
        private TableColumn<Caregiver, String> columnDateOfBirth;

        @FXML
        private TableColumn<Caregiver, String> columnPhoneNumber;

        @FXML
        private TableColumn<Caregiver, String> columnJobTitle;


        @FXML
        private Button buttonDelete;

        @FXML
        private Button buttonAdd;

        @FXML
        private TextField textFieldLastName;

        @FXML
        private TextField textFieldFirstName;

        @FXML
        private TextField textFieldDateOfBirth;

        @FXML
        private TextField textFieldPhoneNumber;

        @FXML
        private TextField textFieldJobTitle;

        private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
        private CaregiverDao dao;


        public void initialize() {
            this.readAllAndShowInTableView();

            this.columnId.setCellValueFactory(new PropertyValueFactory<>("caregiverId"));

            // CellValueFactory to show property values in TableView
            this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            // CellFactory to write property values from with in the TableView
            this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

            this.columnLastName.setCellValueFactory(new PropertyValueFactory<>("surname"));
            this.columnLastName.setCellFactory(TextFieldTableCell.forTableColumn());

            this.columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
            this.columnDateOfBirth.setCellFactory(TextFieldTableCell.forTableColumn());

            this.columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            this.columnPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());

            this.columnJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
            this.columnJobTitle.setCellFactory(TextFieldTableCell.forTableColumn());

            //Anzeigen der Daten
            this.tableView.setItems(this.caregivers);

            this.buttonDelete.setDisable(true);
            this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Caregiver>() {
                @Override
                public void changed(ObservableValue<? extends Caregiver> observableValue, Caregiver oldCaregiver, Caregiver newCaregiver) {;
                    de.hitec.nhplus.controller.AllCaregiverController.this.buttonDelete.setDisable(newCaregiver == null);
                }
            });

            this.buttonAdd.setDisable(true);
            ChangeListener<String> inputNewCaregiverListener = (observableValue, oldText, newText) ->
                    de.hitec.nhplus.controller.AllCaregiverController.this.buttonAdd.setDisable(!de.hitec.nhplus.controller.AllCaregiverController.this.areInputDataValid());
            this.textFieldLastName.textProperty().addListener(inputNewCaregiverListener);
            this.textFieldFirstName.textProperty().addListener(inputNewCaregiverListener);
            this.textFieldDateOfBirth.textProperty().addListener(inputNewCaregiverListener);
            this.textFieldPhoneNumber.textProperty().addListener(inputNewCaregiverListener);
            this.textFieldJobTitle.textProperty().addListener(inputNewCaregiverListener);
        }

        /**
        * Wird aufgerufen, wenn der Vorname in der Tabelle bearbeitet wurde.
        *
        * @param event enthält die geänderte Caregiver-Instanz und den neuen Wert
        */

        @FXML
        public void handleOnEditFirstname(TableColumn.CellEditEvent<Caregiver, String> event) {
            event.getRowValue().setFirstName(event.getNewValue());
            this.doUpdate(event);
        }

        /**
         * Aktualisiert den Nachnamen eines Caregivers nach einer Tabellenbearbeitung.
        *
        * @param event enthält die geänderte Caregiver-Instanz und den neuen Wert
        */
        @FXML
        public void handleOnEditLastName(TableColumn.CellEditEvent<Caregiver, String> event) {
            event.getRowValue().setSurname(event.getNewValue());
            this.doUpdate(event);
        }

        /**
        * Aktualisiert den Nachnamen eines Caregivers nach einer Tabellenbearbeitung.
        *
        * @param event enthält die geänderte Caregiver-Instanz und den neuen Wert
        */
        @FXML
        public void handleOnEditDateOfBirth(TableColumn.CellEditEvent<Caregiver, String> event) {
            event.getRowValue().setDateOfBirth(event.getNewValue());
            this.doUpdate(event);
        }

         /**
        * Aktualisiert den Jobtitel eines Caregivers nach Bearbeitung in der Tabelle.
        *
        * @param event enthält die geänderte Caregiver-Instanz und den neuen Wert
        */
        @FXML
        public void handleOnEditJobTitle(TableColumn.CellEditEvent<Caregiver, String> event) {
            event.getRowValue().setJobTitle(event.getNewValue());
            this.doUpdate(event);
        }

        /**
        * Aktualisiert die Telefonnummer eines Caregivers nach Bearbeitung in der Tabelle.
        *
        * @param event enthält die geänderte Caregiver-Instanz und den neuen Wert
        */
        @FXML
        public void handleOnEditPhoneNumber(TableColumn.CellEditEvent<Caregiver, String> event){
            event.getRowValue().setPhoneNumber(event.getNewValue());
            this.doUpdate(event);
        }

        private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
            try {
                this.dao.update(event.getRowValue());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        private void readAllAndShowInTableView() {
            this.caregivers.clear();
            this.dao = DaoFactory.getDaoFactory().createCaregiverDao();
            try {
                this.caregivers.addAll(this.dao.readAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        @FXML
        public void handleDelete() {
            Caregiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    DaoFactory.getDaoFactory().createCaregiverDao().deleteById(selectedItem.getCaregiverId());
                    this.tableView.getItems().remove(selectedItem);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        @FXML
        public void handleAdd() {
            String surname = this.textFieldLastName.getText();
            String firstName = this.textFieldFirstName.getText();
            String birthday = this.textFieldDateOfBirth.getText();
            LocalDate date = DateConverter.convertStringToLocalDate(birthday);
            String phoneNumber = this.textFieldPhoneNumber.getText();
            String jobTitle = this.textFieldJobTitle.getText();
            try {
                this.dao.create(new Caregiver(firstName, surname, date, jobTitle, phoneNumber));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            readAllAndShowInTableView();
            clearTextfields();
        }

        /**
         * Clears all contents from all <code>TextField</code>s.
         */
        private void clearTextfields() {
            this.textFieldFirstName.clear();
            this.textFieldLastName.clear();
            this.textFieldDateOfBirth.clear();
            this.textFieldPhoneNumber.clear();
            this.textFieldJobTitle.clear();
        }

        private boolean areInputDataValid() {
            if (!this.textFieldDateOfBirth.getText().isBlank()) {
                try {
                    DateConverter.convertStringToLocalDate(this.textFieldDateOfBirth.getText());
                } catch (Exception exception) {
                    return false;
                }
            }

            return !this.textFieldFirstName.getText().isBlank() && !this.textFieldLastName.getText().isBlank() &&
                    !this.textFieldDateOfBirth.getText().isBlank() && !this.textFieldPhoneNumber.getText().isBlank() &&
                    !this.textFieldJobTitle.getText().isBlank();
        }
    }
