package ais.view;

import ais.model.Student;
import ais.service.StudentService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class StudentListView extends VBox {

  private final StudentService studentService;
  private ListView<Student> studentListView;
  private TextArea detailsArea;

  public StudentListView(StudentService studentService) {
    this.studentService = studentService;
    initUI();
    loadStudents();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Students (Read-Only)");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label infoLabel = new Label("You can view student information but cannot edit.");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    TextField searchField = new TextField();
    searchField.setPromptText("Search by name...");
    searchField.textProperty().addListener((obs, old, newVal) -> filterStudents(newVal));

    Label listLabel = new Label("Students:");
    listLabel.setStyle("-fx-font-weight: bold;");

    studentListView = new ListView<>();
    studentListView.setPrefHeight(300);

    studentListView.setOnMouseClicked(e -> {
      Student selected = studentListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        showStudentDetails(selected);
      }
    });

    Label detailsLabel = new Label("Student Details:");
    detailsLabel.setStyle("-fx-font-weight: bold;");

    detailsArea = new TextArea();
    detailsArea.setEditable(false);
    detailsArea.setPrefHeight(150);
    detailsArea.setPromptText("Select a student to view details");

    Button refreshButton = new Button("Refresh List");
    refreshButton.setOnAction(e -> loadStudents());

    getChildren().addAll(
        titleLabel,
        infoLabel,
        new Label("Search:"),
        searchField,
        listLabel,
        studentListView,
        detailsLabel,
        detailsArea,
        refreshButton);
  }

  private void loadStudents() {
    studentListView.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
  }

  private void filterStudents(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
      loadStudents();
    } else {
      var filtered = studentService.getAllStudents().stream()
          .filter(s -> s.getName().toLowerCase().contains(searchText.toLowerCase()))
          .toList();
      studentListView.setItems(FXCollections.observableArrayList(filtered));
    }
  }

  private void showStudentDetails(Student student) {
    StringBuilder details = new StringBuilder();
    details.append("ID: ").append(student.getId()).append("\n");
    details.append("Name: ").append(student.getName()).append("\n");
    details.append("Age: ").append(student.getAge()).append("\n");
    details.append("Address: ").append(student.getAddress()).append("\n");
    if (student.getUser() != null) {
      details.append("Username: ").append(student.getUser().getUsername()).append("\n");
    }
    detailsArea.setText(details.toString());
  }
}
