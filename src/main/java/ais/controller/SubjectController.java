package ais.controller;

import ais.service.SubjectService;
import ais.view.SubjectView;
import ais.model.Subject;

public class SubjectController {

  private final SubjectService service;
  private final SubjectView view;

  public SubjectController(SubjectService service, SubjectView view) {
    this.service = service;
    this.view = view;
    attachHandlers();
  }

  private void attachHandlers() {
    view.addButton.setOnAction(e -> addSubject());
    view.listButton.setOnAction(e -> listSubjects());
  }

  private void addSubject() {
    try {
      String name = view.nameField.getText();
      String description = view.descriptionField.getText();

      Subject s = service.createSubject(name, description);
      view.output.appendText("Added: " + s + "\n");

      view.nameField.clear();
      view.descriptionField.clear();
    } catch (Exception ex) {
      view.output.appendText("Error: " + ex.getMessage() + "\n");
    }
  }

  private void listSubjects() {
    view.output.appendText("\nAll Subjects:\n");
    service.getAllSubjects().forEach(s -> view.output.appendText(s + "\n"));
  }
}
