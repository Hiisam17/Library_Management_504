package org.example.controller.menu;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Main;
import org.example.controller.document.AddDocumentController;
import org.example.controller.document.DeleteDocumentController;
import org.example.controller.document.EditDocumentController;
import org.example.model.Document;
import org.example.repository.DatabaseManager;
import org.example.service.DocumentManager;
import org.example.util.ClockManager;
import org.example.util.DialogUtils;
import org.example.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static org.example.util.DialogUtils.showAlert;


public class MainMenuController implements Initializable {
    private Stage stage;
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
    @FXML
    private Label clockLabel;
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label availableBooksLabel;

    private ObservableList<Document> documentList = FXCollections.observableArrayList();
    private DocumentManager documentManager;
    private final DatabaseManager dbManager = new DatabaseManager();
    private final DialogUtils dialogUtils = new DialogUtils();
    private final SessionManager sessionManager = new SessionManager();

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

    public void setCurrentUserId(String userId) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentManager = new DocumentManager(dbManager);
        setupTableColumns();
        ClockManager.startClock(clockLabel);
        refreshTable();
        updateBookCounts();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));
        isAvailableColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item ? "Có sẵn" : "Đang được mượn");
            }
        });

        addActionColumn();
    }

    private void addActionColumn() {
        TableColumn<Document, Void> actionColumn = new TableColumn<>("Hành động");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết");

            {
                detailButton.setOnAction(event -> {
                    Document doc = getTableView().getItems().get(getIndex());
                    dialogUtils.showBookDetails(doc);
                });
                detailButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailButton);
            }
        });
        documentTableView.getColumns().add(actionColumn);
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        boolean confirmLogout = DialogUtils.showLogoutConfirmation(stage);
        if (confirmLogout) {
            sessionManager.performLogout(stage); // Thực hiện logic đăng xuất
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/AddDoc-view.fxml"));
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
            showAlert("Lỗi", "Không thể tải giao diện thêm tài liệu.");
            e.printStackTrace();
        }
    }


    // Xử lý sự kiện nút Xóa Tài Liệu
    @FXML
    private void handleDeleteDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/Delete_Doc.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/EditDoc-view.fxml"));
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
        refreshTable();
    }


    public void refreshTable() {
        updateBookCounts();
        ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getAllDocument());
        documentTableView.setItems(document);
    }


    public ObservableList<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(ObservableList<Document> documentList) {
        this.documentList = documentList;
    }

    public boolean deleteDocumentById(String id) {
        return documentManager.deleteDocumentById(id);
    }

    private void updateBookCounts() {
        try {
            int totalBooks = documentManager.getTotalBooksFromDatabase();
            int availableBooks = documentManager.getAvailableBooksFromDatabase();
            updateBookCountLabels(totalBooks, availableBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBookCountLabels(int totalBooks, int availableBooks) {
        totalBooksLabel.setText("Tổng số sách: " + totalBooks);
        availableBooksLabel.setText("Sách có sẵn: " + availableBooks);
    }

    @FXML
    private void handleManageUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu/ManageUsersView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Quản lý người dùng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}