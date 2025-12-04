package ais.repository;

import ais.model.Student;
import java.util.List;

public interface IStudentRepository {
  void save(Student student);

  void update(Student student);

  void delete(Integer id);

  Student findById(Integer id);

  List<Student> findAll();
}
