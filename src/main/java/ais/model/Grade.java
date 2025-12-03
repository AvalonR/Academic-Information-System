package ais.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "grades")
public class Grade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne
  @JoinColumn(name = "subject_id", nullable = false)
  private Subject subject;

  @ManyToOne
  @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;

  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Column(nullable = false)
  private Double gradeValue;

  @Column(nullable = true)
  private String letterGrade;

  @Column(nullable = true)
  private String comments;

  @Column(nullable = false)
  private LocalDate dateGiven;

  public Grade() {
    this.dateGiven = LocalDate.now();
  }

  public Grade(Student student, Subject subject, Teacher teacher, Course course,
      Double gradeValue, String letterGrade, String comments) {
    this.student = student;
    this.subject = subject;
    this.teacher = teacher;
    this.course = course;
    this.gradeValue = gradeValue;
    this.letterGrade = letterGrade;
    this.comments = comments;
    this.dateGiven = LocalDate.now();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public void setTeacher(Teacher teacher) {
    this.teacher = teacher;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Double getGradeValue() {
    return gradeValue;
  }

  public void setGradeValue(Double gradeValue) {
    this.gradeValue = gradeValue;
  }

  public String getLetterGrade() {
    return letterGrade;
  }

  public void setLetterGrade(String letterGrade) {
    this.letterGrade = letterGrade;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public LocalDate getDateGiven() {
    return dateGiven;
  }

  public void setDateGiven(LocalDate dateGiven) {
    this.dateGiven = dateGiven;
  }

  @Override
  public String toString() {
    return student.getName() + " - " + subject.getName() + ": " +
        gradeValue + (letterGrade != null ? " (" + letterGrade + ")" : "");
  }
}
