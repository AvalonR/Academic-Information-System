package ais.view;

import ais.model.Student;
import ais.service.StudentService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StudentListView extends VBox {

  private final StudentService studentService;
  private ListView<Student> studentListView;
  private TextArea detailsArea;

  public StudentListView(StudentService studentService) {
    this.studentService = studentService;
    initUI();
    loadStudents();
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("Students");
    titleLabel.getStyleClass().add("label-title");

    Label infoLabel = new Label("You can view student information but cannot edit.");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    VBox searchCard = new VBox(10);
    searchCard.getStyleClass().add("form-section");
    searchCard.setMaxWidth(Double.MAX_VALUE);

    Label searchLabel = new Label("Search:");
    searchLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

    TextField searchField = new TextField();
    searchField.setPromptText("Search by name...");
    searchField.setMaxWidth(Double.MAX_VALUE);
    searchField.textProperty().addListener((obs, old, newVal) -> filterStudents(newVal));

    searchCard.getChildren().addAll(searchLabel, searchField);

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");
    VBox.setVgrow(listCard, Priority.ALWAYS);

    Label listLabel = new Label("Students:");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    studentListView = new ListView<>();
    studentListView.setPrefHeight(300);
    VBox.setVgrow(studentListView, Priority.ALWAYS);

    studentListView.setOnMouseClicked(e -> {
      Student selected = studentListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        showStudentDetails(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, studentListView);

    VBox detailsCard = new VBox(10);
    detailsCard.getStyleClass().add("form-section");

    Label detailsLabel = new Label("Student Details:");
    detailsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    detailsArea = new TextArea();
    detailsArea.setEditable(false);
    detailsArea.setPrefHeight(150);
    detailsArea.setPromptText("Select a student to view details");
    detailsArea.getStyleClass().add("message-area");

    Button refreshButton = new Button("Refresh List");
    refreshButton.getStyleClass().add("button-default");
    refreshButton.setMaxWidth(Double.MAX_VALUE);
    refreshButton.setOnAction(e -> loadStudents());

    detailsCard.getChildren().addAll(detailsLabel, detailsArea, refreshButton);

    getChildren().addAll(
        titleLabel,
        infoLabel,
        searchCard,
        listCard,
        detailsCard);
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
