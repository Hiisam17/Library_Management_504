package org.example.usermenu;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.menubar.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

  private Stage stage;


  private DocumentManager documentManager;
  private final DatabaseManager dbManager = new DatabaseManager();

  public void setDocumentManager(DocumentManager documentManager) {
    this.documentManager = documentManager;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    documentManager = new DocumentManager(dbManager);

    updateBookCounts();

    // Định dạng cột
    idColumn.setStyle("-fx-alignment: CENTER;"); // Căn giữa
    titleColumn.setStyle("-fx-alignment: CENTER_LEFT;"); // Căn trái
    authorColumn.setStyle("-fx-alignment: CENTER_LEFT;");
    publisherColumn.setStyle("-fx-alignment: CENTER_LEFT;");
    publishedDateColumn.setStyle("-fx-alignment: CENTER;");
    isAvailableColumn.setStyle("-fx-alignment: CENTER;");

    // Đặt chiều rộng tối thiểu cho các cột
    titleColumn.setMinWidth(200);
    authorColumn.setMinWidth(200);

    // Khởi tạo các cột của TableView
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
          setText(item ? "Có sẵn" : "Đang được mượn");
        }
      }
    });

    TableColumn<Document, Void> actionColumn = new TableColumn<>("Hành động");

    actionColumn.setCellFactory(param -> new TableCell<>() {
      private final Button detailButton = new Button("Chi tiết");

      {
        // Xử lý sự kiện khi nhấn nút
        detailButton.setOnAction(event -> {
          Document doc = getTableView().getItems().get(getIndex());
          showBookDetails(doc); // Gọi hàm hiển thị đánh giá
        });

        // Tùy chỉnh giao diện nút
        detailButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-cursor: hand;");
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null); // Nếu hàng rỗng, không hiển thị nút
        } else {
          setGraphic(detailButton); // Hiển thị nút trong ô
        }
      }
    });

    documentTableView.getColumns().add(actionColumn);

    // Tải dữ liệu từ SQL và hiển thị
    refreshTable();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
      LocalDateTime now = LocalDateTime.now();
      clockLabel.setText(now.format(formatter));
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
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

      // Hiển thị cửa sổ đánh giá
      showRateBookDialog(selectedDocument, currentUserId);
    } else {
      showAlert("Thất bại", "Không thể trả tài liệu. Vui lòng thử lại.");
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
    updateBookCounts();
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

  @FXML
  private void handleReload() {
    // Gọi phương thức lấy danh sách tài liệu ban đầu từ DocumentManager
    List<Document> allDocuments = documentManager.getAllDocument();

    // Đặt lại danh sách tài liệu vào TableView
    documentTableView.getItems().setAll(allDocuments);
  }

  @FXML
  public void handleLogout(ActionEvent actionEvent) {
    // Tạo một Alert xác nhận
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Xác nhận Đăng xuất");
    alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
    alert.setContentText("Chọn OK để đăng xuất hoặc Hủy để quay lại.");

    // Hiển thị Alert và chờ người dùng phản hồi
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      if (stage != null) {
        stage.close(); // Đóng cửa sổ chính
        stage=null;
      }
      try {
        Main mainInstance = Main.getInstance();
        if (mainInstance != null) {
          mainInstance.restartApp();
        } else {
          System.err.println("Lỗi: Main chưa được khởi tạo.");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    // Nếu người dùng chọn "Hủy" hoặc đóng Alert, sẽ không làm gì cả
  }

  private void showBookDetails(Document doc) {
    // In ra để debug
    System.out.println("Phương thức showBookDetails được gọi");

    // Tạo cửa sổ mới
    Stage dialogStage = new Stage();
    dialogStage.setTitle("Đánh giá sách: " + doc.getTitle());
    dialogStage.initModality(Modality.WINDOW_MODAL);

    // Lấy danh sách đánh giá từ cơ sở dữ liệu
    DocumentManager documentManager = new DocumentManager(new DatabaseManager());
    List<Review> reviews = documentManager.getReviews(doc.getId());

    // In ra để debug
    System.out.println("Số lượng đánh giá: " + reviews.size());

    // Tạo nội dung hiển thị đánh giá
    VBox reviewBox = new VBox(10);
    reviewBox.setPadding(new Insets(20));
    reviewBox.setAlignment(Pos.CENTER_LEFT);

    for (Review review : reviews) {
      String reviewText = review.getUserName() + ": " + review.getRating() + " - " + review.getComment() + " (" + review.getTimestamp() + ")";
      Label reviewLabel = new Label(reviewText);
      reviewLabel.setWrapText(true);
      reviewBox.getChildren().add(reviewLabel);

      // In ra để debug
      System.out.println("Đánh giá: " + reviewText);
    }

    // Cảnh (Scene)
    Scene dialogScene = new Scene(reviewBox, 400, 200);
    dialogStage.setScene(dialogScene);

    // In ra để debug
    System.out.println("Hiển thị cửa sổ đánh giá");

    // Hiển thị cửa sổ
    dialogStage.showAndWait();

    // In ra để debug
    System.out.println("Đóng cửa sổ đánh giá");
  }

  @FXML
  private void handleRateBook() {
    Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
    if (selectedDocument == null) {
      showAlert("Thông báo", "Vui lòng chọn tài liệu để đánh giá.");
      return;
    }

    // Hiển thị cửa sổ đánh giá
    showRateBookDialog(selectedDocument, currentUserId);
  }

  private void showRateBookDialog(Document doc, String userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/menubar/rate-book-view.fxml"));
      Parent root = loader.load();

      RateBookController controller = loader.getController();
      controller.setDocument(doc);
      controller.setUserId(userId);

      Stage stage = new Stage();
      stage.setTitle("Đánh giá sách: " + doc.getTitle());
      stage.setScene(new Scene(root));
      stage.showAndWait();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private Label totalBooksLabel;

  @FXML
  private Label availableBooksLabel;

  private void updateBookCounts() {
    try {
      // Lấy dữ liệu từ database hoặc danh sách
      int totalBooks = documentManager.getTotalBooksFromDatabase(); // Hoặc getTotalBooks()
      int availableBooks = documentManager.getAvailableBooksFromDatabase(); // Hoặc getAvailableBooks()

      // Cập nhật nhãn
      totalBooksLabel.setText("Tổng số sách: " + totalBooks);
      availableBooksLabel.setText("Sách có sẵn: " + availableBooks);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}