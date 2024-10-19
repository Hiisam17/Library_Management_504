package org.example.menubar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddDocumentController {

    // Khai báo danh sách tài liệu
    private ObservableList<Document> documents;

    {
        documents = FXCollections.observableArrayList();
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
    private DatePicker publishedDatePicker;
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
        String title = titleField.getText();
        String author = authorField.getText();
        LocalDate publishedDate = publishedDatePicker.getValue();

        // Chuyển đổi ngày thành định dạng yyyy-MM-dd
        String formattedDate = publishedDate.format(outputFormatter);
        if ( title.isEmpty() || author.isEmpty() || publishedDate == null) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try {
            // Tạo đối tượng Document mới và thêm vào danh sách
            Document newDocument = new Document( title, author, formattedDate);
            documents.add(newDocument); // Thêm tài liệu vào danh sách
            showAlert("Thành công", "Đã thêm tài liệu mới!");
            clearFields(); // Xóa dữ liệu sau khi lưu
        } catch (DateTimeParseException e) {
            // Nếu sai định dạng, hiển thị Alert lỗi
            showAlert("Lỗi", "Nhập sai định dạng ngày. Vui lòng nhập đúng định dạng dd/MM/yyyy.");
        }
    }

    // Hàm khởi tạo để nhận tham chiếu Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Hàm xóa dữ liệu sau khi lưu
    private void clearFields() {
        titleField.clear();
        authorField.clear();
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
