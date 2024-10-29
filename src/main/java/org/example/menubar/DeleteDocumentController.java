package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            mainMenuController.deleteDocumentById(id); // Gọi phương thức xóa từ MainMenuController
            mainMenuController.refreshTable(); // Cập nhật lại danh sách
            stage.close(); // Đóng cửa sổ sau khi xóa xong
        } else {
            mainMenuController.showAlert("Lỗi", "Vui lòng nhập ID của tài liệu cần xóa.");
        }
    }

    // Xử lý sự kiện khi người dùng nhấn "Hủy"
    @FXML
    private void handleCancel() {
        stage.close();
    }
}


