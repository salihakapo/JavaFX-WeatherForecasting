package com.example.weatherappdemo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML
    protected void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            UserRepository.RegisterStatus status = UserRepository.register(username, password);
            switch (status) {
                case SUCCESS:
                    goToLoginWithMessage("Account created! You can sign in now.");
                    break;
                case ALREADY_REGISTERED:
                    showError("This account already exists. Please sign in.");
                    break;
                case USERNAME_TAKEN:
                    showError("Username is already taken.");
                    break;
                case INVALID_USERNAME:
                    showError("Username cannot contain | or line breaks.");
                    break;
                default:
                    showError("Could not save account. Try again.");
                    break;
            }
        } catch (IOException e) {
            showError("Could not save account. Try again.");
        }
    }

    @FXML
    protected void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            NavigationHelper.showLogin(stage);
        } catch (IOException e) {
            showError("Could not open sign-in screen.");
        }
    }

    private void goToLoginWithMessage(String message) throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        NavigationHelper.showLogin(stage, message);
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
