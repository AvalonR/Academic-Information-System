package ais.view;

import ais.model.Subject;
import ais.model.Teacher;
import ais.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("My Subjects");
    titleLabel.getStyleClass().add("label-title");

    Label infoLabel = new Label("Showing only subjects you teach. You cannot edit subjects.");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    VBox listCard = new VBox(10);
    listCard.getStyleClass().add("form-section");
    VBox.setVgrow(listCard, Priority.ALWAYS);

    Label listLabel = new Label("Your Subjects:");
    listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    subjectListView = new ListView<>();
    subjectListView.setPrefHeight(300);
    VBox.setVgrow(subjectListView, Priority.ALWAYS);

    subjectListView.setOnMouseClicked(e -> {
      Subject selected = subjectListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        showSubjectDetails(selected);
      }
    });

    listCard.getChildren().addAll(listLabel, subjectListView);

    VBox detailsCard = new VBox(10);
    detailsCard.getStyleClass().add("form-section");

    Label detailsLabel = new Label("Subject Details:");
    detailsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    detailsArea = new TextArea();
    detailsArea.setEditable(false);
    detailsArea.setPrefHeight(150);
    detailsArea.setPromptText("Select a subject to view details");
    detailsArea.getStyleClass().add("message-area");

    Button refreshButton = new Button("Refresh List");
    refreshButton.getStyleClass().add("button-default");
    refreshButton.setMaxWidth(Double.MAX_VALUE);
    refreshButton.setOnAction(e -> loadSubjects());

    detailsCard.getChildren().addAll(detailsLabel, detailsArea, refreshButton);

    getChildren().addAll(
        titleLabel,
        infoLabel,
        listCard,
        detailsCard);
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
