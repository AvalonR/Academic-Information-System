package ais.service;

import ais.model.*;
import ais.repository.*;

import java.util.stream.Collectors;
import java.util.List;

public class GradeService {

  private final IGradeRepository gradeRepository;
  private final IStudentRepository studentRepository;
  private final ISubjectRepository subjectRepository;
  private final ITeacherRepository teacherRepository;
  private final ICourseRepository courseRepository;
  private final IGradeComponentRepository gradeComponentRepository;

  public GradeService(IGradeRepository gradeRepository,
      IStudentRepository studentRepository,
      ISubjectRepository subjectRepository,
      ITeacherRepository teacherRepository,
      ICourseRepository courseRepository,
      IGradeComponentRepository gradeComponentRepository) {
    this.gradeRepository = gradeRepository;
    this.studentRepository = studentRepository;
    this.subjectRepository = subjectRepository;
    this.teacherRepository = teacherRepository;
    this.courseRepository = courseRepository;
    this.gradeComponentRepository = gradeComponentRepository;
  }

  public void createGrade(Integer studentId, Integer subjectId, Integer teacherId,
      Integer courseId, Integer componentId, Double gradeValue, String comments) {
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

    boolean studentEnrolled = course.getStudents().stream()
        .anyMatch(s -> s.getId().equals(studentId));

    if (!studentEnrolled) {
      throw new IllegalArgumentException(
          String.format("Student '%s' is not enrolled in course '%s'",
              student.getName(), course.getName()));
    }

    boolean subjectInCourse = course.getSubjects().stream()
        .anyMatch(s -> s.getId().equals(subjectId));

    if (!subjectInCourse) {
      throw new IllegalArgumentException(
          String.format("Subject '%s' is not part of course '%s'",
              subject.getName(), course.getName()));
    }

    boolean teacherAssigned = subject.getTeachers().stream()
        .anyMatch(t -> t.getId().equals(teacherId));

    if (!teacherAssigned) {
      throw new IllegalArgumentException(
          String.format("Teacher '%s' is not assigned to teach '%s'",
              teacher.getName(), subject.getName()));
    }

    if (gradeValue < 0 || gradeValue > 10) {
      throw new IllegalArgumentException("Grade must be between 0 and 10");
    }

    GradeComponent component = null;
    if (componentId != null) {
      component = gradeComponentRepository.findById(componentId);
      if (component == null) {
        throw new IllegalArgumentException("Grade component not found");
      }

      if (!component.getCourse().getId().equals(courseId) ||
          !component.getSubject().getId().equals(subjectId)) {
        throw new IllegalArgumentException(
            String.format("Component '%s' does not belong to '%s' in '%s'",
                component.getName(), subject.getName(), course.getName()));
      }
    }

    Grade grade = new Grade(student, subject, teacher, course, component, gradeValue, comments);
    gradeRepository.save(grade);
  }

  public void updateGrade(Integer gradeId, Double gradeValue, String comments) {
    Grade grade = gradeRepository.findById(gradeId);
    if (grade == null) {
      throw new IllegalArgumentException("Grade not found");
    }

    if (gradeValue < 0 || gradeValue > 10) {
      throw new IllegalArgumentException("Grade must be between 0 and 10");
    }

    grade.setGradeValue(gradeValue);
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

  public static String getPassFailStatus(double gradeValue) {
    return gradeValue >= 5.0 ? "PASS" : "FAIL";
  }

  public Double calculateFinalGrade(Integer studentId, Integer courseId, Integer subjectId) {
    List<Grade> grades = gradeRepository.findAll().stream()
        .filter(g -> g.getStudent().getId().equals(studentId) &&
            g.getCourse().getId().equals(courseId) &&
            g.getSubject().getId().equals(subjectId))
        .toList();

    if (grades.isEmpty())
      return null;

    double weightedSum = 0.0;
    double totalWeight = 0.0;

    for (Grade grade : grades) {
      if (grade.getComponent() != null) {
        weightedSum += grade.getGradeValue() * grade.getComponent().getWeight();
        totalWeight += grade.getComponent().getWeight();
      }
    }

    if (totalWeight == 0.0) {
      return grades.stream().mapToDouble(Grade::getGradeValue).average().orElse(0.0);
    }

    return weightedSum;
  }

  public List<Grade> getGradesByStudentCourseSubject(Integer studentId, Integer courseId, Integer subjectId) {
    return gradeRepository.findAll().stream()
        .filter(g -> g.getStudent().getId().equals(studentId) &&
            g.getCourse().getId().equals(courseId) &&
            g.getSubject().getId().equals(subjectId))
        .collect(Collectors.toList());
  }
}
