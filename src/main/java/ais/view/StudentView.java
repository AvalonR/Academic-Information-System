package ais.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class StudentView {

  public TextField nameField = new TextField();
  public TextField ageField = new TextField();
  public TextField addressField = new TextField();
  public Button addButton = new Button("Add Student");
  public Button listButton = new Button("List Students");
  public TextArea output = new TextArea();

  public VBox getView() {
    nameField.setPromptText("Name");
    ageField.setPromptText("Age");
    addressField.setPromptText("Address");
    output.setEditable(false);

    VBox form = new VBox(10, nameField, ageField, addressField, addButton, listButton, output);
    form.setPadding(new javafx.geometry.Insets(15));
    return form;
  }
}
