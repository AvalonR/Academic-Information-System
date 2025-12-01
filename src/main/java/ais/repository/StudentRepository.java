package ais.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import ais.model.Student;

public class StudentRepository implements StudentRepositoryInterface {
  private final Map<Integer, Student> students = new HashMap<>();

  @Override
  public void add(Student student) {
    students.put(student.getId(), student);
  }

  @Override
  public boolean remove(int id) {
    return students.remove(id) != null;
  }

  @Override
  public boolean update(Student student) {
    if (students.containsKey(student.getId())) {
      students.put(student.getId(), student);
      return true;
    }
    return false;
  }

  @Override
  public Student findById(int id) {
    return students.get(id);
  }

  @Override
  public List<Student> findAll() {
    return new ArrayList<>(students.values());
  }
}
