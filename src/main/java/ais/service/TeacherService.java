package ais.service;

import ais.repository.TeacherRepositoryInterface;
import ais.model.Teacher;

import java.util.List;

public class TeacherService {
  private final TeacherRepositoryInterface teacherRepo;
  private int nextId = 1;

  public TeacherService(TeacherRepositoryInterface teacherRepo) {
    this.teacherRepo = teacherRepo;
  }

  public Teacher createTeacher(String name, String subject, String address) {
    if (subject.isEmpty()) {
      throw new IllegalArgumentException("Teacher is not assigned to any subject.");
    }

    Teacher t = new Teacher(nextId++, name, subject, address);

    teacherRepo.add(t);
    return t;
  }

  public boolean deleteTeacher(int id) {
    return teacherRepo.remove(id);
  }

  public List<Teacher> getAllTeachers() {
    return teacherRepo.findAll();
  }
}
