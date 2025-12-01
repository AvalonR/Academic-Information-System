package ais.repository;

import java.util.List;
import ais.model.Teacher;

public interface TeacherRepositoryInterface {
  void add(Teacher teacher);

  boolean remove(int id);

  boolean update(Teacher teacher);

  Teacher findById(int id);

  List<Teacher> findAll();
}
