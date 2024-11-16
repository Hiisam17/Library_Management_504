package org.example.menubar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIIntegration {

  private static final String API_KEY = "AIzaSyDEdCX7ld8DfmwmDHFhAo0hFHujtUqliYs";
  private static final ExecutorService executor = Executors.newFixedThreadPool(10);

  @FXML
  private TextField isbnField;

  @FXML
  private TextField idField;

  @FXML
  private static TextField titleField;

  @FXML
  private static TextField authorField;

  @FXML
  private static TextField publisherField;

  @FXML
  private static DatePicker publishedDatePicker;

  @FXML
  private Button fetchBookInfoButton;

  // Phương thức để tìm kiếm thông tin sách dựa trên ISBN (không đồng bộ)
  public static Future<String> getBookInfoByISBNAsync(String isbn) {
    return executor.submit(() -> getBookInfoByISBN(isbn));
  }

  // Phương thức để tìm kiếm thông tin sách dựa trên tiêu đề (không đồng bộ)
  public static Future<String> getBookInfoByTitleAsync(String title) {
    return executor.submit(() -> getBookInfoByTitle(title));
  }

  // Phương thức để tìm kiếm thông tin sách dựa trên ISBN
  public static String getBookInfoByISBN(String isbn) {
    String urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + API_KEY;
    return sendGetRequest(urlString);
  }

  // Phương thức để tìm kiếm thông tin sách dựa trên tiêu đề
  public static String getBookInfoByTitle(String title) {
    String urlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&key=" + API_KEY;
    return sendGetRequest(urlString);
  }

  // Phương thức gửi yêu cầu GET đến API và nhận phản hồi
  private static String sendGetRequest(String urlString) {
    StringBuilder result = new StringBuilder();
    try {
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        result.append(inputLine);
      }
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  // Phân tích JSON để lấy thông tin chi tiết sách
  public static Document parseBookInfo(String json) {
    try {
      JSONObject obj = new JSONObject(json);
      JSONArray items = obj.optJSONArray("items");

      if (items != null && items.length() > 0) {
        JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

        // Lấy thông tin từ JSON
        String title = volumeInfo.getString("title");
        String author = volumeInfo.getJSONArray("authors").getString(0);
        String publisher = volumeInfo.optString("publisher", "Không rõ");
        String publishedDate = volumeInfo.optString("publishedDate", ""); // YYYY-MM-DD

        return new Document(title, author, publisher, publishedDate);
      } else {
        System.out.println("Không tìm thấy sách phù hợp.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Lỗi khi phân tích dữ liệu JSON.");
    }
    return null;
  }

}