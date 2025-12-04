package ais.repository;

import ais.model.GradeComponent;
import java.util.List;

public interface IGradeComponentRepository {
  void save(GradeComponent component);

  void update(GradeComponent component);

  void delete(Integer id);

  GradeComponent findById(Integer id);

  List<GradeComponent> findAll();

  List<GradeComponent> findByCourseAndSubject(Integer courseId, Integer subjectId);
}
