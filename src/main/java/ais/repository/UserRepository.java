package ais.repository;

import ais.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserRepository {

  private final SessionFactory sessionFactory;

  public UserRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(User user) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      session.persist(user);
      tx.commit();
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    }
  }

  public User findByUsername(String username) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "FROM User WHERE username = :username", User.class)
          .setParameter("username", username)
          .uniqueResult();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<User> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM User", User.class).list();
    }
  }
}
