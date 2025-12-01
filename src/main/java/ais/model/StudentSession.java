package ais.model;

public class StudentSession {
  private final Student student;
  private final TeachingSession teachingSession;
  private double grade;
  private boolean completed;

  public StudentSession(Student student, TeachingSession teachingSession) {
    this.student = student;
    this.teachingSession = teachingSession;
    this.grade = 0.0;
    this.completed = false;
  }

  public Student getStudent() {
    return student;
  }

  public TeachingSession getTeachingSession() {
    return teachingSession;
  }

  public double getGrade() {
    return grade;
  }

  public void setGrade(double grade) {
    this.grade = grade;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public String toString() {
    return "StudentSession{" +
        "student=" + student.getName() +
        ", subject=" + teachingSession.getSubject().getName() +
        ", teacher=" + teachingSession.getTeacher().getName() +
        ", grade=" + grade +
        ", completed=" + completed +
        '}';
  }
}
