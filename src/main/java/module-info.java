module com.pestrings.pestringstool {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens com.pestrings.pestringstool to javafx.fxml;
    exports com.pestrings.pestringstool;
}