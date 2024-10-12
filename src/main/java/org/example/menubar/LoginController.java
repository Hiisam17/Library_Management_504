package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Xử lý sự kiện nút Đăng nhập
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

    // Xử lý sự kiện nút Thoát
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Hàm kiểm tra thông tin đăng nhập
    private boolean validateLogin(String username, String password) {
        return username.equals("admin") && password.equals("1234");
    }
}

