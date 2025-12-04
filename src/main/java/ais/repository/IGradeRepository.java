package ais.repository;

import ais.model.Grade;
import java.util.List;

public interface IGradeRepository {
  void save(Grade grade);

  void update(Grade grade);

  void delete(Integer id);

  Grade findById(Integer id);

  List<Grade> findAll();

  List<Grade> findByStudent(Integer studentId);

  List<Grade> findByTeacher(Integer teacherId);

  List<Grade> findByCourse(Integer courseId);

  List<Grade> findBySubject(Integer subjectId);
}
