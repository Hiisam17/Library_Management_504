package org.example.service;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.model.RegularUser;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.repository.DatabaseManager.getInstance;

public class UserService {
  @FXML
  private TextField userNameField;
  @FXML
  private TextField userEmailField;
  @FXML
  private TextField userAgeField; // Thêm trường age

  private String userId;

  public boolean addUser(User user) {
    String sql = "INSERT INTO users (user_name, password, role) VALUES (?, ?, ?)";

    try (Connection conn = getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getPassword());
      pstmt.setString(3, user.getRole());

      int rowsAffected = pstmt.executeUpdate();
      return rowsAffected > 0;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return false;
  }

  public static User getUserById(String userId) {
    String sql = "SELECT * FROM users WHERE id = ?";
    try (Connection conn = getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return new RegularUser(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age") // Lấy thông tin từ cột age
        );
      }
    } catch (SQLException e) {
      System.out.println("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
    }
    return null;
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

  public static boolean updateUser(String userId, String newName, String newEmail, int newAge) {
    String sql = "UPDATE users SET name = ?, email = ?, age = ? WHERE id = ?";
    try (Connection conn = getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, newName);
      pstmt.setString(2, newEmail);
      pstmt.setInt(3, newAge);
      pstmt.setString(4, userId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.out.println("Lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
      return false;
    }
  }


}