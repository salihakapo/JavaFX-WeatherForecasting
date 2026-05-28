package com.example.weatherappdemo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        java.util.Optional<String> authenticated = UserRepository.authenticate(username, password);
        if (authenticated.isPresent()) {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                NavigationHelper.showWeather(stage, authenticated.get());
            } catch (IOException e) {
                showError("Could not open the weather screen.");
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    protected void goToSignUp() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            NavigationHelper.showSignUp(stage);
        } catch (IOException e) {
            showError("Could not open sign-up screen.");
        }
    }

    public void showSuccessMessage(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-error");
        if (!statusLabel.getStyleClass().contains("status-success")) {
            statusLabel.getStyleClass().add("status-success");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success");
        if (!statusLabel.getStyleClass().contains("status-error")) {
            statusLabel.getStyleClass().add("status-error");
        }
    }

    @FXML
    public void initialize() {
        statusLabel.getStyleClass().add("status-label");
    }
}
