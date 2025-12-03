package ais.view;

import ais.model.Grade;
import ais.model.Student;
import ais.service.GradeService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentGradeView extends VBox {

  private final GradeService gradeService;
  private final Student currentStudent;

  private TableView<Grade> gradeTable;
  private Label summaryLabel;

  public StudentGradeView(GradeService gradeService, Student currentStudent) {
    this.gradeService = gradeService;
    this.currentStudent = currentStudent;
    initUI();
    loadGrades();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("My Grades");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label studentLabel = new Label("Student: " + currentStudent.getName());
    studentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

    summaryLabel = new Label();
    summaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

    gradeTable = new TableView<>();

    TableColumn<Grade, String> courseCol = new TableColumn<>("Course");
    courseCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getName()));
    courseCol.setPrefWidth(200);

    TableColumn<Grade, String> subjectCol = new TableColumn<>("Subject");
    subjectCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubject().getName()));
    subjectCol.setPrefWidth(150);

    TableColumn<Grade, String> teacherCol = new TableColumn<>("Teacher");
    teacherCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTeacher().getName()));
    teacherCol.setPrefWidth(150);

    TableColumn<Grade, Double> gradeCol = new TableColumn<>("Grade");
    gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeValue"));
    gradeCol.setPrefWidth(80);

    TableColumn<Grade, String> letterCol = new TableColumn<>("Letter");
    letterCol.setCellValueFactory(new PropertyValueFactory<>("letterGrade"));
    letterCol.setPrefWidth(80);

    TableColumn<Grade, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("dateGiven"));
    dateCol.setPrefWidth(100);

    TableColumn<Grade, String> commentsCol = new TableColumn<>("Comments");
    commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));
    commentsCol.setPrefWidth(200);

    gradeTable.getColumns().addAll(courseCol, subjectCol, teacherCol, gradeCol, letterCol, dateCol, commentsCol);

    Button refreshButton = new Button("Refresh");
    refreshButton.setOnAction(e -> loadGrades());

    getChildren().addAll(
        titleLabel,
        studentLabel,
        summaryLabel,
        gradeTable,
        refreshButton);
  }

  private void loadGrades() {
    List<Grade> grades = gradeService.getGradesByStudent(currentStudent.getId());
    gradeTable.setItems(FXCollections.observableArrayList(grades));

    if (!grades.isEmpty()) {
      double average = grades.stream()
          .mapToDouble(Grade::getGradeValue)
          .average()
          .orElse(0.0);
      summaryLabel.setText(String.format("Total Grades: %d | Average: %.2f (%s)",
          grades.size(), average, GradeService.calculateLetterGrade(average)));
    } else {
      summaryLabel.setText("No grades yet");
    }
  }
}
