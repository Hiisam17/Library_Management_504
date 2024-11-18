package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RateBookController {
  @FXML
  private TextField ratingField;
  @FXML
  private TextArea commentField;

  private Document document;
  private String userId;

  public void setDocument(Document document) {
    this.document = document;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @FXML
  private void handleSubmitRating() {
    String ratingText = ratingField.getText().trim();
    String comment = commentField.getText().trim();

    if (ratingText.isEmpty() || comment.isEmpty()) {
      showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin đánh giá.");
      return;
    }

    int rating;
    try {
      rating = Integer.parseInt(ratingText);
      if (rating < 1 || rating > 5) {
        showAlert("Lỗi", "Đánh giá phải nằm trong khoảng từ 1 đến 5.");
        return;
      }
    } catch (NumberFormatException e) {
      showAlert("Lỗi", "Đánh giá phải là một số nguyên.");
      return;
    }

    // Lưu đánh giá vào cơ sở dữ liệu
    DocumentManager documentManager = new DocumentManager(new DatabaseManager());
    documentManager.addReview(document.getId(), userId, rating, comment);

    // Đóng cửa sổ đánh giá
    Stage stage = (Stage) ratingField.getScene().getWindow();
    stage.close();
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }
}