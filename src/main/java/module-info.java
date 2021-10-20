module com.pestrings.pestringstool {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires okhttp3;

    opens com.pestrings.pestringstool to javafx.fxml;
    exports com.pestrings.pestringstool;
    exports com.pestrings.pestringstool.pe;
    opens com.pestrings.pestringstool.pe to javafx.fxml;
}