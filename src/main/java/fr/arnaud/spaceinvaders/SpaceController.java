package fr.arnaud.spaceinvaders;

import fr.arnaud.spaceinvaders.entities.*;
import fr.arnaud.spaceinvaders.utils.*;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

public class SpaceController {

    private Ship ship;
    private ShipShot shipShot;
    private AnimationTimer timer;
    private int shipDeltaX;
    private List<Brick> walls;
    private Alien[][] aliens;
    private static long movingAliensCount;
    private Group groupExplosion;
    private final IntegerProperty score = new SimpleIntegerProperty(0);


    @FXML
    private Pane board;

    @FXML
    private Label lblEndGame, lblScore;

    public SpaceController() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                movingAliensCount++;
                handleShip();
                if (ship.isShipIsShooting()) {
                    handleShipShot();

                }
                shipShotCollisions();

                // On refresh la position tableau d'aliens en fonction de l'incrémentation de leur vitesse
                if (movingAliensCount % (100 - (10L * Alien.getSpeed())) == 0) {
                    Alien.aliensMoving(aliens);
                }


            }
        };
    }

    public void initGame() {
        ship = new Ship(Constants.X_PDS_INIT_SHIP,Constants.Y_PDS_INIT_SHIP,
                Constants.SHIP_WIDTH,Constants.SHIP_HEIGHT);
        shipShot = new ShipShot(-10,-10,Constants.SHIP_SHOT_WIDTH,Constants.SHIP_SHOT_HEIGHT);
        walls = new LinkedList<>();
        aliens = new  Alien[5][10];
        movingAliensCount = 0;

        lblEndGame.setText("");
    }

    @FXML
    void onStartAction() {
        board.requestFocus();
        initGame();
        Initialisation.initShip(ship, board);
        Initialisation.initShipShot(shipShot, board);
        Initialisation.initWalls(80, 400, 80, walls, board);
        Initialisation.initAliens(aliens, board);
        timer.start();
        lblScore.textProperty().bind(Bindings.convert(score));
    }

    @FXML
    void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case LEFT:
                shipDeltaX = - Constants.SHIP_DELTAX;
                handleShip();
                break;
            case RIGHT:
                shipDeltaX =  Constants.SHIP_DELTAX;
                handleShip();
                break;
            case SPACE:
                if (!ship.isShipIsShooting()) {
                    ship.setShipIsShooting(true);
                    ShipShot.shipShotPlacement(shipShot, ship);
                    Audio.playSound(Sounds.SHIP_SHOT);
                }
                break;

        }
    }

    private void handleShip() {
        shipMoveHorizontal(shipDeltaX);
    }

    private void handleShipShot() {
        // On veut que le vaisseau ne puisse pas tirer de rafales
        if (shipShot.getY() <= -20) {
            ship.setShipIsShooting(false);
        }else if (shipShot.getY() >= -20) {
            shipShot.setY(shipShot.getY() - Constants.SHIP_SHOT_DELTAY);
        }
    }

    private void shipShotCollisions() {
        // Collision avec une Brick
        try {
            for (Brick brick : walls) {
                if (brick.getBoundsInParent().intersects(shipShot.getBoundsInParent())) {
                    // On replace le tir hors du board
                    shipShot.setX(-10);
                    shipShot.setY(-10);
                    // On réautorise à tirer en appuyant sur ESPACE
                    ship.setShipIsShooting(false);
                    // On retire la brick Brick du mur walls
                    walls.removeIf(thisBrick -> thisBrick.equals(brick));
                    board.getChildren().remove(brick);
                    // On met à jour le score avec la collision d'un tir sur une brick
                    if (score.get() >= Constants.BRICK_POINTS) {
                        score.set(score.get() - Constants.BRICK_POINTS);
                    }
                }
            }

        } catch (ConcurrentModificationException e) {
            System.out.println("Concurent Exception Ship -> Brick");
        }
        // Collision avec un alien
        for (Alien[] alienRow: aliens) {
            for (Alien alien: alienRow) {
                if (alien.getBoundsInParent().intersects(shipShot.getBoundsInParent())) {
                    //On remplace le tir hors du board
                    shipShot.setX(-10);
                    shipShot.setY(-10);
                    // On réautorise à tirer en appuyant sur ESPACE
                    ship.setShipIsShooting(false);
                    groupExplosion = new Group(Explosion.explode());
                    groupExplosion.setLayoutX(alien.getX()-10);
                    groupExplosion.setLayoutY(alien.getY()-10);
                    board.getChildren().addAll(groupExplosion);
                    // On sort l'alien du board
                    alien.setX(100);
                    alien.setY(-600);
                    // On retire l'alien du board
                    board.getChildren().remove(alien);
                    // On met à jour notre score suivant le type d'alien
                    score.set(score.get() + Constants.ALIEN_POINTS * alien.getType());
                }
            }
        }

    }

    private void shipMoveHorizontal(int shipDeltaX) {

        ship.setX(ship.shipMoving(shipDeltaX));
    }

    @FXML
    void onKeyReleased() {
        shipDeltaX = 0;
    }

    @FXML
    void onStopAction() {
        timer.stop();
    }

}