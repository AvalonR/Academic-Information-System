package ais.service;

import ais.model.Course;
import ais.model.GradeComponent;
import ais.model.Subject;
import ais.repository.ICourseRepository;
import ais.repository.IGradeComponentRepository;
import ais.repository.ISubjectRepository;

import java.util.List;

public class GradeComponentService {

  private final IGradeComponentRepository componentRepository;
  private final ICourseRepository courseRepository;
  private final ISubjectRepository subjectRepository;

  public GradeComponentService(IGradeComponentRepository componentRepository,
      ICourseRepository courseRepository,
      ISubjectRepository subjectRepository) {
    this.componentRepository = componentRepository;
    this.courseRepository = courseRepository;
    this.subjectRepository = subjectRepository;
  }

  public void createComponent(String name, Double weight, Integer courseId, Integer subjectId) {
    Course course = courseRepository.findById(courseId);
    Subject subject = subjectRepository.findById(subjectId);

    if (course == null)
      throw new IllegalArgumentException("Course not found");
    if (subject == null)
      throw new IllegalArgumentException("Subject not found");
    if (weight < 0 || weight > 1)
      throw new IllegalArgumentException("Weight must be between 0 and 1 (e.g., 0.2 for 20%)");

    List<GradeComponent> existing = componentRepository.findByCourseAndSubject(courseId, subjectId);
    double totalWeight = existing.stream().mapToDouble(GradeComponent::getWeight).sum();

    if (totalWeight + weight > 1.0) {
      throw new IllegalArgumentException(
          String.format("Total weight would exceed 100%%. Current: %.0f%%, Adding: %.0f%%, Total: %.0f%%",
              totalWeight * 100, weight * 100, (totalWeight + weight) * 100));
    }

    GradeComponent component = new GradeComponent(name, weight, course, subject);
    componentRepository.save(component);
  }

  public void updateComponent(Integer id, String name, Double weight) {
    GradeComponent component = componentRepository.findById(id);
    if (component == null)
      throw new IllegalArgumentException("Component not found");
    if (weight < 0 || weight > 1)
      throw new IllegalArgumentException("Weight must be between 0 and 1");

    List<GradeComponent> existing = componentRepository.findByCourseAndSubject(
        component.getCourse().getId(), component.getSubject().getId());
    double totalWeight = existing.stream()
        .filter(c -> !c.getId().equals(id))
        .mapToDouble(GradeComponent::getWeight)
        .sum();

    if (totalWeight + weight > 1.0) {
      throw new IllegalArgumentException(
          String.format("Total weight would exceed 100%%. Current (excluding this): %.0f%%, New: %.0f%%",
              totalWeight * 100, weight * 100));
    }

    component.setName(name);
    component.setWeight(weight);
    componentRepository.update(component);
  }

  public void deleteComponent(Integer id) {
    componentRepository.delete(id);
  }

  public List<GradeComponent> getAllComponents() {
    return componentRepository.findAll();
  }

  public List<GradeComponent> getComponentsByCourseAndSubject(Integer courseId, Integer subjectId) {
    return componentRepository.findByCourseAndSubject(courseId, subjectId);
  }

  public GradeComponent getComponentById(Integer id) {
    return componentRepository.findById(id);
  }

  public double getTotalWeightForCourseSubject(Integer courseId, Integer subjectId) {
    return componentRepository.findByCourseAndSubject(courseId, subjectId).stream()
        .mapToDouble(GradeComponent::getWeight)
        .sum();
  }
}
