package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static org.example.menubar.Library.documents;

public class MainMenuController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private DatePicker publishedDatePicker;

    @FXML
    private TableView<Document> documentTableView;
    @FXML
    private TableColumn<Document, String> idColumn;
    @FXML
    private TableColumn<Document, String> titleColumn;
    @FXML
    private TableColumn<Document, String> authorColumn;
    @FXML
    private TableColumn<Document, Integer> yearColumn;

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

