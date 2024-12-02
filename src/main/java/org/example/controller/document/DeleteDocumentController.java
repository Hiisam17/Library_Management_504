package org.example.controller.document;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.controller.menu.MainMenuController;

import static org.example.util.DialogUtils.showAlert;

/**
 * Controller for deleting a document.
 * Handles user interactions for confirming or canceling the deletion process.
 */
public class DeleteDocumentController {

    @FXML private TextField deleteIdField;

    private Stage stage;
    private MainMenuController mainMenuController;

    /**
     * Sets the MainMenuController for accessing menu-related functionality.
     *
     * @param mainMenuController the instance of MainMenuController.
     */
    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the event when the user confirms the deletion of a document.
     * Validates the input, performs the deletion in a background thread, and updates the UI upon completion.
     */
    @FXML
    private void handleConfirmDelete() {
        String id = deleteIdField.getText();

        if (id != null && !id.isEmpty()) {
            // Create a task to handle deletion in a separate thread
            Task<Void> deleteTask =
                    new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                // Call delete method from MainMenuController and check the result
                                boolean isDeleted = mainMenuController.deleteDocumentById(id);
                                if (isDeleted) {
                                    // Update the UI after successful deletion
                                    Platform.runLater(
                                            () -> {
                                                mainMenuController.refreshTable(); // Ensure this is called on the UI thread
                                                showAlert("Thông báo", "Tài liệu đã được xóa thành công.");
                                            });
                                } else {
                                    // Show an error message if the ID does not exist
                                    Platform.runLater(() -> showAlert("Lỗi", "Id không tồn tại"));
                                }
                            } catch (Exception e) {
                                // Handle exceptions
                                updateMessage("Lỗi khi xóa tài liệu: " + e.getMessage());
                                e.printStackTrace();
                                Platform.runLater(
                                        () -> showAlert("Lỗi", "Có lỗi khi thực hiện xóa tài liệu."));
                            }
                            return null;
                        }
                    };

            // Run the task in a separate thread
            new Thread(deleteTask).start();
        } else {
            // Show an alert if the ID field is empty
            showAlert("Lỗi", "Vui lòng nhập ID tài liệu cần xóa.");
        }
    }

    /**
     * Handles the event when the user cancels the deletion process.
     * Closes the current stage.
     */
    @FXML
    private void handleCancel() {
        stage.close();
    }
}
