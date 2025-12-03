package ais.repository;

import ais.model.Course;
import ais.model.Student;
import ais.model.Subject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class CourseRepository {

  private final SessionFactory sessionFactory;

  public CourseRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Course course) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(course);
      tx.commit();
      System.out.println("Saved course: " + course);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving course: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(Course course) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(course);
      tx.commit();
      System.out.println("Updated course: " + course);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating course: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      Course course = session.get(Course.class, id);
      if (course != null) {
        session.remove(course);
        System.out.println("Deleted course ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting course: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Course findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(Course.class, id);
    } catch (Exception e) {
      System.err.println("Error finding course: " + e.getMessage());
      return null;
    }
  }

  public List<Course> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Course ORDER BY name", Course.class).list();
    } catch (Exception e) {
      System.err.println("Error listing courses: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public void addSubjectToCourse(Integer courseId, Integer subjectId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Course course = session.get(Course.class, courseId);
      Subject subject = session.get(Subject.class, subjectId);

      if (course != null && subject != null) {
        course.getSubjects().add(subject);
        session.merge(course);
      }

      tx.commit();
      System.out.println("Added subject " + subjectId + " to course " + courseId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error adding subject to course: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public void removeSubjectFromCourse(Integer courseId, Integer subjectId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Course course = session.get(Course.class, courseId);
      if (course != null) {
        course.getSubjects().removeIf(s -> s.getId().equals(subjectId));
        session.merge(course);
      }

      tx.commit();
      System.out.println("Removed subject " + subjectId + " from course " + courseId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error removing subject from course: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public void enrollStudent(Integer courseId, Integer studentId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Course course = session.get(Course.class, courseId);
      Student student = session.get(Student.class, studentId);

      if (course != null && student != null) {
        course.getStudents().add(student);
        session.merge(course);
      }

      tx.commit();
      System.out.println("Enrolled student " + studentId + " in course " + courseId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error enrolling student: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public void unenrollStudent(Integer courseId, Integer studentId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Course course = session.get(Course.class, courseId);
      if (course != null) {
        course.getStudents().removeIf(s -> s.getId().equals(studentId));
        session.merge(course);
      }

      tx.commit();
      System.out.println("Unenrolled student " + studentId + " from course " + courseId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error unenrolling student: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }
}
