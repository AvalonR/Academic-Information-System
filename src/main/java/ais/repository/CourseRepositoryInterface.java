package ais.repository;

import ais.model.Course;
import java.util.List;

public interface CourseRepositoryInterface {
  Course save(Course course);

  Course findById(int id);

  List<Course> findAll();

  void delete(int id);
}
