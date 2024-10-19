package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    private ObservableList<Document> documents = FXCollections.observableArrayList(); // Danh sách tài liệu

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField idField;

    // Xử lý sự kiện nút Thêm Tài Liệu
    @FXML
    private void handleAddDocument() {
        try {
            // Tải FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddDoc-view.fxml"));
            Parent root = loader.load();

            // Tạo và thiết lập Stage mới
            Stage stage = new Stage();
            stage.setTitle("Thêm Tài Liệu");
            stage.initModality(Modality.APPLICATION_MODAL); // Đảm bảo cửa sổ mới là modal
            stage.setScene(new Scene(root));

            // Thiết lập tham chiếu cho AddDocumentController
            AddDocumentController controller = loader.getController();
            controller.setStage(stage);

            // Hiển thị cửa sổ
            stage.showAndWait(); // Đợi cho đến khi cửa sổ được đóng
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi nếu cần
        }
    }

    @FXML
    private void handleSaveDocument() {
        String title = titleField.getText();
        String author = authorField.getText();
        String year = yearField.getText();
        String id = idField.getId();

        if (title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
        } else {
            // Thêm logic lưu tài liệu mới
            Document newDocument = new Document(id, title, author, year);
            documents.add(newDocument); // library là danh sách các tài liệu
            showAlert("Thành công", "Đã thêm tài liệu mới!");
        }
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

    @FXML
    private TextField searchField;

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        String jsonResponse = APIIntegration.getBookInfoByTitle(query);
        APIIntegration.parseBookInfo(jsonResponse);
    }

    // Hàm hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

