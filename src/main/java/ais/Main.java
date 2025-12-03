package ais;

import ais.model.User;
import ais.model.UserRole;
import ais.repository.StudentRepository;
import ais.repository.SubjectRepository;
import ais.repository.TeacherRepository;
import ais.repository.UserRepository;
import ais.service.AuthService;
import ais.service.StudentService;
import ais.service.SubjectService;
import ais.service.TeacherService;
import ais.view.LoginView;
import ais.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main extends Application {

  private SessionFactory sessionFactory;
  private AuthService authService;
  private StudentService studentService;
  private TeacherService teacherService;
  private SubjectService subjectService;

  @Override
  public void init() {
    try {
      System.out.println("Initializing Hibernate...");
      sessionFactory = new Configuration().configure().buildSessionFactory();
      System.out.println("Hibernate initialized successfully!");

      UserRepository userRepository = new UserRepository(sessionFactory);
      StudentRepository studentRepository = new StudentRepository(sessionFactory);
      TeacherRepository teacherRepository = new TeacherRepository(sessionFactory);
      SubjectRepository subjectRepository = new SubjectRepository(sessionFactory);

      authService = new AuthService(userRepository);
      studentService = new StudentService(studentRepository, userRepository);
      teacherService = new TeacherService(teacherRepository, userRepository);
      subjectService = new SubjectService(subjectRepository, teacherRepository);

      createDefaultUsers(userRepository);

    } catch (Exception e) {
      System.err.println("Failed to initialize application");
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    LoginView loginView = new LoginView(authService, () -> {
      MainView mainView = new MainView(authService, studentService, teacherService, subjectService);
      mainView.show();
    });

    loginView.show();
  }

  private void createDefaultUsers(UserRepository userRepository) {
    if (userRepository.findAll().isEmpty()) {
      System.out.println("Creating default users...");

      User admin = new User("admin", "admin", UserRole.ADMIN);
      userRepository.save(admin);

      System.out.println("Default users created:");
      System.out.println("  - admin/admin (ADMIN)");
    }
  }

  @Override
  public void stop() {
    if (sessionFactory != null) {
      sessionFactory.close();
      System.out.println("Application closed");
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
