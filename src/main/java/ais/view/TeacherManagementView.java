package ais.view;

import ais.model.Teacher;
import ais.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TeacherManagementView extends VBox {

  private final TeacherService teacherService;

  private TextField nameField;
  private TextField addressField;
  private TextField specializationField;
  private ListView<Teacher> teacherListView;
  private TextArea messageArea;

  private Teacher selectedTeacher = null;

  public TeacherManagementView(TeacherService teacherService) {
    this.teacherService = teacherService;
    initUI();
    refreshTeacherList();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Teacher Management");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    VBox formBox = new VBox(10);
    formBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label formTitle = new Label("Teacher Form");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    nameField = new TextField();
    nameField.setPromptText("Full Name (e.g., Robert Smith)");

    addressField = new TextField();
    addressField.setPromptText("Address");

    specializationField = new TextField();
    specializationField.setPromptText("Specialization (e.g., Mathematics, Physics)");

    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Teacher");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Teacher");
    updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Teacher");
    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    deleteButton.setOnAction(e -> handleDelete());

    Button clearButton = new Button("Clear Form");
    clearButton.setOnAction(e -> clearForm());

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

    formBox.getChildren().addAll(
        formTitle,
        new Label("Name:"),
        nameField,
        new Label("Address:"),
        addressField,
        new Label("Specialization:"),
        specializationField,
        buttonBox);

    Label listLabel = new Label("Teachers (click to edit):");
    listLabel.setStyle("-fx-font-weight: bold;");

    teacherListView = new ListView<>();
    teacherListView.setPrefHeight(200);
    VBox.setVgrow(teacherListView, Priority.ALWAYS);

    teacherListView.setOnMouseClicked(e -> {
      Teacher selected = teacherListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadTeacherToForm(selected);
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
        teacherListView,
        messageLabel,
        messageArea);
  }

  private void handleAdd() {
    try {
      String name = nameField.getText().trim();
      String address = addressField.getText().trim();
      String specialization = specializationField.getText().trim();

      if (name.isEmpty() || address.isEmpty()) {
        showMessage("Error: Name and Address are required", true);
        return;
      }

      teacherService.createTeacher(name, address, specialization);
      showMessage("Success: Teacher added - " + name, false);

      clearForm();
      refreshTeacherList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUpdate() {
    try {
      if (selectedTeacher == null) {
        showMessage("Error: Please select a teacher from the list first", true);
        return;
      }

      String name = nameField.getText().trim();
      String address = addressField.getText().trim();
      String specialization = specializationField.getText().trim();

      if (name.isEmpty() || address.isEmpty()) {
        showMessage("Error: Name and Address are required", true);
        return;
      }

      teacherService.updateTeacher(selectedTeacher.getId(), name, address, specialization);
      showMessage("Success: Teacher updated - " + name, false);

      clearForm();
      refreshTeacherList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleDelete() {
    if (selectedTeacher == null) {
      showMessage("Error: Please select a teacher from the list first", true);
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Teacher");
    alert.setHeaderText("Delete " + selectedTeacher.getName() + "?");
    alert.setContentText("This will also delete the teacher's user account. This action cannot be undone.");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        try {
          String name = selectedTeacher.getName();
          teacherService.deleteTeacher(selectedTeacher.getId());
          showMessage("Success: Deleted teacher - " + name, false);
          clearForm();
          refreshTeacherList();
        } catch (Exception e) {
          showMessage("Error: " + e.getMessage(), true);
        }
      }
    });
  }

  private void loadTeacherToForm(Teacher teacher) {
    selectedTeacher = teacher;
    nameField.setText(teacher.getName());
    addressField.setText(teacher.getAddress());
    specializationField.setText(teacher.getSpecialization() != null ? teacher.getSpecialization() : "");
    showMessage("Loaded: " + teacher.getName() + " (ID: " + teacher.getId() + ")", false);
  }

  private void clearForm() {
    selectedTeacher = null;
    nameField.clear();
    addressField.clear();
    specializationField.clear();
    teacherListView.getSelectionModel().clearSelection();
  }

  private void refreshTeacherList() {
    teacherListView.setItems(FXCollections.observableArrayList(teacherService.getAllTeachers()));
    showMessage("Teacher list refreshed. Total: " + teacherService.getAllTeachers().size(), false);
  }

  private void showMessage(String message, boolean isError) {
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
