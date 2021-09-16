module fr.arnaud.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens fr.arnaud.spaceinvaders to javafx.fxml;
    exports fr.arnaud.spaceinvaders;
}