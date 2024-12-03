package org.example.model;

import javafx.stage.Stage;

/**
 * Abstract class representing a user.
 * Contains shared properties and methods for different user roles such as Admin and Regular User.
 */
public abstract class User {

  private String username;
  private String password;
  private String role; // "admin" or "user"
  private String id;
  private String name;
  private String email;
  private int age;

  /**
   * Constructs a User with the specified username, password, and role.
   *
   * @param username the username of the user.
   * @param password the password of the user.
   * @param role     the role of the user ("admin" or "user").
   */
  public User(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  /**
   * Constructs a User with the specified ID, name, email, and age.
   *
   * @param id    the ID of the user.
   * @param name  the name of the user.
   * @param email the email of the user.
   * @param age   the age of the user.
   */
  public User(String id, String name, String email, int age) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.age = age;
  }

  /**
   * Gets the username of the user.
   *
   * @return the username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username the username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the password of the user.
   *
   * @return the password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the user.
   *
   * @param password the password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the role of the user.
   *
   * @return the role ("admin" or "user").
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the role of the user.
   *
   * @param role the role to set ("admin" or "user").
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Gets the ID of the user.
   *
   * @return the ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the ID of the user.
   *
   * @param id the ID to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name of the user.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the user.
   *
   * @param name the name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the email of the user.
   *
   * @return the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email of the user.
   *
   * @param email the email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the age of the user.
   *
   * @return the age.
   */
  public int getAge() {
    return age;
  }

  /**
   * Sets the age of the user.
   *
   * @param age the age to set.
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Abstract method to load the user's login view based on their role.
   * Must be implemented by subclasses.
   *
   * @param userId the ID of the user.
   * @param stage  the stage to display the login view.
   */
  public abstract void LoginLoad(String userId, Stage stage);
}
