package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Đăng nhập thành công!");
            alert.setContentText("Chào mừng, " + username + "!");
            alert.showAndWait();
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

    private boolean validateLogin(String username, String password) {
        return username.equals("admin") && password.equals("1234");
    }
}


