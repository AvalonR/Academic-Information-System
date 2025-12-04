package ais.service;

import ais.model.Course;
import ais.model.Student;
import ais.model.Subject;
import ais.repository.ICourseRepository;
import ais.repository.IStudentRepository;
import ais.repository.ISubjectRepository;

import java.util.List;
import java.util.Set;

public class CourseService {

  private final ICourseRepository courseRepository;
  private final ISubjectRepository subjectRepository;
  private final IStudentRepository studentRepository;

  public CourseService(ICourseRepository courseRepository,
      ISubjectRepository subjectRepository,
      IStudentRepository studentRepository) {
    this.courseRepository = courseRepository;
    this.subjectRepository = subjectRepository;
    this.studentRepository = studentRepository;
  }

  public void createCourse(String name, String description, Integer year, String semester) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Course name is required");
    }

    Course course = new Course(name, description, year, semester);
    courseRepository.save(course);
  }

  public void updateCourse(Integer id, String name, String description, Integer year, String semester) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Course name is required");
    }

    Course course = courseRepository.findById(id);
    if (course == null) {
      throw new IllegalArgumentException("Course not found");
    }

    course.setName(name);
    course.setDescription(description);
    course.setYear(year);
    course.setSemester(semester);

    courseRepository.update(course);
  }

  public void deleteCourse(Integer id) {
    courseRepository.delete(id);
  }

  public void addSubject(Integer courseId, Integer subjectId) {
    Course course = courseRepository.findById(courseId);
    Subject subject = subjectRepository.findById(subjectId);

    if (course == null) {
      throw new IllegalArgumentException("Course not found");
    }
    if (subject == null) {
      throw new IllegalArgumentException("Subject not found");
    }

    if (course.getSubjects().stream().anyMatch(s -> s.getId().equals(subjectId))) {
      throw new IllegalArgumentException("Subject is already added to this course");
    }

    courseRepository.addSubjectToCourse(courseId, subjectId);
  }

  public void removeSubject(Integer courseId, Integer subjectId) {
    courseRepository.removeSubjectFromCourse(courseId, subjectId);
  }

  public void enrollStudent(Integer courseId, Integer studentId) {
    Course course = courseRepository.findById(courseId);
    Student student = studentRepository.findById(studentId);

    if (course == null) {
      throw new IllegalArgumentException("Course not found");
    }
    if (student == null) {
      throw new IllegalArgumentException("Student not found");
    }

    if (course.getStudents().stream().anyMatch(s -> s.getId().equals(studentId))) {
      throw new IllegalArgumentException("Student is already enrolled in this course");
    }

    courseRepository.enrollStudent(courseId, studentId);
  }

  public void unenrollStudent(Integer courseId, Integer studentId) {
    courseRepository.unenrollStudent(courseId, studentId);
  }

  public List<Course> getAllCourses() {
    return courseRepository.findAll();
  }

  public Course getCourseById(Integer id) {
    return courseRepository.findById(id);
  }

  public Set<Subject> getSubjectsForCourse(Integer courseId) {
    Course course = courseRepository.findById(courseId);
    return course != null ? course.getSubjects() : Set.of();
  }

  public Set<Student> getStudentsForCourse(Integer courseId) {
    Course course = courseRepository.findById(courseId);
    return course != null ? course.getStudents() : Set.of();
  }
}
