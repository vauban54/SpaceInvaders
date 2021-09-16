package fr.arnaud.spaceinvaders.entities;

import fr.arnaud.spaceinvaders.utils.Constants;
import fr.arnaud.spaceinvaders.utils.Utility;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Alien extends Entity {

    private static boolean goRight = true;
    private static boolean alienPosition = true;
    private static int speed = Constants.ALIEN_SPEED;
    private int type;

    public Alien(double x, double y, double width, double height, Image image) {
        super(x, y, width, height);
        super.setImg(image);
        super.setImgPattern(new ImagePattern(super.getImg()));
        super.setFill(super.getImgPattern());
    }

    public Alien(double x, double y, double width, double height, Image image, int type) {
        super(x, y, width, height);
        super.setImg(image);
        super.setImgPattern(new ImagePattern(super.getImg()));
        super.setFill(super.getImgPattern());
        this.type = type;
    }

    public static void aliensMoving(Alien[][] aliens) {
        if (goRight) { // Déplacement vers la droite
            for (int column = 0; column < 10; column++) {
                for (int line = 0; line < 5; line++) {
                    aliens[line][column].setX(aliens[line][column].getX() + Constants.ALIEN_DELTAX);

                }
            }
        } else {
            for (int column = 0; column < 10; column++) {
                for (int line = 0; line < 5; line++) {
                    aliens[line][column].setX(aliens[line][column].getX() - Constants.ALIEN_DELTAX);
                }
            }
        }
        int imageNumber;
        if (alienPosition) {
            imageNumber = 2;
            alienPosition = false;
        } else {
            imageNumber = 1;
            alienPosition = true;
        }
        Utility.displayAlternateAlienImage(aliens,imageNumber);

        // On test si on est arrivé à la marge du board à gauche ou à droite
        aliensMovingIntoBoard(aliens);
    }
    // Méthode qui change le sens de déplacement des aliens et les descend d'un cran
    // On test tout les aliens au cas où ils seraient détruit
    public static void aliensMovingIntoBoard(Alien[][] aliens) {
        if (Utility.aliensTouchRightSide(aliens)) {
            // Les aliens descendent...
            for (int column = 0; column < 10; column++) {
                for (int line = 0; line < 5; line++) {
                    aliens[line][column].setY(aliens[line][column].getY() + Constants.ALIEN_DELTAY);
                }
            }
            // Puis, ils se dirignet dans l'autre sens...
            goRight = false;
            // Puis, leur vitesse augmente
            //TODO
            if (Alien.getSpeed() < 9) {
                Alien.setSpeed(Alien.getSpeed() + 1);
            }

        } else if (Utility.aliensTouchLeftSide(aliens)) {
            // Les aliens descandent...
            for (int column = 0; column < 10; column++) {
                for (int line = 0; line < 5; line++) {
                    aliens[line][column].setY(aliens[line][column].getY() + Constants.ALIEN_DELTAY);
                }
            }
            // Puis, ils se dirignet dans l'autre sens...
            goRight = true;
            // Puis, leur vitesse augmente
            //TODO
            if (Alien.getSpeed() < 9) {
                Alien.setSpeed(Alien.getSpeed() + 1);
            }
        }

    }
    // Getters & Setters
    public static int getSpeed() {
        return speed;
    }

    public int getType() {
        return type;
    }

    public static void setSpeed(int speed) {
        Alien.speed = speed;


    }
}
