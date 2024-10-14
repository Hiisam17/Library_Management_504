package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MainMenuController {

    // Xử lý sự kiện nút Thêm Tài Liệu
    @FXML
    private void handleAddDocument() {
        showAlert("Thêm Tài Liệu", "Chức năng thêm tài liệu đang trong quá trình phát triển.");
    }

    // Xử lý sự kiện nút Xóa Tài Liệu
    @FXML
    private void handleDeleteDocument() {
        showAlert("Xóa Tài Liệu", "Chức năng xóa tài liệu đang trong quá trình phát triển.");
    }

    // Xử lý sự kiện nút Sửa Tài Liệu
    @FXML
    private void handleEditDocument() {
        showAlert("Sửa Tài Liệu", "Chức năng sửa tài liệu đang trong quá trình phát triển.");
    }

    // Xử lý sự kiện nút Tìm Kiếm Tài Liệu
    @FXML
    private void handleSearchDocument() {
        showAlert("Tìm Kiếm Tài Liệu", "Chức năng tìm kiếm tài liệu đang trong quá trình phát triển.");
    }



    // Hàm hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

