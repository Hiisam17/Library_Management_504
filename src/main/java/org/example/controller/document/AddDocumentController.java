package org.example.controller.document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.controller.menu.MainMenuController;
import org.example.model.Document;
import org.example.repository.DatabaseManager;
import org.example.service.APIIntegration;
import org.example.service.DocumentManager;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;

public class AddDocumentController {


    private static MainMenuController mainMenuController;
    DatabaseManager dbManager = DatabaseManager.getInstance();
    private final ExecutorService executorService = Executors.newCachedThreadPool();


    public static void setMainMenuController(MainMenuController mainMenuController) {
        AddDocumentController.mainMenuController = mainMenuController;
    }

    // Tham chiếu đến Stage
    private Stage stage;
    // input and output for date
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private TextField idField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private DatePicker publishedDatePicker;

    @FXML
    private TextField searchField;

    private Runnable onDocumentAddedCallback; // Callback để thông báo khi tài liệu được thêm

    // Các thuộc tính và phương thức hiện tại khác như titleField, authorField...

    // Đặt callback
    public void setOnDocumentAddedCallback(Runnable onDocumentAddedCallback) {
        this.onDocumentAddedCallback = onDocumentAddedCallback;
    }

    @FXML
    public void initialize() {
        //  hiển thị đúng định dạng dd/MM/yyyy
        publishedDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(inputFormatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null; // Trường hợp chuỗi rỗng
                }
                try {
                    if (string.matches("\\d{4}")) { // Nếu chỉ nhập năm (yyyy)
                        return LocalDate.of(Integer.parseInt(string), 1, 1); // Chuyển thành ngày đầu năm
                    } else { // Nếu nhập ngày đầy đủ (dd/MM/yyyy)
                        return LocalDate.parse(string, inputFormatter); // Chuyển chuỗi thành LocalDate
                    }
                } catch (DateTimeParseException e) {
                    showAlert("Lỗi", "Ngày nhập sai định dạng. Vui lòng nhập theo định dạng dd/MM/yyyy hoặc yyyy.");
                    return null;
                }
            }
        });
    }

    // Xử lý sự kiện nút Lưu
    @FXML
    private void handleSaveDocument() {
        Document newDocument = new Document();
        newDocument.setId(idField.getText());
        newDocument.setTitle(titleField.getText());
        newDocument.setAuthor(authorField.getText());
        newDocument.setPublisher(publisherField.getText());

        String rawDate = publishedDatePicker.getEditor().getText();
        String formattedDate;

        try {
            if (rawDate.matches("\\d{4}")) {
                formattedDate = rawDate;
            } else {
                LocalDate date = LocalDate.parse(rawDate, inputFormatter);
                formattedDate = date.format(outputFormatter);
            }
            newDocument.setPublishedDate(formattedDate);
        } catch (DateTimeParseException e) {
            showAlert("Lỗi", "Hãy nhập ngày theo định dạng dd/MM/yyyy hoặc yyyy.");
            return;
        }

        if (newDocument.getId().isEmpty() || newDocument.getTitle().isEmpty() || newDocument.getAuthor().isEmpty()
                || newDocument.getPublishedDate().isEmpty()) {
            showAlert("Lỗi", "Hãy điền tất cả các trường.");
            return;
        }

        // Thực hiện lưu trong ExecutorService
        executorService.execute(() -> {
            try {
                DocumentManager documentManager = new DocumentManager(dbManager);
                boolean success = documentManager.insertDocument(
                        newDocument.getId(),
                        newDocument.getTitle(),
                        newDocument.getAuthor(),
                        newDocument.getPublisher(),
                        newDocument.getPublishedDate()
                );

                Platform.runLater(() -> {
                    if (success) {
                        showAlert("Thành công", "Tài liệu được thêm thành công!");
                        if (onDocumentAddedCallback != null) {
                            onDocumentAddedCallback.run();
                        }
                        clearFields();
                    } else {
                        showAlert("Lỗi", "Không thể thêm tài liệu.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Lỗi", "Đã xảy ra lỗi khi thêm tài liệu."));
                e.printStackTrace();
            }
        });
    }


    @FXML
    private void handleSearchAPI() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập từ khóa để tìm kiếm.");
            return;
        }

        executorService.execute(() -> {
            try {
                String jsonResponse = APIIntegration.getBookInfoByTitle(query);
                Document document = APIIntegration.parseBookInfo(jsonResponse);

                Platform.runLater(() -> {
                    if (document != null) {
                        titleField.setText(document.getTitle());
                        authorField.setText(document.getAuthor());
                        publisherField.setText(document.getPublisher());

                        String publishedDate = document.getPublishedDate();
                        if (publishedDate == null || publishedDate.isEmpty()) {
                            publishedDate = "9999";
                        }

                        try {
                            if (publishedDate.length() == 4) {
                                publishedDatePicker.getEditor().setText(publishedDate);
                            } else {
                                LocalDate date = LocalDate.parse(publishedDate);
                                publishedDatePicker.setValue(date);
                            }
                        } catch (DateTimeParseException e) {
                            System.err.println("Lỗi khi phân tích ngày xuất bản: " + e.getMessage());
                        }
                    } else {
                        showAlert("Thông báo", "Không tìm thấy thông tin sách.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Lỗi", "Đã xảy ra lỗi trong quá trình tìm kiếm."));
                e.printStackTrace();
            }
        });
    }

    // Hàm khởi tạo để nhận tham chiếu Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Hàm xóa dữ liệu sau khi lưu
    private void clearFields() {
        idField.clear();
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        publishedDatePicker.setValue(null);
    }

    // Xử lý sự kiện nút Quay lại
    @FXML
    private void handleBack() {
        // Đóng cửa sổ thêm tài liệu
        if (stage != null) {
            stage.close(); // Đóng cửa sổ hiện tại
        }
    }

    public void shutdownExecutor() {
        executorService.shutdownNow();
    }
}

