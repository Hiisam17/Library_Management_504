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
public class UserDAO {
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (user_name, password, is_admin) VALUES (?, ?, false)";

        try (Connection conn = connect();
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
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:data/liba.db";
            // Kết nối tới cơ sở dữ liệu
            conn = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
// tính năng đăng ký dùng dòng lệnh