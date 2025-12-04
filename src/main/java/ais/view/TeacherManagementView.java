package ais.view;

import ais.model.Teacher;
import ais.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Teacher Management");
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

    Label formTitle = new Label("Teacher Form");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label nameLabel = new Label("Name:");
    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    nameField = new TextField();
    nameField.setPromptText("Full Name (e.g., Robert Smith)");
    nameField.setMaxWidth(Double.MAX_VALUE);

    Label addressLabel = new Label("Address:");
    addressLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    addressField = new TextField();
    addressField.setPromptText("Address");
    addressField.setMaxWidth(Double.MAX_VALUE);

    Label specializationLabel = new Label("Specialization:");
    specializationLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    specializationField = new TextField();
    specializationField.setPromptText("Specialization (e.g., Mathematics, Physics)");
    specializationField.setMaxWidth(Double.MAX_VALUE);

    GridPane buttonGrid = new GridPane();
    buttonGrid.setHgap(10);
    buttonGrid.setVgap(10);

    Button addButton = new Button("Add Teacher");
    addButton.getStyleClass().add("button-primary");
    addButton.setMaxWidth(Double.MAX_VALUE);
    addButton.setOnAction(e -> handleAdd());

    Button updateButton = new Button("Update Teacher");
    updateButton.getStyleClass().add("button-secondary");
    updateButton.setMaxWidth(Double.MAX_VALUE);
    updateButton.setOnAction(e -> handleUpdate());

    Button deleteButton = new Button("Delete Teacher");
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
        addressLabel,
        addressField,
        specializationLabel,
        specializationField,
        buttonGrid);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");

    Label listLabel = new Label("Teachers (click to edit):");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    teacherListView = new ListView<>();
    teacherListView.setPrefHeight(250);

    teacherListView.setOnMouseClicked(e -> {
      Teacher selected = teacherListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        loadTeacherToForm(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, teacherListView);

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
