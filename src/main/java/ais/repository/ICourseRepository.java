package ais.repository;

import ais.model.Course;
import java.util.List;

public interface ICourseRepository {
  void save(Course course);

  void update(Course course);

  void delete(Integer id);

  Course findById(Integer id);

  List<Course> findAll();

  void addSubjectToCourse(Integer courseId, Integer subjectId);

  void removeSubjectFromCourse(Integer courseId, Integer subjectId);

  void enrollStudent(Integer courseId, Integer studentId);

  void unenrollStudent(Integer courseId, Integer studentId);
}
