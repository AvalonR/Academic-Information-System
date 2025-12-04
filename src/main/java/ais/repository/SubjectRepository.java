package ais.repository;

import ais.model.Subject;
import ais.model.Teacher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class SubjectRepository implements ISubjectRepository {

  private final SessionFactory sessionFactory;

  public SubjectRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Subject subject) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(subject);
      tx.commit();
      System.out.println("Saved subject: " + subject);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving subject: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(Subject subject) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(subject);
      tx.commit();
      System.out.println("Updated subject: " + subject);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating subject: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      Subject subject = session.get(Subject.class, id);
      if (subject != null) {
        session.remove(subject);
        System.out.println("Deleted subject ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting subject: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Subject findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(Subject.class, id);
    } catch (Exception e) {
      System.err.println("Error finding subject: " + e.getMessage());
      return null;
    }
  }

  public List<Subject> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Subject ORDER BY name", Subject.class).list();
    } catch (Exception e) {
      System.err.println("Error listing subjects: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public void assignTeacherToSubject(Integer subjectId, Integer teacherId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Subject subject = session.get(Subject.class, subjectId);
      Teacher teacher = session.get(Teacher.class, teacherId);

      if (subject != null && teacher != null) {
        subject.getTeachers().add(teacher);
        session.merge(subject);
      }

      tx.commit();
      System.out.println("Assigned teacher " + teacherId + " to subject " + subjectId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error assigning teacher to subject: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public void removeTeacherFromSubject(Integer subjectId, Integer teacherId) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();

      Subject subject = session.get(Subject.class, subjectId);
      if (subject != null) {
        subject.getTeachers().removeIf(t -> t.getId().equals(teacherId));
        session.merge(subject);
      }

      tx.commit();
      System.out.println("Removed teacher " + teacherId + " from subject " + subjectId);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error removing teacher from subject: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }
}
