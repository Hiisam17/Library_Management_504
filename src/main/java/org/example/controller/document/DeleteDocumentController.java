package org.example.controller.document;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.util.DialogUtils;
import org.example.controller.menu.MainMenuController;
import javafx.concurrent.Task;

public class DeleteDocumentController {

    @FXML
    private TextField deleteIdField;

    private Stage stage;
    private MainMenuController mainMenuController;
    //private final DialogUtils dialogUtils = new DialogUtils();

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Xử lý sự kiện khi người dùng nhấn "Xác nhận Xóa"
    @FXML
    private void handleConfirmDelete() {
        String id = deleteIdField.getText();

        if (id != null && !id.isEmpty()) {
            // Tạo một task để xử lý xóa trong một luồng riêng
            Task<Void> deleteTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Gọi phương thức xóa từ MainMenuController
                        mainMenuController.deleteDocumentById(id);
                        mainMenuController.refreshTable(); // Cập nhật lại danh sách
                    } catch (Exception e) {
                        updateMessage("Lỗi khi xóa tài liệu: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            // Xử lý khi task hoàn tất
            deleteTask.setOnSucceeded(event -> {
                DialogUtils.showAlert("Thông báo", "Tài liệu đã được xóa thành công.");
                stage.close(); // Đóng cửa sổ sau khi xóa xong
            });

            // Xử lý khi task gặp lỗi
            deleteTask.setOnFailed(event -> {
                DialogUtils.showAlert("Lỗi", deleteTask.getMessage());
            });

            // Chạy task trong một thread riêng
            new Thread(deleteTask).start();
        } else {
            DialogUtils.showAlert("Lỗi", "Vui lòng nhập ID của tài liệu cần xóa.");
        }
    }

    // Xử lý sự kiện khi người dùng nhấn "Hủy"
    @FXML
    private void handleCancel() {
        stage.close();
    }
}
