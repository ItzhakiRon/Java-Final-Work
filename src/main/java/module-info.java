module com.example.tictactoegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.tictactoegame to javafx.fxml;
    exports com.example.tictactoegame;
}