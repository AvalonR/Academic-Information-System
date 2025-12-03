package ais.service;

import ais.model.*;
import ais.repository.*;

import java.util.List;

public class GradeService {

  private final GradeRepository gradeRepository;
  private final StudentRepository studentRepository;
  private final SubjectRepository subjectRepository;
  private final TeacherRepository teacherRepository;
  private final CourseRepository courseRepository;

  public GradeService(GradeRepository gradeRepository,
      StudentRepository studentRepository,
      SubjectRepository subjectRepository,
      TeacherRepository teacherRepository,
      CourseRepository courseRepository) {
    this.gradeRepository = gradeRepository;
    this.studentRepository = studentRepository;
    this.subjectRepository = subjectRepository;
    this.teacherRepository = teacherRepository;
    this.courseRepository = courseRepository;
  }

  public void createGrade(Integer studentId, Integer subjectId, Integer teacherId,
      Integer courseId, Double gradeValue, String letterGrade, String comments) {
    Student student = studentRepository.findById(studentId);
    Subject subject = subjectRepository.findById(subjectId);
    Teacher teacher = teacherRepository.findById(teacherId);
    Course course = courseRepository.findById(courseId);

    if (student == null)
      throw new IllegalArgumentException("Student not found");
    if (subject == null)
      throw new IllegalArgumentException("Subject not found");
    if (teacher == null)
      throw new IllegalArgumentException("Teacher not found");
    if (course == null)
      throw new IllegalArgumentException("Course not found");

    if (gradeValue < 0 || gradeValue > 10) {
      throw new IllegalArgumentException("Grade must be between 0 and 10");
    }

    Grade grade = new Grade(student, subject, teacher, course, gradeValue, letterGrade, comments);
    gradeRepository.save(grade);
  }

  public void updateGrade(Integer gradeId, Double gradeValue, String letterGrade, String comments) {
    Grade grade = gradeRepository.findById(gradeId);
    if (grade == null) {
      throw new IllegalArgumentException("Grade not found");
    }

    if (gradeValue < 0 || gradeValue > 10) {
      throw new IllegalArgumentException("Grade must be between 0 and 10");
    }

    grade.setGradeValue(gradeValue);
    grade.setLetterGrade(letterGrade);
    grade.setComments(comments);

    gradeRepository.update(grade);
  }

  public void deleteGrade(Integer gradeId) {
    gradeRepository.delete(gradeId);
  }

  public List<Grade> getAllGrades() {
    return gradeRepository.findAll();
  }

  public List<Grade> getGradesByStudent(Integer studentId) {
    return gradeRepository.findByStudent(studentId);
  }

  public List<Grade> getGradesByTeacher(Integer teacherId) {
    return gradeRepository.findByTeacher(teacherId);
  }

  public List<Grade> getGradesByCourse(Integer courseId) {
    return gradeRepository.findByCourse(courseId);
  }

  public List<Grade> getGradesBySubject(Integer subjectId) {
    return gradeRepository.findBySubject(subjectId);
  }

  public Grade getGradeById(Integer gradeId) {
    return gradeRepository.findById(gradeId);
  }

  public static String calculateLetterGrade(double gradeValue) {
    if (gradeValue >= 9.0)
      return "A";
    if (gradeValue >= 8.0)
      return "B";
    if (gradeValue >= 7.0)
      return "C";
    if (gradeValue >= 6.0)
      return "D";
    if (gradeValue >= 5.0)
      return "E";
    return "F";
  }
}
