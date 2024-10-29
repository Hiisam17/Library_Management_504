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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.example.menubar.DatabaseManager.SQL_connect;


public class MainMenuController {

    @FXML
    private TextField idField;
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
    private TableColumn<Document, String> publisherColumn;
    @FXML
    private TableColumn<Document, String> publishedDateColumn;

    private ObservableList<Document> documentList = FXCollections.observableArrayList();
    private final DocumentManager documentManager;

    public MainMenuController() {
        this.documentManager = new DocumentManager(new DatabaseManager());
    }


    // Xử lý sự kiện nút Thêm Tài Liệu
    @FXML
    private void handleAddDocument() {
        try {
            // Tải FXML cho cửa sổ thêm tài liệu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddDoc-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Tài Liệu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Lấy controller của AddDocumentController và truyền các tham chiếu cần thiết
            AddDocumentController controller = loader.getController();
            AddDocumentController.setMainMenuController(this); // "this" là MainMenuController
            controller.setStage(stage);

            // Thiết lập callback để cập nhật TableView sau khi thêm tài liệu
            // Gọi phương thức cập nhật TableView
            controller.setOnDocumentAddedCallback(this::refreshTable);

            stage.showAndWait(); // Đợi cho đến khi cửa sổ được đóng
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Khởi tạo các cột của TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        // Gán ObservableList vào TableView
        documentTableView.setItems(documentList);

        refreshTable(); // Cập nhật TableView
    }

    // Xử lý sự kiện nút Xóa Tài Liệu
    @FXML
    private void handleDeleteDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Delete_Doc.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Xóa Tài Liệu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Lấy controller của DeleteDocumentController và truyền các tham chiếu cần thiết
            DeleteDocumentController controller = loader.getController();
            controller.setMainMenuController(this); // truyền MainMenuController hiện tại
            controller.setStage(stage);

            stage.showAndWait(); // Đợi cho đến khi cửa sổ được đóng
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void refreshTable() {
        documentTableView.getItems().clear();

        List<Document> documents = documentManager.getAllDocuments();

        // In ra số lượng tài liệu để kiểm tra
        System.out.println("Số lượng tài liệu: " + documents.size());

        documentTableView.getItems().addAll(documents);
    }

    public ObservableList<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(ObservableList<Document> documentList) {
        this.documentList = documentList;
    }

    public static class UserDAO {
        public boolean register(String username, String password) {
            String sql = "INSERT INTO users (user_name, password, is_admin) VALUES (?, ?, false)";

            try (Connection conn = SQL_connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);

                int rowsAffected = pstmt.executeUpdate();// true nếu đăng ký thành công

                return rowsAffected > 0;

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return false;
        }

    }

    public void deleteDocumentById(String id) {
        documentManager.deleteDocumentById(id); // Gọi phương thức trong DatabaseManager
        refreshTable(); // Cập nhật lại TableView sau khi xóa
    }
}

