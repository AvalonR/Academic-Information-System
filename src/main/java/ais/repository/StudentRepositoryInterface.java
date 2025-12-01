package ais.repository;

import java.util.List;
import ais.model.Student;

public interface StudentRepositoryInterface {
  void add(Student student);

  boolean remove(int id);

  boolean update(Student student);

  Student findById(int id);

  List<Student> findAll();
}
