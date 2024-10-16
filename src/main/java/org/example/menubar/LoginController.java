package org.example.menubar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView logoImageView; // Thêm ImageView cho logo

    // Phương thức khởi tạo
    @FXML
    public void initialize() {
        // Tải hình ảnh vào ImageView
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Image_login.png"))); // Đường dẫn đến hình ảnh
        logoImageView.setImage(logo);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            // Chuyển đến menu chính
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
                Parent mainMenuRoot = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(mainMenuRoot, 600, 400));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Đăng nhập thất bại!");
            alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    //connect to SQL
    public static Connection SQL_connect() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:data/liba.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }


    private boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE user_name = ? AND password = ?";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            // Nếu có kết quả thì đăng nhập thành công
            return rs.next();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}


