package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddDocumentController {

    // Khai báo danh sách tài liệu
    private ObservableList<Document> documents = FXCollections.observableArrayList();

    // Tham chiếu đến Stage
    private Stage stage;

    @FXML
    private TextField idField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publishedDateField;

    // Xử lý sự kiện nút Lưu
    @FXML
    private void handleSaveDocument() {
        String title = titleField.getText();
        String author = authorField.getText();
        String publishedDate = publishedDateField.getText();
        String id = idField.getId();

        if (title.isEmpty() || author.isEmpty() || publishedDate.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
        } else {
            try {
                Document newDocument = new Document(id, title, author, publishedDate);
                documents.add(newDocument); // Thêm tài liệu vào danh sách
                showAlert("Thành công", "Đã thêm tài liệu mới!");
                clearFields(); // Xóa dữ liệu sau khi lưu
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Năm xuất bản phải là số.");
            }
        }
    }

    // Hàm khởi tạo để nhận tham chiếu Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Hàm xóa dữ liệu sau khi lưu
    private void clearFields() {
        idField.clear();
        titleField.clear();
        authorField.clear();
        publishedDateField.clear();
    }

    // Hàm hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Xử lý sự kiện nút Quay lại
    @FXML
    private void handleBack() {
        // Đóng cửa sổ thêm tài liệu
        if (stage != null) {
            stage.close(); // Đóng cửa sổ hiện tại
        }
    }
}
