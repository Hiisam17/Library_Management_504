package org.example.usermenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.menubar.User;
import org.example.menubar.UserService;

import java.io.IOException;

public class UserInfoController {
  @FXML
  private TextField userIdLabel;
  @FXML
  private TextField userNameField;
  @FXML
  private TextField userEmailField;
  @FXML
  private TextField userAgeField; // Thêm trường age
  @FXML
  private Button editButton;
  @FXML
  private Button saveButton;

  private String userId;

  private Stage stage;

  public void setUserId(String userId) {
    this.userId = userId;
    loadUserInfo();
  }

  private void loadUserInfo() {
    User user = UserService.getUserById(userId);
    if (user != null) {
      userIdLabel.setText(user.getId());
      userNameField.setText(user.getName());
      userEmailField.setText(user.getEmail());
      userAgeField.setText(String.valueOf(user.getAge())); // Hiển thị thông tin age
    } else {
      System.out.println("Không tìm thấy thông tin người dùng.");
    }
  }

  @FXML
  private void handleEditUserInfo() {
    enableEditing(true);
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) userIdLabel.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleSaveUserInfo() {
    try {

      // Thực hiện lưu thông tin vào database hoặc xử lý logic ở đây
      String newUsername = userNameField.getText();
      String newEmail = userEmailField.getText();
      int newAge = Integer.parseInt(userAgeField.getText());
      boolean success = UserService.updateUser(userId, newUsername, newEmail, newAge);
      if (success) {
        System.out.println("Cập nhật thông tin người dùng thành công.");
      } else {
        System.out.println("Cập nhật thông tin người dùng thất bại.");
      }

      // Tắt chế độ chỉnh sửa
      enableEditing(false);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Lỗi khi lưu thông tin.");
    }
  }

  private void enableEditing(boolean enable) {
    userNameField.setEditable(enable);
    userEmailField.setEditable(enable);
    userAgeField.setEditable(enable);

    editButton.setVisible(!enable);
    saveButton.setVisible(enable);
  }

}