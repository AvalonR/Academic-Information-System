package ais.controller;

import ais.service.TeacherService;
import ais.view.TeacherView;
import ais.model.Teacher;

public class TeacherController {

  private final TeacherService service;
  private final TeacherView view;

  public TeacherController(TeacherService service, TeacherView view) {
    this.service = service;
    this.view = view;
    attachHandlers();
  }

  private void attachHandlers() {
    view.addButton.setOnAction(e -> addTeacher());
    view.listButton.setOnAction(e -> listTeachers());
  }

  private void addTeacher() {
    try {
      String name = view.nameField.getText();
      String subject = view.subjectField.getText();
      String address = view.addressField.getText();

      Teacher s = service.createTeacher(name, subject, address);
      view.output.appendText("Added: " + s + "\n");

      view.nameField.clear();
      view.subjectField.clear();
      view.addressField.clear();
    } catch (Exception ex) {
      view.output.appendText("Error: " + ex.getMessage() + "\n");
    }
  }

  private void listTeachers() {
    view.output.appendText("\nAll Teachers:\n");
    service.getAllTeachers().forEach(s -> view.output.appendText(s + "\n"));
  }
}
