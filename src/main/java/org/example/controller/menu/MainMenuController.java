package org.example.controller.menu;

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
import org.example.controller.ManageUsersController;
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
import java.util.ResourceBundle;

import static org.example.util.DialogUtils.showAlert;

/**
 * The {@code MainMenuController} class is responsible for controlling the main menu UI.
 * It handles user interactions, displays documents, and manages actions such as add, edit,
 * delete, and search documents. This class interacts with services and repositories
 * to fetch and update data.
 */
public class MainMenuController implements Initializable {

    private Stage stage;
    @FXML
    private TextField searchField;
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


    private final DocumentManager documentManager;
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final DialogUtils dialogUtils = new DialogUtils();
    private final SessionManager sessionManager = new SessionManager();

    /**
     * Constructor initializes the {@link DocumentManager} with the {@link DatabaseManager}.
     */
    public MainMenuController() {
        this.documentManager = new DocumentManager(dbManager);
    }

    /**
     * Sets the primary {@link Stage} for this controller.
     *
     * @param stage the primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Library Manager");
    }

    /**
     * Gets the primary {@link Stage}.
     *
     * @return the primary stage
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Sets the current user ID for further reference (if needed).
     *
     * @param userId the user ID
     */
    public void setCurrentUserId(String userId) {
    }

    /**
     * Initializes the main menu UI, sets up table columns, starts the clock,
     * and refreshes document data.
     *
     * @param location  the location of the FXML resource
     * @param resources the resource bundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (dbManager.getConnection().isClosed()) {
                throw new SQLException("Database connection is closed!");
            }
            setupTableColumns();
            ClockManager.startClock(clockLabel);
            refreshTable();
            updateBookCounts();
        } catch (SQLException e) {
            showAlert("Database Error", "Could not initialize database connection.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Initialization Error", "An error occurred during initialization.");
            e.printStackTrace();
        }
    }

    /**
     * Configures the table columns for the document list view.
     */
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

    /**
     * Adds an action column to the document table, allowing users to view details.
     */
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

    /**
     * Handles the logout action, showing a confirmation dialog before logging out.
     *
     * @param actionEvent the logout event
     */
    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        boolean confirmLogout = DialogUtils.showLogoutConfirmation(stage);
        if (confirmLogout) {
            sessionManager.performLogout(stage);
        }
    }

    /**
     * Restarts the application.
     *
     * @throws IOException if the application cannot restart
     */
    private void restartApp() throws IOException {
        Main mainApp = new Main();
        Main.getInstance().restartApp();
    }

    /**
     * Handles the addition of a new document, showing the Add Document window.
     */
    @FXML
    private void handleAddDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/AddDoc-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Tài Liệu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            AddDocumentController controller = loader.getController();
            AddDocumentController.setMainMenuController(this);
            controller.setStage(stage);
            controller.setOnDocumentAddedCallback(this::refreshTable);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Lỗi", "Không thể tải giao diện thêm tài liệu.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the event when the "Delete Document" button is clicked.
     */
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

    /**
     * Handles the event when the "Edit Document" button is clicked.
     */
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
    private void handleReload() {
        refreshTable();
    }

    /**
     * Refreshes the table view with the latest document data.
     */
    public void refreshTable() {
        updateBookCounts();
        ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getAllDocument());
        documentTableView.setItems(document);
    }

    public boolean deleteDocumentById(String id) {
        return documentManager.deleteDocumentById(id);
    }

    /**
     * Updates the book count labels based on current database data.
     */
    private void updateBookCounts() {
        try {
            int totalBooks = documentManager.getTotalBooksFromDatabase();
            int availableBooks = documentManager.getAvailableBooksFromDatabase();
            updateBookCountLabels(totalBooks, availableBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the total and available books labels in the UI.
     *
     * @param totalBooks     the total number of books
     * @param availableBooks the number of available books
     */
    private void updateBookCountLabels(int totalBooks, int availableBooks) {
        totalBooksLabel.setText("Tổng số sách: " + totalBooks);
        availableBooksLabel.setText("Sách có sẵn: " + availableBooks);
    }

    @FXML
    private void handleManageUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/ManageUsersView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Quản lý người dùng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));


            ManageUsersController controller = loader.getController();
            controller.setStage(stage);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
