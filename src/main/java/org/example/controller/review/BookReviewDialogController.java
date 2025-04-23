package org.example.controller.review;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Document;
import org.example.model.Review;
import org.example.service.DocumentManager;
import org.example.repository.DatabaseManager;

import java.util.List;

/**
 * The {@code BookReviewDialogController} class is responsible for displaying reviews of a specific book.
 * It retrieves and formats reviews for the currently selected book and provides an option to close the dialog.
 */
public class BookReviewDialogController {

    @FXML
    private VBox reviewContainer;

    private DocumentManager documentManager;
    private Document currentDocument;
    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    /**
     * Initializes the controller by setting up the {@link DocumentManager}.
     */
    public void initialize() {
        documentManager = new DocumentManager(dbManager);
    }

    /**
     * Sets the current {@link Document} and populates the review container with its reviews.
     * If there are no reviews, a message indicating so is displayed.
     *
     * @param document the document whose reviews are to be displayed
     */
    public void setDocument(Document document) {
        currentDocument = document;

        List<Review> reviews = documentManager.getReviews(document.getId());
        if (reviews.isEmpty()) {
            Label noReviewsLabel = new Label("Chưa có đánh giá nào cho cuốn sách này.");
            noReviewsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            reviewContainer.getChildren().add(noReviewsLabel);
        } else {
            for (Review review : reviews) {
                VBox reviewItem = createReviewItem(review);
                reviewContainer.getChildren().add(reviewItem);
            }
        }
    }

    /**
     * Creates a styled {@link VBox} representing a single review.
     *
     * @param review the review to be displayed
     * @return a {@link VBox} containing formatted review details
     */
    private VBox createReviewItem(Review review) {
        VBox reviewItem = new VBox(5);
        reviewItem.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 5; -fx-padding: 10; -fx-spacing: 5;");

        Label userNameLabel = new Label("Người dùng: " + review.getUserName());
        userNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        Label ratingLabel = new Label("Xếp hạng: " + review.getRating() + "⭐");
        ratingLabel.setStyle("-fx-text-fill: #666666;");

        Label commentLabel = new Label("Nhận xét: " + review.getComment());
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-text-fill: #555555;");

        Label timestampLabel = new Label("Thời gian: " + review.getTimestamp());
        timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");

        reviewItem.getChildren().addAll(userNameLabel, ratingLabel, commentLabel, timestampLabel);

        return reviewItem;
    }

    /**
     * Handles the "Close" button action, closing the review dialog.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) reviewContainer.getScene().getWindow();
        stage.close();
    }
}
