package org.example.controller.document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.example.controller.menu.UserMenuController;
import org.example.model.Document;
import org.example.service.DocumentManager;
import org.example.repository.DatabaseManager;
import org.example.controller.review.RateBookController;
import org.example.util.DialogUtils;
import org.example.util.FXMLUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;

public class BorrowedDocumentsController implements Initializable {
    private String userId;
    private final DialogUtils dialogUtils = new DialogUtils();

    @FXML
    private TableView<Document> borrowedDocumentTableView;

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

    private DocumentManager documentManager;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentManager = new DocumentManager(new DatabaseManager());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        refreshTable();
    }

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
                    dialogUtils.showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
                    dialogUtils.showRateBookDialog(selectedDocument, userId);
                    refreshTable(); // Tải lại danh sách tài liệu đã mượn sau khi trả tài liệu
                    UserMenuController.getInstance().refreshTable(); // Cập nhật lại bảng trong giao diện người dùng
                } else {
                    dialogUtils.showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
                }
            });
        });
    }

    private void refreshTable() {
        executorService.execute(() -> {
            List<Document> documents = documentManager.getBorrowedDocumentsByUserId(userId);
            ObservableList<Document> borrowedDocuments = FXCollections.observableArrayList(documents);

            Platform.runLater(() -> borrowedDocumentTableView.setItems(borrowedDocuments));
        });
    }

    public void refreshBorrowedDocuments(String userId) {
        ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId(userId));
        borrowedDocumentTableView.setItems(document);
    }

    public void shutdownExecutor() {
        executorService.shutdown();
    }
}