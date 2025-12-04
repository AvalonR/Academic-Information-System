package ais.repository;

import ais.model.Teacher;
import java.util.List;

public interface ITeacherRepository {
  void save(Teacher teacher);

  void update(Teacher teacher);

  void delete(Integer id);

  Teacher findById(Integer id);

  List<Teacher> findAll();
}
