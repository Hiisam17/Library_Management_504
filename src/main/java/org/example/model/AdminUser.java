package org.example.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.menu.MainMenuController;

import java.io.IOException;

/**
 * Represents an administrative user with special privileges.
 * Extends the functionality of the {@link User} class to provide admin-specific features.
 */
public class AdminUser extends User {

    /**
     * Constructs an AdminUser with a username and password.
     *
     * @param username the username of the admin user.
     * @param password the password of the admin user.
     */
    public AdminUser(String username, String password) {
        super(username, password, "admin");
    }

    /**
     * Loads the admin-specific user interface upon login.
     *
     * @param userId the unique identifier of the currently logged-in user.
     * @param stage  the primary stage used to display the admin interface.
     */
    @Override
    public void LoginLoad(String userId, Stage stage) {
        try {
            // Load the admin-specific menu view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu/menu-view.fxml"));
            Parent root = loader.load();

            // Set up the controller for the admin menu
            MainMenuController controller = loader.getController();
            controller.setCurrentUserId(userId);

            // Display the admin interface
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
        } catch (IOException e) {
            System.out.println("Lỗi khi tải giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
