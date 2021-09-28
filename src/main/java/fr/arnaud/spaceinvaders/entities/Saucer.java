package fr.arnaud.spaceinvaders.entities;

import fr.arnaud.spaceinvaders.utils.Constants;
import fr.arnaud.spaceinvaders.utils.Images;
import fr.arnaud.spaceinvaders.utils.Sounds;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;

public class Saucer extends Entity {

    private AudioClip saucerPassingSound = new AudioClip(Sounds.SAUCER_PASSING);

    // Permet de stopper le son de la soucoupe si elle est abbatue
    private boolean isDead;

    public Saucer(double x, double y, double width, double height) {
        super(x, y, width, height);
        super.setImgPattern(new ImagePattern(Images.SAUCER));
        super.setFill(super.getImgPattern());
        this.saucerPassingSound.setVolume(0.05);
        this.saucerPassingSound.play();
        this.isDead = false;
    }

    public void saucerMoving(int deltaX) {
        if (super.getX() > -Constants.SAUCER_WIDTH &&
                super.getX() < Constants.WINDOW_WIDTH + 1) {
            super.setX(super.getX() - deltaX);
        } else {
            // Le son de la soucoupe est coupé quand elle est sortie du board ou détruite
            this.saucerPassingSound.stop();
        }
    }

    // Getters & Setters


    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isDead() {
        return isDead;
    }

    public AudioClip getSaucerPassingSound() {

        return saucerPassingSound;
    }
}
