package org.example.menubar;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminUser extends User {
    public AdminUser(String username, String password) {
        super(username, password,"admin");
    }
    public AdminUser(String id, String name, String email, int age) { // Thêm age vào constructor
        super(id,name,email,age);
    }
  //  @Override
    public void LoginLoad(String userId,Stage stage)  {
        try {
            // Tải giao diện dành cho Admin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();

            // Thiết lập controller cho Admin
            MainMenuController controller = loader.getController();
            controller.setCurrentUserId(userId);

            // Hiển thị giao diện
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
        } catch (IOException e) {
            System.out.println("Lỗi khi tải giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
