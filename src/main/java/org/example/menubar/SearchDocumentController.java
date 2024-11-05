package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class SearchDocumentController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Document> documentTableView;

    @FXML
    private TableColumn<Document, String> idColumn;         // Cột cho ID
    @FXML
    private TableColumn<Document, String> titleColumn;      // Cột cho Tiêu đề
    @FXML
    private TableColumn<Document, String> authorColumn;     // Cột cho Tác giả
    @FXML
    private TableColumn<Document, String> publisherColumn;   // Cột cho Nhà xuất bản
    @FXML
    private TableColumn<Document, String> publishedDateColumn; // Cột cho Ngày xuất bản

    private final DocumentManager documentManager = new DocumentManager(new DatabaseManager());

    @FXML
    public void initialize() {
        // Khởi tạo các cột trong TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        // Kiểm tra từ khóa tìm kiếm không rỗng
        if (keyword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        // Lấy kết quả tìm kiếm từ DocumentManager
        ObservableList<Document> searchResults = FXCollections.observableArrayList(documentManager.searchDocuments(keyword));
        documentTableView.setItems(searchResults);
    }

    private void showAlert(String title, String message) {
        // Hiện thông báo cảnh báo cho người dùng
        // Bạn có thể sử dụng Alert từ JavaFX để làm điều này
        // Thêm mã để hiện thông báo
    }
}


