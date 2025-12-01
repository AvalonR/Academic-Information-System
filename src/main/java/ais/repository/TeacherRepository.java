package ais.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import ais.model.Teacher;

public class TeacherRepository implements TeacherRepositoryInterface {
  private final Map<Integer, Teacher> teachers = new HashMap<>();

  @Override
  public void add(Teacher teacher) {
    teachers.put(teacher.getId(), teacher);
  }

  @Override
  public boolean remove(int id) {
    return teachers.remove(id) != null;
  }

  @Override
  public boolean update(Teacher teacher) {
    if (teachers.containsKey(teacher.getId())) {
      teachers.put(teacher.getId(), teacher);
      return true;
    }
    return false;
  }

  @Override
  public Teacher findById(int id) {
    return teachers.get(id);
  }

  @Override
  public List<Teacher> findAll() {
    return new ArrayList<>(teachers.values());
  }
}
