package com.example.weatherappdemo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherController {

    @FXML private TextField cityInput;
    @FXML private Label temperatureLabel;
    @FXML private Label humidityLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label welcomeLabel;
    @FXML private VBox mainContainer;
    @FXML private ImageView weatherIcon;
    @FXML private Button logoutButton;
    @FXML private HBox forecastContainer;

    private final String apiKey = System.getenv().getOrDefault(
            "OPENWEATHER_API_KEY", "9c80314ebff0d80b09ed195651c6855d");

    private String loggedInUser;

    public void setUsername(String username) {
        this.loggedInUser = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hello, " + username);
        }
    }

    @FXML
    public void getWeatherData() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            descriptionLabel.setText("Please enter a city name.");
            return;
        }

        try {
            String formattedCity = city.replace(" ", "%20");
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + formattedCity + "&appid=" + apiKey + "&units=metric";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                double temp = json.getJSONObject("main").getDouble("temp");
                int humidity = json.getJSONObject("main").getInt("humidity");
                String description = json.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");

                temperatureLabel.setText(String.format("Temperature: %.1f°C", temp));
                humidityLabel.setText("Humidity: " + humidity + "%");
                descriptionLabel.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                weatherIcon.setImage(new Image(
                        "https://openweathermap.org/img/wn/" + iconCode + "@2x.png", true));

                saveSearchHistory(city);
                loadForecast(city);
            } else {
                descriptionLabel.setText("City not found. Check the name and try again.");
            }
        } catch (IOException e) {
            descriptionLabel.setText("Could not fetch weather data. Check your connection.");
        }
    }

    private void saveSearchHistory(String city) {
        try {
            Path historyFile = Path.of("history.txt");

            String user = (loggedInUser != null && !loggedInUser.isBlank()) ? loggedInUser : "Guest";

            String entry = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())
                    + " - User: [" + user + "] searched for: " + city + System.lineSeparator();

            Files.writeString(historyFile, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("-> [SUCCESS] Search logged via AppPaths for user: " + user);

        } catch (IOException e) {
            System.out.println("-> [ERROR] Supplementary logger failure: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogout() {
        try {
            Stage stage = (Stage) cityInput.getScene().getWindow();
            NavigationHelper.showLogin(stage);
        } catch (IOException ignored) {
            descriptionLabel.setText("Could not return to sign-in.");
        }
    }

    @FXML
    public void handleViewHistory() {
        if (loggedInUser == null || loggedInUser.isBlank()) {
            descriptionLabel.setText("Sign in to view search history.");
            return;
        }
        try {
            Stage stage = (Stage) cityInput.getScene().getWindow();
            NavigationHelper.showHistory(stage, loggedInUser);
        } catch (IOException ignored) {
            descriptionLabel.setText("Could not open history.");
        }
    }

    private void loadForecast(String city) {
        try {
            forecastContainer.getChildren().clear();
            String formattedCity = city.replace(" ", "%20");
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?q="
                    + formattedCity + "&appid=" + apiKey + "&units=metric";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray list = new JSONObject(response.toString()).getJSONArray("list");
            int cardCount = 0;
            String lastDate = "";

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String fullDateTime = item.getString("dt_txt");
                String currentDate = fullDateTime.substring(5, 10);
                String currentHour = fullDateTime.substring(11, 19);

                if (!currentDate.equals(lastDate)
                        && (currentHour.equals("12:00:00") || i == list.length() - 1 || cardCount == 0)) {
                    if (cardCount >= 5) {
                        break;
                    }
                    lastDate = currentDate;
                    cardCount++;

                    double temp = item.getJSONObject("main").getDouble("temp");
                    String iconCode = item.getJSONArray("weather").getJSONObject(0).getString("icon");

                    ImageView forecastIcon = new ImageView(new Image(
                            "https://openweathermap.org/img/wn/" + iconCode + "@2x.png", true));
                    forecastIcon.setFitWidth(52);
                    forecastIcon.setFitHeight(52);

                    Label dateLabel = new Label(currentDate);
                    dateLabel.getStyleClass().add("forecast-day-label");

                    Label tempLabel = new Label(String.format("%.1f°C", temp));
                    tempLabel.getStyleClass().add("forecast-day-label");

                    VBox dayBox = new VBox(8, dateLabel, forecastIcon, tempLabel);
                    dayBox.setAlignment(javafx.geometry.Pos.CENTER);
                    dayBox.getStyleClass().add("forecast-day");

                    forecastContainer.getChildren().add(dayBox);
                }
            }
        } catch (IOException ignored) {
            // Forecast is supplementary
        }
    }
}
