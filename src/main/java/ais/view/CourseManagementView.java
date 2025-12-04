package ais.view;

import ais.model.Course;
import ais.model.Student;
import ais.model.Subject;
import ais.service.CourseService;
import ais.service.StudentService;
import ais.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Set;

public class CourseManagementView extends VBox {

  private final CourseService courseService;
  private final SubjectService subjectService;
  private final StudentService studentService;

  private TextField nameField;
  private TextArea descriptionField;
  private TextField yearField;
  private ComboBox<String> semesterCombo;
  private ListView<Course> courseListView;

  private ListView<Subject> courseSubjectsListView;
  private ComboBox<Subject> availableSubjectsCombo;

  private ListView<Student> courseStudentsListView;
  private ComboBox<Student> availableStudentsCombo;

  private TextArea messageArea;

  private Course selectedCourse = null;

  public CourseManagementView(CourseService courseService, SubjectService subjectService,
      StudentService studentService) {
    this.courseService = courseService;
    this.subjectService = subjectService;
    this.studentService = studentService;
    initUI();
    refreshCourseList();
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Course Management");
    titleLabel.getStyleClass().add("label-title");

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background-color: transparent;");
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    VBox contentBox = new VBox(15);
    contentBox.setPadding(new Insets(10));

    VBox formCard = new VBox(15);
    formCard.getStyleClass().add("form-section");
    formCard.setMaxWidth(Double.MAX_VALUE);

    Label formTitle = new Label("Course Form");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label nameLabel = new Label("Name:");
    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    nameField = new TextField();
    nameField.setPromptText("Course Name (e.g., Computer Science 101)");
    nameField.setMaxWidth(Double.MAX_VALUE);

    Label descriptionLabel = new Label("Description:");
    descriptionLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    descriptionField = new TextArea();
    descriptionField.setPromptText("Description (optional)");
    descriptionField.setPrefRowCount(3);
    descriptionField.setMaxWidth(Double.MAX_VALUE);

    Label yearLabel = new Label("Year:");
    yearLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    yearField = new TextField();
    yearField.setPromptText("Year (e.g., 2024)");
    yearField.setMaxWidth(Double.MAX_VALUE);

    Label semesterLabel = new Label("Semester:");
    semesterLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    semesterCombo = new ComboBox<>();
    semesterCombo.setItems(FXCollections.observableArrayList("Fall", "Spring", "Summer", "Winter"));
    semesterCombo.setPromptText("Select Semester");
    semesterCombo.setMaxWidth(Double.MAX_VALUE);

    GridPane buttonGrid = new GridPane();
    buttonGrid.setHgap(10);
    buttonGrid.setVgap(10);

    Button addButton = new Button("Add Course");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Course");
    updateButton.getStyleClass().add("button-secondary");
    updateButton.setMaxWidth(Double.MAX_VALUE);
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Course");
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setMaxWidth(Double.MAX_VALUE);
    deleteButton.setOnAction(e -> handleDelete());

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
        nameLabel,
        nameField,
        descriptionLabel,
        descriptionField,
        yearLabel,
        yearField,
        semesterLabel,
        semesterCombo,
        buttonGrid);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");

    Label listLabel = new Label("Courses (click to edit):");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    courseListView = new ListView<>();
    courseListView.setPrefHeight(200);

    courseListView.setOnMouseClicked(e -> {
      Course selected = courseListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadCourseToForm(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, courseListView);

    VBox subjectsCard = new VBox(15);
    subjectsCard.getStyleClass().add("form-section");

    Label subjectsTitle = new Label("Course Subjects");
    subjectsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label subjectsInLabel = new Label("Subjects in this course:");
    subjectsInLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    courseSubjectsListView = new ListView<>();
    courseSubjectsListView.setPrefHeight(120);

    Button removeSubjectButton = new Button("Remove Selected Subject");
    removeSubjectButton.getStyleClass().add("button-warning");
    removeSubjectButton.setMaxWidth(Double.MAX_VALUE);
    removeSubjectButton.setOnAction(e -> handleRemoveSubject());

    Label addSubjectLabel = new Label("Add subject:");
    addSubjectLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    availableSubjectsCombo = new ComboBox<>();
    availableSubjectsCombo.setPromptText("Select subject to add");
    availableSubjectsCombo.setMaxWidth(Double.MAX_VALUE);
    Button addSubjectButton = new Button("Add Subject");
    addSubjectButton.getStyleClass().add("button-primary");
    addSubjectButton.setMaxWidth(Double.MAX_VALUE);
    addSubjectButton.setOnAction(e -> handleAddSubject());

    subjectsCard.getChildren().addAll(
        subjectsTitle,
        subjectsInLabel,
        courseSubjectsListView,
        removeSubjectButton,
        addSubjectLabel,
        availableSubjectsCombo,
        addSubjectButton);

    VBox studentsCard = new VBox(15);
    studentsCard.getStyleClass().add("form-section");

    Label studentsTitle = new Label("Enrolled Students");
    studentsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label studentsEnrolledLabel = new Label("Students enrolled:");
    studentsEnrolledLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    courseStudentsListView = new ListView<>();
    courseStudentsListView.setPrefHeight(120);

    Button unenrollButton = new Button("Unenroll Selected Student");
    unenrollButton.getStyleClass().add("button-warning");
    unenrollButton.setMaxWidth(Double.MAX_VALUE);
    unenrollButton.setOnAction(e -> handleUnenrollStudent());

    Label enrollStudentLabel = new Label("Enroll student:");
    enrollStudentLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    availableStudentsCombo = new ComboBox<>();
    availableStudentsCombo.setPromptText("Select student to enroll");
    availableStudentsCombo.setMaxWidth(Double.MAX_VALUE);

    Button enrollButton = new Button("Enroll Student");
    enrollButton.getStyleClass().add("button-primary");
    enrollButton.setMaxWidth(Double.MAX_VALUE);
    enrollButton.setOnAction(e -> handleEnrollStudent());

    studentsCard.getChildren().addAll(
        studentsTitle,
        studentsEnrolledLabel,
        courseStudentsListView,
        unenrollButton,
        enrollStudentLabel,
        availableStudentsCombo,
        enrollButton);

    contentBox.getChildren().addAll(formCard, listCard, subjectsCard, studentsCard);
    scrollPane.setContent(contentBox);

    messageArea = new TextArea();
    messageArea.setEditable(false);
    messageArea.setPrefHeight(100);
    messageArea.getStyleClass().add("message-area");

    Label messageLabel = new Label("Messages:");
    messageLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    getChildren().addAll(
        titleLabel,
        scrollPane,
        messageLabel,
        messageArea);
  }

  private void handleAdd() {
    try {
      String name = nameField.getText().trim();
      String description = descriptionField.getText().trim();
      String yearText = yearField.getText().trim();
      String semester = semesterCombo.getValue();

      if (name.isEmpty()) {
        showMessage("Error: Course name is required", true);
        return;
      }

      Integer year = null;
      if (!yearText.isEmpty()) {
        try {
          year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
          showMessage("Error: Year must be a valid number", true);
          return;
        }
      }

      courseService.createCourse(name, description.isEmpty() ? null : description, year, semester);
      showMessage("Success: Course added - " + name, false);

      clearForm();
      refreshCourseList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUpdate() {
    try {
      if (selectedCourse == null) {
        showMessage("Error: Please select a course from the list first", true);
        return;
      }

      String name = nameField.getText().trim();
      String description = descriptionField.getText().trim();
      String yearText = yearField.getText().trim();
      String semester = semesterCombo.getValue();

      if (name.isEmpty()) {
        showMessage("Error: Course name is required", true);
        return;
      }

      Integer year = null;
      if (!yearText.isEmpty()) {
        try {
          year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
          showMessage("Error: Year must be a valid number", true);
          return;
        }
      }

      courseService.updateCourse(selectedCourse.getId(), name,
          description.isEmpty() ? null : description, year, semester);
      showMessage("Success: Course updated - " + name, false);

      clearForm();
      refreshCourseList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleDelete() {
    if (selectedCourse == null) {
      showMessage("Error: Please select a course from the list first", true);
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Course");
    alert.setHeaderText("Delete " + selectedCourse.getName() + "?");
    alert.setContentText("This action cannot be undone.");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        try {
          String name = selectedCourse.getName();
          courseService.deleteCourse(selectedCourse.getId());
          showMessage("Success: Deleted course - " + name, false);
          clearForm();
          refreshCourseList();
        } catch (Exception e) {
          showMessage("Error: " + e.getMessage(), true);
        }
      }
    });
  }

  private void handleAddSubject() {
    if (selectedCourse == null) {
      showMessage("Error: Please select a course first", true);
      return;
    }

    Subject subject = availableSubjectsCombo.getValue();
    if (subject == null) {
      showMessage("Error: Please select a subject to add", true);
      return;
    }

    try {
      courseService.addSubject(selectedCourse.getId(), subject.getId());
      showMessage("Success: Added " + subject.getName() + " to " + selectedCourse.getName(), false);
      loadCourseToForm(courseService.getCourseById(selectedCourse.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleRemoveSubject() {
    if (selectedCourse == null) {
      showMessage("Error: Please select a course first", true);
      return;
    }

    Subject subject = courseSubjectsListView.getSelectionModel().getSelectedItem();
    if (subject == null) {
      showMessage("Error: Please select a subject to remove", true);
      return;
    }

    try {
      courseService.removeSubject(selectedCourse.getId(), subject.getId());
      showMessage("Success: Removed " + subject.getName() + " from " + selectedCourse.getName(), false);
      loadCourseToForm(courseService.getCourseById(selectedCourse.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleEnrollStudent() {
    if (selectedCourse == null) {
      showMessage("Error: Please select a course first", true);
      return;
    }

    Student student = availableStudentsCombo.getValue();
    if (student == null) {
      showMessage("Error: Please select a student to enroll", true);
      return;
    }

    try {
      courseService.enrollStudent(selectedCourse.getId(), student.getId());
      showMessage("Success: Enrolled " + student.getName() + " in " + selectedCourse.getName(), false);
      loadCourseToForm(courseService.getCourseById(selectedCourse.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUnenrollStudent() {
    if (selectedCourse == null) {
      showMessage("Error: Please select a course first", true);
      return;
    }

    Student student = courseStudentsListView.getSelectionModel().getSelectedItem();
    if (student == null) {
      showMessage("Error: Please select a student to unenroll", true);
      return;
    }

    try {
      courseService.unenrollStudent(selectedCourse.getId(), student.getId());
      showMessage("Success: Unenrolled " + student.getName() + " from " + selectedCourse.getName(), false);
      loadCourseToForm(courseService.getCourseById(selectedCourse.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void loadCourseToForm(Course course) {
    selectedCourse = course;
    nameField.setText(course.getName());
    descriptionField.setText(course.getDescription() != null ? course.getDescription() : "");
    yearField.setText(course.getYear() != null ? course.getYear().toString() : "");
    semesterCombo.setValue(course.getSemester());

    Set<Subject> subjects = courseService.getSubjectsForCourse(course.getId());
    courseSubjectsListView.setItems(FXCollections.observableArrayList(subjects));

    Set<Student> students = courseService.getStudentsForCourse(course.getId());
    courseStudentsListView.setItems(FXCollections.observableArrayList(students));

    showMessage("Loaded: " + course.getName() + " - " +
        subjects.size() + " subject(s), " + students.size() + " student(s)", false);
  }

  private void clearForm() {
    selectedCourse = null;
    nameField.clear();
    descriptionField.clear();
    yearField.clear();
    semesterCombo.setValue(null);
    courseListView.getSelectionModel().clearSelection();
    courseSubjectsListView.getItems().clear();
    courseStudentsListView.getItems().clear();
    availableSubjectsCombo.setValue(null);
    availableStudentsCombo.setValue(null);
  }

  private void refreshCourseList() {
    courseListView.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
    availableSubjectsCombo.setItems(FXCollections.observableArrayList(subjectService.getAllSubjects()));
    availableStudentsCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
    showMessage("Course list refreshed. Total: " + courseService.getAllCourses().size(), false);
  }

  private void showMessage(String message, boolean isError) {
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
