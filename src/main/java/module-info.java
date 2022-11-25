module com.example.mapbuildermodern {
    requires javafx.controls;
    requires java.xml;
    requires javafx.graphics;


    opens com.example.mapbuildermodern to javafx.graphics;
    exports com.example.mapbuildermodern;

}