package com.example.weatherappdemo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public final class NavigationHelper {

    public static final double WIDTH = 920;
    public static final double HEIGHT = 700;
    private static final String BASE = "/com/example/weatherappdemo/";
    private static final URL STYLES =
            NavigationHelper.class.getResource(BASE + "styles.css");

    private NavigationHelper() {
    }

    public static void showLogin(Stage stage) throws IOException {
        showLogin(stage, null);
    }

    public static void showLogin(Stage stage, String statusMessage) throws IOException {
        show(stage, "login-view.fxml", "SkyCast — Sign In", loader -> {
            if (statusMessage != null && !statusMessage.isBlank()) {
                LoginController controller = loader.getController();
                controller.showSuccessMessage(statusMessage);
            }
        });
    }

    public static void showSignUp(Stage stage) throws IOException {
        show(stage, "signup-view.fxml", "SkyCast — Create Account", null);
    }

    public static void showWeather(Stage stage, String username) throws IOException {
        show(stage, "weather-view.fxml", "SkyCast — " + username, loader -> {
            WeatherController controller = loader.getController();
            controller.setUsername(username);
        });
    }

    public static void showHistory(Stage stage, String username) throws IOException {
        show(stage, "history-view.fxml", "SkyCast — History", loader -> {
            HistoryController controller = loader.getController();
            controller.setUsername(username);
        });
    }

    private static void show(
            Stage stage,
            String fxml,
            String title,
            Consumer<FXMLLoader> configure) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(BASE + fxml));
        Parent root = loader.load();
        if (configure != null) {
            configure.accept(loader);
        }
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        if (STYLES != null && !scene.getStylesheets().contains(STYLES.toExternalForm())) {
            scene.getStylesheets().add(STYLES.toExternalForm());
        }
        stage.setTitle(title);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }
}
