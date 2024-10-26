package org.example.menubar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (user_name, password) VALUES (?, ?)";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsAffected = pstmt.executeUpdate();// true nếu đăng ký thành công

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void handleRegister(ActionEvent actionEvent) {
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}