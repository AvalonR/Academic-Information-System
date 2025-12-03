package ais.view;

import ais.model.*;
import ais.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class GradeManagementView extends VBox {

  private final GradeService gradeService;
  private final CourseService courseService;
  private final SubjectService subjectService;
  private final StudentService studentService;
  private final Teacher currentTeacher;

  private ComboBox<Course> courseCombo;
  private ComboBox<Subject> subjectCombo;
  private ComboBox<Student> studentCombo;
  private TextField gradeValueField;
  private TextField letterGradeField;
  private TextArea commentsField;
  private ListView<Grade> gradeListView;
  private TextArea messageArea;

  private Grade selectedGrade = null;

  public GradeManagementView(GradeService gradeService, CourseService courseService,
      SubjectService subjectService, StudentService studentService,
      Teacher currentTeacher) {
    this.gradeService = gradeService;
    this.courseService = courseService;
    this.subjectService = subjectService;
    this.studentService = studentService;
    this.currentTeacher = currentTeacher;
    initUI();
    refreshGradeList();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Grade Management");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label infoLabel = new Label("Teacher: " + currentTeacher.getName());
    infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

    VBox formBox = new VBox(10);
    formBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label formTitle = new Label("Grade Entry Form");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    Label scaleLabel = new Label("Grading Scale: 0-10 (9-10: A, 8-9: B, 7-8: C, 6-7: D, 5-6: E, 0-5: F)");
    scaleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2196F3; -fx-font-style: italic;");

    courseCombo = new ComboBox<>();
    courseCombo.setPromptText("Select Course");
    courseCombo.setMaxWidth(Double.MAX_VALUE);
    courseCombo.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));

    subjectCombo = new ComboBox<>();
    subjectCombo.setPromptText("Select Subject");
    subjectCombo.setMaxWidth(Double.MAX_VALUE);

    List<Subject> teacherSubjects = subjectService.getAllSubjects().stream()
        .filter(s -> s.getTeachers().stream().anyMatch(t -> t.getId().equals(currentTeacher.getId())))
        .toList();
    subjectCombo.setItems(FXCollections.observableArrayList(teacherSubjects));

    studentCombo = new ComboBox<>();
    studentCombo.setPromptText("Select Student");
    studentCombo.setMaxWidth(Double.MAX_VALUE);
    studentCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));

    gradeValueField = new TextField();
    gradeValueField.setPromptText("Grade (0-10, e.g., 8.5)");
    gradeValueField.textProperty().addListener((obs, old, newVal) -> {
      if (!newVal.isEmpty()) {
        try {
          double value = Double.parseDouble(newVal);
          letterGradeField.setText(GradeService.calculateLetterGrade(value));
        } catch (NumberFormatException e) {
        }
      }
    });

    letterGradeField = new TextField();
    letterGradeField.setPromptText("Letter Grade (auto-calculated)");

    commentsField = new TextArea();
    commentsField.setPromptText("Comments (optional)");
    commentsField.setPrefRowCount(3);

    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Grade");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addButton.setOnAction(e -> handleAddGrade());

    Button updateButton = new Button("Update Grade");
    updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    updateButton.setOnAction(e -> handleUpdateGrade());

    Button deleteButton = new Button("Delete Grade");
    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    deleteButton.setOnAction(e -> handleDeleteGrade());

    Button clearButton = new Button("Clear Form");
    clearButton.setOnAction(e -> clearForm());

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

    formBox.getChildren().addAll(
        formTitle,
        new Label("Course:"),
        courseCombo,
        new Label("Subject:"),
        subjectCombo,
        new Label("Student:"),
        studentCombo,
        new Label("Grade Value:"),
        gradeValueField,
        new Label("Letter Grade:"),
        letterGradeField,
        new Label("Comments:"),
        commentsField,
        new Label("Grade Scale:"),
        scaleLabel,
        buttonBox);

    Label listLabel = new Label("Grades (click to edit):");
    listLabel.setStyle("-fx-font-weight: bold;");

    gradeListView = new ListView<>();
    gradeListView.setPrefHeight(200);
    VBox.setVgrow(gradeListView, Priority.ALWAYS);

    gradeListView.setOnMouseClicked(e -> {
      Grade selected = gradeListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadGradeToForm(selected);
      }
    });

    messageArea = new TextArea();
    messageArea.setEditable(false);
    messageArea.setPrefHeight(100);
    messageArea.setStyle("-fx-font-family: monospace; -fx-font-size: 11px;");

    Label messageLabel = new Label("Messages:");
    messageLabel.setStyle("-fx-font-weight: bold;");

    getChildren().addAll(
        titleLabel,
        infoLabel,
        formBox,
        listLabel,
        gradeListView,
        messageLabel,
        messageArea);
  }

  private void handleAddGrade() {
    try {
      Course course = courseCombo.getValue();
      Subject subject = subjectCombo.getValue();
      Student student = studentCombo.getValue();
      String gradeText = gradeValueField.getText().trim();
      String letterGrade = letterGradeField.getText().trim();
      String comments = commentsField.getText().trim();

      if (course == null || subject == null || student == null || gradeText.isEmpty()) {
        showMessage("Error: Course, Subject, Student, and Grade are required", true);
        return;
      }

      Double gradeValue = Double.parseDouble(gradeText);

      gradeService.createGrade(
          student.getId(),
          subject.getId(),
          currentTeacher.getId(),
          course.getId(),
          gradeValue,
          letterGrade.isEmpty() ? null : letterGrade,
          comments.isEmpty() ? null : comments);

      showMessage("Success: Grade added for " + student.getName(), false);
      clearForm();
      refreshGradeList();

    } catch (NumberFormatException e) {
      showMessage("Error: Grade must be a valid number", true);
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUpdateGrade() {
    try {
      if (selectedGrade == null) {
        showMessage("Error: Please select a grade from the list first", true);
        return;
      }

      String gradeText = gradeValueField.getText().trim();
      String letterGrade = letterGradeField.getText().trim();
      String comments = commentsField.getText().trim();

      if (gradeText.isEmpty()) {
        showMessage("Error: Grade value is required", true);
        return;
      }

      Double gradeValue = Double.parseDouble(gradeText);

      gradeService.updateGrade(
          selectedGrade.getId(),
          gradeValue,
          letterGrade.isEmpty() ? null : letterGrade,
          comments.isEmpty() ? null : comments);

      showMessage("Success: Grade updated", false);
      clearForm();
      refreshGradeList();

    } catch (NumberFormatException e) {
      showMessage("Error: Grade must be a valid number", true);
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleDeleteGrade() {
    if (selectedGrade == null) {
      showMessage("Error: Please select a grade from the list first", true);
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Grade");
    alert.setHeaderText("Delete this grade?");
    alert.setContentText("This action cannot be undone.");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        try {
          gradeService.deleteGrade(selectedGrade.getId());
          showMessage("Success: Grade deleted", false);
          clearForm();
          refreshGradeList();
        } catch (Exception e) {
          showMessage("Error: " + e.getMessage(), true);
        }
      }
    });
  }

  private void loadGradeToForm(Grade grade) {
    selectedGrade = grade;
    courseCombo.setValue(grade.getCourse());
    subjectCombo.setValue(grade.getSubject());
    studentCombo.setValue(grade.getStudent());
    gradeValueField.setText(grade.getGradeValue().toString());
    letterGradeField.setText(grade.getLetterGrade() != null ? grade.getLetterGrade() : "");
    commentsField.setText(grade.getComments() != null ? grade.getComments() : "");
    showMessage("Loaded: " + grade, false);
  }

  private void clearForm() {
    selectedGrade = null;
    courseCombo.setValue(null);
    subjectCombo.setValue(null);
    studentCombo.setValue(null);
    gradeValueField.clear();
    letterGradeField.clear();
    commentsField.clear();
    gradeListView.getSelectionModel().clearSelection();
  }

  private void refreshGradeList() {
    List<Grade> grades = gradeService.getGradesByTeacher(currentTeacher.getId());
    gradeListView.setItems(FXCollections.observableArrayList(grades));
    showMessage("Grade list refreshed. Total: " + grades.size(), false);
  }

  private void showMessage(String message, boolean isError) {
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
