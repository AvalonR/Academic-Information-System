package ais.service;

import ais.model.User;
import ais.repository.IUserRepository;

public class AuthService {

  private final IUserRepository userRepository;
  private User currentUser;

  public AuthService(IUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean login(String username, String password) {
    User user = userRepository.findByUsername(username);

    if (user != null && user.getPassword().equals(password)) {
      currentUser = user;
      return true;
    }

    return false;
  }

  public void logout() {
    currentUser = null;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public boolean isLoggedIn() {
    return currentUser != null;
  }

  public boolean isAdmin() {
    return currentUser != null &&
        currentUser.getRole() == ais.model.UserRole.ADMIN;
  }

  public boolean isTeacher() {
    return currentUser != null &&
        currentUser.getRole() == ais.model.UserRole.TEACHER;
  }

  public boolean isStudent() {
    return currentUser != null &&
        currentUser.getRole() == ais.model.UserRole.STUDENT;
  }
}
