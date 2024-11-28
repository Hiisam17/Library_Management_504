package org.example.controller.document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.model.Document;
import org.example.service.DocumentManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.util.DialogUtils.showAlert;
import static org.example.util.DialogUtils.showConfirmation;

public class EditDocumentController {

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
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Stage stage;
    private Document document;
    private DocumentManager documentManager;
    private Runnable onDocumentEdited;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public void setOnDocumentEdited(Runnable onDocumentEdited) {
        this.onDocumentEdited = onDocumentEdited;
    }

    public void setDocument(Document document) {
        this.document = document;

        // Cập nhật các trường giao diện
        idField.setText(document.getId());
        idField.setEditable(false); // Không cho phép chỉnh sửa ID
        titleField.setText(document.getTitle());
        authorField.setText(document.getAuthor());
        publisherField.setText(document.getPublisher());

        // Xử lý ngày xuất bản
        String rawDate = document.getPublishedDate();

        if (rawDate != null && !rawDate.isEmpty()) {
            try {
                if (rawDate.matches("\\d{4}")) { // Nếu chỉ có năm
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

    public void initialize() {
        // Cài đặt converter để hiển thị đúng định dạng dd/MM/yyyy
        publishedDatePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(inputFormatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null; // Nếu chuỗi trống, trả về null
                }
                try {
                    if (string.matches("\\d{4}")) { // Nếu chỉ nhập năm
                        return LocalDate.of(Integer.parseInt(string), 1, 1);
                    } else {
                        return LocalDate.parse(string, inputFormatter); // dd/MM/yyyy
                    }
                } catch (DateTimeParseException e) {
                    showAlert("Lỗi", "Ngày không hợp lệ. Vui lòng nhập ngày theo định dạng dd/MM/yyyy hoặc yyyy.");
                    return null;
                }
            }
        });
    }
    @FXML
    private void handleSaveChanges() {
        if (showConfirmation("Xác nhận", "Bạn có chắc chắn muốn tiếp tục?")) {
            // Tiến hành lưu thay đổi trong một Task (tạo luồng riêng biệt)
            executorService.submit(() -> {
                // Cập nhật thông tin tài liệu
                document.setTitle(titleField.getText());
                document.setAuthor(authorField.getText());
                document.setPublisher(publisherField.getText());

                String rawDate = publishedDatePicker.getEditor().getText();
                String formattedDate;

                try {
                    if (rawDate.matches("\\d{4}")) { // Chỉ có năm
                        formattedDate = rawDate;
                    } else {
                        LocalDate date = LocalDate.parse(rawDate, inputFormatter);
                        formattedDate = date.format(outputFormatter); // yyyy-MM-dd
                    }
                    document.setPublishedDate(formattedDate); // Lưu ngày vào Document
                } catch (DateTimeParseException e) {
                    showAlert("Lỗi", "Ngày không hợp lệ. Vui lòng kiểm tra và nhập lại.");
                    return; // Dừng việc lưu nếu ngày không hợp lệ
                }

                documentManager.updateDocument(document);

                // Cập nhật giao diện khi hoàn thành công việc (trên UI thread)
                Platform.runLater(() -> {
                    if (onDocumentEdited != null) {
                        onDocumentEdited.run();
                    }
                    stage.close();
                });
            });
        } else {
            showAlert("Hủy thay đổi", "Thay đổi của bạn không được lưu.");
        }
    }
}



