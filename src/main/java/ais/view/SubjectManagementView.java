package ais.view;

import ais.model.Subject;
import ais.model.Teacher;
import ais.service.SubjectService;
import ais.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Subject Management");
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

    Label formTitle = new Label("Subject Form");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label nameLabel = new Label("Name:");
    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    nameField = new TextField();
    nameField.setPromptText("Subject Name (e.g., Mathematics)");
    nameField.setMaxWidth(Double.MAX_VALUE);

    Label languageLabel = new Label("Language:");
    languageLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    languageField = new TextField();
    languageField.setPromptText("Language (e.g., English)");
    languageField.setMaxWidth(Double.MAX_VALUE);

    Label roomLabel = new Label("Room:");
    roomLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    roomField = new TextField();
    roomField.setPromptText("Room (optional, e.g., A-101)");
    roomField.setMaxWidth(Double.MAX_VALUE);

    GridPane buttonGrid = new GridPane();
    buttonGrid.setHgap(10);
    buttonGrid.setVgap(10);

    Button addButton = new Button("Add Subject");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Subject");
    updateButton.getStyleClass().add("button-secondary");
    updateButton.setMaxWidth(Double.MAX_VALUE);
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Subject");
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
        languageLabel,
        languageField,
        roomLabel,
        roomField,
        buttonGrid);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");

    Label listLabel = new Label("Subjects (click to edit):");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    subjectListView = new ListView<>();
    subjectListView.setPrefHeight(200);

    subjectListView.setOnMouseClicked(e -> {
      Subject selected = subjectListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadSubjectToForm(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, subjectListView);

    VBox teacherCard = new VBox(15);
    teacherCard.getStyleClass().add("form-section");

    Label teacherTitle = new Label("Assign Teachers");
    teacherTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label infoLabel = new Label("Select a subject first");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    Label assignedLabel = new Label("Assigned Teachers:");
    assignedLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    assignedTeachersListView = new ListView<>();
    assignedTeachersListView.setPrefHeight(150);

    Button removeTeacherButton = new Button("Remove Selected Teacher");
    removeTeacherButton.getStyleClass().add("button-warning");
    removeTeacherButton.setMaxWidth(Double.MAX_VALUE);
    removeTeacherButton.setOnAction(e -> handleRemoveTeacher());

    Label availableLabel = new Label("Add Teacher:");
    availableLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    availableTeachersCombo = new ComboBox<>();
    availableTeachersCombo.setPromptText("Select teacher to assign");
    availableTeachersCombo.setMaxWidth(Double.MAX_VALUE);

    Button assignTeacherButton = new Button("Assign Teacher");
    assignTeacherButton.getStyleClass().add("button-primary");
    assignTeacherButton.setMaxWidth(Double.MAX_VALUE);
    assignTeacherButton.setOnAction(e -> handleAssignTeacher());

    teacherCard.getChildren().addAll(
        teacherTitle,
        infoLabel,
        assignedLabel,
        assignedTeachersListView,
        removeTeacherButton,
        availableLabel,
        availableTeachersCombo,
        assignTeacherButton);

    contentBox.getChildren().addAll(formCard, listCard, teacherCard);
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
