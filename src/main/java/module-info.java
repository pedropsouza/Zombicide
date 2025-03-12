module org.engcomp.Zombicide {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.engcomp.Zombicide to javafx.fxml;
    exports org.engcomp.Zombicide;
    exports org.engcomp.Zombicide.Actors;
    opens org.engcomp.Zombicide.Actors to javafx.fxml;
}