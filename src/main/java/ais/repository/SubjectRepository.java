package ais.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import ais.model.Subject;

public class SubjectRepository implements SubjectRepositoryInterface {
  private final Map<Integer, Subject> subjects = new HashMap<>();

  @Override
  public void add(Subject subject) {
    subjects.put(subject.getId(), subject);
  }

  @Override
  public boolean remove(int id) {
    return subjects.remove(id) != null;
  }

  @Override
  public boolean update(Subject subject) {
    if (subjects.containsKey(subject.getId())) {
      subjects.put(subject.getId(), subject);
      return true;
    }
    return false;
  }

  @Override
  public Subject findById(int id) {
    return subjects.get(id);
  }

  @Override
  public List<Subject> findAll() {
    return new ArrayList<>(subjects.values());
  }
}
