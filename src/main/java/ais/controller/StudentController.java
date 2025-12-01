package ais.controller;

import ais.service.StudentService;
import ais.view.StudentView;
import ais.model.Student;

public class StudentController {

  private final StudentService service;
  private final StudentView view;

  public StudentController(StudentService service, StudentView view) {
    this.service = service;
    this.view = view;
    attachHandlers();
  }

  private void attachHandlers() {
    view.addButton.setOnAction(e -> addStudent());
    view.listButton.setOnAction(e -> listStudents());
  }

  private void addStudent() {
    try {
      String name = view.nameField.getText();
      int age = Integer.parseInt(view.ageField.getText());
      String address = view.addressField.getText();

      Student s = service.createStudent(name, age, address);
      view.output.appendText("Added: " + s + "\n");

      view.nameField.clear();
      view.ageField.clear();
      view.addressField.clear();
    } catch (Exception ex) {
      view.output.appendText("Error: " + ex.getMessage() + "\n");
    }
  }

  private void listStudents() {
    view.output.appendText("\nAll Students:\n");
    service.getAllStudents().forEach(s -> view.output.appendText(s + "\n"));
  }
}
