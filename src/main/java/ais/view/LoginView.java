package ais.view;

import ais.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

  private final AuthService authService;
  private final Runnable onLoginSuccess;
  private final Stage stage;

  public LoginView(AuthService authService, Runnable onLoginSuccess) {
    this.authService = authService;
    this.onLoginSuccess = onLoginSuccess;
    this.stage = new Stage();
    initUI();
  }

  private void initUI() {
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

    VBox loginCard = new VBox(20);
    loginCard.setAlignment(Pos.CENTER);
    loginCard.setMaxWidth(400);
    loginCard.setMaxHeight(500);
    loginCard.getStyleClass().add("card");
    loginCard.setPadding(new Insets(40));

    Label iconLabel = new Label("AIS");
    iconLabel.setFont(Font.font("System", FontWeight.BOLD, 48));
    iconLabel.setStyle("-fx-text-fill: #2196F3;");
    iconLabel.setFont(Font.font(60));

    Label titleLabel = new Label("Academic Information System");
    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
    titleLabel.setTextFill(Color.web("#333"));

    Label subtitleLabel = new Label("Sign in to continue");
    subtitleLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");

    VBox formBox = new VBox(15);
    formBox.setAlignment(Pos.CENTER);

    HBox usernameBox = new HBox(10);
    usernameBox.setAlignment(Pos.CENTER_LEFT);
    usernameBox.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 5px; -fx-padding: 5px 10px;");
    Label userIcon = new Label("●");
    userIcon.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
    TextField usernameField = new TextField();
    usernameField.setPromptText("Username");
    usernameField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    HBox.setHgrow(usernameField, Priority.ALWAYS);
    usernameBox.getChildren().addAll(userIcon, usernameField);

    HBox passwordBox = new HBox(10);
    passwordBox.setAlignment(Pos.CENTER_LEFT);
    passwordBox.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 5px; -fx-padding: 5px 10px;");
    Label lockIcon = new Label("●");
    lockIcon.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Password");
    passwordField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    HBox.setHgrow(passwordField, Priority.ALWAYS);
    passwordBox.getChildren().addAll(lockIcon, passwordField);

    Button loginButton = new Button("Sign In");
    loginButton.getStyleClass().add("button-primary");
    loginButton.setPrefWidth(200);
    loginButton.setPrefHeight(40);
    loginButton.setFont(Font.font("System", FontWeight.BOLD, 14));

    Label messageLabel = new Label();
    messageLabel.setWrapText(true);
    messageLabel.setMaxWidth(350);
    messageLabel.setAlignment(Pos.CENTER);

    VBox infoBox = new VBox(5);
    infoBox.setAlignment(Pos.CENTER);
    infoBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5px; -fx-padding: 15px;");

    Label infoTitle = new Label("Default Accounts:");
    infoTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
    Label adminInfo = new Label("Admin: admin / admin");
    adminInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

    infoBox.getChildren().addAll(infoTitle, adminInfo);

    formBox.getChildren().addAll(usernameBox, passwordBox, loginButton, messageLabel);

    loginCard.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, formBox, infoBox);

    StackPane centerPane = new StackPane(loginCard);
    centerPane.setPadding(new Insets(40));
    root.setCenter(centerPane);

    Runnable handleLogin = () -> {
      String username = usernameField.getText().trim();
      String password = passwordField.getText().trim();

      if (username.isEmpty() || password.isEmpty()) {
        messageLabel.setText("⚠️ Please enter both username and password");
        messageLabel.getStyleClass().add("label-error");
        return;
      }

      if (authService.login(username, password)) {
        messageLabel.setText("✓ Login successful!");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("label-success");
        stage.close();
        onLoginSuccess.run();
      } else {
        messageLabel.setText("✗ Invalid username or password");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("label-error");
        passwordField.clear();

        shakeNode(loginCard);
      }
    };

    loginButton.setOnAction(e -> handleLogin.run());
    passwordField.setOnAction(e -> handleLogin.run());

    Scene scene = new Scene(root, 600, 700);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle("AIS - Login");
    stage.setResizable(false);
  }

  private void shakeNode(javafx.scene.Node node) {
    javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
        javafx.util.Duration.millis(50), node);
    tt.setFromX(0);
    tt.setByX(10);
    tt.setCycleCount(4);
    tt.setAutoReverse(true);
    tt.play();
  }

  public void show() {
    stage.show();
  }
}
