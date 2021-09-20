package fr.arnaud.spaceinvaders.entities;

import fr.arnaud.spaceinvaders.utils.Audio;
import fr.arnaud.spaceinvaders.utils.Constants;
import fr.arnaud.spaceinvaders.utils.Images;
import fr.arnaud.spaceinvaders.utils.Sounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class AlienShot extends Entity {

    public AlienShot(double x, double y, double width, double height) {
        super(x, y, width, height);
        super.setImgPattern(new ImagePattern(Images.ALIEN_SHOT));
        super.setFill(super.getImgPattern());
    }

    public static void handleAlienShot(LinkedList<AlienShot> alienShotList, Pane board) {
        try {
            alienShotList.forEach(shot -> {
                // On gère le passage du tir hors du board
                if (shot.getY() > Constants.WINDOW_HEIGHT) {
                    alienShotList.remove(shot);
                    board.getChildren().remove(shot);
                } else { // Sinon on gère la progression du tir
                    shot.setY(shot.getY() + Constants.ALIEN_SHOT_DELTAY);
                    // On gère le son si on veut
                    //Audio.playSound(Sounds.ALIEN_SHOT);

                }
            });
        } catch (ConcurrentModificationException e) {
            System.out.println("ALIEN SHOT -> CONCURENT MODIFICATION EXCEPTION");
        }
        }


}
