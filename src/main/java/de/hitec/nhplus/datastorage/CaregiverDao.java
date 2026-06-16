package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.EncryptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CaregiverDao extends DaoImp<Caregiver>{


    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Caregiver(
                result.getInt("caregiver_id"),
                EncryptionUtil.decrypt(result.getString("first_name")),
                EncryptionUtil.decrypt(result.getString("last_name")),
                DateConverter.convertStringToLocalDate(EncryptionUtil.decrypt(result.getString("birth_date"))),
                EncryptionUtil.decrypt(result.getString("job_title")),
                EncryptionUtil.decrypt(result.getString("phone_number")));
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Caregiver> list = new ArrayList<>();
        while (result.next()) {
            LocalDate date = DateConverter.convertStringToLocalDate(EncryptionUtil.decrypt(result.getString("birth_date")));
            Caregiver caregiver = new Caregiver(
                    result.getInt("caregiver_id"),
                    EncryptionUtil.decrypt(result.getString("first_name")),
                    EncryptionUtil.decrypt(result.getString("last_name")),
                    date,
                    EncryptionUtil.decrypt(result.getString("job_title")),
                    EncryptionUtil.decrypt(result.getString("phone_number")));
            list.add(caregiver);
        }
        return list;
    }

    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        try {
            final String SQL = "INSERT INTO caregiver (first_name, last_name, birth_date, phone_number, job_title) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, EncryptionUtil.encrypt(caregiver.getFirstName()));
            preparedStatement.setString(2, EncryptionUtil.encrypt(caregiver.getSurname()));
            preparedStatement.setString(3, EncryptionUtil.encrypt(caregiver.getDateOfBirth()));
            preparedStatement.setString(4, EncryptionUtil.encrypt(caregiver.getPhoneNumber()));
            preparedStatement.setString(5, EncryptionUtil.encrypt(caregiver.getJobTitle()));
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw  new RuntimeException(exception);
        }
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long caregiverId) {
        try {
            final String SQL = "SELECT * FROM caregiver WHERE caregiver_id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, caregiverId);
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new   RuntimeException(exception);
        }
    }

    @Override
    protected PreparedStatement getReadAllStatement() {

        try {
            final String SQL = "SELECT * FROM caregiver";
            PreparedStatement statement = this.connection.prepareStatement(SQL);
            return statement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw  new RuntimeException(exception);
        }
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {

        try {
            final String SQL =
                    "UPDATE caregiver SET " +
                            "first_name = ?, " +
                            "last_name = ?, " +
                            "birth_date = ?, " +
                            "job_title = ?, " +
                            "phone_number = ? " +
                            "WHERE caregiver_id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, EncryptionUtil.encrypt(caregiver.getFirstName()));
            preparedStatement.setString(2, EncryptionUtil.encrypt(caregiver.getSurname()));
            preparedStatement.setString(3, EncryptionUtil.encrypt(caregiver.getDateOfBirth()));
            preparedStatement.setString(4, EncryptionUtil.encrypt(caregiver.getJobTitle()));
            preparedStatement.setString(5, EncryptionUtil.encrypt(caregiver.getPhoneNumber()));
            preparedStatement.setLong(6, caregiver.getCaregiverId());
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw  new RuntimeException(exception);
        }
    }

    @Override
    protected PreparedStatement getDeleteStatement(long caregiverId) {
        try {
            final String SQL = "DELETE FROM Caregiver WHERE caregiver_id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, caregiverId);
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
