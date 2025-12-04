package ais.view;

import ais.model.Student;
import ais.service.StudentService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Student Management");
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

    Label formTitle = new Label("Student Form");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label nameLabel = new Label("Name:");
    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    nameField = new TextField();
    nameField.setPromptText("Full Name (e.g., John Doe)");
    nameField.setMaxWidth(Double.MAX_VALUE);

    Label ageLabel = new Label("Age:");
    ageLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    ageField = new TextField();
    ageField.setPromptText("Age");
    ageField.setMaxWidth(Double.MAX_VALUE);

    Label addressLabel = new Label("Address:");
    addressLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    addressField = new TextField();
    addressField.setPromptText("Address");
    addressField.setMaxWidth(Double.MAX_VALUE);

    GridPane buttonGrid = new GridPane();
    buttonGrid.setHgap(10);
    buttonGrid.setVgap(10);

    Button addButton = new Button("Add Student");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Student");
    updateButton.getStyleClass().add("button-secondary");
    updateButton.setMaxWidth(Double.MAX_VALUE);
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Student");
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
        ageLabel,
        ageField,
        addressLabel,
        addressField,
        buttonGrid);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");

    Label listLabel = new Label("Students (click to edit):");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    studentListView = new ListView<>();
    studentListView.setPrefHeight(250);

    studentListView.setOnMouseClicked(e -> {
      Student selected = studentListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadStudentToForm(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, studentListView);

    contentBox.getChildren().addAll(formCard, listCard);
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
