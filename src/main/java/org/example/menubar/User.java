package org.example.menubar;

import javafx.stage.Stage;

public abstract class User {
  private String username;
  private String password;
  private String role; // "admin" or "user"

  private String id;
  private String name;
  private String email;
  private int age;

  public  User(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  // Getters and setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getAge() { // Thêm getter cho age
    return age;
  }

  public void setAge(int age) { // Thêm setter cho age
    this.age = age;
  }

  public User(String id, String name, String email, int age) { // Thêm age vào constructor
    this.id = id;
    this.name = name;
    this.email = email;
    this.age = age;
  }
    public abstract void LoginLoad(String UserId, Stage stage);
}


