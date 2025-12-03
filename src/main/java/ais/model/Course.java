package ais.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "subject_id")
  private Subject subject;

  @ManyToMany
  @JoinTable(name = "course_students", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
  private List<Student> students = new ArrayList<>();

  public Course() {
  }

  public Course(String name, Subject subject) {
    this.name = name;
    this.subject = subject;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

  public void addStudent(Student s) {
    if (!students.contains(s)) {
      students.add(s);
    }
  }

  public void removeStudent(Student s) {
    students.remove(s);
  }

  @Override
  public String toString() {
    return name + " (" + (subject != null ? subject.getName() : "No Subject") + ")";
  }
}
