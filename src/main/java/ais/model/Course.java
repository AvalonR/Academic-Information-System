package ais.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
  private int id;
  private String name;
  private String description;
  private List<Subject> subjects = new ArrayList<>();
  private List<Student> students = new ArrayList<>();

  public Course(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Course{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        '}';
  }

  public int getId() {
    return id;
  }

  public List<Subject> getSubjects() {
    return subjects;
  }

  public void setSubjects(List<Subject> subjects) {
    this.subjects = subjects;
    for (Subject s : subjects) {
      // s.setCourse(this);
    }
    this.subjects = subjects;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
    for (Student s : students) {
      s.setCourse(this);
      // s.setSubjects(new ArrayList<>());
    }
    this.students = students;
  }
}
