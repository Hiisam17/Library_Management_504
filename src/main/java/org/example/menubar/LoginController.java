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
            String userId = validateLogin(username, password);
            if (userId != null) {
                System.out.println("Login successful! User ID: " + userId);
                try {
                    FXMLLoader loader;
                    if (isAdmin(userId)) {
                        loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
                    } else {
                        loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
                    }
                    Parent mainMenuRoot = loader.load();

                    // Lấy controller và thiết lập Stage
                    if (isAdmin(userId)) {
                        MainMenuController controller = loader.getController();
                        controller.setStage((Stage) usernameField.getScene().getWindow());
                    } else {
                        UserMenuController controller = loader.getController();
                        controller.setCurrentUserId(userId);
                    }

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(mainMenuRoot, 1200, 800));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Đăng nhập thất bại!", "Tên đăng nhập hoặc mật khẩu không đúng.");
            }
        } else {
            showAlert("Lỗi hệ thống", "Ứng dụng chưa được khởi tạo đúng cách.");
        }
    }



    @FXML
    private void handleExit() {
        System.exit(0);
    }

    //connect to SQL



    private String validateLogin(String username, String password) {
        String sql = "SELECT id, is_admin FROM users WHERE user_name = ? AND password = ?";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("id");
                return userId;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
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
           // alert.setTitle("Lỗi");
          //  alert.setHeaderText("Không thể mở cửa sổ đăng ký");
            alert.setContentText("Lỗi: " + e.getMessage());
            alert.showAndWait(); // Hiển thị thông báo lỗi
            e.printStackTrace();
        }
    }


    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isAdmin(String userId) {
        String sql = "SELECT is_admin FROM users WHERE id = ?";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("is_admin") == 1;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}


