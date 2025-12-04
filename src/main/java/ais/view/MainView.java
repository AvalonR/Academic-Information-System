package ais.view;

import ais.model.Student;
import ais.model.Teacher;
import ais.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainView {

  private final AuthService authService;
  private final StudentService studentService;
  private final TeacherService teacherService;
  private final SubjectService subjectService;
  private final CourseService courseService;
  private final GradeService gradeService;
  private final Stage stage;
  private final Runnable onSwitchUser;

  private final GradeComponentService gradeComponentService;

  public MainView(AuthService authService, StudentService studentService,
      TeacherService teacherService, SubjectService subjectService,
      CourseService courseService, GradeService gradeService,
      GradeComponentService gradeComponentService,
      Runnable onSwitchUser) {
    this.authService = authService;
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.subjectService = subjectService;
    this.courseService = courseService;
    this.gradeService = gradeService;
    this.gradeComponentService = gradeComponentService;
    this.onSwitchUser = onSwitchUser;
    this.stage = new Stage();
    initUI();
  }

  private void initUI() {
    BorderPane root = new BorderPane();

    HBox topBar = new HBox(15);
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPadding(new Insets(15, 20, 15, 20));
    topBar.getStyleClass().add("top-bar");

    VBox welcomeBox = new VBox(2);
    Label welcomeLabel = new Label("Welcome, " + authService.getCurrentUser().getUsername());
    welcomeLabel.getStyleClass().add("top-bar-label");
    welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

    Label roleLabel = new Label(authService.getCurrentUser().getRole().toString());
    roleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");

    welcomeBox.getChildren().addAll(welcomeLabel, roleLabel);

    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    Button logoutButton = new Button("🚪 Logout");
    logoutButton.getStyleClass().add("button-danger");
    logoutButton.setFont(Font.font("System", FontWeight.BOLD, 12));
    logoutButton.setOnAction(e -> handleLogout());

    topBar.getChildren().addAll(welcomeBox, spacer, logoutButton);

    TabPane tabPane = new TabPane();

    if (authService.isAdmin()) {

      Tab dashboardTab = new Tab("📊 Dashboard");
      dashboardTab.setClosable(false);
      dashboardTab
          .setContent(new DashboardView(studentService, teacherService, subjectService, courseService, gradeService));
      Tab studentsTab = new Tab("Students");
      studentsTab.setClosable(false);
      studentsTab.setContent(new StudentManagementView(studentService));

      Tab teachersTab = new Tab("Teachers");
      teachersTab.setClosable(false);
      teachersTab.setContent(new TeacherManagementView(teacherService));

      Tab subjectsTab = new Tab("Subjects");
      subjectsTab.setClosable(false);
      subjectsTab.setContent(new SubjectManagementView(subjectService, teacherService));

      Tab coursesTab = new Tab("Courses");
      coursesTab.setClosable(false);
      coursesTab.setContent(new CourseManagementView(courseService, subjectService, studentService));

      tabPane.getTabs().addAll(dashboardTab, studentsTab, teachersTab, subjectsTab, coursesTab);

    } else if (authService.isTeacher()) {

      Teacher teacher = teacherService.getAllTeachers().stream()
          .filter(t -> t.getUser() != null && t.getUser().getId().equals(authService.getCurrentUser().getId()))
          .findFirst()
          .orElse(null);

      if (teacher != null) {
        Tab subjectsTab = new Tab("My Subjects");
        subjectsTab.setClosable(false);
        subjectsTab.setContent(new SubjectListView(subjectService, teacher));

        Tab coursesTab = new Tab("My Courses");
        coursesTab.setClosable(false);
        coursesTab.setContent(new CourseListView(courseService, teacher));

        Tab gradesTab = new Tab("Grade Students");
        gradesTab.setClosable(false);
        gradesTab.setContent(new GradeManagementView(gradeService, courseService, subjectService, studentService,
            gradeComponentService, teacher));

        tabPane.getTabs().addAll(subjectsTab, coursesTab, gradesTab);
      } else {
        Label noTeacherLabel = new Label("No teacher record found for this account. Please contact an administrator.");
        noTeacherLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        VBox noTeacherBox = new VBox(noTeacherLabel);
        noTeacherBox.setAlignment(Pos.CENTER);
        noTeacherBox.setPadding(new Insets(50));
        Tab infoTab = new Tab("Info");
        infoTab.setClosable(false);
        infoTab.setContent(noTeacherBox);
        tabPane.getTabs().add(infoTab);
      }
    } else if (authService.isStudent()) {
      Student student = studentService.getAllStudents().stream()
          .filter(s -> s.getUser() != null && s.getUser().getId().equals(authService.getCurrentUser().getId()))
          .findFirst()
          .orElse(null);

      if (student != null) {
        Tab gradesTab = new Tab("My Grades");
        gradesTab.setClosable(false);
        gradesTab.setContent(new StudentGradeView(gradeService, student));
        tabPane.getTabs().add(gradesTab);
      } else {
        Label noDataLabel = new Label("No student record found for this account");
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        VBox noDataBox = new VBox(noDataLabel);
        noDataBox.setAlignment(Pos.CENTER);
        noDataBox.setPadding(new Insets(50));
        Tab infoTab = new Tab("Info");
        infoTab.setClosable(false);
        infoTab.setContent(noDataBox);
        tabPane.getTabs().add(infoTab);
      }
    }

    root.setTop(topBar);
    root.setCenter(tabPane);

    Scene scene = new Scene(root, 1100, 750);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle("AIS - " + authService.getCurrentUser().getRole());
  }

  private void handleLogout() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Logout");
    alert.setHeaderText("What would you like to do?");
    alert.setContentText("Choose your option:");

    ButtonType switchUserButton = new ButtonType("Switch User");
    ButtonType exitButton = new ButtonType("Exit Application");
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(switchUserButton, exitButton, cancelButton);

    alert.showAndWait().ifPresent(response -> {
      if (response == switchUserButton) {
        authService.logout();
        stage.close();

        javafx.application.Platform.runLater(() -> {
          onSwitchUser.run();
        });

      } else if (response == exitButton) {
        authService.logout();
        stage.close();
        javafx.application.Platform.exit();
        System.exit(0);
      }
    });
  }

  public void show() {
    stage.show();
  }
}
