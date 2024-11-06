package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

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

    private Stage stage;
    private Document document;
    private DocumentManager documentManager;
    private Runnable onDocumentEdited;

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
        idField.setText(document.getId());
        idField.setEditable(false); // Không cho phép chỉnh sửa ID
        titleField.setText(document.getTitle());
        authorField.setText(document.getAuthor());
        publisherField.setText(document.getPublisher());
        publishedDatePicker.setValue(LocalDate.parse(document.getPublishedDate()));
    }

    @FXML
    private void handleSaveChanges() {
        // Tạo một hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận sửa đổi");
        alert.setHeaderText("Bạn có chắc muốn lưu các thay đổi?");
        alert.setContentText("Các thay đổi sẽ được áp dụng cho tài liệu này.");

        // Hiển thị hộp thoại và chờ người dùng phản hồi
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Nếu người dùng chọn OK, tiến hành lưu thay đổi
            document.setTitle(titleField.getText());
            document.setAuthor(authorField.getText());
            document.setPublisher(publisherField.getText());
            document.setPublishedDate(publishedDatePicker.getValue().toString());

            documentManager.updateDocument(document); // Cập nhật tài liệu trong cơ sở dữ liệu

            if (onDocumentEdited != null) {
                onDocumentEdited.run();
            }

            stage.close();
        } else {
            // Nếu người dùng chọn Cancel hoặc đóng hộp thoại, không thực hiện thay đổi
            Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
            cancelAlert.setTitle("Hủy thay đổi");
            cancelAlert.setHeaderText(null);
            cancelAlert.setContentText("Các thay đổi không được lưu.");
            cancelAlert.showAndWait();
        }
    }

}

