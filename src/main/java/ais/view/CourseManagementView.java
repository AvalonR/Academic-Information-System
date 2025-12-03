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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Course (Group) Management");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    HBox mainContent = new HBox(15);

    VBox leftSide = new VBox(10);
    leftSide.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label formTitle = new Label("Course Form");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    nameField = new TextField();
    nameField.setPromptText("Course Name (e.g., Computer Science 101)");

    descriptionField = new TextArea();
    descriptionField.setPromptText("Description (optional)");
    descriptionField.setPrefRowCount(3);

    yearField = new TextField();
    yearField.setPromptText("Year (e.g., 2024)");

    semesterCombo = new ComboBox<>();
    semesterCombo.setItems(FXCollections.observableArrayList("Fall", "Spring", "Summer", "Winter"));
    semesterCombo.setPromptText("Select Semester");
    semesterCombo.setMaxWidth(Double.MAX_VALUE);

    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Course");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Course");
    updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Course");
    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    deleteButton.setOnAction(e -> handleDelete());

    Button clearButton = new Button("Clear Form");
    clearButton.setOnAction(e -> clearForm());

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

    Label listLabel = new Label("Courses (click to edit):");
    listLabel.setStyle("-fx-font-weight: bold;");

    courseListView = new ListView<>();
    courseListView.setPrefHeight(150);
    VBox.setVgrow(courseListView, Priority.ALWAYS);

    courseListView.setOnMouseClicked(e -> {
      Course selected = courseListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadCourseToForm(selected);
      }
    });

    leftSide.getChildren().addAll(
        formTitle,
        new Label("Name:"),
        nameField,
        new Label("Description:"),
        descriptionField,
        new Label("Year:"),
        yearField,
        new Label("Semester:"),
        semesterCombo,
        buttonBox,
        new Separator(),
        listLabel,
        courseListView);

    VBox rightSide = new VBox(15);

    VBox subjectsSection = new VBox(10);
    subjectsSection
        .setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label subjectsTitle = new Label("Course Subjects");
    subjectsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    courseSubjectsListView = new ListView<>();
    courseSubjectsListView.setPrefHeight(120);

    Button removeSubjectButton = new Button("Remove Selected Subject");
    removeSubjectButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
    removeSubjectButton.setOnAction(e -> handleRemoveSubject());

    availableSubjectsCombo = new ComboBox<>();
    availableSubjectsCombo.setPromptText("Select subject to add");
    availableSubjectsCombo.setMaxWidth(Double.MAX_VALUE);

    Button addSubjectButton = new Button("Add Subject");
    addSubjectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addSubjectButton.setOnAction(e -> handleAddSubject());

    subjectsSection.getChildren().addAll(
        subjectsTitle,
        new Label("Subjects in this course:"),
        courseSubjectsListView,
        removeSubjectButton,
        new Label("Add subject:"),
        availableSubjectsCombo,
        addSubjectButton);

    VBox studentsSection = new VBox(10);
    studentsSection
        .setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label studentsTitle = new Label("Enrolled Students");
    studentsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    courseStudentsListView = new ListView<>();
    courseStudentsListView.setPrefHeight(120);

    Button unenrollButton = new Button("Unenroll Selected Student");
    unenrollButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
    unenrollButton.setOnAction(e -> handleUnenrollStudent());

    availableStudentsCombo = new ComboBox<>();
    availableStudentsCombo.setPromptText("Select student to enroll");
    availableStudentsCombo.setMaxWidth(Double.MAX_VALUE);

    Button enrollButton = new Button("Enroll Student");
    enrollButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    enrollButton.setOnAction(e -> handleEnrollStudent());

    studentsSection.getChildren().addAll(
        studentsTitle,
        new Label("Students enrolled:"),
        courseStudentsListView,
        unenrollButton,
        new Label("Enroll student:"),
        availableStudentsCombo,
        enrollButton);

    rightSide.getChildren().addAll(subjectsSection, studentsSection);

    HBox.setHgrow(leftSide, Priority.ALWAYS);
    HBox.setHgrow(rightSide, Priority.ALWAYS);
    mainContent.getChildren().addAll(leftSide, rightSide);

    messageArea = new TextArea();
    messageArea.setEditable(false);
    messageArea.setPrefHeight(100);
    messageArea.setStyle("-fx-font-family: monospace; -fx-font-size: 11px;");

    Label messageLabel = new Label("Messages:");
    messageLabel.setStyle("-fx-font-weight: bold;");

    getChildren().addAll(
        titleLabel,
        mainContent,
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
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("H:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
