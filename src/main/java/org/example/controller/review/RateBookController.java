package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.repository.DatabaseManager;
import org.example.model.Document;
import org.example.service.DocumentManager;

import static org.example.util.DialogUtils.showAlert;

public class RateBookController {
  @FXML
  private TextField ratingField;
  @FXML
  private TextArea commentField;

  private Document document;
  private String userId;
  DatabaseManager dbManager = DatabaseManager.getInstance();

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
    DocumentManager documentManager = new DocumentManager(dbManager);
    documentManager.addReview(document.getId(), userId, rating, comment);
    showAlert("Thành công","Cảm ơn vì đánh giá của bạn.");
    // Đóng cửa sổ đánh giá
    Stage stage = (Stage) ratingField.getScene().getWindow();
    stage.close();
  }

}