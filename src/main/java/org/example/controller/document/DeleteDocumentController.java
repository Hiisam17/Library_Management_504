package org.example.controller.document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.controller.menu.MainMenuController;
import javafx.concurrent.Task;
import static org.example.util.DialogUtils.showAlert;

public class DeleteDocumentController {

    @FXML
    private TextField deleteIdField;

    private Stage stage;
    private MainMenuController mainMenuController;

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
                        // Gọi phương thức xóa từ MainMenuController và kiểm tra kết quả
                        boolean isDeleted = mainMenuController.deleteDocumentById(id);
                        if (isDeleted) {

                            // Cập nhật lại danh sách sau khi xóa thành công
                            Platform.runLater(() -> {
                                mainMenuController.refreshTable(); // Đảm bảo gọi trên UI thread
                                showAlert("Thông báo", "Tài liệu đã được xóa thành công.");
                            });
                        } else {
                            // Hiển thị thông báo lỗi nếu ID không tồn tại
                            Platform.runLater(() -> {
                                showAlert("Lỗi", "Id không tồn tại");
                            });
                        }
                    } catch (Exception e) {
                        // Xử lý ngoại lệ nếu có
                        updateMessage("Lỗi khi xóa tài liệu: " + e.getMessage());
                        e.printStackTrace();
                        // Hiển thị lỗi trong trường hợp có ngoại lệ
                        Platform.runLater(() -> showAlert("Lỗi", "Có lỗi khi thực hiện xóa tài liệu."));
                    }
                    return null;
                }
            };

            // Chạy task trong một thread riêng
            new Thread(deleteTask).start();
        } else {
            // Thông báo khi ID trống
            showAlert("Lỗi", "Vui lòng nhập ID tài liệu cần xóa.");
        }
    }

    // Xử lý sự kiện khi người dùng nhấn "Hủy"
    @FXML
    private void handleCancel() {
        stage.close();
    }
}
