package ais.service;

import ais.model.Subject;
import ais.model.Teacher;
import ais.repository.SubjectRepository;
import ais.repository.TeacherRepository;

import java.util.List;
import java.util.Set;

public class SubjectService {

  private final SubjectRepository subjectRepository;
  private final TeacherRepository teacherRepository;

  public SubjectService(SubjectRepository subjectRepository, TeacherRepository teacherRepository) {
    this.subjectRepository = subjectRepository;
    this.teacherRepository = teacherRepository;
  }

  public void createSubject(String name, String language, String room) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject name is required");
    }
    if (language == null || language.trim().isEmpty()) {
      throw new IllegalArgumentException("Language is required");
    }

    Subject subject = new Subject(name, language, room);
    subjectRepository.save(subject);
  }

  public void updateSubject(Integer id, String name, String language, String room) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject name is required");
    }
    if (language == null || language.trim().isEmpty()) {
      throw new IllegalArgumentException("Language is required");
    }

    Subject subject = subjectRepository.findById(id);
    if (subject == null) {
      throw new IllegalArgumentException("Subject not found");
    }

    subject.setName(name);
    subject.setLanguage(language);
    subject.setRoom(room);

    subjectRepository.update(subject);
  }

  public void deleteSubject(Integer id) {
    subjectRepository.delete(id);
  }

  public void assignTeacher(Integer subjectId, Integer teacherId) {
    Subject subject = subjectRepository.findById(subjectId);
    Teacher teacher = teacherRepository.findById(teacherId);

    if (subject == null) {
      throw new IllegalArgumentException("Subject not found");
    }
    if (teacher == null) {
      throw new IllegalArgumentException("Teacher not found");
    }

    if (subject.getTeachers().stream().anyMatch(t -> t.getId().equals(teacherId))) {
      throw new IllegalArgumentException("Teacher is already assigned to this subject");
    }

    subjectRepository.assignTeacherToSubject(subjectId, teacherId);
  }

  public void removeTeacher(Integer subjectId, Integer teacherId) {
    try {
      subjectRepository.removeTeacherFromSubject(subjectId, teacherId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to remove teacher from subject: " + e.getMessage(), e);
    }
  }

  public List<Subject> getAllSubjects() {
    return subjectRepository.findAll();
  }

  public Subject getSubjectById(Integer id) {
    return subjectRepository.findById(id);
  }

  public Set<Teacher> getTeachersForSubject(Integer subjectId) {
    Subject subject = subjectRepository.findById(subjectId);
    return subject != null ? subject.getTeachers() : Set.of();
  }
}
