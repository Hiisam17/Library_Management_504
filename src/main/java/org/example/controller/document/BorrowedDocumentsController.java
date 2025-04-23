package org.example.controller.document;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.controller.menu.UserMenuController;
import org.example.model.Document;
import org.example.repository.DatabaseManager;
import org.example.service.DocumentManager;
import org.example.util.DialogUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;

/**
 * Controller for managing borrowed documents.
 * Provides functionality to display, refresh, and return borrowed documents.
 */
public class BorrowedDocumentsController implements Initializable {

    private String userId;
    private final DialogUtils dialogUtils = new DialogUtils();
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private DocumentManager documentManager;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML private TableView<Document> borrowedDocumentTableView;
    @FXML private TableColumn<Document, String> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> authorColumn;
    @FXML private TableColumn<Document, String> publisherColumn;
    @FXML private TableColumn<Document, String> publishedDateColumn;

    /**
     * Sets the user ID for fetching borrowed documents.
     *
     * @param userId the ID of the user.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Initializes the controller. Sets up table columns and loads borrowed documents.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if
     *                  unknown.
     * @param resources the resources used to localize the root object, or null if not available.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentManager = new DocumentManager(dbManager);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        refreshTable();
    }

    /**
     * Handles the return of a selected borrowed document.
     * Prompts the user to select a document and processes the return asynchronously.
     */
    @FXML
    private void handleReturnDocument() {
        Document selectedDocument = borrowedDocumentTableView.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showAlert("Thông báo", "Vui lòng chọn tài liệu để trả.");
            return;
        }

        executorService.execute(() -> {
            boolean success = documentManager.returnDocument(selectedDocument.getId(), userId);

            Platform.runLater(() -> {
                if (success) {
                    showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
                    dialogUtils.showRateBookDialog(selectedDocument, userId);
                    refreshTable();
                    UserMenuController.getInstance().refreshTable();
                } else {
                    showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
                }
            });
        });
    }

    /**
     * Refreshes the table with the user's borrowed documents.
     */
    private void refreshTable() {
        executorService.execute(() -> {
            List<Document> documents = documentManager.getBorrowedDocumentsByUserId(userId);
            ObservableList<Document> borrowedDocuments = FXCollections.observableArrayList(documents);

            Platform.runLater(() -> borrowedDocumentTableView.setItems(borrowedDocuments));
        });
    }

    /**
     * Refreshes the list of borrowed documents for the given user.
     *
     * @param userId the ID of the user whose documents need to be refreshed.
     */
    public void refreshBorrowedDocuments(String userId) {
        ObservableList<Document> document =
                FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId(userId));
        borrowedDocumentTableView.setItems(document);
    }

    /**
     * Shuts down the executor service to release resources.
     * This should be called when the controller is no longer in use.
     */
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
