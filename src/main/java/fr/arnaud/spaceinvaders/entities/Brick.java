package fr.arnaud.spaceinvaders.entities;


import fr.arnaud.spaceinvaders.utils.Images;
import javafx.scene.paint.ImagePattern;
import java.util.ArrayList;
import java.util.Random;

public class Brick extends Entity {

    private static ArrayList<ImagePattern> randomWall = new ArrayList<>();
    private static Random random = new Random();

    public Brick(double x, double y, double width, double height, ImagePattern sprite ) {
        super(x, y, width, height);
        super.setImgPattern(sprite);
        super.setFill(super.getImgPattern());

    }
    public static ImagePattern setRandomBrick() {
        randomWall.add(Images.BRICK1);
        randomWall.add(Images.BRICK2);
        randomWall.add(Images.BRICK3);
        randomWall.add(Images.BRICK4);
        randomWall.add(Images.BRICK5);
        randomWall.add(Images.BRICK6);
        randomWall.add(Images.BRICK7);
        int index = random.nextInt(randomWall.size());
        return randomWall.get(index);
    }
}
