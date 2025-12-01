package ais.model;

import java.util.ArrayList;

public class Teacher {
  private int id;
  private String name;
  private String subject;
  private String address;
  private ArrayList<Subject> subjects = new ArrayList<>();

  public Teacher(int id, String name, String subject, String address) {
    this.id = id;
    this.name = name;
    this.subject = subject;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "Teacher{" +
        "name='" + name + '\'' +
        ", subject='" + subject + '\'' +
        ", address='" + address + '\'' +
        '}';
  }

  public int getId() {
    return id;
  }

  public ArrayList<Subject> getSubjects() {
    return subjects;
  }

  public void setSubjects(ArrayList<Subject> subjects) {
    this.subjects = subjects;
    for (Subject s : subjects) {
      // s.setTeacher(this);
    }
  }
}
