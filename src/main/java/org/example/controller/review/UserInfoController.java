package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;

import static org.example.util.DialogUtils.showAlert;

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

      if (!isValidName(newUsername)) {
        showAlert("Lỗi","Tên không đuọc chứa ký hiệu đặc biệt.");
        return;
      }

      // Kiểm tra định dạng email
      if (!isValidEmail(newEmail)) {
        showAlert("Lỗi","Email không hợp lệ.");
        return;
      }
      if (!isValidAge(newAge)){
        showAlert("Thất bại","Cập nhật thông tin người dùng thất bại.");
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
  // Hàm kiểm tra tên không chứa ký tự đặc biệt
  public boolean isValidName(String name) {
    String nameRegex = "^[a-zA-ZÀ-ỹ\\s]+$";
    return name != null && name.matches(nameRegex);
  }


  // Hàm kiểm tra định dạng email
  public boolean isValidEmail(String email) {
    String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,}$"; // Biểu thức regex kiểm tra email
    return email != null && email.matches(emailRegex);
  }
  public boolean isValidAge(int age){
    return age > 0 && age <= 200;
  }
}