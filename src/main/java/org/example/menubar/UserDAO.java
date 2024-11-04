package org.example.menubar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static org.example.menubar.DatabaseManager.SQL_connect;

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

    @FXML
    public void initialize() {
        try {
            // Tải hình ảnh vào ImageView
            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Image_login.png")));
            logoImageView.setImage(logo);
        } catch (NullPointerException e) {
            System.err.println("Không tìm thấy hình ảnh: Image_login.png");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private boolean validateRegister(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            showAlert("Thông báo", "Mật khẩu không khớp!", Alert.AlertType.ERROR);
            return false;
        }

        String checkUserSql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        String insertUserSql = "INSERT INTO users (user_name, password) VALUES (?, ?)";

        try (Connection conn = SQL_connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {

            // Kiểm tra tên người dùng
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Thông báo", "Tên người dùng đã tồn tại!", Alert.AlertType.ERROR);
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void DAOhandleRegister(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền tất cả các trường!", Alert.AlertType.ERROR);
            return;
        }

        if (validateRegister(username, password, confirmPassword)) {
            showAlert("Thành công", "Đăng ký thành công!", Alert.AlertType.INFORMATION);
        }
    }

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
