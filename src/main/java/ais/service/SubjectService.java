package ais.service;

import ais.repository.SubjectRepositoryInterface;
import ais.model.Subject;

import java.util.List;

public class SubjectService {
  private final SubjectRepositoryInterface subjectRepo;
  private int nextId = 1;

  public SubjectService(SubjectRepositoryInterface subjectRepo) {
    this.subjectRepo = subjectRepo;
  }

  public Subject createSubject(String name, String description) {
    if (description.isEmpty()) {
      throw new IllegalArgumentException("Subject is not assigned to any description.");
    }

    Subject s = new Subject(nextId++, name, description);

    subjectRepo.add(s);
    return s;
  }

  public boolean deleteSubject(int id) {
    return subjectRepo.remove(id);
  }

  public List<Subject> getAllSubjects() {
    return subjectRepo.findAll();
  }
}
