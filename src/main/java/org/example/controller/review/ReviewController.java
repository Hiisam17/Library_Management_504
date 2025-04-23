package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.repository.DatabaseManager;
import org.example.service.DocumentManager;
import org.example.model.Review;

import java.util.List;

/**
 * The {@code ReviewController} class handles adding and displaying reviews for a specific document.
 * Users can submit a new review, and existing reviews are displayed in a list view.
 */
public class ReviewController {

  @FXML
  private TextField ratingField;

  @FXML
  private TextArea commentField;

  @FXML
  private ListView<String> reviewListView;

  private String documentId;
  private final DatabaseManager dbManager = DatabaseManager.getInstance();

  /**
   * Sets the ID of the document whose reviews are being managed.
   * Loads existing reviews for the document.
   *
   * @param documentId the ID of the document
   */
  public void setDocumentId(String documentId) {
    this.documentId = documentId;
    loadReviews();
  }

  /**
   * Handles adding a new review for the current document.
   * The review includes a rating and a comment.
   */
  @FXML
  private void handleAddReview() {
    try {
      int rating = Integer.parseInt(ratingField.getText().trim());
      if (rating < 1 || rating > 5) {
        throw new IllegalArgumentException("Rating must be between 1 and 5.");
      }

      String comment = commentField.getText().trim();
      if (comment.isEmpty()) {
        throw new IllegalArgumentException("Comment cannot be empty.");
      }

      DocumentManager documentManager = new DocumentManager(dbManager);
      documentManager.addReview(documentId, "currentUserId", rating, comment);

      // Refresh the reviews list after adding a new review
      loadReviews();
    } catch (NumberFormatException e) {
      System.err.println("Invalid rating value. Please enter a number.");
      // Optionally, display an alert to the user
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      // Optionally, display an alert to the user
    }
  }

  /**
   * Loads all reviews for the current document and displays them in the list view.
   */
  private void loadReviews() {
    DocumentManager documentManager = new DocumentManager(dbManager);
    List<Review> reviews = documentManager.getReviews(documentId);
    reviewListView.getItems().clear();
    for (Review review : reviews) {
      reviewListView.getItems().add(
              review.getUserName() + ": " + review.getRating() + " - " + review.getComment()
      );
    }
  }
}
