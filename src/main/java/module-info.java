module fr.arnaud.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.arnaud.spaceinvaders to javafx.fxml;
    exports fr.arnaud.spaceinvaders;
}