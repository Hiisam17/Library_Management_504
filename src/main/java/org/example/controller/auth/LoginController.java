package org.example.controller.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.AdminUser;
import org.example.Main;
import org.example.model.RegularUser;
import org.example.model.User;
import org.example.repository.UserLogin;
import org.example.util.DialogUtils;
import org.example.util.FXMLUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.repository.DatabaseManager.DB_URL;
import static org.example.util.DialogUtils.showAlert;

public class LoginController {
    private Main mainApp;
    private final DialogUtils dialogUtils = new DialogUtils();

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView logoImageView; // Thêm ImageView cho logo

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

            if (mainApp != null) {
                UserLogin userlogin = new UserLogin();
                UserLogin.LoginResult loginResult = userlogin.validateLogin(username, password);

                if (loginResult == null) {
                    DialogUtils.showAlert("Đăng nhập thất bại!", "Tên đăng nhập hoặc mật khẩu không đúng.");
                    return;
                }

                User user = loginResult.isAdmin
                        ? new AdminUser(username, password)
                        : new RegularUser(username, password);

                user.LoginLoad(loginResult.userId, (Stage) usernameField.getScene().getWindow());
            }


    }
    @FXML
    private void handleExit() {
        System.exit(0);
    }

        @FXML
        private void handleRegister(ActionEvent actionEvent) {
            try {
                // Sử dụng FXMLUtils để mở giao diện đăng ký
                FXMLUtils.openWindow(
                        "/views/user/UserDAO.fxml", // Đường dẫn FXML
                        "Đăng ký",                  // Tiêu đề cửa sổ
                        (Stage) usernameField.getScene().getWindow(), // Cửa sổ cha
                        "/styles/auth/login.css"   // Đường dẫn CSS
                );
            } catch (IOException e) {
                DialogUtils.showAlert("Không thể mở cửa sổ Đăng ký", e.getMessage());
                e.printStackTrace();
            }
        }

}


