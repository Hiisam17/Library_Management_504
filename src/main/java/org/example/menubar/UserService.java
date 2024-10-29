package org.example.menubar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.example.menubar.DatabaseManager.SQL_connect;

public class UserService {

  public boolean addUser(User user) {
    String sql = "INSERT INTO users (user_name, password, role) VALUES (?, ?, ?)";

    try (Connection conn = SQL_connect();
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
}