package org.example.controller.menu;

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
import org.example.model.Document;
import org.example.repository.DatabaseManager;
import org.example.service.DocumentManager;
import org.example.controller.document.BorrowedDocumentsController;
import org.example.controller.review.UserInfoController;
import org.example.util.ClockManager;
import org.example.util.DialogUtils;
import org.example.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.example.util.DialogUtils.showAlert;

public class UserMenuController implements Initializable {
  private static UserMenuController instance;
  private String currentUserId;

  @FXML
  private Label clockLabel;

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
  private Label totalBooksLabel;

  @FXML
  private Label availableBooksLabel;

  private Stage stage;
  private DocumentManager documentManager;
  private final DatabaseManager dbManager = new DatabaseManager();
  private final DialogUtils dialogUtils = new DialogUtils();
  private final SessionManager sessionManager = new SessionManager();
  private final ClockManager clockManager = new ClockManager();

  public void setDocumentManager(DocumentManager documentManager) {
    this.documentManager = documentManager;
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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/document/borrowed-documents-view.fxml"));
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
  private void handleUserInfo() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/user-info-view.fxml"));
      Parent userInfoRoot = loader.load();

      // Lấy UserInfoController và truyền thông tin người dùng
      UserInfoController controller = loader.getController();
      controller.setUserId(currentUserId);

      Stage stage = new Stage();
      stage.setTitle("Thông tin người dùng");
      stage.setScene(new Scene(userInfoRoot, 600, 400));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void refreshTable() {
    updateBookCounts();
    ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getAllDocument());
    documentTableView.setItems(document);
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

  @FXML
  private void handleReload() {
    refreshTable();
  }

  @FXML
  public void handleLogout(ActionEvent actionEvent) {
    boolean confirmLogout = DialogUtils.showLogoutConfirmation(stage);
    if (confirmLogout) {
      sessionManager.performLogout(stage); // Thực hiện logic đăng xuất
    }
  }


  @FXML
  private void handleRateBook() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để đánh giá.");
      return;
    }

    // Hiển thị cửa sổ đánh giá
    dialogUtils.showRateBookDialog(selectedDocument, currentUserId);
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

}