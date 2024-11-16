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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.menubar.Document;
import org.example.menubar.DocumentManager;
import org.example.menubar.DatabaseManager;
import org.example.menubar.SearchDocController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {
  private static UserMenuController instance;
  private String currentUserId;


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

    boolean success = documentManager.borrowDocument(selectedDocument.getId(), currentUserId);
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

      BorrowedDocumentsController controller = loader.getController();
      controller.setUserId(currentUserId);
      controller.refreshBorrowedDocuments(currentUserId);

      Stage stage = new Stage();
      stage.setTitle("Borrowed Documents List");
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

    boolean success = documentManager.returnDocument(selectedDocument.getId(), currentUserId);
    if (success) {
      showAlert("Thành công", "Bạn đã trả tài liệu thành công.");
      refreshTable(); // Tải lại dữ liệu sau khi trả tài liệu
    } else {
      showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
    }
  }

  @FXML
  private void handleSearch() {
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
  }

  @FXML
  private void handleUserInfo() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/menubar/user-info-view.fxml"));
      Parent userInfoRoot = loader.load();

      // Lấy UserInfoController và truyền thông tin người dùng
      UserInfoController controller = loader.getController();
      controller.setUserId(currentUserId);

      Stage stage = new Stage();
      stage.setTitle("Thông tin người dùng");
      stage.setScene(new Scene(userInfoRoot, 400, 300));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void refreshTable() {
    ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getAllDocument());
    documentTableView.setItems(document);
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public UserMenuController() {
    instance = this;
  }

  public static UserMenuController getInstance() {
    return instance;
  }

  public void setCurrentUserId(String userId) {
    this.currentUserId = userId;
    refreshTable();
  }



}