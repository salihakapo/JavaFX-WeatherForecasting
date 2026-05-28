package com.example.weatherappdemo;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class WeatherApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(true);
        NavigationHelper.showLogin(stage);
    }
}
