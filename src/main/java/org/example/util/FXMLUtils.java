package org.example.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXMLUtils {

    /**
     * Tải một tệp FXML và trả về gốc giao diện (Parent).
     *
     * @param fxmlPath Đường dẫn đến tệp FXML (tương đối từ `resources`).
     * @return Parent là gốc của giao diện được định nghĩa trong FXML.
     * @throws IOException Nếu không thể tải được tệp FXML.
     */
    public static Parent loadFXML(String fxmlPath) throws IOException {
        URL resource = FXMLUtils.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("Không tìm thấy tệp FXML: " + fxmlPath);
        }
        return FXMLLoader.load(resource);
    }

    /**
     * Mở một cửa sổ (Stage) mới với giao diện được định nghĩa trong tệp FXML.
     *
     * @param fxmlPath Đường dẫn đến tệp FXML (tương đối từ `resources`).
     * @param title    Tiêu đề của cửa sổ mới.
     * @param owner    Cửa sổ cha (Stage) hiện tại, có thể null nếu không có cha.
     * @param cssPath  Đường dẫn đến tệp CSS (tương đối từ `resources`), có thể null.
     * @throws IOException Nếu không thể tải tệp FXML hoặc CSS.
     */
    public static void openWindow(String fxmlPath, String title, Stage owner, String cssPath) throws IOException {
        Parent root = loadFXML(fxmlPath);

        // Tạo Scene từ giao diện đã tải
        Scene scene = new Scene(root);

        // Nạp stylesheet (CSS) nếu được cung cấp
        if (cssPath != null) {
            URL cssURL = FXMLUtils.class.getResource(cssPath);
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
            } else {
                System.out.println("Không tìm thấy tệp CSS, tiếp tục mà không có CSS.");
            }
        }

        // Tạo một cửa sổ (Stage) mới
        Stage stage = new Stage();
        stage.setTitle(title);         // Đặt tiêu đề cho cửa sổ
        stage.setScene(scene);         // Đặt Scene
        stage.initOwner(owner);        // Đặt cửa sổ cha (nếu có)
        stage.initModality(Modality.APPLICATION_MODAL); // Đặt chế độ modal (chặn tương tác với các cửa sổ khác)
        stage.showAndWait();           // Hiển thị cửa sổ và chờ nó đóng
    }

}

