package ais.view;

import ais.service.AuthService;
import ais.service.StudentService;
import ais.service.SubjectService;
import ais.service.TeacherService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainView {

  private final AuthService authService;
  private final StudentService studentService;
  private final TeacherService teacherService;
  private final SubjectService subjectService;
  private final Stage stage;

  public MainView(AuthService authService, StudentService studentService,
      TeacherService teacherService, SubjectService subjectService) {
    this.authService = authService;
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.subjectService = subjectService;
    this.stage = new Stage();
    initUI();
  }

  private void initUI() {
    BorderPane root = new BorderPane();

    HBox topBar = new HBox(10);
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPadding(new Insets(10));
    topBar.setStyle("-fx-background-color: #2196F3;");

    Label welcomeLabel = new Label("Welcome, " + authService.getCurrentUser().getUsername());
    welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    Label roleLabel = new Label("[" + authService.getCurrentUser().getRole() + "]");
    roleLabel.setStyle("-fx-text-fill: white;");

    Button logoutButton = new Button("Logout");
    logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    logoutButton.setOnAction(e -> handleLogout());

    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    topBar.getChildren().addAll(welcomeLabel, roleLabel, spacer, logoutButton);

    TabPane tabPane = new TabPane();

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
    coursesTab.setContent(new Label("Course management coming soon..."));

    if (authService.isAdmin()) {
      tabPane.getTabs().addAll(studentsTab, teachersTab, subjectsTab, coursesTab);
    } else if (authService.isTeacher()) {
      tabPane.getTabs().addAll(studentsTab, subjectsTab, coursesTab);
    } else {
      tabPane.getTabs().add(coursesTab);
    }

    root.setTop(topBar);
    root.setCenter(tabPane);

    Scene scene = new Scene(root, 1000, 700);
    stage.setScene(scene);
    stage.setTitle("AIS - " + authService.getCurrentUser().getRole());
  }

  private void handleLogout() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Logout");
    alert.setHeaderText("Are you sure you want to logout?");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        authService.logout();
        stage.close();
        System.exit(0);
      }
    });
  }

  public void show() {
    stage.show();
  }
}
