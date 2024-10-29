package org.example.menubar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
  private List<User> users;

  public UserService() {
    users = new ArrayList<>();
    // Add default users for testing
    users.add(new User("admin", "password", "admin"));
    users.add(new User("user", "password", "user"));
  }

  public Optional<User> authenticate(String username, String password) {
    return users.stream()
            .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
            .findFirst();
  }

  public void addUser(User user) {
    users.add(user);
  }
}