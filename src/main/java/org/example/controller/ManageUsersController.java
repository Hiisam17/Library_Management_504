package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.model.RegularUser;
import org.example.model.User;
import org.example.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersController {

  @FXML
  private TableColumn<User, String> usernameColumn; // Khai báo cột mới

  @FXML
  private TableView<User> userTableView;
  @FXML
  private TableColumn<User, String> nameColumn;
  @FXML
  private TableColumn<User, Integer> ageColumn;
  @FXML
  private TableColumn<User, String> emailColumn;
  @FXML
  private TableColumn<User, String> idColumn;
  @FXML
  private Button closeButton;
  DatabaseManager dbManager = DatabaseManager.getInstance();
  private ObservableList<User> userList = FXCollections.observableArrayList();

  public void initialize() {
    setupTableColumns();
    userList.addAll(loadRegularUsers());
    userTableView.setItems(userList);
  }

  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
  }

  public List<RegularUser> loadRegularUsers() {
    List<RegularUser> users = new ArrayList<>();
    String query = "SELECT id, user_name, name, email, age FROM users WHERE is_admin = 0";


    try (Connection conn = DatabaseManager.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        int age = rs.getInt("age");
        String username = rs.getString("user_name"); // Lấy tên đăng nhập từ cơ sở dữ liệu

        RegularUser user = new RegularUser(id, username, name, email, age);
        users.add(user);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return users;
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleDeleteUser() {
    User selectedUser = userTableView.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
      // Hiển thị xác nhận trước khi xóa
      boolean confirmed = confirmDelete();
      if (confirmed) {
        System.out.println("Attempting to delete user: " + selectedUser);
        boolean success = deleteUserFromDatabase(selectedUser.getId());

        if (success) {
          System.out.println("User deleted successfully: " + selectedUser);
          userList.remove(selectedUser); // Cập nhật bảng
        } else {
          System.out.println("Failed to delete user: " + selectedUser);
        }
      } else {
        System.out.println("User deletion canceled by user.");
      }
    } else {
      System.out.println("No user selected for deletion.");
    }
  }


  private boolean deleteUserFromDatabase(String userId) {
    String query = "DELETE FROM users WHERE id = ?";
    try (Connection conn = DatabaseManager.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, userId);
      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("Database: Successfully deleted user with ID " + userId);
        return true;
      } else {
        System.out.println("Database: No user found with ID " + userId);
        return false;
      }
    } catch (SQLException e) {
      System.err.println("Database error while deleting user: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  private boolean confirmDelete() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Xác nhận xóa");
    alert.setHeaderText(null);
    alert.setContentText("Bạn có chắc chắn muốn xóa người dùng này?");

    return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
  }

}
