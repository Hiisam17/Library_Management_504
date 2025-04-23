package org.example.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.menu.UserMenuController;

import java.io.IOException;

/**
 * Represents a regular user with standard privileges.
 * Extends the {@link User} class to provide functionality specific to regular users.
 */
public class RegularUser extends User {

    /**
     * Constructs a RegularUser with a username and password.
     *
     * @param username the username of the regular user.
     * @param password the password of the regular user.
     */
    public RegularUser(String username, String password) {
        super(username, password, "user");
    }

    /**
     * Constructs a RegularUser with additional details.
     *
     * @param id    the unique identifier of the regular user.
     * @param name  the name of the regular user.
     * @param email the email of the regular user.
     * @param age   the age of the regular user.
     */
    public RegularUser(String id, String name, String email, int age) {
        super(id, name, email, age);
    }

    /**
     * Constructs a RegularUser with all attributes including username.
     *
     * @param id       the unique identifier of the regular user.
     * @param username the username of the regular user.
     * @param name     the name of the regular user.
     * @param email    the email of the regular user.
     * @param age      the age of the regular user.
     */
    public RegularUser(String id, String username, String name, String email, int age) {
        super(id, name, email, age);
        this.setUsername(username);
    }

    /**
     * Loads the user interface for a regular user upon login.
     *
     * @param userId the unique identifier of the currently logged-in user.
     * @param stage  the primary stage used to display the user interface.
     */
    @Override
    public void LoginLoad(String userId, Stage stage) {
        try {
            // Load the user-specific menu view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/user-view.fxml"));
            Parent root = loader.load();

            // Set up the controller for the user menu
            UserMenuController controller = loader.getController();
            controller.setCurrentUserId(userId);

            // Display the user interface
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
        } catch (IOException e) {
            System.out.println("Lỗi khi tải giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
