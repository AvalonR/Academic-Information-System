package ais.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SubjectView {

  public TextField nameField = new TextField();
  public TextField descriptionField = new TextField();
  public Button addButton = new Button("Add Subject");
  public Button listButton = new Button("List Subjects");
  public TextArea output = new TextArea();

  public VBox getView() {
    nameField.setPromptText("Name");
    descriptionField.setPromptText("Description");
    output.setEditable(false);

    VBox form = new VBox(10, nameField, descriptionField, addButton, listButton, output);
    form.setPadding(new javafx.geometry.Insets(15));
    return form;
  }
}
