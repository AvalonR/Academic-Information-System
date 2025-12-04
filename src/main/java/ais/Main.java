package ais;

import ais.model.User;
import ais.model.UserRole;
import ais.repository.*;
import ais.service.*;
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
  private CourseService courseService;
  private GradeService gradeService;
  private GradeComponentService gradeComponentService;

  @Override
  public void init() {
    try {
      System.out.println("Initializing Hibernate...");
      sessionFactory = new Configuration().configure().buildSessionFactory();
      System.out.println("Hibernate initialized successfully!");

      IUserRepository userRepository = new UserRepository(sessionFactory);
      IStudentRepository studentRepository = new StudentRepository(sessionFactory);
      ITeacherRepository teacherRepository = new TeacherRepository(sessionFactory);
      ISubjectRepository subjectRepository = new SubjectRepository(sessionFactory);
      ICourseRepository courseRepository = new CourseRepository(sessionFactory);
      IGradeRepository gradeRepository = new GradeRepository(sessionFactory);
      IGradeComponentRepository gradeComponentRepository = new GradeComponentRepository(sessionFactory);

      authService = new AuthService(userRepository);
      studentService = new StudentService(studentRepository, userRepository);
      teacherService = new TeacherService(teacherRepository, userRepository);
      subjectService = new SubjectService(subjectRepository, teacherRepository);
      courseService = new CourseService(courseRepository, subjectRepository, studentRepository);
      gradeService = new GradeService(gradeRepository, studentRepository, subjectRepository,
          teacherRepository, courseRepository, gradeComponentRepository);
      gradeComponentService = new GradeComponentService(gradeComponentRepository, courseRepository, subjectRepository);

      createDefaultUsers(userRepository);

    } catch (Exception e) {
      System.err.println("Failed to initialize application");
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    showLoginScreen();
  }

  private void showLoginScreen() {
    LoginView loginView = new LoginView(authService, () -> {
      MainView mainView = new MainView(authService, studentService, teacherService,
          subjectService, courseService, gradeService,
          gradeComponentService, this::showLoginScreen);
      mainView.show();
    });
    loginView.show();
  }

  private void createDefaultUsers(IUserRepository userRepository) {
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
