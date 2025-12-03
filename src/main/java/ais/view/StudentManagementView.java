package ais.view;

import ais.model.Student;
import ais.service.StudentService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentManagementView extends VBox {

  private final StudentService studentService;

  private TextField nameField;
  private TextField ageField;
  private TextField addressField;
  private ListView<Student> studentListView;
  private TextArea messageArea;

  private Student selectedStudent = null;

  public StudentManagementView(StudentService studentService) {
    this.studentService = studentService;
    initUI();
    refreshStudentList();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Student Management");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    VBox formBox = new VBox(10);
    formBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label formTitle = new Label("Student Form");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    nameField = new TextField();
    nameField.setPromptText("Full Name (e.g., John Doe)");

    ageField = new TextField();
    ageField.setPromptText("Age");

    addressField = new TextField();
    addressField.setPromptText("Address");

    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Student");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Student");
    updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Student");
    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    deleteButton.setOnAction(e -> handleDelete());

    Button clearButton = new Button("Clear Form");
    clearButton.setOnAction(e -> clearForm());

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

    formBox.getChildren().addAll(
        formTitle,
        new Label("Name:"),
        nameField,
        new Label("Age:"),
        ageField,
        new Label("Address:"),
        addressField,
        buttonBox);

    Label listLabel = new Label("Students (click to edit):");
    listLabel.setStyle("-fx-font-weight: bold;");

    studentListView = new ListView<>();
    studentListView.setPrefHeight(200);
    VBox.setVgrow(studentListView, Priority.ALWAYS);

    studentListView.setOnMouseClicked(e -> {
      Student selected = studentListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadStudentToForm(selected);
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
        formBox,
        listLabel,
        studentListView,
        messageLabel,
        messageArea);
  }

  private void handleAdd() {
    try {
      String name = nameField.getText().trim();
      String ageText = ageField.getText().trim();
      String address = addressField.getText().trim();

      if (name.isEmpty() || ageText.isEmpty() || address.isEmpty()) {
        showMessage("Error: All fields are required", true);
        return;
      }

      Integer age = Integer.parseInt(ageText);

      studentService.createStudent(name, age, address);
      showMessage("Success: Student added - " + name, false);

      clearForm();
      refreshStudentList();

    } catch (NumberFormatException e) {
      showMessage("Error: Age must be a valid number", true);
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUpdate() {
    try {
      if (selectedStudent == null) {
        showMessage("Error: Please select a student from the list first", true);
        return;
      }

      String name = nameField.getText().trim();
      String ageText = ageField.getText().trim();
      String address = addressField.getText().trim();

      if (name.isEmpty() || ageText.isEmpty() || address.isEmpty()) {
        showMessage("Error: All fields are required", true);
        return;
      }

      Integer age = Integer.parseInt(ageText);

      studentService.updateStudent(selectedStudent.getId(), name, age, address);
      showMessage("Success: Student updated - " + name, false);

      clearForm();
      refreshStudentList();

    } catch (NumberFormatException e) {
      showMessage("Error: Age must be a valid number", true);
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleDelete() {
    if (selectedStudent == null) {
      showMessage("Error: Please select a student from the list first", true);
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Student");
    alert.setHeaderText("Delete " + selectedStudent.getName() + "?");
    alert.setContentText("This will also delete the student's user account. This action cannot be undone.");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        try {
          String name = selectedStudent.getName();
          studentService.deleteStudent(selectedStudent.getId());
          showMessage("Success: Deleted student - " + name, false);
          clearForm();
          refreshStudentList();
        } catch (Exception e) {
          showMessage("Error: " + e.getMessage(), true);
        }
      }
    });
  }

  private void loadStudentToForm(Student student) {
    selectedStudent = student;
    nameField.setText(student.getName());
    ageField.setText(student.getAge().toString());
    addressField.setText(student.getAddress());
    showMessage("Loaded: " + student.getName() + " (ID: " + student.getId() + ")", false);
  }

  private void clearForm() {
    selectedStudent = null;
    nameField.clear();
    ageField.clear();
    addressField.clear();
    studentListView.getSelectionModel().clearSelection();
  }

  private void refreshStudentList() {
    studentListView.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
    showMessage("Student list refreshed. Total: " + studentService.getAllStudents().size(), false);
  }

  private void showMessage(String message, boolean isError) {
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
