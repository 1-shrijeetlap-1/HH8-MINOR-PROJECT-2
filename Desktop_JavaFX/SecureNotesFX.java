import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.io.File;
import javafx.stage.FileChooser;

public class SecureNotesFX extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Secure Notes Vault");

        // UI Components
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Write your secret note here...");
        noteArea.setWrapText(true);
        noteArea.getStyleClass().add("text-area");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Encryption Password");
        passwordField.setPrefWidth(250);
        passwordField.getStyleClass().add("password-field");

        Label strengthLabel = new Label("Strength: N/A");
        strengthLabel.getStyleClass().add("strength-label");

        Button saveParamsBtn = new Button("Encrypted Save");
        saveParamsBtn.getStyleClass().addAll("button", "save-button");

        Button loadParamsBtn = new Button("Decrypt Load");
        loadParamsBtn.getStyleClass().addAll("button", "load-button");

        // Layout - Top Bar
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        Label title = new Label("Secure Notes");
        title.getStyleClass().add("header-label");

        Label subtitle = new Label("AES-256 GCM Encrypted Storage");
        subtitle.setStyle("-fx-text-fill: #bcc0cc; -fx-font-size: 12px;");

        headerBox.getChildren().addAll(title, subtitle);

        // Layout - Controls
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(15, 0, 0, 0));
        controls.getChildren().addAll(passwordField, saveParamsBtn, loadParamsBtn);

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        statusBox.setPadding(new Insets(5, 0, 0, 0));
        statusBox.getChildren().add(strengthLabel);

        // Main Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(25));
        root.setTop(headerBox);
        root.setCenter(noteArea);

        VBox bottomContainer = new VBox(10);
        bottomContainer.getChildren().addAll(controls, statusBox);
        root.setBottom(bottomContainer);

        // Logic
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            String strength = SecurityUtil.checkStrength(newVal);
            strengthLabel.setText("Strength: " + strength);
            strengthLabel.setStyle(""); // Reset inline style

            if (strength.startsWith("Weak"))
                strengthLabel.setStyle("-fx-text-fill: #f38ba8;"); // Red/Pink
            else if (strength.equals("Medium"))
                strengthLabel.setStyle("-fx-text-fill: #fab387;"); // Orange
            else
                strengthLabel.setStyle("-fx-text-fill: #a6e3a1;"); // Green
        });

        saveParamsBtn.setOnAction(e -> {
            String pwd = passwordField.getText();
            if (pwd.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Password Required",
                        "Please enter a password to encrypt your note.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Encrypted Note");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encrypted Notes", "*.enc"));
            File file = fileChooser.showSaveDialog(stage);

            if (file == null) {
                return;
            }

            char[] passChars = pwd.toCharArray();
            try {
                SecurityUtil.save(noteArea.getText(), passChars, file);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Note encrypted and saved securely.");
                noteArea.clear(); // Clear text after save
                passwordField.clear(); // Clear password field
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Encryption Error", "Failed to save note: " + ex.getMessage());
            } finally {
                // Best effort to clear memory
                Arrays.fill(passChars, '\0');
            }
        });

        loadParamsBtn.setOnAction(e -> {
            String pwd = passwordField.getText();
            if (pwd.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Password Required",
                        "Please enter the password to decrypt the note.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Encrypted Note");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encrypted Notes", "*.enc"));
            File file = fileChooser.showOpenDialog(stage);

            if (file == null) {
                return;
            }

            char[] passChars = pwd.toCharArray();
            try {
                String text = SecurityUtil.load(passChars, file);
                noteArea.setText(text);
                passwordField.clear();
            } catch (Exception ex) {
                noteArea.clear();
                String msg = ex.getMessage();
                if (ex instanceof javax.crypto.AEADBadTagException) {
                    msg = "Incorrect Password or Corrupted File.";
                }
                showAlert(Alert.AlertType.ERROR, "Decryption Error", msg);
            } finally {
                // Best effort to clear memory
                Arrays.fill(passChars, '\0');
            }
        });

        Scene scene = new Scene(root, 700, 550);
        // Load CSS
        String cssPath = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Apply logic to style dialogs if needed, though they pop up natively often
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

