package org.example.menubar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private ImageView logoImageView;
    @FXML
    private PasswordField confirmPasswordField;
    private Stage stage;
    @FXML
    public void initialize() {
        // Tải hình ảnh vào ImageView
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Image_login.png"))); // Đường dẫn đến hình ảnh
        logoImageView.setImage(logo);
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean validateRegister(String username, String password, String confirmPassword) {
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        String insertUserSql = "INSERT INTO users (user_name, password) VALUES (?, ?)";

        try (Connection conn = SQL_connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {

            // Kiểm tra tên người dùng
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Tên người dùng đã tồn tại
                showAlert("Thông báo", "Tên người dùng đã tồn tại!", Alert.AlertType.ERROR);
                return false; // Hoặc có thể ném ra ngoại lệ
            }

            // Kiểm tra mật khẩu và mật khẩu xác nhận
            if (!password.equals(confirmPassword)) {
                showAlert("Thông báo", "Mật khẩu không khớp!", Alert.AlertType.ERROR);
                return false;
            }

            // Chèn tên người dùng vào cơ sở dữ liệu
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);

                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0; // true nếu đăng ký thành công
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Không hiển thị header
        alert.setContentText(message); // Nội dung thông báo
        alert.showAndWait(); // Hiển thị alert và chờ người dùng đóng
    }
    @FXML
    private void DAOhandleRegister(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword=confirmPasswordField.getText();
        if(validateRegister(username, password,confirmPassword))
        {
            showAlert("Thành công", "Đăng ký thành công!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void DAOhandleExit(ActionEvent actionEvent) {
           if(stage!=null) {
               stage.close(); // Đóng cửa sổ hiện tại
           }
    }


}