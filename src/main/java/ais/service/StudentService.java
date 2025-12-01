package ais.service;

import ais.repository.StudentRepositoryInterface;
import ais.model.Student;

import java.util.List;

public class StudentService {
  private final StudentRepositoryInterface studentRepo;
  private int nextId = 1;

  public StudentService(StudentRepositoryInterface studentRepo) {
    this.studentRepo = studentRepo;
  }

  public Student createStudent(String name, int age, String address) {
    if (age < 5) {
      throw new IllegalArgumentException("Student is too young.");
    }

    Student s = new Student(nextId++, name, age, address);

    studentRepo.add(s);
    return s;
  }

  public boolean deleteStudent(int id) {
    return studentRepo.remove(id);
  }

  public List<Student> getAllStudents() {
    return studentRepo.findAll();
  }
}
