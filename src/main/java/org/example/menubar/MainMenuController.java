package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.usermenu.UserMenuController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.example.menubar.DatabaseManager.SQL_connect;


public class MainMenuController {
    private Stage stage;
    private String currentUserId;
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
    @FXML
    private TableColumn<Document, Boolean> isAvailableColumn;

    private ObservableList<Document> documentList = FXCollections.observableArrayList();
    private final DocumentManager documentManager;

    public MainMenuController() {
        this.documentManager = new DocumentManager(new DatabaseManager());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Library Manager");
    }
    // Phương thức getter để lấy stage
    public Stage getStage() {
        return this.stage;
    }

    public void setCurrentUserId(String userId) { this.currentUserId =userId;}
    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        // Tạo một Alert xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText("Chọn OK để đăng xuất hoặc Hủy để quay lại.");

        // Hiển thị Alert và chờ người dùng phản hồi
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (stage != null) {
                stage.close(); // Đóng cửa sổ chính
                stage=null;
            }
            try {
                Main mainInstance = Main.getInstance();
                if (mainInstance != null) {
                    mainInstance.restartApp();
                } else {
                    System.err.println("Lỗi: Main chưa được khởi tạo.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // Nếu người dùng chọn "Hủy" hoặc đóng Alert, sẽ không làm gì cả
    }
    private void restartApp() throws IOException {
        // Tạo lại đối tượng Main và gọi phương thức restart
        Main mainApp = new Main();
        Main.getInstance().restartApp();
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

        // Định dạng cột
        idColumn.setStyle("-fx-alignment: CENTER;"); // Căn giữa
        titleColumn.setStyle("-fx-alignment: CENTER_LEFT;"); // Căn trái
        authorColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        publisherColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        publishedDateColumn.setStyle("-fx-alignment: CENTER;");
        isAvailableColumn.setStyle("-fx-alignment: CENTER;");

        // Đặt chiều rộng tối thiểu cho các cột
        titleColumn.setMinWidth(200);
        authorColumn.setMinWidth(200);

        // Khởi tạo các cột của TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));

        // Sử dụng cellFactory tùy chỉnh để hiển thị 1 và 0 thay vì TRUE và FALSE
        isAvailableColumn.setCellFactory(column -> new TableCell<Document, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Có sẵn" : "Đang được mượn");
                }
            }
        });

        TableColumn<Document, Void> actionColumn = new TableColumn<>("Hành động");

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết");

            {
                // Xử lý sự kiện khi nhấn nút
                detailButton.setOnAction(event -> {
                    Document doc = getTableView().getItems().get(getIndex());
                    showBookDetails(doc); // Gọi hàm hiển thị đánh giá
                });

                // Tùy chỉnh giao diện nút
                detailButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Nếu hàng rỗng, không hiển thị nút
                } else {
                    setGraphic(detailButton); // Hiển thị nút trong ô
                }
            }
        });

        documentTableView.getColumns().add(actionColumn);


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
    /*@FXML
    private void handleSearchDocument() {
        try {
            // Tải FXML của giao diện tìm kiếm
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/menubar/SearchDoc-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Tạo cửa sổ mới cho giao diện tìm kiếm
            Stage searchStage = new Stage();
            searchStage.setTitle("Tìm kiếm Tài liệu");
            searchStage.setScene(scene);
            searchStage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ khác cho đến khi đóng
            searchStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @FXML
    private void handleSearchDocument() {
        // Lấy từ khóa từ thanh tìm kiếm
        String keyword = searchField.getText().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        // Lấy kết quả tìm kiếm từ DocumentManager
        ObservableList<Document> searchResults = FXCollections.observableArrayList(documentManager.searchDocuments(keyword));
        documentTableView.setItems(searchResults);
    }

    @FXML
    private TextField searchField;

    @FXML
    private void handleReload() {
        // Gọi phương thức lấy danh sách tài liệu ban đầu từ DocumentManager
        List<Document> allDocuments = documentManager.getAllDocument();

        // Đặt lại danh sách tài liệu vào TableView
        documentTableView.getItems().setAll(allDocuments);
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
        List<Document> documents = documentManager.getAllDocument();

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
    
    
    public void deleteDocumentById(String id) {
        documentManager.deleteDocumentById(id); // Gọi phương thức trong DatabaseManager
        refreshTable(); // Cập nhật lại TableView sau khi xóa
    }

    private void showBookDetails(Document doc) {
        // Tạo cửa sổ mới
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Đánh giá sách: " + doc.getTitle());
        dialogStage.initModality(Modality.WINDOW_MODAL);

        // Nội dung đánh giá (ví dụ: hiển thị từ cơ sở dữ liệu hoặc danh sách tĩnh)
        Label reviewLabel = new Label("Đánh giá của người dùng:\n\n" + doc.getReviews());
        reviewLabel.setWrapText(true);

        // Bố cục hộp thoại
        VBox dialogVBox = new VBox(10, reviewLabel);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        // Cảnh (Scene)
        Scene dialogScene = new Scene(dialogVBox, 400, 200);
        dialogStage.setScene(dialogScene);

        // Hiển thị cửa sổ
        dialogStage.showAndWait();
    }

}