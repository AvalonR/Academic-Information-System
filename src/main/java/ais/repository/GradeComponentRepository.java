package ais.repository;

import ais.model.GradeComponent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class GradeComponentRepository implements IGradeComponentRepository {

  private final SessionFactory sessionFactory;

  public GradeComponentRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(GradeComponent component) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(component);
      tx.commit();
      System.out.println("Saved grade component: " + component);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error saving grade component: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void update(GradeComponent component) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.merge(component);
      tx.commit();
      System.out.println("Updated grade component: " + component);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error updating grade component: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void delete(Integer id) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      GradeComponent component = session.get(GradeComponent.class, id);
      if (component != null) {
        session.remove(component);
        System.out.println("Deleted grade component ID: " + id);
      }
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      System.err.println("Error deleting grade component: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public GradeComponent findById(Integer id) {
    try (Session session = sessionFactory.openSession()) {
      return session.get(GradeComponent.class, id);
    } catch (Exception e) {
      System.err.println("Error finding grade component: " + e.getMessage());
      return null;
    }
  }

  public List<GradeComponent> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM GradeComponent ORDER BY name", GradeComponent.class).list();
    } catch (Exception e) {
      System.err.println("Error listing grade components: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public List<GradeComponent> findByCourseAndSubject(Integer courseId, Integer subjectId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM GradeComponent WHERE course.id = :courseId AND subject.id = :subjectId ORDER BY name",
          GradeComponent.class)
          .setParameter("courseId", courseId)
          .setParameter("subjectId", subjectId)
          .list();
    } catch (Exception e) {
      System.err.println("Error finding grade components: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }
}
