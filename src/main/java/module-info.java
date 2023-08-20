module com.sachet.multithreadingreentrant {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sachet.multithreadingreentrant to javafx.fxml;
    exports com.sachet.multithreadingreentrant;
}