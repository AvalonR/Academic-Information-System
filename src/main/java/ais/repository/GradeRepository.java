package ais.repository;

import ais.model.Grade;
import ais.model.Student;
import ais.model.Subject;
import ais.model.Teacher;
import ais.model.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class GradeRepository {

  private final SessionFactory sessionFactory;

  public GradeRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Grade grade) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(grade);
      tx.commit();
      System.out.println("Saved grade: " + grade);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving grade: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(Grade grade) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(grade);
      tx.commit();
      System.out.println("Updated grade: " + grade);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating grade: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      Grade grade = session.get(Grade.class, id);
      if (grade != null) {
        session.remove(grade);
        System.out.println("Deleted grade ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting grade: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Grade findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(Grade.class, id);
    } catch (Exception e) {
      System.err.println("Error finding grade: " + e.getMessage());
      return null;
    }
  }

  public List<Grade> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Grade ORDER BY dateGiven DESC", Grade.class).list();
    } catch (Exception e) {
      System.err.println("Error listing grades: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Grade> findByStudent(Integer studentId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM Grade WHERE student.id = :studentId ORDER BY dateGiven DESC", Grade.class)
          .setParameter("studentId", studentId)
          .list();
    } catch (Exception e) {
      System.err.println("Error finding grades by student: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Grade> findByTeacher(Integer teacherId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM Grade WHERE teacher.id = :teacherId ORDER BY dateGiven DESC", Grade.class)
          .setParameter("teacherId", teacherId)
          .list();
    } catch (Exception e) {
      System.err.println("Error finding grades by teacher: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Grade> findByCourse(Integer courseId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM Grade WHERE course.id = :courseId ORDER BY dateGiven DESC", Grade.class)
          .setParameter("courseId", courseId)
          .list();
    } catch (Exception e) {
      System.err.println("Error finding grades by course: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Grade> findBySubject(Integer subjectId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM Grade WHERE subject.id = :subjectId ORDER BY dateGiven DESC", Grade.class)
          .setParameter("subjectId", subjectId)
          .list();
    } catch (Exception e) {
      System.err.println("Error finding grades by subject: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }
}
