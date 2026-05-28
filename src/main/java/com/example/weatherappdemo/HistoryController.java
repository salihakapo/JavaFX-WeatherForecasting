package com.example.weatherappdemo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HistoryController {

    @FXML private ListView<String> historyListView;
    @FXML private Label historyTitleLabel;
    @FXML private Button backButton;
    @FXML private Button clearButton;

    private String currentUser;

    public void setUsername(String username) {
        this.currentUser = username;
        if (historyTitleLabel != null) {
            historyTitleLabel.setText("Search history — " + username);
        }
        loadHistory();
    }

    private void loadHistory() {
        List<String> historyList = new ArrayList<>();
        try {
            Path file = Path.of("history.txt");
            if (!Files.exists(file)) {
                historyListView.setItems(FXCollections.observableArrayList("No searches yet."));
                return;
            }
            try (BufferedReader br = Files.newBufferedReader(file)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("User: [" + currentUser + "]")) {
                        historyList.add(line);
                    }
                }
            }
            if (historyList.isEmpty()) {
                historyList.add("No searches yet for this account.");
            }
            historyListView.setItems(FXCollections.observableArrayList(historyList));
        } catch (IOException e) {
            historyListView.setItems(FXCollections.observableArrayList("Could not load history."));
        }
    }

    @FXML
    private void handleClearHistory() {
        try {
            Path inputFile = Path.of("history.txt");
            if (!Files.exists(inputFile)) {
                return;
            }
            Path tempFile = inputFile.resolveSibling("history.tmp");
            List<String> linesToPreserve = new ArrayList<>();
            String targetUserToken = "User: [" + currentUser + "]";

            try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains(targetUserToken)) {
                        linesToPreserve.add(line);
                    }
                }
            }

            try (BufferedWriter writer = Files.newBufferedWriter(inputFile)) {
                for (String preservedLine : linesToPreserve) {
                    writer.write(preservedLine);
                    writer.newLine();
                }
                writer.flush();
            }

            Files.deleteIfExists(tempFile);

            historyListView.setItems(FXCollections.observableArrayList("History cleared."));
            System.out.println("-> [SUCCESS] Local history file purged for user: " + currentUser);

        } catch (IOException e) {
            System.out.println("-> [ERROR] Critical file failure during purge transaction: " + e.getMessage());
            historyListView.getItems().add("Could not clear history.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) historyListView.getScene().getWindow();
            NavigationHelper.showWeather(stage, currentUser);
        } catch (IOException ignored) {
            // Stay on history screen
        }
    }
}
