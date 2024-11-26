package org.example.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseService {
  private static final ExecutorService executor = Executors.newFixedThreadPool(10);

  // Method to execute a database query asynchronously
  public static Future<String> executeQueryAsync(String query) {
    return executor.submit(() -> executeQuery(query));
  }

  // Method to execute a database query
  private static String executeQuery(String query) {
    // Simulate a database query
    try {
      Thread.sleep(2000); // Simulate a long-running query
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "Query result for: " + query;
  }
}