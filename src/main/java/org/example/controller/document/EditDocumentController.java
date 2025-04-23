package org.example.controller.document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.model.Document;
import org.example.service.DocumentManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;
import static org.example.util.DialogUtils.showConfirmation;

/**
 * Controller for editing a document.
 * Provides functionalities to display document details and save edits made by the user.
 */
public class EditDocumentController {

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publisherField;
    @FXML private DatePicker publishedDatePicker;

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Stage stage;
    private Document document;
    private DocumentManager documentManager;
    private Runnable onDocumentEdited;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the DocumentManager instance for managing document-related operations.
     *
     * @param documentManager the DocumentManager instance to set.
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    /**
     * Sets a callback to be executed after the document is successfully edited.
     *
     * @param onDocumentEdited a Runnable to execute when the document is edited.
     */
    public void setOnDocumentEdited(Runnable onDocumentEdited) {
        this.onDocumentEdited = onDocumentEdited;
    }

    /**
     * Populates the form with details of the document to be edited.
     *
     * @param document the document to edit.
     */
    public void setDocument(Document document) {
        this.document = document;

        // Populate the fields with the document's data
        idField.setText(document.getId());
        idField.setEditable(false);
        titleField.setText(document.getTitle());
        authorField.setText(document.getAuthor());
        publisherField.setText(document.getPublisher());

        // Handle published date
        String rawDate = document.getPublishedDate();
        if (rawDate != null && !rawDate.isEmpty()) {
            try {
                if (rawDate.matches("\\d{4}")) { // If only year is provided
                    LocalDate date = LocalDate.of(Integer.parseInt(rawDate), 1, 1);
                    publishedDatePicker.setValue(date);
                } else {
                    LocalDate date = LocalDate.parse(rawDate, outputFormatter);
                    publishedDatePicker.setValue(date);
                }
            } catch (DateTimeParseException e) {
                showAlert("Lỗi", "Ngày xuất bản không hợp lệ: " + rawDate);
                publishedDatePicker.setValue(null);
            }
        } else {
            publishedDatePicker.setValue(null);
        }
    }

    /**
     * Initializes the controller and sets up the date picker with a custom string converter.
     */
    public void initialize() {
        publishedDatePicker.setConverter(
                new StringConverter<>() {
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
                            if (string.matches("\\d{4}")) { // If only year is provided
                                return LocalDate.of(Integer.parseInt(string), 1, 1);
                            } else {
                                return LocalDate.parse(string, inputFormatter);
                            }
                        } catch (DateTimeParseException e) {
                            showAlert("Lỗi", "Định dạng ngày không hợp lệ. Vui lòng sử dụng dd/MM/yyyy hoặc yyyy.");
                            return null;
                        }
                    }
                });
    }

    /**
     * Handles the "Save Changes" button click event.
     * Validates user input and saves changes to the document asynchronously.
     */
    @FXML
    private void handleSaveChanges() {
        if (showConfirmation("Xác nhận", "Bạn có chắc chắn muốn lưu thay đổi?")) {
            executorService.submit(
                    () -> {
                        // Update document information
                        document.setTitle(titleField.getText());
                        document.setAuthor(authorField.getText());
                        document.setPublisher(publisherField.getText());

                        String rawDate = publishedDatePicker.getEditor().getText();
                        String formattedDate;
                        try {
                            if (rawDate.matches("\\d{4}")) { // Only year is provided
                                formattedDate = rawDate;
                            } else {
                                LocalDate date = LocalDate.parse(rawDate, inputFormatter);
                                formattedDate = date.format(outputFormatter);
                            }
                            document.setPublishedDate(formattedDate);
                        } catch (DateTimeParseException e) {
                            showAlert("Lỗi", "Ngày không hợp lệ. Vui lòng kiểm tra và thử lại.");
                            return;
                        }

                        documentManager.updateDocument(document);

                        // Update UI on the JavaFX Application thread
                        Platform.runLater(
                                () -> {
                                    if (onDocumentEdited != null) {
                                        onDocumentEdited.run();
                                    }
                                    stage.close();
                                });
                    });
        } else {
            showAlert("Đã hủy", "Không có thay đổi nào được lưu.");
        }
    }
}
