package ais.service;

import ais.model.Student;
import ais.model.User;
import ais.model.UserRole;
import ais.repository.StudentRepository;
import ais.repository.UserRepository;

import java.util.List;

public class StudentService {

  private final StudentRepository studentRepository;
  private final UserRepository userRepository;

  public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
  }

  public void createStudent(String name, Integer age, String address) {
    if (age < 5) {
      throw new IllegalArgumentException("Student must be at least 5 years old");
    }

    Student student = new Student(name, age, address);

    String[] nameParts = name.split(" ");
    String username = nameParts[0].toLowerCase();
    String password = nameParts.length > 1 ? nameParts[nameParts.length - 1].toLowerCase() : username;

    User user = new User(username, password, UserRole.STUDENT);
    student.setUser(user);

    studentRepository.save(student);
  }

  public void updateStudent(Integer id, String name, Integer age, String address) {
    if (age < 5) {
      throw new IllegalArgumentException("Student must be at least 5 years old");
    }

    Student student = studentRepository.findById(id);
    if (student == null) {
      throw new IllegalArgumentException("Student not found");
    }

    student.setName(name);
    student.setAge(age);
    student.setAddress(address);

    if (student.getUser() != null) {
      String[] nameParts = name.split(" ");
      String username = nameParts[0].toLowerCase();
      String password = nameParts.length > 1 ? nameParts[nameParts.length - 1].toLowerCase() : username;

      student.getUser().setUsername(username);
      student.getUser().setPassword(password);
    }

    studentRepository.update(student);
  }

  public void deleteStudent(Integer id) {
    studentRepository.delete(id);
  }

  public List<Student> getAllStudents() {
    return studentRepository.findAll();
  }

  public Student getStudentById(Integer id) {
    return studentRepository.findById(id);
  }
}
