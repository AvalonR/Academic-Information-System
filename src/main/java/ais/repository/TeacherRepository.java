package ais.repository;

import ais.model.Teacher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TeacherRepository implements ITeacherRepository {

  private final SessionFactory sessionFactory;

  public TeacherRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Teacher teacher) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(teacher);
      tx.commit();
      System.out.println("Saved teacher: " + teacher);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving teacher: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(Teacher teacher) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(teacher);
      tx.commit();
      System.out.println("Updated teacher: " + teacher);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating teacher: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      Teacher teacher = session.get(Teacher.class, id);
      if (teacher != null) {
        session.remove(teacher);
        System.out.println("Deleted teacher ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting teacher: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Teacher findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(Teacher.class, id);
    } catch (Exception e) {
      System.err.println("Error finding teacher: " + e.getMessage());
      return null;
    }
  }

  public List<Teacher> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Teacher ORDER BY id", Teacher.class).list();
    } catch (Exception e) {
      System.err.println("Error listing teachers: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }
}
