package ais.view;

import ais.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

  private final AuthService authService;
  private final Stage stage;
  private final Runnable onLoginSuccess;

  private TextField usernameField;
  private PasswordField passwordField;
  private Label messageLabel;

  public LoginView(AuthService authService, Runnable onLoginSuccess) {
    this.authService = authService;
    this.onLoginSuccess = onLoginSuccess;
    this.stage = new Stage();
    initUI();
  }

  private void initUI() {
    VBox root = new VBox(15);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(30));
    root.setStyle("-fx-background-color: #f5f5f5;");

    Label titleLabel = new Label("Academic Information System");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    usernameField = new TextField();
    usernameField.setPromptText("Username");
    usernameField.setMaxWidth(250);

    passwordField = new PasswordField();
    passwordField.setPromptText("Password");
    passwordField.setMaxWidth(250);

    Button loginButton = new Button("Login");
    loginButton.setPrefWidth(250);
    loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

    messageLabel = new Label();
    messageLabel.setStyle("-fx-text-fill: red;");

    Label infoLabel = new Label("Default: admin / admin");
    infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

    loginButton.setOnAction(e -> handleLogin());
    passwordField.setOnAction(e -> handleLogin());

    root.getChildren().addAll(
        titleLabel,
        new Label("Username:"),
        usernameField,
        new Label("Password:"),
        passwordField,
        loginButton,
        messageLabel,
        infoLabel);

    Scene scene = new Scene(root, 400, 350);
    stage.setScene(scene);
    stage.setTitle("Login");
    stage.setResizable(false);
  }

  private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      messageLabel.setText("Please enter username and password");
      return;
    }

    if (authService.login(username, password)) {
      messageLabel.setStyle("-fx-text-fill: green;");
      messageLabel.setText("Login successful!");
      stage.close();
      onLoginSuccess.run();
    } else {
      messageLabel.setStyle("-fx-text-fill: red;");
      messageLabel.setText("Invalid username or password");
      passwordField.clear();
    }
  }

  public void show() {
    stage.show();
  }
}
