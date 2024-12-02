package org.example.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.example.model.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * APIIntegration class is responsible for interacting with the Google Books API to fetch book details.
 * It provides methods to fetch book information based on ISBN or title asynchronously.
 */
public class APIIntegration {

  private static final String API_KEY = "AIzaSyDEdCX7ld8DfmwmDHFhAo0hFHujtUqliYs"; // API key for Google Books API

  /**
   * Fetches book information from the Google Books API based on title.
   *
   * @param title The title of the book to search for
   * @return The raw JSON response from the API
   */
  public static String getBookInfoByTitle(String title) {
    String urlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&key=" + API_KEY;
    return sendGetRequest(urlString);
  }

  /**
   * Sends a GET request to the specified URL and returns the response as a String.
   *
   * @param urlString The URL to send the GET request to
   * @return The response body from the API
   */
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

  /**
   * Parses the JSON response from the Google Books API to extract book details.
   *
   * @param json The raw JSON response from the API
   * @return A Document object containing the book's details, or null if parsing fails
   */
  public static Document parseBookInfo(String json) {
    try {
      JSONObject obj = new JSONObject(json);
      JSONArray items = obj.optJSONArray("items");

      if (items != null && items.length() > 0) {
        JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

        // Extracting book details from JSON
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
