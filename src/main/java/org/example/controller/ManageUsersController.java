package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class ManageUsersController {
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
  }

  public List<RegularUser> loadRegularUsers() {
    List<RegularUser> users = new ArrayList<>();
    String query = "SELECT id, name, email, age FROM users WHERE is_admin = 0";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        int age = rs.getInt("age");

        RegularUser user = new RegularUser(id, name, email, age);
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
}
