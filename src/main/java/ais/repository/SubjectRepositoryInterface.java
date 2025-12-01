package ais.repository;

import java.util.List;
import ais.model.Subject;

public interface SubjectRepositoryInterface {
  void add(Subject subject);

  boolean remove(int id);

  boolean update(Subject subject);

  Subject findById(int id);

  List<Subject> findAll();
}
