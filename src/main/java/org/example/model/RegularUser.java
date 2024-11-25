package org.example.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.menu.UserMenuController;

import java.io.IOException;

public class RegularUser extends User {
    public RegularUser(String username, String password) {
        super(username, password,"user");
    }
    public RegularUser(String id, String name, String email, int age) { // Thêm age vào constructor
        super(id,name,email,age);
    }
  //  @Override
    public void LoginLoad(String userId, Stage stage)  {
        // Tải giao diện dành cho người dùng bình thường
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/user-view.fxml"));
           Parent root = loader.load();

           // Thiết lập controller cho Regular User
           UserMenuController controller = loader.getController();
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
