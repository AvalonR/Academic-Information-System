package ais.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class TeacherView {

  public TextField nameField = new TextField();
  public TextField subjectField = new TextField();
  public TextField addressField = new TextField();
  public Button addButton = new Button("Add Teacher");
  public Button listButton = new Button("List Teachers");
  public TextArea output = new TextArea();

  public VBox getView() {
    nameField.setPromptText("Name");
    subjectField.setPromptText("Subject");
    addressField.setPromptText("Address");
    output.setEditable(false);

    VBox form = new VBox(10, nameField, subjectField, addressField, addButton, listButton, output);
    form.setPadding(new javafx.geometry.Insets(15));
    return form;
  }
}
