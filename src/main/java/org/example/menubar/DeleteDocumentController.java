package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DeleteDocumentController {

    @FXML
    private TextField idField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Hàm xử lý khi nhấn nút "Xóa"
    @FXML
    private void handleDeleteDocument() {
        String id = idField.getText();
        if (id == null || id.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập ID tài liệu");
            return;
        }

        boolean isDeleted = Library.deleteDocumentById(id); // Giả sử bạn có hàm này trong lớp Library

        if (isDeleted) {
            showAlert("Thành công", "Tài liệu với ID " + id + " đã được xóa.");
            idField.clear(); // Xóa nội dung ô nhập sau khi xóa thành công
        } else {
            showAlert("Thất bại", "Không tìm thấy tài liệu với ID " + id);
        }
    }

    // Hàm xử lý khi nhấn nút "Quay lại"
    @FXML
    private void handleBack() {
        if (stage != null) {
            stage.close();
        }
    }

    // Hàm hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


