package ais.view;

import ais.model.*;
import ais.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.stream.Collectors;
import java.util.List;

public class GradeManagementView extends VBox {

  private final GradeService gradeService;
  private final CourseService courseService;
  private final SubjectService subjectService;
  private final StudentService studentService;
  private final Teacher currentTeacher;
  private final GradeComponentService componentService;
  private ComboBox<GradeComponent> componentCombo;
  private Button manageComponentsButton;

  private ComboBox<Course> courseCombo;
  private ComboBox<Subject> subjectCombo;
  private ComboBox<Student> studentCombo;
  private TextField gradeValueField;
  private TextArea commentsField;
  private ListView<Grade> gradeListView;
  private TextArea messageArea;

  private Grade selectedGrade = null;

  public GradeManagementView(GradeService gradeService, CourseService courseService,
      SubjectService subjectService, StudentService studentService,
      GradeComponentService componentService, Teacher currentTeacher) {
    this.gradeService = gradeService;
    this.courseService = courseService;
    this.subjectService = subjectService;
    this.studentService = studentService;
    this.componentService = componentService;
    this.currentTeacher = currentTeacher;
    initUI();
    refreshGradeList();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Grade Management");
    titleLabel.getStyleClass().add("label-title");

    Label infoLabel = new Label("Teacher: " + currentTeacher.getName());
    infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

    Label scaleLabel = new Label("Grading Scale: 0-10 | Pass ≥5 | Fail <5");
    scaleLabel.setStyle(
        "-fx-font-size: 12px; -fx-text-fill: #2196F3; -fx-font-style: italic; -fx-padding: 10px; -fx-background-color: #e3f2fd; -fx-background-radius: 5px;");

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background-color: transparent;");
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    VBox contentBox = new VBox(15);
    contentBox.setPadding(new Insets(10));

    VBox formCard = new VBox(15);
    formCard.getStyleClass().add("form-section");
    formCard.setMaxWidth(Double.MAX_VALUE);

    Label formTitle = new Label("Grade Entry Form");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label courseLabel = new Label("Course:");
    courseLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    courseCombo = new ComboBox<>();
    courseCombo.setPromptText("Select Course");
    courseCombo.setMaxWidth(Double.MAX_VALUE);

    List<Course> relevantCourses = courseService.getAllCourses().stream()
        .filter(c -> c.getSubjects().stream()
            .anyMatch(s -> s.getTeachers().stream()
                .anyMatch(t -> t.getId().equals(currentTeacher.getId()))))
        .toList();

    courseCombo.setItems(FXCollections.observableArrayList(relevantCourses));

    Label subjectLabel = new Label("Subject:");
    subjectLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    subjectCombo = new ComboBox<>();
    subjectCombo.setPromptText("Select Subject");
    subjectCombo.setMaxWidth(Double.MAX_VALUE);

    List<Subject> teacherSubjects = subjectService.getAllSubjects().stream()
        .filter(s -> s.getTeachers().stream().anyMatch(t -> t.getId().equals(currentTeacher.getId())))
        .toList();
    subjectCombo.setItems(FXCollections.observableArrayList(teacherSubjects));

    Label validationLabel = new Label();
    validationLabel.setWrapText(true);
    validationLabel.setMaxWidth(Double.MAX_VALUE);
    validationLabel.setVisible(false);

    Label componentLabel = new Label("Component:");
    componentLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    componentCombo = new ComboBox<>();
    componentCombo.setPromptText("Select Component (e.g., Homework, Exam)");
    componentCombo.setMaxWidth(Double.MAX_VALUE);

    manageComponentsButton = new Button("Manage Components");
    manageComponentsButton.getStyleClass().add("button-info");
    manageComponentsButton.setMaxWidth(Double.MAX_VALUE);
    manageComponentsButton.setOnAction(e -> showComponentManagement());

    Label studentLabel = new Label("Student:");
    studentLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    studentCombo = new ComboBox<>();
    studentCombo.setPromptText("Select Student");
    studentCombo.setMaxWidth(Double.MAX_VALUE);
    studentCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));

    Label gradeLabel = new Label("Grade Value:");
    gradeLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    gradeValueField = new TextField();
    gradeValueField.setPromptText("Grade (0-10, e.g., 8.5)");
    gradeValueField.setMaxWidth(Double.MAX_VALUE);

    Label commentsLabel = new Label("Comments:");
    commentsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    commentsField = new TextArea();
    commentsField.setPromptText("Comments (optional)");
    commentsField.setPrefRowCount(3);
    commentsField.setMaxWidth(Double.MAX_VALUE);

    GridPane buttonGrid = new GridPane();
    buttonGrid.setHgap(10);
    buttonGrid.setVgap(10);

    Button addButton = new Button("Add Grade");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);
    addButton.setOnAction(e -> handleAddGrade());

    Button updateButton = new Button("Update Grade");
    updateButton.getStyleClass().add("button-secondary");
    updateButton.setMaxWidth(Double.MAX_VALUE);
    updateButton.setOnAction(e -> handleUpdateGrade());

    Button deleteButton = new Button("Delete Grade");
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setMaxWidth(Double.MAX_VALUE);
    deleteButton.setOnAction(e -> handleDeleteGrade());

    Button clearButton = new Button("Clear Form");
    clearButton.getStyleClass().add("button-default");
    clearButton.setMaxWidth(Double.MAX_VALUE);
    clearButton.setOnAction(e -> clearForm());

    buttonGrid.add(addButton, 0, 0);
    buttonGrid.add(updateButton, 1, 0);
    buttonGrid.add(deleteButton, 0, 1);
    buttonGrid.add(clearButton, 1, 1);

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(50);
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(50);
    buttonGrid.getColumnConstraints().addAll(col1, col2);

    formCard.getChildren().addAll(
        formTitle,
        courseLabel,
        courseCombo,
        subjectLabel,
        subjectCombo,
        validationLabel,
        componentLabel,
        componentCombo,
        manageComponentsButton,
        studentLabel,
        studentCombo,
        gradeLabel,
        gradeValueField,
        commentsLabel,
        commentsField,
        buttonGrid);

    VBox gradeListCard = new VBox(10);
    gradeListCard.getStyleClass().add("form-section");

    Label listLabel = new Label("Grades (click to edit):");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    gradeListView = new ListView<>();
    gradeListView.setPrefHeight(250);

    gradeListView.setOnMouseClicked(e -> {
      Grade selected = gradeListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadGradeToForm(selected);
      }
    });

    gradeListCard.getChildren().addAll(listLabel, gradeListView);

    contentBox.getChildren().addAll(formCard, gradeListCard);
    scrollPane.setContent(contentBox);

    messageArea = new TextArea();
    messageArea.setEditable(false);
    messageArea.setPrefHeight(100);
    messageArea.getStyleClass().add("message-area");

    Label messageLabel = new Label("Messages:");
    messageLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    getChildren().addAll(
        titleLabel,
        infoLabel,
        scaleLabel,
        scrollPane,
        messageLabel,
        messageArea);

    courseCombo.setOnAction(e -> {
      updateComponents();
      updateValidationLabel(validationLabel);
    });

    subjectCombo.setOnAction(e -> {
      updateComponents();
      updateValidationLabel(validationLabel);
    });

    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void handleAddGrade() {
    try {
      Course course = courseCombo.getValue();
      Subject subject = subjectCombo.getValue();
      Student student = studentCombo.getValue();
      GradeComponent component = componentCombo.getValue();
      String gradeText = gradeValueField.getText().trim();
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
          component != null ? component.getId() : null,
          gradeValue,
          comments.isEmpty() ? null : comments);

      showMessage("Success: Grade added for " + student.getName() + " - " +
          GradeService.getPassFailStatus(gradeValue), false);
      clearForm();
      refreshGradeList();

    } catch (NumberFormatException e) {
      showMessage("Error: Grade must be a valid number", true);
    } catch (IllegalArgumentException e) {
      showMessage("Validation Error: " + e.getMessage(), true);
      showAlert("Cannot Add Grade", e.getMessage(), Alert.AlertType.ERROR);
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
      String comments = commentsField.getText().trim();

      if (gradeText.isEmpty()) {
        showMessage("Error: Grade value is required", true);
        return;
      }

      Double gradeValue = Double.parseDouble(gradeText);

      gradeService.updateGrade(
          selectedGrade.getId(),
          gradeValue,
          comments.isEmpty() ? null : comments);

      showMessage("Success: Grade updated - " + GradeService.getPassFailStatus(gradeValue), false);
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
    componentCombo.setValue(grade.getComponent());
    gradeValueField.setText(grade.getGradeValue().toString());
    commentsField.setText(grade.getComments() != null ? grade.getComments() : "");
    showMessage("Loaded: " + grade, false);
  }

  private void clearForm() {
    selectedGrade = null;
    courseCombo.setValue(null);
    subjectCombo.setValue(null);
    studentCombo.setValue(null);
    componentCombo.setValue(null);
    gradeValueField.clear();
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

  private void updateComponents() {
    Course course = courseCombo.getValue();
    Subject subject = subjectCombo.getValue();

    if (course != null && subject != null) {
      List<GradeComponent> components = componentService.getComponentsByCourseAndSubject(
          course.getId(), subject.getId());
      componentCombo.setItems(FXCollections.observableArrayList(components));

      double totalWeight = componentService.getTotalWeightForCourseSubject(
          course.getId(), subject.getId());
      showMessage(String.format("Total weight for %s in %s: %.0f%%",
          subject.getName(), course.getName(), totalWeight * 100), false);

      updateValidStudents(course, subject);
    } else {
      studentCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
    }
  }

  private void updateValidStudents(Course course, Subject subject) {
    List<Student> enrolledStudents = course.getStudents().stream()
        .collect(Collectors.toList());

    studentCombo.setItems(FXCollections.observableArrayList(enrolledStudents));

    if (enrolledStudents.isEmpty()) {
      showMessage(String.format("Warning: No students enrolled in '%s'", course.getName()), true);
    } else {
      showMessage(String.format("Showing %d students enrolled in '%s'",
          enrolledStudents.size(), course.getName()), false);
    }
  }

  private void showComponentManagement() {
    Course course = courseCombo.getValue();
    Subject subject = subjectCombo.getValue();

    if (course == null || subject == null) {
      showAlert("Error", "Please select a course and subject first", Alert.AlertType.ERROR);
      return;
    }

    Stage componentStage = new Stage();
    componentStage.setTitle("Grade Components");

    VBox layout = new VBox(15);
    layout.setPadding(new Insets(20));
    layout.setStyle("-fx-background-color: #f5f7fa;");

    Label titleLabel = new Label("Grade Components");
    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

    Label subtitleLabel = new Label(subject.getName() + " in " + course.getName());
    subtitleLabel.setStyle("-fx-text-fill: #666;");

    VBox formCard = new VBox(10);
    formCard.getStyleClass().add("form-section");

    Label formTitle = new Label("Add New Component");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    TextField nameField = new TextField();
    nameField.setPromptText("Component name (e.g., Homework, Exam)");
    nameField.setMaxWidth(Double.MAX_VALUE);

    HBox weightBox = new HBox(10);
    TextField weightField = new TextField();
    weightField.setPromptText("0.20");
    weightField.setPrefWidth(100);
    Label weightLabel = new Label("(e.g., 0.2 for 20%)");
    weightLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
    weightBox.getChildren().addAll(weightField, weightLabel);

    Button addButton = new Button("Add Component");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);

    formCard.getChildren().addAll(
        formTitle,
        new Label("Component Name:"),
        nameField,
        new Label("Weight:"),
        weightBox,
        addButton);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");

    Label listTitle = new Label("Existing Components");
    listTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    ListView<GradeComponent> componentList = new ListView<>();
    componentList.setPrefHeight(200);

    Button deleteButton = new Button("Delete Selected");
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setMaxWidth(Double.MAX_VALUE);

    Label totalLabel = new Label();
    totalLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

    listCard.getChildren().addAll(listTitle, componentList, deleteButton, totalLabel);

    updateTotalWeight(totalLabel, course.getId(), subject.getId());
    componentList.setItems(FXCollections.observableArrayList(
        componentService.getComponentsByCourseAndSubject(course.getId(), subject.getId())));

    addButton.setOnAction(e -> {
      try {
        String name = nameField.getText().trim();
        Double weight = Double.parseDouble(weightField.getText().trim());

        componentService.createComponent(name, weight, course.getId(), subject.getId());

        componentList.setItems(FXCollections.observableArrayList(
            componentService.getComponentsByCourseAndSubject(course.getId(), subject.getId())));
        updateTotalWeight(totalLabel, course.getId(), subject.getId());
        updateComponents();

        nameField.clear();
        weightField.clear();

        showAlert("Success", "Component added successfully", Alert.AlertType.INFORMATION);
      } catch (Exception ex) {
        showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
      }
    });

    deleteButton.setOnAction(e -> {
      GradeComponent selected = componentList.getSelectionModel().getSelectedItem();
      if (selected != null) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + selected.getName() + "?");
        confirm.setContentText("This will remove the component and may affect final grade calculations.");

        confirm.showAndWait().ifPresent(response -> {
          if (response == ButtonType.OK) {
            componentService.deleteComponent(selected.getId());
            componentList.setItems(FXCollections.observableArrayList(
                componentService.getComponentsByCourseAndSubject(course.getId(), subject.getId())));
            updateTotalWeight(totalLabel, course.getId(), subject.getId());
            updateComponents();
          }
        });
      }
    });

    layout.getChildren().addAll(titleLabel, subtitleLabel, formCard, listCard);

    Scene scene = new Scene(layout, 500, 650);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    componentStage.setScene(scene);
    componentStage.show();
  }

  private void showAlert(String title, String content, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void updateTotalWeight(Label label, Integer courseId, Integer subjectId) {
    double total = componentService.getTotalWeightForCourseSubject(courseId, subjectId);
    label.setText(String.format("Total Weight: %.0f%% (should be 100%%)", total * 100));
    label.setStyle(
        total == 1.0
            ? "-fx-text-fill: green; -fx-font-weight: bold;"
            : "-fx-text-fill: red; -fx-font-weight: bold;");
  }

  private void updateValidationLabel(Label validationLabel) {
    Course course = courseCombo.getValue();
    Subject subject = subjectCombo.getValue();

    if (course != null && subject != null) {
      boolean subjectInCourse = course.getSubjects().stream()
          .anyMatch(s -> s.getId().equals(subject.getId()));

      boolean teacherAssigned = subject.getTeachers().stream()
          .anyMatch(t -> t.getId().equals(currentTeacher.getId()));

      if (!subjectInCourse) {
        validationLabel
            .setText("Warning: Subject '" + subject.getName() + "' is not part of course '" + course.getName() + "'");
        validationLabel.setVisible(true);
        validationLabel.setStyle(
            "-fx-text-fill: #721c24; -fx-font-size: 11px; -fx-padding: 8px; -fx-background-color: #f8d7da; -fx-border-color: #f44336; -fx-border-width: 1px; -fx-border-radius: 3px; -fx-background-radius: 3px;");
      } else if (!teacherAssigned) {
        validationLabel.setText("Warning: You are not assigned to teach '" + subject.getName() + "'");
        validationLabel.setVisible(true);
        validationLabel.setStyle(
            "-fx-text-fill: #721c24; -fx-font-size: 11px; -fx-padding: 8px; -fx-background-color: #f8d7da; -fx-border-color: #f44336; -fx-border-width: 1px; -fx-border-radius: 3px; -fx-background-radius: 3px;");
      } else {
        validationLabel.setText("Valid selection - You can grade students in this course/subject");
        validationLabel.setVisible(true);
        validationLabel.setStyle(
            "-fx-text-fill: #155724; -fx-font-size: 11px; -fx-padding: 8px; -fx-background-color: #d4edda; -fx-border-color: #4CAF50; -fx-border-width: 1px; -fx-border-radius: 3px; -fx-background-radius: 3px;");
      }
    } else {
      validationLabel.setVisible(false);
    }
  }
}
