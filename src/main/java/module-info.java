module com.example.weatherappdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.json;
    requires java.desktop;

    opens com.example.weatherappdemo to javafx.fxml, java.base;
    exports com.example.weatherappdemo;
}
