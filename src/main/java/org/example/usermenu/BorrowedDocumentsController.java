package org.example.usermenu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import org.example.menubar.Document;
import org.example.menubar.DocumentManager;
import org.example.menubar.DatabaseManager;

import java.net.URL;
import java.util.ResourceBundle;

public class BorrowedDocumentsController implements Initializable {
    private String userId;

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

        boolean success = documentManager.returnDocument(selectedDocument.getId());
        if (success) {
            showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
            refreshTable(); // Tải lại danh sách tài liệu đã mượn sau khi trả tài liệu
            UserMenuController.getInstance().refreshTable(); // Cập nhật lại bảng trong giao diện người dùng
        } else {
            showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
        }
    }

    private void refreshTable() {
        ObservableList<Document> borrowedDocument = FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId("4")); // Sử dụng id >= 4
        borrowedDocumentTableView.setItems(borrowedDocument);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshBorrowedDocuments(String userId) {
        ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId(userId));
        borrowedDocumentTableView.setItems(document);
    }
}