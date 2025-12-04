package ais.view;

import ais.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardView extends VBox {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final SubjectService subjectService;
  private final CourseService courseService;
  private final GradeService gradeService;

  public DashboardView(StudentService studentService, TeacherService teacherService,
      SubjectService subjectService, CourseService courseService,
      GradeService gradeService) {
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.subjectService = subjectService;
    this.courseService = courseService;
    this.gradeService = gradeService;
    initUI();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(20);

    Label titleLabel = new Label("📊 Dashboard");
    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

    HBox statsBox = new HBox(20);
    statsBox.setAlignment(Pos.CENTER);

    statsBox.getChildren().addAll(
        createStatCard("👥 Students", String.valueOf(studentService.getAllStudents().size()), "#4CAF50"),
        createStatCard("👨‍🏫 Teachers", String.valueOf(teacherService.getAllTeachers().size()), "#2196F3"),
        createStatCard("📚 Subjects", String.valueOf(subjectService.getAllSubjects().size()), "#ff9800"),
        createStatCard("🎓 Courses", String.valueOf(courseService.getAllCourses().size()), "#9c27b0"));

    Label welcomeLabel = new Label("Welcome to the Academic Information System");
    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
    welcomeLabel.setWrapText(true);

    VBox.setVgrow(new Region(), Priority.ALWAYS);

    getChildren().addAll(titleLabel, statsBox, welcomeLabel);

    getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private VBox createStatCard(String title, String value, String color) {
    VBox card = new VBox(10);
    card.setAlignment(Pos.CENTER);
    card.getStyleClass().add("card");
    card.setPrefSize(200, 150);
    card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 4 0;");

    Label valueLabel = new Label(value);
    valueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
    valueLabel.setStyle("-fx-text-fill: " + color + ";");

    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
    titleLabel.setStyle("-fx-text-fill: #666;");

    card.getChildren().addAll(valueLabel, titleLabel);

    return card;
  }
}
