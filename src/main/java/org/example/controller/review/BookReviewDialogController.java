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

public class BookReviewDialogController {
    @FXML
    private VBox reviewContainer;

    private DocumentManager documentManager;
    private Document currentDocument;

    public void initialize() {
        documentManager = new DocumentManager(new DatabaseManager());
    }

    // Thiết lập dữ liệu và hiển thị đánh giá
    public void setDocument(Document document) {
        currentDocument = document;

        List<Review> reviews = documentManager.getReviews(document.getId());
        if (reviews.isEmpty()) {
            Label noReviewsLabel = new Label("Chưa có đánh giá nào cho cuốn sách này.");
            noReviewsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            reviewContainer.getChildren().add(noReviewsLabel);
        } else {
            for (Review review : reviews) {
                // Mỗi đánh giá là một VBox
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
                reviewContainer.getChildren().add(reviewItem);
            }
        }
    }

    // Xử lý khi bấm nút "Đóng"
    @FXML
    private void handleClose() {
        Stage stage = (Stage) reviewContainer.getScene().getWindow();
        stage.close();
    }
}
