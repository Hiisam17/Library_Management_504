package org.example.usermenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.menubar.Document;
import org.example.menubar.DocumentManager;
import org.example.menubar.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {

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


  private DocumentManager documentManager;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Khởi tạo DocumentManager (cần kết nối đến cơ sở dữ liệu qua DatabaseManager)
    documentManager = new DocumentManager(new DatabaseManager());

    // Thiết lập các cột trong bảng TableView
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
          setText(item ? "1" : "0");
        }
      }
    });

    // Tải dữ liệu từ SQL và hiển thị
    refreshTable();
  }

  @FXML
  private void handleBorrowDocument() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để mượn.");
      return;
    }

    boolean success = documentManager.borrowDocument(selectedDocument.getId());
    if (success) {
      showAlert("Thành công", "Bạn đã mượn tài liệu thành công.");
      refreshTable(); // Tải lại dữ liệu sau khi mượn tài liệu
    } else {
      showAlert("Thất bại", "Không thể mượn tài liệu. Vui lòng thử lại.");
    }
  }

  @FXML
  private void handleShowBorrowedDocuments() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/menubar/borrowed-documents-view.fxml"));
      Parent root = loader.load();
      Stage stage = new Stage();
      stage.setTitle("Danh Sách Tài Liệu Đã Mượn");
      stage.setScene(new Scene(root));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleReturnDocument() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để trả.");
      return;
    }

    boolean success = documentManager.returnDocument(selectedDocument.getId());
    if (success) {
      showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
      refreshTable();
    } else {
      showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
    }
  }

  @FXML
  private void handleSearch() {
//    String searchText = searchField.getText();
//    ObservableList<Document> searchResults = FXCollections.observableArrayList(documentManager.searchDocuments(searchText));
//    documentTableView.setItems(searchResults);
  }

  private void refreshTable() {
    // Lấy danh sách tài liệu từ cơ sở dữ liệu và cập nhật vào TableView
    ObservableList<Document> documents = FXCollections.observableArrayList(documentManager.getAllDocument());
    documentTableView.setItems(documents);
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

}