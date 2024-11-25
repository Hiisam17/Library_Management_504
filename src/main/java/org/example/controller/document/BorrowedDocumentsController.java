package org.example.controller.document;

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
import java.util.ResourceBundle;

import static org.example.util.DialogUtils.showAlert;

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

        boolean success = documentManager.returnDocument(selectedDocument.getId(), userId);
        if (success) {
            showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
            showRateBookDialog(selectedDocument, userId);
            refreshTable(); // Tải lại danh sách tài liệu đã mượn sau khi trả tài liệu
            UserMenuController.getInstance().refreshTable(); // Cập nhật lại bảng trong giao diện người dùng
        } else {
            showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
        }
    }

    private void refreshTable() {
        ObservableList<Document> borrowedDocument = FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId(userId));
        borrowedDocumentTableView.setItems(borrowedDocument);
    }

    public void refreshBorrowedDocuments(String userId) {
        ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getBorrowedDocumentsByUserId(userId));
        borrowedDocumentTableView.setItems(document);
    }

    private void showRateBookDialog(Document doc, String userId) {
        try {
            // Sử dụng FXML để tải FXML và lấy root và controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/rate-book-view.fxml"));
            Parent root = loader.getRoot();

            // Truy cập controller và thiết lập dữ liệu
            RateBookController controller = loader.getController();
            controller.setDocument(doc);
            controller.setUserId(userId);

            // Mở cửa sổ với giao diện đã thiết lập
            FXMLUtils.openWindow(
                    String.valueOf(root),
                    "Đánh giá sách: " + doc.getTitle(),
                    null, // Không có cửa sổ cha
                    null  // Không sử dụng CSS
            );
        } catch (IOException e) {
            DialogUtils.showAlert("Lỗi khi mở cửa sổ đánh giá sách", e.getMessage());
            e.printStackTrace();
        }
    }

}