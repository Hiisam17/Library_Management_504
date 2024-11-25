package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.repository.DatabaseManager;
import org.example.service.DocumentManager;
import org.example.model.Review;

import java.util.List;

public class ReviewController {
  private String documentId;

  @FXML
  private TextField ratingField;
  @FXML
  private TextArea commentField;
  @FXML
  private ListView<String> reviewListView;

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
    loadReviews();
  }

  @FXML
  private void handleAddReview() {
    int rating = Integer.parseInt(ratingField.getText());
    String comment = commentField.getText();

    DocumentManager documentManager = new DocumentManager(new DatabaseManager());
    documentManager.addReview(documentId, "currentUserId", rating, comment);

    loadReviews();
  }

  private void loadReviews() {
    DocumentManager documentManager = new DocumentManager(new DatabaseManager());
    List<Review> reviews = documentManager.getReviews(documentId);
    reviewListView.getItems().clear();
    for (Review review : reviews) {
      reviewListView.getItems().add(review.getUserName() + ": " + review.getRating() + " - " + review.getComment());
    }
  }
}