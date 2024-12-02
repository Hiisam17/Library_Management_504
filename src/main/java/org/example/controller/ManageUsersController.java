package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.RegularUser;
import org.example.model.User;
import org.example.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.util.DialogUtils.showAlert;
import static org.example.util.DialogUtils.showConfirmation;

/**
 * The {@code ManageUsersController} class handles user management functionalities,
 * including displaying a list of regular users, deleting users, and closing the management view.
 */
public class ManageUsersController {

  @FXML
  private TableView<User> userTableView;

  @FXML
  private TableColumn<User, String> idColumn;

  @FXML
  private TableColumn<User, String> nameColumn;

  @FXML
  private TableColumn<User, Integer> ageColumn;

  @FXML
  private TableColumn<User, String> emailColumn;

  @FXML
  private TableColumn<User, String> usernameColumn;

  @FXML
  private Button closeButton;

  private final DatabaseManager dbManager = DatabaseManager.getInstance();
  private ObservableList<User> userList = FXCollections.observableArrayList();
  private Stage stage;

  /**
   * Sets the {@link Stage} for this controller.
   *
   * @param stage the primary stage
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Initializes the user management table by setting up columns
   * and loading the list of regular users.
   */
  public void initialize() {
    setupTableColumns();
    userList.addAll(loadRegularUsers());
    userTableView.setItems(userList);
  }

  /**
   * Configures the columns of the user table.
   */
  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
  }

  /**
   * Loads regular users from the database who are not administrators.
   *
   * @return a list of regular users
   */
  public List<RegularUser> loadRegularUsers() {
    List<RegularUser> users = new ArrayList<>();
    String query = "SELECT id, user_name, name, email, age FROM users WHERE is_admin = 0";

    try (Connection conn = dbManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        int age = rs.getInt("age");
        String username = rs.getString("user_name");

        RegularUser user = new RegularUser(id, username, name, email, age);
        users.add(user);
      }
    } catch (Exception e) {
      System.err.println("Error loading users from database: " + e.getMessage());
      e.printStackTrace();
    }
    return users;
  }

  /**
   * Closes the current stage.
   */
  @FXML
  private void handleClose() {
    if (stage != null) {
      stage.close();
    }
  }

  /**
   * Handles the deletion of a selected user from the user table.
   * Prompts a confirmation dialog before deletion.
   */
  @FXML
  private void handleDeleteUser() {
    User selectedUser = userTableView.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
      if (showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa người dùng này?")) {
        System.out.println("Attempting to delete user: " + selectedUser);
        boolean success = deleteUserFromDatabase(selectedUser.getId());

        if (success) {
          System.out.println("User deleted successfully: " + selectedUser);
          userList.remove(selectedUser); // Update table
        } else {
          System.err.println("Failed to delete user: " + selectedUser);
        }
      } else {
        System.out.println("User deletion canceled by user.");
      }
    } else {
      System.out.println("No user selected for deletion.");
    }
  }

  /**
   * Deletes a user from the database based on their ID.
   *
   * @param userId the ID of the user to delete
   * @return {@code true} if the user was successfully deleted; {@code false} otherwise
   */
  private boolean deleteUserFromDatabase(String userId) {
    String query = "DELETE FROM users WHERE id = ?";
    try (Connection conn = dbManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, userId);
      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        showAlert("Thành công","Xóa người dùng thành công.");
        return true;
      } else {
        showAlert("Thất bại","Không tồn tại Id của người dùng.");
        return false;
      }
    } catch (SQLException e) {
      System.err.println("Database error while deleting user: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}
