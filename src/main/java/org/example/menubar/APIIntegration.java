package org.example.menubar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIIntegration {

  private static final String API_KEY = "AIzaSyDEdCX7ld8DfmwmDHFhAo0hFHujtUqliYs";

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
  public static void parseBookInfo(String json) {
    JSONObject obj = new JSONObject(json);
    JSONArray items = obj.getJSONArray("items");
    if (items.length() > 0) {
      JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

      // Lấy thông tin từ JSON
      String title = volumeInfo.getString("title");
      String author = volumeInfo.getJSONArray("authors").getString(0);
      String publisher = volumeInfo.optString("publisher", "Unknown");
      String publishedDate = volumeInfo.optString("publishedDate", "Unknown");

      // In thông tin sách
      System.out.println("Title: " + title);
      System.out.println("Author: " + author);
      System.out.println("Publisher: " + publisher);
      System.out.println("Published Date: " + publishedDate);
    } else {
      System.out.println("No book found for the given search.");
    }
  }
}