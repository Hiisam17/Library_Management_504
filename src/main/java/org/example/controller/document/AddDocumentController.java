package org.example.controller.document;

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

import static org.example.util.DialogUtils.showAlert;

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

        // Lấy dữ liệu từ DatePicker
        String rawDate = publishedDatePicker.getEditor().getText(); // Lấy chuỗi người dùng nhập vào
        String formattedDate;

        try {
            if (rawDate.matches("\\d{4}")) { // Kiểm tra nếu chỉ có năm (yyyy)
                formattedDate = rawDate; // Giữ nguyên
            } else { // Ngày đầy đủ theo dd/MM/yyyy
                LocalDate date = LocalDate.parse(rawDate, inputFormatter); // Chuyển sang LocalDate
                formattedDate = date.format(outputFormatter); // Định dạng lại thành yyyy-MM-dd
            }
            newDocument.setPublishedDate(formattedDate); // Lưu ngày vào Document
        } catch (DateTimeParseException e) {
            showAlert("Lỗi", "Hãy nhập ngày theo định dạng dd/MM/yyyy hoặc yyyy.");
            return; // Dừng việc lưu nếu ngày không hợp lệ
        }

        // Kiểm tra các trường thông tin trước khi lưu
        if (!newDocument.getId().isEmpty() &&
                !newDocument.getTitle().isEmpty() &&
                !newDocument.getAuthor().isEmpty() &&
                !newDocument.getPublishedDate().isEmpty()) {
            try {
                // Use DocumentManager to insert the document
                DocumentManager documentManager = new DocumentManager(new DatabaseManager());
                if (documentManager.insertDocument(
                        newDocument.getId(),
                        newDocument.getTitle(),
                        newDocument.getAuthor(),
                        newDocument.getPublisher(),
                        newDocument.getPublishedDate())) {
                    showAlert("Thành công", "Tài liệu được thêm thành công!");
                }

                // Call the callback to refresh the TableView
                if (onDocumentAddedCallback != null) {
                    onDocumentAddedCallback.run();
                }

                clearFields(); // Clear fields after saving
            } catch (Exception e) {
                showAlert("Lỗi", "Không thể thêm tài liệu.");
                e.printStackTrace();
            }
        } else {
            showAlert("Lỗi", "Hãy điền tất cả các trường.");
        }
    }


    @FXML
    private void handleSearchAPI() {
        try {
            String query = searchField.getText(); // Lấy nội dung từ thanh tìm kiếm
            String jsonResponse = APIIntegration.getBookInfoByTitle(query); // Gửi yêu cầu đến API

            // Gọi parseBookInfo và nhận về đối tượng Document
            Document document = APIIntegration.parseBookInfo(jsonResponse);

            // Kiểm tra kết quả và cập nhật giao diện
            if (document != null) {
                titleField.setText(document.getTitle());
                authorField.setText(document.getAuthor());
                publisherField.setText(document.getPublisher());

                String publishedDate = document.getPublishedDate();
                if (publishedDate == null || publishedDate.isEmpty()) {
                    publishedDate = "9999"; // Gán năm mặc định nếu không có dữ liệu
                }

                try {
                    if (publishedDate.length() == 4) { // Chỉ có năm
                        publishedDatePicker.getEditor().setText(publishedDate); // Hiển thị năm
                    } else if (publishedDate.length() == 7) { // Có năm và tháng (YYYY-MM)
                        publishedDatePicker.getEditor().setText(publishedDate.substring(0, 4));
                    } else { // Định dạng đầy đủ YYYY-MM-DD
                        LocalDate date = LocalDate.parse(publishedDate);
                        publishedDatePicker.setValue(date);
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Lỗi khi phân tích ngày xuất bản: " + e.getMessage());
                }
            } else {
                System.out.println("Không có dữ liệu để cập nhật.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi trong quá trình tìm kiếm.");
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


    // Xử lý sự kiện nút Quay lại
    @FXML
    private void handleBack() {
        // Đóng cửa sổ thêm tài liệu
        if (stage != null) {
            stage.close(); // Đóng cửa sổ hiện tại
        }
    }
}

