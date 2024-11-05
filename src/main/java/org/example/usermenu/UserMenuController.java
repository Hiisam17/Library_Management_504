package org.example.usermenu;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UserMenuController {

  @FXML
  private TextField searchField;

  @FXML
  private TableView<?> documentTableView;

  @FXML
  private void handleBorrowDocument() {
    // Xử lý sự kiện mượn tài liệu
  }

  @FXML
  private void handleReturnDocument() {
    // Xử lý sự kiện trả tài liệu
  }

  @FXML
  private void handleSearch() {
    // Xử lý sự kiện tìm kiếm tài liệu
  }
}