package ais.view;

import ais.model.Subject;
import ais.model.Teacher;
import ais.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class SubjectListView extends VBox {

  private final SubjectService subjectService;
  private final Teacher currentTeacher;
  private ListView<Subject> subjectListView;
  private TextArea detailsArea;

  public SubjectListView(SubjectService subjectService, Teacher currentTeacher) {
    this.subjectService = subjectService;
    this.currentTeacher = currentTeacher;
    initUI();
    loadSubjects();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("My Subjects (Read-Only)");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label infoLabel = new Label("Showing only subjects you teach. You cannot edit subjects.");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    Label listLabel = new Label("Your Subjects:");
    listLabel.setStyle("-fx-font-weight: bold;");

    subjectListView = new ListView<>();
    subjectListView.setPrefHeight(300);

    subjectListView.setOnMouseClicked(e -> {
      Subject selected = subjectListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        showSubjectDetails(selected);
      }
    });

    Label detailsLabel = new Label("Subject Details:");
    detailsLabel.setStyle("-fx-font-weight: bold;");

    detailsArea = new TextArea();
    detailsArea.setEditable(false);
    detailsArea.setPrefHeight(150);
    detailsArea.setPromptText("Select a subject to view details");

    Button refreshButton = new Button("Refresh List");
    refreshButton.setOnAction(e -> loadSubjects());

    getChildren().addAll(
        titleLabel,
        infoLabel,
        listLabel,
        subjectListView,
        detailsLabel,
        detailsArea,
        refreshButton);
  }

  private void loadSubjects() {
    List<Subject> mySubjects = subjectService.getAllSubjects().stream()
        .filter(s -> s.getTeachers().stream()
            .anyMatch(t -> t.getId().equals(currentTeacher.getId())))
        .toList();

    subjectListView.setItems(FXCollections.observableArrayList(mySubjects));
  }

  private void showSubjectDetails(Subject subject) {
    StringBuilder details = new StringBuilder();
    details.append("ID: ").append(subject.getId()).append("\n");
    details.append("Name: ").append(subject.getName()).append("\n");
    details.append("Language: ").append(subject.getLanguage()).append("\n");
    if (subject.getRoom() != null) {
      details.append("Room: ").append(subject.getRoom()).append("\n");
    }
    details.append("\nTeachers assigned:\n");
    subject.getTeachers().forEach(t -> details.append("  - ").append(t.getName()).append("\n"));

    detailsArea.setText(details.toString());
  }
}
