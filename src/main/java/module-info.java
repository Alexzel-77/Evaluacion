module ni.edu.ni.evaluacion {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.sql;

    opens ni.edu.ni.evaluacion to javafx.fxml;
    opens ni.edu.ni.evaluacion.controller to javafx.fxml;
    opens ni.edu.ni.evaluacion.model to javafx.base;

    exports ni.edu.ni.evaluacion;
}