package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.example.menubar.DatabaseManager.SQL_connect;

public class AddDocumentController {


    private static MainMenuController mainMenuController;


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
                    return null;
                }
                try {
                    // Chuyển chuỗi thành LocalDate theo định dạng dd/MM/yyyy
                    return LocalDate.parse(string, inputFormatter);
                } catch (DateTimeParseException e) {
                    showAlert("Lỗi", "Ngày nhập sai định dạng. Vui lòng nhập theo định dạng dd/MM/yyyy.");
                    return null;
                }
            }
        });
    }
    // Xử lý sự kiện nút Lưu
    @FXML
    private void handleSaveDocument() {
        Document newDocument = new Document(idField.getText(), titleField.getText(), authorField.getText(), publisherField.getText(), publishedDatePicker.getValue().format(outputFormatter));
        newDocument.setId(idField.getText());
        newDocument.setTitle(titleField.getText());
        newDocument.setAuthor(authorField.getText());
        newDocument.setPublisher(publisherField.getText());

        LocalDate publishedDate = publishedDatePicker.getValue();
        String formattedDate = publishedDate.format(outputFormatter);
        newDocument.setPublishedDate(formattedDate);

        if ( !newDocument.getId().isEmpty() && !newDocument.getTitle().isEmpty() && !newDocument.getAuthor().isEmpty() && !newDocument.getPublishedDate().isEmpty()) {
            try {
                // Use DocumentManager to insert the document
                DocumentManager documentManager = new DocumentManager(new DatabaseManager());
                documentManager.insertDocument(idField.getId(), titleField.getText(), authorField.getText(), publisherField.getText(), formattedDate);

                showAlert("Success", "Document added successfully!");

                // Call the callback to refresh the TableView
                if (onDocumentAddedCallback != null) {
                    onDocumentAddedCallback.run();
                }

                clearFields(); // Clear fields after saving
            } catch (Exception e) {
                showAlert("Error", "Failed to add document to the database.");
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please fill in all required fields.");
        }
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

    // Hàm hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Xử lý sự kiện nút Quay lại
    @FXML
    private void handleBack() {
        // Đóng cửa sổ thêm tài liệu
        if (stage != null) {
            stage.close(); // Đóng cửa sổ hiện tại
        }
    }
}

