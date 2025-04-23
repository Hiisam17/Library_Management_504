package org.example.controller.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.AdminUser;
import org.example.model.RegularUser;
import org.example.model.User;
import org.example.repository.UserLogin;
import org.example.util.DialogUtils;
import org.example.util.FXMLUtils;

import java.io.IOException;

import static org.example.util.DialogUtils.showAlert;

/**
 * Controller class for managing the login functionality of the application.
 */
public class LoginController {

    private Main mainApp;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView logoImageView;

    /**
     * Sets the main application reference.
     *
     * @param mainApp the main application instance
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Handles the login action. Validates user credentials and loads the appropriate dashboard
     * based on the user type (Admin or Regular).
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (mainApp != null) {
            UserLogin userLogin = new UserLogin();
            UserLogin.LoginResult loginResult = userLogin.validateLogin(username, password);

            if (loginResult == null) {
                showAlert("Đăng nhập thất bại!", "Tên đăng nhập hoặc mật khẩu không đúng.");
                return;
            }

            User user =
                    loginResult.isAdmin
                            ? new AdminUser(username, password)
                            : new RegularUser(username, password);

            user.LoginLoad(loginResult.userId, (Stage) usernameField.getScene().getWindow());
        }
    }

    /**
     * Handles the exit action. Terminates the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    /**
     * Handles the register action. Opens the registration window.
     *
     * @param actionEvent the triggered event when the user clicks the "Register" button
     */
    @FXML
    private void handleRegister(ActionEvent actionEvent) {
        try {
            FXMLUtils.openWindow(
                    "/views/user/UserDAO.fxml", // Path to the FXML file
                    "Đăng ký", // Window title
                    (Stage) usernameField.getScene().getWindow(), // Parent window
                    "/styles/auth/login.css" // Path to the CSS file
            );
        } catch (IOException e) {
            showAlert("Không thể mở cửa sổ Đăng ký", e.getMessage());
            e.printStackTrace();
        }
    }
}
