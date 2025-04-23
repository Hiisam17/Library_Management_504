package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;

import static org.example.util.DialogUtils.showAlert;

/**
 * Controller to manage user information editing.
 * Allows the user to view and update their personal details.
 */
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

  /**
   * Sets the user ID and loads the user information.
   *
   * @param userId the ID of the user whose information is to be loaded
   */
  public void setUserId(String userId) {
    this.userId = userId;
    loadUserInfo();
  }

  /**
   * Loads the user's information from the UserService and displays it in the fields.
   */
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

  /**
   * Enables editing mode, allowing the user to update their information.
   */
  @FXML
  private void handleEditUserInfo() {
    enableEditing(true);
  }

  /**
   * Closes the current window.
   */
  @FXML
  private void handleClose() {
    Stage stage = (Stage) userIdLabel.getScene().getWindow();
    stage.close();
  }

  /**
   * Saves the updated user information after validation.
   * Displays appropriate messages depending on whether the update is successful.
   */
  @FXML
  private void handleSaveUserInfo() {
    try {
      // Lấy thông tin người dùng từ các trường nhập liệu
      String newUsername = userNameField.getText();
      String newEmail = userEmailField.getText();
      String ageText = userAgeField.getText();

      // Kiểm tra nếu có bất kỳ trường nào bị thiếu
      if (newUsername == null || newUsername.trim().isEmpty() ||
              newEmail == null || newEmail.trim().isEmpty() ||
              ageText == null || ageText.trim().isEmpty()) {
        showAlert("Lỗi", "Vui lòng điền đủ các trường.");
        return;
      }

      // Chuyển đổi tuổi thành kiểu int
      int newAge = Integer.parseInt(ageText);

      // Kiểm tra tên người dùng hợp lệ
      if (!isValidName(newUsername)) {
        showAlert("Lỗi", "Tên không được chứa ký hiệu đặc biệt.");
        return;
      }

      // Kiểm tra định dạng email hợp lệ
      if (!isValidEmail(newEmail)) {
        showAlert("Lỗi", "Email không hợp lệ.");
        return;
      }

      // Kiểm tra độ tuổi hợp lệ
      if (!isValidAge(newAge)) {
        showAlert("Thất bại", "Tuổi không phù hợp.");
        return;
      }

      // Lưu thông tin người dùng vào database hoặc xử lý logic ở đây
      boolean success = UserService.updateUser(userId, newUsername, newEmail, newAge);
      if (success) {
        showAlert("Thành công","Cập nhật thông tin người dùng thành công.");
      } else {
        showAlert("Lỗi","Cập nhật thông tin người dùng thất bại.");
      }

      // Tắt chế độ chỉnh sửa
      enableEditing(false);

    } catch (Exception e) {
      e.printStackTrace();
      showAlert("Lỗi", "Đã xảy ra lỗi khi lưu thông tin.");
    }
  }

  /**
   * Toggles the editability of the user information fields and buttons.
   *
   * @param enable true to enable editing, false to disable it
   */
  private void enableEditing(boolean enable) {
    userNameField.setEditable(enable);
    userEmailField.setEditable(enable);
    userAgeField.setEditable(enable);

    editButton.setVisible(!enable);
    saveButton.setVisible(enable);
  }

  /**
   * Validates that the user name does not contain special characters.
   *
   * @param name the user name to be validated
   * @return true if the name is valid, false otherwise
   */
  public boolean isValidName(String name) {
    String nameRegex = "^[a-zA-ZÀ-ỹ\\s]+$";
    return name != null && name.matches(nameRegex);
  }

  /**
   * Validates that the email is in a proper format.
   *
   * @param email the email to be validated
   * @return true if the email is valid, false otherwise
   */
  public boolean isValidEmail(String email) {
    String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,}$"; // Biểu thức regex kiểm tra email
    return email != null && email.matches(emailRegex);
  }

  /**
   * Validates that the age is a positive integer and less than or equal to 200.
   *
   * @param age the age to be validated
   * @return true if the age is valid, false otherwise
   */
  public boolean isValidAge(int age){
    return age > 0 && age <= 200;
  }
}
