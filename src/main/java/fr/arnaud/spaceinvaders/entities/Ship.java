package fr.arnaud.spaceinvaders.entities;

import fr.arnaud.spaceinvaders.utils.Constants;
import fr.arnaud.spaceinvaders.utils.Images;
import javafx.scene.paint.ImagePattern;

public class Ship extends Entity {

    // Permet de savoir si le vaisseau tire
    private boolean shipIsShooting;

    public Ship(double x, double y, double width, double height) {
        super(x, y, width, height);
        super.setImgPattern(new ImagePattern(Images.SHIP));
        super.setFill(super.getImgPattern());
        this.shipIsShooting = false;
    }

    public double shipMoving(int shipDeltaX) {
        // On renvoie la position bornér du vaisseau
        if (shipDeltaX < 0) {
            if (super.getX() > Constants.SHIP_LEFT_WINDOW_LIMIT) {
                super.setX(super.getX() + shipDeltaX);
            }
        }else if (shipDeltaX > 0) {
            if (super.getX() < Constants.SHIP_RIGHT_WINDOW_LIMIT) {
                super.setX(super.getX() + shipDeltaX);
            }
        }
        return super.getX();
    }

    // Getter & Setter
    public boolean isShipIsShooting() {
        return shipIsShooting;
    }

    public void setShipIsShooting(boolean shipIsShooting) {
        this.shipIsShooting = shipIsShooting;
    }
}
