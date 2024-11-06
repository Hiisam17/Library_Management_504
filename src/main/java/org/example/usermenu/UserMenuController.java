//package org.example.usermenu;
//
//
//import javafx.fxml.FXML;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import org.example.menubar.Document;
//import org.example.menubar.DocumentManager;
//import org.example.menubar.DatabaseManager;
//
//public class UserMenuController {
//
//  @FXML
//  private TextField searchField;
//
//  @FXML
//  private TableView<Document> documentTableView;
//
//  @FXML
//  private void handleBorrowDocument() {
//    // Logic for borrowing a document
//  }
//
//  @FXML
//  private void handleReturnDocument() {
//    // Logic for returning a document
//  }
//
//  @FXML
//  private void handleSearch() {
//    String searchText = searchField.getText();
//    // Logic for searching documents based on searchText
//  }
//}

package org.example.usermenu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.menubar.Document;
import org.example.menubar.DocumentManager;
import org.example.menubar.DatabaseManager;

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

  private DocumentManager documentManager;

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
  private void handleBorrowDocument() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để mượn.");
      return;
    }

    boolean success = documentManager.borrowDocument(selectedDocument.getId());
    if (success) {
      showAlert("Thành công", "Bạn đã mượn tài liệu thành công.");
      refreshTable();
    } else {
      showAlert("Thất bại", "Không thể mượn tài liệu. Vui lòng thử lại.");
    }
  }

  @FXML
  private void handleReturnDocument() {
//    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
//    if (selectedDocument == null) {
//      showAlert("Thông báo", "Vui lòng chọn tài liệu để trả.");
//      return;
//    }
//
//    boolean success = documentManager.returnDocument(selectedDocument.getId());
//    if (success) {
//      showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
//      refreshTable();
//    } else {
//      showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
//    }
  }

  @FXML
  private void handleSearch() {
//    String searchText = searchField.getText();
//    ObservableList<Document> searchResults = FXCollections.observableArrayList(documentManager.searchDocuments(searchText));
//    documentTableView.setItems(searchResults);
  }

  private void refreshTable() {
    ObservableList<Document> documents = FXCollections.observableArrayList(documentManager.getAllDocuments());
    documentTableView.setItems(documents);
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }
}