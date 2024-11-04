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

    @FXML
    private void handleEditDocument() {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showAlert("Thông báo", "Vui lòng chọn tài liệu để sửa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditDoc-view.fxml"));
            Parent root = loader.load();

            EditDocumentController controller = loader.getController();
            controller.setStage(new Stage());
            controller.setDocumentManager(documentManager);
            controller.setDocument(selectedDocument);
            controller.setOnDocumentEdited(this::refreshTable);

            Stage stage = new Stage();
            stage.setTitle("Sửa Tài Liệu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Xử lý sự kiện nút Tìm Kiếm Tài Liệu
    @FXML
    private void handleSearchDocument() {
        String keyword = searchField.getText().trim();

        // Kiểm tra từ khóa tìm kiếm không rỗng
        if (keyword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        // Lấy kết quả tìm kiếm từ DocumentManager
        List<Document> searchResults = documentManager.searchDocuments(keyword);

        // Xóa dữ liệu cũ và thêm dữ liệu mới vào TableView
        documentTableView.getItems().clear();
        documentTableView.getItems().addAll(searchResults);
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
        // Xóa sạch dữ liệu cũ trong bảng
        documentTableView.getItems().clear();

        // Lấy danh sách tài liệu từ cơ sở dữ liệu thông qua documentManager
        List<Document> documents = documentManager.getAllDocuments();

        // Kiểm tra nếu không có tài liệu nào hoặc lỗi kết nối cơ sở dữ liệu
        if (documents == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu tài liệu.");
            showAlert("Lỗi kết nối", "Không thể kết nối tới cơ sở dữ liệu. Vui lòng kiểm tra lại kết nối.");
            return;
        }

        // In ra số lượng tài liệu để kiểm tra
        System.out.println("Số lượng tài liệu: " + documents.size());

        if (documents.isEmpty()) {
            System.out.println("Danh sách tài liệu hiện tại trống.");
        }

        // Thêm tất cả tài liệu vào TableView
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

