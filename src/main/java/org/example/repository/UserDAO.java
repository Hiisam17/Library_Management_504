package org.example.repository;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.repository.DatabaseManager.getInstance;
import static org.example.util.DialogUtils.showAlert;

/**
 * UserDAO class handles the registration process for new users, including form validation
 * and inserting new users into the database.
 */
public class UserDAO {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ImageView logoImageView;

    private Stage stage;

    /**
     * Sets the stage for the current window.
     *
     * @param stage the Stage instance for the current window
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Validates the user registration by checking if the passwords match and if the username is unique.
     * If valid, inserts the new user into the database.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @param confirmPassword the password confirmation entered by the user
     * @return true if registration is successful, false otherwise
     */
    private boolean validateRegister(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            showAlert("Thông báo", "Mật khẩu không khớp!");
            return false;
        }

        String checkUserSql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        String insertUserSql = "INSERT INTO users (user_name, password) VALUES (?, ?)";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {

            // Kiểm tra tên người dùng
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Thông báo", "Tên người dùng đã tồn tại!");
                return false;
            }

            // Chèn tên người dùng vào cơ sở dữ liệu
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);

                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
        }

        return false;
    }

    /**
     * Handles the registration action when the "Register" button is clicked.
     * It validates the input fields and calls the registration method.
     *
     * @param actionEvent the event triggered by clicking the register button
     */
    @FXML
    private void DAOhandleRegister(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền tất cả các trường!");
            return;
        }

        if (validateRegister(username, password, confirmPassword)) {
            showAlert("Thành công", "Đăng ký thành công!");
        }
    }

    /**
     * Handles the exit action when the "Exit" button is clicked.
     * It closes the current window.
     *
     * @param actionEvent the event triggered by clicking the exit button
     */
    @FXML
    private void DAOhandleExit(ActionEvent actionEvent) {
        if (stage != null) {
            stage.close();
        } else {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            if (currentStage != null) {
                currentStage.close();
            }
        }
    }
}
