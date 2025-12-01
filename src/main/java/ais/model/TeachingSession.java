package ais.model;

import java.util.ArrayList;
import java.util.List;

public class TeachingSession {
  private final Subject subject; // The curriculum subject
  private final Teacher teacher; // Lead teacher
  private final List<Teacher> assistants; // Optional TAs or co-teachers
  private final Course course; // The course this session belongs to
  private final String semester; // e.g., "Fall 2025"

  public TeachingSession(Subject subject, Teacher teacher, Course course, String semester) {
    this.subject = subject;
    this.teacher = teacher;
    this.course = course;
    this.semester = semester;
    this.assistants = new ArrayList<>();
  }

  public Subject getSubject() {
    return subject;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public List<Teacher> getAssistants() {
    return assistants;
  }

  public Course getCourse() {
    return course;
  }

  public String getSemester() {
    return semester;
  }

  public void addAssistant(Teacher assistant) {
    assistants.add(assistant);
  }

  @Override
  public String toString() {
    return "TeachingSession{" +
        "subject=" + subject.getName() +
        ", teacher=" + teacher.getName() +
        ", assistants=" + assistants.size() +
        ", course=" + course.getName() +
        ", semester='" + semester + '\'' +
        '}';
  }
}
