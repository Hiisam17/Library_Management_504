package org.example.usermenu;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.menubar.User;
import org.example.menubar.UserService;

public class EditUserInfoController {
  @FXML
  private TextField userNameField;
  @FXML
  private TextField userEmailField;
  @FXML
  private TextField userAgeField;

  private String userId;

  public void setUserId(String userId) {
    this.userId = userId;
    loadUserInfo();
  }

  private void loadUserInfo() {
    User user = UserService.getUserById(userId);
    if (user != null) {
      userNameField.setText(user.getName());
      userEmailField.setText(user.getEmail());
      userAgeField.setText(String.valueOf(user.getAge())); // Hiển thị thông tin age
    } else {
      System.out.println("Không tìm thấy thông tin người dùng.");
    }
  }

  @FXML
  private void handleEditUserInfo() {
    String newUsername = userNameField.getText();
    String newEmail = userEmailField.getText();
    int newAge = Integer.parseInt(userAgeField.getText());
    boolean success = UserService.updateUser(userId, newUsername, newEmail, newAge);
    if (success) {
      System.out.println("Cập nhật thông tin người dùng thành công.");
    } else {
      System.out.println("Cập nhật thông tin người dùng thất bại.");
    }
  }

  @FXML
  private void handleSave() {
    String newUsername = userNameField.getText();
    String newEmail = userEmailField.getText();
    int newAge = Integer.parseInt(userAgeField.getText());
    boolean success = UserService.updateUser(userId, newUsername, newEmail, newAge);
    if (success) {
      System.out.println("Cập nhật thông tin người dùng thành công.");
    } else {
      System.out.println("Cập nhật thông tin người dùng thất bại.");
    }
    handleClose();
  }

  @FXML
  private void handleCancel() {
    handleClose();
  }

  private void handleClose() {
    Stage stage = (Stage) userNameField.getScene().getWindow();
    stage.close();
  }
}