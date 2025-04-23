package org.example.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DatabaseService is responsible for executing database queries asynchronously.
 * It uses an ExecutorService to manage threads for query execution.
 */
public class DatabaseService {

  // ExecutorService to manage a pool of threads for executing queries
  private static final ExecutorService executor = Executors.newFixedThreadPool(10);

  /**
   * Executes a database query asynchronously in a separate thread.
   *
   * @param query the database query to execute
   * @return a Future object representing the result of the query execution
   */
  public static Future<String> executeQueryAsync(String query) {
    return executor.submit(() -> executeQuery(query));
  }

  /**
   * Simulates executing a database query. In a real-world scenario, this method would interact with a database.
   *
   * @param query the database query to execute
   * @return the result of the query
   */
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
