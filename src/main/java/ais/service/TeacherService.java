package ais.service;

import ais.model.Teacher;
import ais.model.User;
import ais.model.UserRole;
import ais.repository.TeacherRepository;
import ais.repository.UserRepository;

import java.util.List;

public class TeacherService {

  private final TeacherRepository teacherRepository;
  private final UserRepository userRepository;

  public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository) {
    this.teacherRepository = teacherRepository;
    this.userRepository = userRepository;
  }

  public void createTeacher(String name, String address, String specialization) {
    Teacher teacher = new Teacher(name, address, specialization);

    String[] nameParts = name.split(" ");
    String username = nameParts[0].toLowerCase();
    String password = nameParts.length > 1 ? nameParts[nameParts.length - 1].toLowerCase() : username;

    User user = new User(username, password, UserRole.TEACHER);
    teacher.setUser(user);

    teacherRepository.save(teacher);
  }

  public void updateTeacher(Integer id, String name, String address, String specialization) {
    Teacher teacher = teacherRepository.findById(id);
    if (teacher == null) {
      throw new IllegalArgumentException("Teacher not found");
    }

    teacher.setName(name);
    teacher.setAddress(address);
    teacher.setSpecialization(specialization);

    if (teacher.getUser() != null) {
      String[] nameParts = name.split(" ");
      String username = nameParts[0].toLowerCase();
      String password = nameParts.length > 1 ? nameParts[nameParts.length - 1].toLowerCase() : username;

      teacher.getUser().setUsername(username);
      teacher.getUser().setPassword(password);
    }

    teacherRepository.update(teacher);
  }

  public void deleteTeacher(Integer id) {
    teacherRepository.delete(id);
  }

  public List<Teacher> getAllTeachers() {
    return teacherRepository.findAll();
  }

  public Teacher getTeacherById(Integer id) {
    return teacherRepository.findById(id);
  }
}
