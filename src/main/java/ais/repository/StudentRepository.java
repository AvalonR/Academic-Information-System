package ais.repository;

import ais.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class StudentRepository implements IStudentRepository {

  private final SessionFactory sessionFactory;

  public StudentRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Student student) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(student);
      tx.commit();
      System.out.println("Saved student: " + student);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving student: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(Student student) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(student);
      tx.commit();
      System.out.println("Updated student: " + student);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating student: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      Student student = session.get(Student.class, id);
      if (student != null) {
        session.remove(student);
        System.out.println("Deleted student ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting student: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Student findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(Student.class, id);
    } catch (Exception e) {
      System.err.println("Error finding student: " + e.getMessage());
      return null;
    }
  }

  public List<Student> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Student ORDER BY id", Student.class).list();
    } catch (Exception e) {
      System.err.println("Error listing students: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }
}
