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

/**
 * The {@code UserMenuController} class handles the user menu functionality.
 * It allows users to view, borrow, and search for documents,
 * as well as access their borrowed documents and user information.
 */
public class UserMenuController implements Initializable {

  private static UserMenuController instance;
  private String currentUserId;

  DatabaseManager dbManager = DatabaseManager.getInstance();
  private DocumentManager documentManager;
  private final DialogUtils dialogUtils = new DialogUtils();
  private final SessionManager sessionManager = new SessionManager();
  private final ClockManager clockManager = new ClockManager();

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

  @FXML
  private TextField searchField;

  private Stage stage;

  /**
   * Constructor initializes the singleton instance of the controller.
   */
  public UserMenuController() {
    instance = this;
  }

  /**
   * Gets the singleton instance of the {@code UserMenuController}.
   *
   * @return the singleton instance
   */
  public static UserMenuController getInstance() {
    return instance;
  }

  /**
   * Sets the {@link DocumentManager} instance for managing document operations.
   *
   * @param documentManager the document manager to set
   */
  public void setDocumentManager(DocumentManager documentManager) {
    this.documentManager = documentManager;
  }

  /**
   * Initializes the user menu by setting up the table columns, starting the clock,
   * and loading the initial data into the table view.
   *
   * @param location  the location of the FXML resource
   * @param resources the resource bundle for internationalization
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    documentManager = new DocumentManager(dbManager);
    setupTableColumns();
    ClockManager.startClock(clockLabel);
    refreshTable();
    updateBookCounts();
  }

  /**
   * Configures the columns of the document table view.
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
   * Adds an action column to the table for viewing document details.
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
   * Handles borrowing a document for the current user.
   */
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
      refreshTable();
    } else {
      showAlert("Thất bại", "Không thể mượn tài liệu. Vui lòng thử lại.");
    }
  }

  /**
   * Displays a list of documents borrowed by the current user.
   */
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

  /**
   * Searches for documents based on the keyword entered by the user.
   */
  @FXML
  private void handleSearchDocument() {
    String keyword = searchField.getText().trim().toLowerCase();

    if (keyword.isEmpty()) {
      showAlert("Lỗi", "Vui lòng nhập từ khóa tìm kiếm.");
      return;
    }

    ObservableList<Document> searchResults = FXCollections.observableArrayList(documentManager.searchDocuments(keyword));
    documentTableView.setItems(searchResults);
  }

  /**
   * Displays user information in a separate window.
   */
  @FXML
  private void handleUserInfo() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/user-info-view.fxml"));
      Parent userInfoRoot = loader.load();

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

  /**
   * Refreshes the document table with the latest data.
   */
  public void refreshTable() {
    updateBookCounts();
    ObservableList<Document> document = FXCollections.observableArrayList(documentManager.getAllDocument());
    documentTableView.setItems(document);
  }

  /**
   * Sets the current user ID and refreshes the document table.
   *
   * @param userId the user ID to set
   */
  public void setCurrentUserId(String userId) {
    this.currentUserId = userId;
    refreshTable();
  }

  /**
   * Handles reloading the document table view.
   */
  @FXML
  private void handleReload() {
    refreshTable();
  }

  /**
   * Logs out the current user after showing a confirmation dialog.
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
   * Opens a dialog for the user to rate a selected book.
   */
  @FXML
  private void handleRateBook() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để đánh giá.");
      return;
    }

    dialogUtils.showRateBookDialog(selectedDocument, currentUserId);
  }

  /**
   * Updates the labels for the total and available book counts.
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
   * Updates the UI labels for total and available book counts.
   *
   * @param totalBooks     the total number of books
   * @param availableBooks the number of available books
   */
  private void updateBookCountLabels(int totalBooks, int availableBooks) {
    totalBooksLabel.setText("Tổng số sách: " + totalBooks);
    availableBooksLabel.setText("Sách có sẵn: " + availableBooks);
  }
}
