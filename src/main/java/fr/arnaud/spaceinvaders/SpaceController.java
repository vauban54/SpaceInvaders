package fr.arnaud.spaceinvaders;

import fr.arnaud.spaceinvaders.entities.*;
import fr.arnaud.spaceinvaders.utils.*;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SpaceController implements Sounds{

    private Ship ship;
    private ShipShot shipShot;
    private AnimationTimer timer;
    private int shipDeltaX;
    private List<Brick> walls;
    private Alien[][] aliens;
    private static long movingAliensCount;
    private Group groupExplosion;
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private static boolean initStartButton = false;
    private static Random random = new Random();
    private static LinkedList<AlienShot> alienShotList;
    private Saucer saucer;
    private long saucerTime = 0;

    @FXML
    private Pane board;

    @FXML
    private Label lblEndGame, lblLeftScore, lblRightScore;

    @FXML
    private ImageView imgLogo;

    public SpaceController() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                movingAliensCount++;
                saucerTime++;
                handleShip();
                if (ship.isShipIsShooting()) {
                    handleShipShot();

                }
                collisions();

                // On refresh la position tableau d'aliens en fonction de l'incrémentation de leur vitesse
                if (movingAliensCount % (100 - (10L * Alien.getSpeed())) == 0) {
                    Alien.aliensMoving(aliens);
                }

                aliensShooting();
                AlienShot.handleAlienShot(alienShotList, board);
                if (saucerTime % 400 == 0) {
                    saucer = new Saucer(Constants.X_POS_INIT_SAUCER, Constants.Y_POS_INIT_SAUCER,
                            Constants.SAUCER_WIDTH, Constants.SAUCER_HEIGHT);
                    board.getChildren().add(saucer);
                    saucerTime = 1;

                } else if (saucer != null) {
                    saucer.saucerMoving(Constants.SAUCER_DELTAX);
                }
            }
        };
    }

    public void initGame() {
        ship = new Ship(Constants.X_PDS_INIT_SHIP,Constants.Y_PDS_INIT_SHIP,
                Constants.SHIP_WIDTH,Constants.SHIP_HEIGHT);
        shipShot = new ShipShot(-10,-10,Constants.SHIP_SHOT_WIDTH,Constants.SHIP_SHOT_HEIGHT);
        walls = new LinkedList<Brick>();
        aliens = new  Alien[5][10];
        movingAliensCount = 0;
        alienShotList = new LinkedList<AlienShot>();

        lblEndGame.setText("");
    }

    @FXML
    public void onStartAction() {
        if (!initStartButton) {
            // On play la petite animation de début

            TranslateTransition animation = new TranslateTransition(Duration.millis(800), imgLogo);
            animation.setFromY(50);
            animation.setToY(-500);
            animation.setInterpolator(Interpolator.EASE_OUT);
            animation.play();



            board.requestFocus();
            initGame();
            Initialisation.initShip(ship, board);
            Initialisation.initShipShot(shipShot, board);
            Initialisation.initWalls(80, 400, 80, walls, board);
            Initialisation.initAliens(aliens, board);
            timer.start();

            // On lie le lblscore avec notre IntegerProperty -> score
            lblRightScore.textProperty().bind(Bindings.convert(score));
            // On rend visible les deux labels qui concernent le score
            lblLeftScore.setVisible(true);
            lblRightScore.setVisible(true);
            score.set(0);
            initStartButton = true;
        }

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
                    Audio.playSound(SHIP_SHOT);
                }
                break;

        }
    }

    private void handleShip() {
        shipMoveHorizontal(shipDeltaX);
    }
    private void shipMoveHorizontal(int shipDeltaX) {

        ship.setX(ship.shipMoving(shipDeltaX));
    }
    private void handleShipShot() {
        // On veut que le vaisseau ne puisse pas tirer de rafales
        if (shipShot.getY() <= -20) {
            ship.setShipIsShooting(false);
        }else if (shipShot.getY() >= -20) {
            shipShot.setY(shipShot.getY() - Constants.SHIP_SHOT_DELTAY);
        }
    }

    private void aliensShooting() {
        // Pour chaque invader, on détermine aléatoirement s'il tire
        for (Alien[] alienRow : aliens) {
            for (Alien alien : alienRow) {
                if (!alien.isDead()) {
                    int shootProbability =  5000;
                    // Si la condition est respectée ...
                    if (random.nextInt(shootProbability) == 0) {
                        // ... on instancie un nouveau tir
                        AlienShot alienShot = new AlienShot(alien.getX() + Constants.ALIEN_WIDTH / 2,
                                alien.getY() + Constants.ALIEN_HEIGHT,
                                Constants.ALIEN_SHOT_WIDTH, Constants.ALIEN_SHOT_HEIGHT);
                        // On ajoute le tir à la liste de tirs
                        alienShotList.add(alienShot);
                        // On ajoute le tir sur le board
                        board.getChildren().add(alienShot);
                    }
                }

            }
        }
    }

    private void collisions() {
        shipShotCollisions();
        aliensShotsBricksCollisions();
        aliensWallsCollisions();
    }

    private void aliensWallsCollisions() {
        // Collision des aliens avec les murs
        try {
            for (Brick brick : walls) {
                for (Alien[] alienRow : aliens) {
                    for (Alien alien : alienRow) {
                        if (brick.getBoundsInParent().intersects(alien.getBoundsInParent())) {
                            walls.removeIf(thisBrick -> thisBrick.equals(brick));
                            board.getChildren().remove(brick);
                            // On peut aussi gérer le son
                            //Audio.playSound(Sounds.BRICK_DESTRUCTION);
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ALIENS -> WALL : ConcurrentModificationException !!!");
        }
    }

    private void aliensShotsBricksCollisions() {
        // Collisions des tirs d'alien avec les bricks
        try {
            for (Brick brick : walls) {
                for (AlienShot alienShot : alienShotList) {
                    if (brick.getBoundsInParent().intersects(alienShot.getBoundsInParent())) {
                        walls.removeIf(thisBrick -> thisBrick.equals(brick));
                        alienShotList.removeIf(thisAlienShot -> thisAlienShot.equals(alienShot));
                        board.getChildren().removeAll(alienShot, brick);
                        Audio.playSound(Sounds.BRICK_DESTRUCTION);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ALIEN SHOT -> BRICK : ConcurrentModificationException !!!");
        }
    }


    private void shipShotCollisions() {
        // Collision avec une Brick brick
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
                    // On émet le son de destruction d'une brick
                    Audio.playSound(Sounds.BRICK_DESTRUCTION);
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
                    // On instancie un nouveau Group : groupExplosion
                    groupExplosion = new Group(Explosion.explode());
                    groupExplosion.setLayoutX(alien.getX()-10);
                    groupExplosion.setLayoutY(alien.getY()-10);
                    board.getChildren().addAll(groupExplosion);
                    // On sort l'alien du board ET on fait bien attention de le placer au delà du niveau de la marge
                    // De manière à ne pas gêner leurs mouvements futurs
                    alien.setX(100);
                    alien.setY(-600);
                    alien.setDead(true);
                    // On retire l'alien du board
                    board.getChildren().remove(alien);
                    // On émet le son de la déstruction de l'alien
                    Audio.playSound(Sounds.ALIEN_DESTRUCTION);
                    // On met à jour notre score suivant le type d'alien
                    score.set(score.get() + Constants.ALIEN_POINTS * alien.getType());
                }
            }
        }

    }



    @FXML
    void onKeyReleased() {
        shipDeltaX = 0;
    }

    @FXML
    void onStopAction() {
        timer.stop();
        initStartButton = false;
        walls.clear();
        alienShotList.clear();
        Alien.setSpeed(Constants.ALIEN_SPEED);
        board.getChildren().clear();
        if (saucer != null) {
            saucer.getSaucerPassingSound().stop();
        }
        // On rend invisible les deux labels qui concernent le score
        lblLeftScore.setVisible(false);
        lblRightScore.setVisible(false);
    }

}