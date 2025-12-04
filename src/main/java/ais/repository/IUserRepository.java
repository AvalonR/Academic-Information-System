package ais.repository;

import ais.model.User;
import java.util.List;

public interface IUserRepository {
  void save(User user);

  void update(User user);

  void delete(Integer id);

  User findById(Integer id);

  User findByUsername(String username);

  List<User> findAll();
}
