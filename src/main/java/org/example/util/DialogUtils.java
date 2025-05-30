package org.example.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.review.BookReviewDialogController;
import org.example.controller.review.RateBookController;
import org.example.model.Document;

import java.io.IOException;
import java.util.Optional;

/**
 * Utility class for displaying various types of dialogs, such as alert, confirmation, and custom dialogs.
 */
public class DialogUtils {

    /**
     * Displays an informational alert with the given title and message.
     *
     * @param title   the title of the alert
     * @param message the message content of the alert
     */
    public static void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Displays a confirmation dialog with the given title and message.
     *
     * @param title   the title of the confirmation dialog
     * @param message the message content of the confirmation dialog
     * @return true if the user confirms, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a dialog for rating a book, where the user can rate the given document.
     *
     * @param doc     the document (book) to be rated
     * @param userId  the user ID who is rating the book
     */
    public void showRateBookDialog(Document doc, String userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/rate-book-view.fxml"));
            Parent root = loader.load();

            RateBookController controller = loader.getController();
            controller.setDocument(doc);
            controller.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Đánh giá sách: " + doc.getTitle());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a dialog to display details of a book, including user reviews.
     *
     * @param doc the document (book) to display details for
     */
    public void showBookDetails(Document doc) {
        try {
            // Tải file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/show-rate-view.fxml"));
            Parent root = loader.load();

            // Lấy controller và truyền dữ liệu
            BookReviewDialogController controller = loader.getController();
            controller.setDocument(doc);

            // Tạo và hiển thị cửa sổ
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Đánh giá sách: " + doc.getTitle());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // Đặt kích thước cửa sổ (ví dụ: rộng 600px và cao 400px)
            Scene scene = new Scene(root, 600, 400);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a confirmation dialog for logging out.
     *
     * @param stage the stage of the application that is requesting the logout
     * @return true if the user confirms logout, false otherwise
     */
    public static boolean showLogoutConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText("Chọn OK để đăng xuất hoặc Hủy để quay lại.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
