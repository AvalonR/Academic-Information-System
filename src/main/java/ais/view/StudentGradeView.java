package ais.view;

import ais.model.Grade;
import ais.model.Student;
import ais.service.GradeService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentGradeView extends VBox {

  private final GradeService gradeService;
  private final Student currentStudent;

  private TableView<Grade> gradeTable;
  private Label summaryLabel;
  private VBox finalGradesBox;

  public StudentGradeView(GradeService gradeService, Student currentStudent) {
    this.gradeService = gradeService;
    this.currentStudent = currentStudent;
    initUI();
    loadGrades();

    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("My Grades");
    titleLabel.getStyleClass().add("label-title");

    Label studentLabel = new Label("Student: " + currentStudent.getName());
    studentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

    summaryLabel = new Label();
    summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    VBox finalGradesCard = new VBox(10);
    finalGradesCard.getStyleClass().add("form-section");

    Label finalGradesTitle = new Label("Final Course Grades (Weighted Averages)");
    finalGradesTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
    finalGradesTitle.setStyle("-fx-text-fill: #333;");

    finalGradesBox = new VBox(5);

    finalGradesCard.getChildren().addAll(finalGradesTitle, finalGradesBox);

    VBox gradesCard = new VBox(10);
    gradesCard.getStyleClass().add("form-section");
    VBox.setVgrow(gradesCard, Priority.ALWAYS);

    Label gradesTitle = new Label("Individual Grade Components");
    gradesTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
    gradesTitle.setStyle("-fx-text-fill: #333;");

    gradeTable = new TableView<>();
    VBox.setVgrow(gradeTable, Priority.ALWAYS);
    gradeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<Grade, String> courseCol = new TableColumn<>("Course");
    courseCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getName()));
    courseCol.setMinWidth(150);

    TableColumn<Grade, String> subjectCol = new TableColumn<>("Subject");
    subjectCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubject().getName()));
    subjectCol.setMinWidth(120);

    TableColumn<Grade, String> teacherCol = new TableColumn<>("Teacher");
    teacherCol.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTeacher().getName()));
    teacherCol.setMinWidth(120);

    TableColumn<Grade, String> componentCol = new TableColumn<>("Component");
    componentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
        data.getValue().getComponent() != null ? data.getValue().getComponent().getName() : "N/A"));
    componentCol.setMinWidth(100);

    TableColumn<Grade, Double> gradeCol = new TableColumn<>("Grade");
    gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeValue"));
    gradeCol.setMinWidth(60);

    TableColumn<Grade, String> statusCol = new TableColumn<>("Status");
    statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
        GradeService.getPassFailStatus(data.getValue().getGradeValue())));
    statusCol.setMinWidth(60);
    statusCol.setCellFactory(column -> new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setStyle("");
        } else {
          setText(item);
          if ("PASS".equals(item)) {
            setStyle(
                "-fx-text-fill: white; -fx-background-color: #4CAF50; -fx-alignment: center; -fx-font-weight: bold;");
          } else {
            setStyle(
                "-fx-text-fill: white; -fx-background-color: #f44336; -fx-alignment: center; -fx-font-weight: bold;");
          }
        }
      }
    });

    TableColumn<Grade, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("dateGiven"));
    dateCol.setMinWidth(100);

    TableColumn<Grade, String> commentsCol = new TableColumn<>("Comments");
    commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));
    commentsCol.setMinWidth(150);

    gradeTable.getColumns().addAll(courseCol, subjectCol, teacherCol, componentCol, gradeCol, statusCol, dateCol,
        commentsCol);

    Button refreshButton = new Button("Refresh");
    refreshButton.getStyleClass().add("button-default");
    refreshButton.setOnAction(e -> loadGrades());

    gradesCard.getChildren().addAll(gradesTitle, gradeTable, refreshButton);

    getChildren().addAll(
        titleLabel,
        studentLabel,
        summaryLabel,
        finalGradesCard,
        gradesCard);
  }

  private void loadGrades() {
    List<Grade> grades = gradeService.getGradesByStudent(currentStudent.getId());
    gradeTable.setItems(FXCollections.observableArrayList(grades));

    if (!grades.isEmpty()) {
      double average = grades.stream()
          .mapToDouble(Grade::getGradeValue)
          .average()
          .orElse(0.0);
      String status = GradeService.getPassFailStatus(average);
      String statusColor = "PASS".equals(status) ? "green" : "red";
      summaryLabel.setText(String.format("Total Grade Entries: %d | Overall Average: %.2f [%s]",
          grades.size(), average, status));
      summaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");
    } else {
      summaryLabel.setText("No grades yet");
      summaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
    }

    displayFinalGrades(grades);
  }

  private void displayFinalGrades(List<Grade> grades) {
    finalGradesBox.getChildren().clear();

    Map<String, List<Grade>> groupedGrades = grades.stream()
        .collect(Collectors.groupingBy(g -> g.getCourse().getName() + " - " + g.getSubject().getName()));

    if (groupedGrades.isEmpty()) {
      Label noGradesLabel = new Label("No final grades to calculate yet");
      noGradesLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
      finalGradesBox.getChildren().add(noGradesLabel);
      return;
    }

    for (Map.Entry<String, List<Grade>> entry : groupedGrades.entrySet()) {
      String courseSubject = entry.getKey();
      List<Grade> courseGrades = entry.getValue();

      double weightedSum = 0.0;
      double totalWeight = 0.0;

      for (Grade grade : courseGrades) {
        if (grade.getComponent() != null) {
          weightedSum += grade.getGradeValue() * grade.getComponent().getWeight();
          totalWeight += grade.getComponent().getWeight();
        }
      }

      double finalGrade;
      String calculation;
      if (totalWeight == 0.0) {
        finalGrade = courseGrades.stream()
            .mapToDouble(Grade::getGradeValue)
            .average()
            .orElse(0.0);
        calculation = "Simple Average";
      } else if (totalWeight < 1.0) {
        finalGrade = weightedSum;
        calculation = String.format("Weighted (%.0f%% complete)", totalWeight * 100);
      } else {
        finalGrade = weightedSum;
        calculation = "Weighted Average";
      }

      String status = GradeService.getPassFailStatus(finalGrade);

      VBox gradeBox = new VBox(5);
      gradeBox.setStyle(
          "-fx-background-color: #f9f9f9; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

      Label courseLabel = new Label(courseSubject);
      courseLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

      Label gradeLabel = new Label(String.format("Final Grade: %.2f (%s)", finalGrade, calculation));
      gradeLabel.setStyle("-fx-font-size: 12px;");

      Label statusLabel = new Label(status);
      statusLabel.setStyle(
          "PASS".equals(status)
              ? "-fx-text-fill: white; -fx-background-color: #4CAF50; -fx-padding: 3px 8px; -fx-background-radius: 3px; -fx-font-weight: bold; -fx-font-size: 11px;"
              : "-fx-text-fill: white; -fx-background-color: #f44336; -fx-padding: 3px 8px; -fx-background-radius: 3px; -fx-font-weight: bold; -fx-font-size: 11px;");

      StringBuilder breakdown = new StringBuilder("Components: ");
      for (Grade grade : courseGrades) {
        String componentName = grade.getComponent() != null ? grade.getComponent().getName() : "N/A";
        String weight = grade.getComponent() != null ? String.format("%.0f%%", grade.getComponent().getWeight() * 100)
            : "N/A";
        breakdown.append(String.format("%s (%.1f, %s), ", componentName, grade.getGradeValue(), weight));
      }
      if (breakdown.length() > 12) {
      }

      Label breakdownLabel = new Label(breakdown.toString());
      breakdownLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
      breakdownLabel.setWrapText(true);

      gradeBox.getChildren().addAll(courseLabel, gradeLabel, statusLabel, breakdownLabel);
      finalGradesBox.getChildren().add(gradeBox);
    }
  }
}
