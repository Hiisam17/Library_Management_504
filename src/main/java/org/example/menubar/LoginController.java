package org.example.menubar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.usermenu.UserMenuController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static org.example.menubar.DatabaseManager.SQL_connect;

public class LoginController {
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

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
        //Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Image_login.png"))); // Đường dẫn đến hình ảnh
        //logoImageView.setImage(logo);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (mainApp != null) {
            LoginResult loginResult = validateLogin(username, password);
            if (loginResult == null) {
                showAlert("Đăng nhập thất bại!", "Tên đăng nhập hoặc mật khẩu không đúng.");
                return;
            }

            System.out.println("Login successful! User ID: " + loginResult.userId);
            User user;
            if (loginResult.isAdmin) {
                user = new AdminUser(username, password);
            } else {
                user = new RegularUser(username, password);
            }

            // Gọi phương thức LoginLoad để tải giao diện tương ứng
            user.LoginLoad(loginResult.userId,(Stage) usernameField.getScene().getWindow());
        }
    }
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    //connect to SQL



    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
        @FXML
        private void handleRegister(ActionEvent actionEvent) {
            try {
                // Tải giao diện FXML cho cửa sổ đăng ký
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDAO.fxml"));
                Parent root = loader.load(); // Tải tệp FXML

                // Tạo một Stage mới cho cửa sổ đăng ký
                Stage stage = new Stage();
                stage.setTitle("Đăng ký");
                stage.initModality(Modality.APPLICATION_MODAL); // Đặt cửa sổ này ở trên cửa sổ chính
                stage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow()); // Đặt chủ sở hữu của cửa sổ hiện tại

                // Tạo Scene với root từ tệp FXML
                Scene scene = new Scene(root);

                // Thêm CSS nếu tồn tại
                URL cssURL = getClass().getResource("login.css");
                if (cssURL != null) {
                    scene.getStylesheets().add(cssURL.toExternalForm());
                } else {
                    System.out.println("Không tìm thấy tệp CSS, tiếp tục mà không có CSS.");
                }

                stage.setScene(scene); // Đặt Scene cho Stage

                // Hiển thị cửa sổ mới
                stage.showAndWait(); // Đợi cửa sổ đăng ký đóng

            } catch (IOException e) {
                // Thông báo lỗi khi không tải được tệp FXML
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Lỗi: " + e.getMessage());
                alert.showAndWait(); // Hiển thị thông báo lỗi
                e.printStackTrace();
            }
        }
    private LoginResult validateLogin(String username, String password) {
        String sql = "SELECT id, is_admin FROM users WHERE user_name = ? AND password = ?";
        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new LoginResult(rs.getString("id"), rs.getInt("is_admin") == 1);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
        }
        return null;
    }
    private static class LoginResult {
        final String userId;
        final boolean isAdmin;

        LoginResult(String userId, boolean isAdmin) {
            this.userId = userId;
            this.isAdmin = isAdmin;
        }
    }

}


