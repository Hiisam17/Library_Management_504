package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.repository.DatabaseManager;
import org.example.model.Document;
import org.example.service.DocumentManager;

import static org.example.util.DialogUtils.showAlert;

/**
 * The {@code RateBookController} class handles the functionality for rating a book.
 * It allows users to submit a rating and comment for a specific document.
 */
public class RateBookController {

  @FXML
  private TextField ratingField;

  @FXML
  private TextArea commentField;

  private Document document;
  private String userId;
  private final DatabaseManager dbManager = DatabaseManager.getInstance();

  /**
   * Sets the {@link Document} for which the rating is being submitted.
   *
   * @param document the document to be rated
   */
  public void setDocument(Document document) {
    this.document = document;
  }

  /**
   * Sets the user ID of the user submitting the rating.
   *
   * @param userId the ID of the user
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Handles the submission of a book rating.
   * Validates the input, saves the review to the database, and closes the window on success.
   */
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

    // Save the review to the database
    DocumentManager documentManager = new DocumentManager(dbManager);
    documentManager.addReview(document.getId(), userId, rating, comment);
    showAlert("Thành công", "Cảm ơn vì đánh giá của bạn.");

    // Close the rating window
    Stage stage = (Stage) ratingField.getScene().getWindow();
    stage.close();
  }
}
