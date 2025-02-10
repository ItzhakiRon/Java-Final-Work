module com.example.finalwork {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.finalwork to javafx.fxml;
    exports com.example.finalwork;
}