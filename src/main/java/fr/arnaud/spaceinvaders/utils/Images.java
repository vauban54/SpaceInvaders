package fr.arnaud.spaceinvaders.utils;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Images {

    private final static String PATH = "File:./src/main/resources/fr/arnaud/spaceinvaders/images/";

    public final static Image SHIP = new Image(PATH + "vaisseau.png");
    public final static Image SHIP_SHOT = new Image(PATH + "ship1Shoot.png");

    public final static Image IMG_BRICK1 = new Image(PATH + "wall1.png");
    public final static Image IMG_BRICK2 = new Image(PATH + "wall2.png");
    public final static Image IMG_BRICK3 = new Image(PATH + "wall3.png");
    public final static Image IMG_BRICK4 = new Image(PATH + "wall4.png");

    public final static ImagePattern BRICK1 = new ImagePattern(IMG_BRICK1);
    public final static ImagePattern BRICK2 = new ImagePattern(IMG_BRICK2);
    public final static ImagePattern BRICK3 = new ImagePattern(IMG_BRICK3);
    public final static ImagePattern BRICK4 = new ImagePattern(IMG_BRICK4);

    public final static Image ALIENHIGHT1 = new Image(PATH + "alienHigh1.png");
    public final static Image ALIENHIGHT2 = new Image(PATH + "alienHigh2.png");
    public final static Image ALIENMIDDLE1 = new Image(PATH + "alienMiddle1.png");
    public final static Image ALIENMIDDLE2 = new Image(PATH + "alienMiddle2.png");
    public final static Image ALIENBOTTOM1 = new Image(PATH + "alienBottom1.png");
    public final static Image ALIENBOTTOM2 = new Image(PATH + "alienBottom2.png");

    public final static Image EX1 = new Image(PATH + "explosion1.png");
    public final static Image EX2 = new Image(PATH + "explosion2.png");
    public final static Image EX3 = new Image(PATH + "explosion3.png");
    public final static Image EX4 = new Image(PATH + "explosion4.png");
    public final static Image EX5 = new Image(PATH + "explosion5.png");
    public final static Image EX6 = new Image(PATH + "explosion6.png");
    public final static Image EX7 = new Image(PATH + "explosion7.png");
}
