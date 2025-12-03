package ais.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects")
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String language;

  @Column(nullable = true)
  private String room;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "subject_teachers", joinColumns = @JoinColumn(name = "subject_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
  private Set<Teacher> teachers = new HashSet<>();

  public Subject() {
  }

  public Subject(String name, String language, String room) {
    this.name = name;
    this.language = language;
    this.room = room;
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

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public Set<Teacher> getTeachers() {
    return teachers;
  }

  public void setTeachers(Set<Teacher> teachers) {
    this.teachers = teachers;
  }

  public void addTeacher(Teacher teacher) {
    this.teachers.add(teacher);
  }

  public void removeTeacher(Teacher teacher) {
    this.teachers.remove(teacher);
  }

  @Override
  public String toString() {
    return id + ": " + name + " (" + language + ")" +
        (room != null && !room.isEmpty() ? " - Room " + room : "");
  }
}
