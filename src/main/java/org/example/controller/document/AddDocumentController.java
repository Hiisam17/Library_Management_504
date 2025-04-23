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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;

/**
 * Controller class for handling the addition of new documents.
 */
public class AddDocumentController {

    private static MainMenuController mainMenuController;
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Stage stage;
    private Runnable onDocumentAddedCallback;

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publisherField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private TextField searchField;

    /**
     * Sets the reference to the main menu controller.
     *
     * @param mainMenuController the main menu controller instance
     */
    public static void setMainMenuController(MainMenuController mainMenuController) {
        AddDocumentController.mainMenuController = mainMenuController;
    }

    /**
     * Sets the callback function to notify when a document is added.
     *
     * @param onDocumentAddedCallback a Runnable to execute after a document is added
     */
    public void setOnDocumentAddedCallback(Runnable onDocumentAddedCallback) {
        this.onDocumentAddedCallback = onDocumentAddedCallback;
    }

    /**
     * Sets the stage reference for this controller.
     *
     * @param stage the current stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /** Initializes the controller and sets up the date picker converter. */
    @FXML
    public void initialize() {
        publishedDatePicker.setConverter(
                new StringConverter<LocalDate>() {
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
                            if (string.matches("\\d{4}")) {
                                return LocalDate.of(Integer.parseInt(string), 1, 1);
                            } else {
                                return LocalDate.parse(string, inputFormatter);
                            }
                        } catch (DateTimeParseException e) {
                            showAlert("Lỗi", "Ngày nhập sai định dạng. Vui lòng nhập theo định dạng dd/MM/yyyy hoặc yyyy.");
                            return null;
                        }
                    }
                });
    }

    /** Handles the Save button action to add a new document. */
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

        if (newDocument.getId().isEmpty()
                || newDocument.getTitle().isEmpty()
                || newDocument.getAuthor().isEmpty()
                || newDocument.getPublishedDate().isEmpty()) {
            showAlert("Lỗi", "Hãy điền tất cả các trường.");
            return;
        }

        executorService.execute(
                () -> {
                    try {
                        DocumentManager documentManager = new DocumentManager(dbManager);
                        boolean success =
                                documentManager.insertDocument(
                                        newDocument.getId(),
                                        newDocument.getTitle(),
                                        newDocument.getAuthor(),
                                        newDocument.getPublisher(),
                                        newDocument.getPublishedDate());

                        Platform.runLater(
                                () -> {
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

    /** Handles the API search action to fetch document details. */
    @FXML
    private void handleSearchAPI() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập từ khóa để tìm kiếm.");
            return;
        }

        executorService.execute(
                () -> {
                    try {
                        String jsonResponse = APIIntegration.getBookInfoByTitle(query);
                        Document document = APIIntegration.parseBookInfo(jsonResponse);

                        Platform.runLater(
                                () -> {
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

    /** Clears all input fields after saving a document. */
    private void clearFields() {
        idField.clear();
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        publishedDatePicker.setValue(null);
    }

    /** Handles the Back button action to close the current stage. */
    @FXML
    private void handleBack() {
        if (stage != null) {
            stage.close();
        }
    }

    /** Shuts down the executor service. */
    public void shutdownExecutor() {
        executorService.shutdownNow();
    }
}
