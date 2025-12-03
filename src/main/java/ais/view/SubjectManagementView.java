package ais.view;

import ais.model.Subject;
import ais.model.Teacher;
import ais.service.SubjectService;
import ais.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Set;

public class SubjectManagementView extends VBox {

  private final SubjectService subjectService;
  private final TeacherService teacherService;

  private TextField nameField;
  private TextField languageField;
  private TextField roomField;
  private ListView<Subject> subjectListView;
  private ListView<Teacher> assignedTeachersListView;
  private ComboBox<Teacher> availableTeachersCombo;
  private TextArea messageArea;

  private Subject selectedSubject = null;

  public SubjectManagementView(SubjectService subjectService, TeacherService teacherService) {
    this.subjectService = subjectService;
    this.teacherService = teacherService;
    initUI();
    refreshSubjectList();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Subject Management");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    HBox mainContent = new HBox(15);

    VBox leftSide = new VBox(10);
    leftSide.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label formTitle = new Label("Subject Form");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    nameField = new TextField();
    nameField.setPromptText("Subject Name (e.g., Mathematics)");

    languageField = new TextField();
    languageField.setPromptText("Language (e.g., English)");

    roomField = new TextField();
    roomField.setPromptText("Room (optional, e.g., A-101)");

    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Subject");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Subject");
    updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Subject");
    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    deleteButton.setOnAction(e -> handleDelete());

    Button clearButton = new Button("Clear Form");
    clearButton.setOnAction(e -> clearForm());

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

    Label listLabel = new Label("Subjects (click to edit):");
    listLabel.setStyle("-fx-font-weight: bold;");

    subjectListView = new ListView<>();
    subjectListView.setPrefHeight(200);
    VBox.setVgrow(subjectListView, Priority.ALWAYS);

    subjectListView.setOnMouseClicked(e -> {
      Subject selected = subjectListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadSubjectToForm(selected);
      }
    });

    leftSide.getChildren().addAll(
        formTitle,
        new Label("Name:"),
        nameField,
        new Label("Language:"),
        languageField,
        new Label("Room:"),
        roomField,
        buttonBox,
        new Separator(),
        listLabel,
        subjectListView);

    VBox rightSide = new VBox(10);
    rightSide.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");

    Label teacherTitle = new Label("Assign Teachers");
    teacherTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    Label infoLabel = new Label("Select a subject first");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    Label assignedLabel = new Label("Assigned Teachers:");
    assignedLabel.setStyle("-fx-font-weight: bold;");

    assignedTeachersListView = new ListView<>();
    assignedTeachersListView.setPrefHeight(150);
    VBox.setVgrow(assignedTeachersListView, Priority.ALWAYS);

    Button removeTeacherButton = new Button("Remove Selected Teacher");
    removeTeacherButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
    removeTeacherButton.setOnAction(e -> handleRemoveTeacher());

    Label availableLabel = new Label("Add Teacher:");
    availableLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

    availableTeachersCombo = new ComboBox<>();
    availableTeachersCombo.setPromptText("Select teacher to assign");
    availableTeachersCombo.setMaxWidth(Double.MAX_VALUE);

    Button assignTeacherButton = new Button("Assign Teacher");
    assignTeacherButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    assignTeacherButton.setOnAction(e -> handleAssignTeacher());

    rightSide.getChildren().addAll(
        teacherTitle,
        infoLabel,
        assignedLabel,
        assignedTeachersListView,
        removeTeacherButton,
        availableLabel,
        availableTeachersCombo,
        assignTeacherButton);

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
      String language = languageField.getText().trim();
      String room = roomField.getText().trim();

      if (name.isEmpty() || language.isEmpty()) {
        showMessage("Error: Name and Language are required", true);
        return;
      }

      subjectService.createSubject(name, language, room.isEmpty() ? null : room);
      showMessage("Success: Subject added - " + name, false);

      clearForm();
      refreshSubjectList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleUpdate() {
    try {
      if (selectedSubject == null) {
        showMessage("Error: Please select a subject from the list first", true);
        return;
      }

      String name = nameField.getText().trim();
      String language = languageField.getText().trim();
      String room = roomField.getText().trim();

      if (name.isEmpty() || language.isEmpty()) {
        showMessage("Error: Name and Language are required", true);
        return;
      }

      subjectService.updateSubject(selectedSubject.getId(), name, language, room.isEmpty() ? null : room);
      showMessage("Success: Subject updated - " + name, false);

      clearForm();
      refreshSubjectList();

    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleDelete() {
    if (selectedSubject == null) {
      showMessage("Error: Please select a subject from the list first", true);
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Subject");
    alert.setHeaderText("Delete " + selectedSubject.getName() + "?");
    alert.setContentText("This action cannot be undone.");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        try {
          String name = selectedSubject.getName();
          subjectService.deleteSubject(selectedSubject.getId());
          showMessage("Success: Deleted subject - " + name, false);
          clearForm();
          refreshSubjectList();
        } catch (Exception e) {
          showMessage("Error: " + e.getMessage(), true);
        }
      }
    });
  }

  private void handleAssignTeacher() {
    if (selectedSubject == null) {
      showMessage("Error: Please select a subject first", true);
      return;
    }

    Teacher teacher = availableTeachersCombo.getValue();
    if (teacher == null) {
      showMessage("Error: Please select a teacher to assign", true);
      return;
    }

    try {
      subjectService.assignTeacher(selectedSubject.getId(), teacher.getId());
      showMessage("Success: Assigned " + teacher.getName() + " to " + selectedSubject.getName(), false);
      loadSubjectToForm(subjectService.getSubjectById(selectedSubject.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void handleRemoveTeacher() {
    if (selectedSubject == null) {
      showMessage("Error: Please select a subject first", true);
      return;
    }

    Teacher teacher = assignedTeachersListView.getSelectionModel().getSelectedItem();
    if (teacher == null) {
      showMessage("Error: Please select a teacher to remove", true);
      return;
    }

    try {
      subjectService.removeTeacher(selectedSubject.getId(), teacher.getId());
      showMessage("Success: Removed " + teacher.getName() + " from " + selectedSubject.getName(), false);
      loadSubjectToForm(subjectService.getSubjectById(selectedSubject.getId()));
    } catch (Exception e) {
      showMessage("Error: " + e.getMessage(), true);
    }
  }

  private void loadSubjectToForm(Subject subject) {
    selectedSubject = subject;
    nameField.setText(subject.getName());
    languageField.setText(subject.getLanguage());
    roomField.setText(subject.getRoom() != null ? subject.getRoom() : "");

    Set<Teacher> teachers = subjectService.getTeachersForSubject(subject.getId());
    assignedTeachersListView.setItems(FXCollections.observableArrayList(teachers));

    availableTeachersCombo.setItems(FXCollections.observableArrayList(teacherService.getAllTeachers()));

    showMessage("Loaded: " + subject.getName() + " (ID: " + subject.getId() + ") - " +
        teachers.size() + " teacher(s) assigned", false);
  }

  private void clearForm() {
    selectedSubject = null;
    nameField.clear();
    languageField.clear();
    roomField.clear();
    subjectListView.getSelectionModel().clearSelection();
    assignedTeachersListView.getItems().clear();
    availableTeachersCombo.setValue(null);
  }

  private void refreshSubjectList() {
    subjectListView.setItems(FXCollections.observableArrayList(subjectService.getAllSubjects()));
    availableTeachersCombo.setItems(FXCollections.observableArrayList(teacherService.getAllTeachers()));
    showMessage("Subject list refreshed. Total: " + subjectService.getAllSubjects().size(), false);
  }

  private void showMessage(String message, boolean isError) {
    String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    String formattedMessage = "[" + timestamp + "] " + message + "\n";
    messageArea.appendText(formattedMessage);
  }
}
