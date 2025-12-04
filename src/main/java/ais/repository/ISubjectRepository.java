package ais.repository;

import ais.model.Subject;
import java.util.List;

public interface ISubjectRepository {
  void save(Subject subject);

  void update(Subject subject);

  void delete(Integer id);

  Subject findById(Integer id);

  List<Subject> findAll();

  void assignTeacherToSubject(Integer subjectId, Integer teacherId);

  void removeTeacherFromSubject(Integer subjectId, Integer teacherId);
}
