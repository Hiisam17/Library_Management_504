package org.example.menubar;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

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
        document.setTitle(titleField.getText());
        document.setAuthor(authorField.getText());
        document.setPublisher(publisherField.getText());
        document.setPublishedDate(publishedDatePicker.getValue().toString());

        documentManager.updateDocument(document); // Cập nhật tài liệu trong cơ sở dữ liệu

        if (onDocumentEdited != null) {
            onDocumentEdited.run();
        }

        stage.close();
    }
}

