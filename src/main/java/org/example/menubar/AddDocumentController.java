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

import static org.example.menubar.SQLlib.SQL_connect;

public class AddDocumentController {



    // Tham chiếu đến Stage
    private Stage stage;
    // input and output for date
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
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
        Document newDocument;
        newDocument = new Document();
        newDocument.setTitle(titleField.getText());
        newDocument.setAuthor(authorField.getText());
        newDocument.setPublisher(publisherField.getText());

        // Chuyển đổi ngày thành định dạng yyyy-MM-dd
        LocalDate publishedDate = publishedDatePicker.getValue();
        String formattedDate = publishedDate.format(outputFormatter);
        newDocument.setPublishedDate(formattedDate);
        String sql = "INSERT INTO document (title, author ,publisher, publishedDate) VALUES (?, ?, ?, ?)";


        if (!newDocument.getTitle().isEmpty() && !newDocument.getAuthor().isEmpty() && !newDocument.getPublishedDate().isEmpty()) {
            try (Connection conn = SQL_connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newDocument.getTitle());
                pstmt.setString(2, newDocument.getAuthor());
                pstmt.setString(3, newDocument.getPublisher());
                pstmt.setString(4, newDocument.getPublishedDate());

                // Thực thi câu lệnh
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Thành công", "Đã thêm tài liệu mới!");
                    clearFields(); // Xóa dữ liệu sau khi lưu
                }
            } catch (DateTimeParseException e) {
                showAlert("Lỗi", "Nhập sai định dạng ngày. Vui lòng nhập đúng định dạng dd/MM/yyyy.");
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể thêm tài liệu vào cơ sở dữ liệu.");
                e.printStackTrace();
            }
        } else {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
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
