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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

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
    private static final Rectangle saucer100Rect = new Rectangle();
    private static int shipNumberFromHome;

    @FXML
    private Pane board;

    @FXML
    private Label lblEndGame, lblLeftScore, lblRightScore;

    @FXML
    private ImageView imgLogo;

    // TODO TEST DE TRANSFERT DE DONNEES
    public void transferShipNumber(int shipNumber) {
        switch (shipNumber) {
            case 1:
                shipNumberFromHome = 1;
                break;
            case 2:
                shipNumberFromHome = 2;
                break;
        }
    }
    public SpaceController() {
       /** timer = new AnimationTimer() {
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
        };*/
       timer = new AnimationTimer(){
            private final long SECOND_NANO = 1000000000;
            private int frameCount = 0;
            private float frameRate = 0;
            private long deltaTime = 0;
            private long timeCounter = 0;
            private long time = System.nanoTime();

            private void before(long now) {
                deltaTime = now - time;
                timeCounter += deltaTime;
                if (timeCounter > SECOND_NANO) {
                    frameRate = frameCount;
                    frameCount = 0;
                    timeCounter %= SECOND_NANO;
                    System.out.println(frameRate);
                }
            }

            private void after(long now) {
                frameCount++;
                time = now;
            }

            @Override
            public void handle(long now) {
                before(now);
                loop();
                after(now);

            }
       };
    }
    public void loop() {
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
        endGame();
    }
    public void initGame(int numberShip) {
        ship = new Ship(Constants.X_PDS_INIT_SHIP,Constants.Y_PDS_INIT_SHIP,
                Constants.SHIP_WIDTH,Constants.SHIP_HEIGHT);
        switch (numberShip) {
            case 1:
                ship.setFill(new ImagePattern(Images.SHIP1));
                break;
            case 2:
                ship.setFill(new ImagePattern(Images.SHIP2));
                break;
        }
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

            Animation.animateLogoSpaceInvaders(imgLogo,0,-500,600,1,0,400);



            board.requestFocus();
            initGame(shipNumberFromHome);
            Initialisation.initShip(ship, board);
            Initialisation.initShipShot(shipShot, board);
            Initialisation.initWalls(80, 400, 80, walls, board);
            Initialisation.initAliens(aliens, board);
            Initialisation.initSaucer100(saucer100Rect, board);
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
                        // On peut appliquer un son RANDOM à chaque tir Alien
                        int randomNumber = (int) (Math.round(Math.random() * 3) + 1);
                        switch (randomNumber) {
                            case 1:
                                Audio.playSound(ALIEN_SHOT1);
                                break;
                            case 2:
                                Audio.playSound(ALIEN_SHOT2);
                                break;
                            case 3:
                                Audio.playSound(ALIEN_SHOT3);
                                break;
                            case 4:
                                Audio.playSound(ALIEN_SHOT4);
                                break;
                        }
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
        Brick brickToRemove = null;
//        try {
            for (Brick brick : walls) {
                for (Alien[] alienRow : aliens) {
                    for (Alien alien : alienRow) {
                        if (brick.getBoundsInParent().intersects(alien.getBoundsInParent())) {
                            brickToRemove = brick;
                            // On peut aussi gérer le son
                            //Audio.playSound(Sounds.BRICK_DESTRUCTION);
                        }
                    }
                }
            }
//        } catch (ConcurrentModificationException e) {
//            System.out.println("ALIENS -> WALL : ConcurrentModificationException !!!");
//        }
        if (brickToRemove != null) {
            walls.remove(brickToRemove);
            board.getChildren().remove(brickToRemove);
        }

    }

    private void aliensShotsBricksCollisions() {
        // Collisions des tirs d'alien avec les bricks
        Brick brickToRemove = null;
        AlienShot shotToRemove = null;
//        try {
            for (Brick brick : walls) {
                for (AlienShot alienShot : alienShotList) {
                    if (brick.getBoundsInParent().intersects(alienShot.getBoundsInParent())) {
                        brickToRemove = brick;
                        shotToRemove = alienShot;
                    }
                }
            }
//        } catch (ConcurrentModificationException e) {
//            System.out.println("ALIEN SHOT -> BRICK : ConcurrentModificationException !!!");
//        }
        if (brickToRemove != null) {
            walls.remove(brickToRemove);
            board.getChildren().remove(brickToRemove);
        }
        if (shotToRemove != null) {
            alienShotList.remove(shotToRemove);
            board.getChildren().remove(shotToRemove);
        }
        Audio.playSound(Sounds.BRICK_DESTRUCTION);
    }


    private void shipShotCollisions() {
        // Collision avec une Brick brick
        Brick brickToRemove = null;
//        try {
            for (Brick brick : walls) {
                if (brick.getBoundsInParent().intersects(shipShot.getBoundsInParent())) {
                    // On replace le tir hors du board
                    brickToRemove = brick;
                    shipShot.setX(-10);
                    shipShot.setY(-10);
                    // On réautorise à tirer en appuyant sur ESPACE
                    ship.setShipIsShooting(false);
                    // On retire la brick Brick du mur walls

                    // On émet le son de destruction d'une brick
                    Audio.playSound(Sounds.BRICK_DESTRUCTION);
                    // On met à jour le score avec la collision d'un tir sur une brick
                    if (score.get() >= Constants.BRICK_POINTS) {
                        score.set(score.get() - Constants.BRICK_POINTS);
                    }
                }
            }

//        } catch (ConcurrentModificationException e) {
//            System.out.println("Concurent Exception Ship -> Brick");
//        }
        if (brickToRemove != null) {
            walls.remove(brickToRemove);
            board.getChildren().remove(brickToRemove);
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
                    groupExplosion = new Group(Explosion.explosionAlien());
                    groupExplosion.setLayoutX(alien.getX()-10);
                    groupExplosion.setLayoutY(alien.getY()-10);
                    board.getChildren().addAll(groupExplosion);
                    // On sort l'alien du board ET on fait bien attention de le placer au delà du niveau de la marge
                    // De manière à ne pas gêner leurs mouvements futurs
                    alien.setX(300);
                    alien.setY(0);
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

        // Collision avec la soucoupe
        if (saucer != null) {
            if (!saucer.isDead()) {
                if (shipShot.getBoundsInParent().intersects(saucer.getBoundsInParent())) {
                    saucer.setDead(true);
                    groupExplosion = new Group(Explosion.explosionAlien());
                    groupExplosion.setLayoutX(saucer.getX() - (double)Constants.SAUCER_WIDTH / 2);
                    groupExplosion.setLayoutY(saucer.getY() - (double)Constants.SAUCER_HEIGHT / 2);
                    board.getChildren().addAll(groupExplosion);
                    shipShot.setX(-10);
                    shipShot.setY(-10);
                    board.getChildren().remove(saucer);
                    // On coupe le son de la soucoupe
                    saucer.getSaucerPassingSound().stop();
                    Audio.playSound(SAUCER_DESTRUCTION);
                    // On met à jour le score
                    score.set(score.get() + Constants.SAUCER_SCORE_POINTS);
                    // On positionne le score de destruction à l'endroit où la soucoupe a été détruite
                    // Attention de positionner le score AVANT le repositionnement de la soucoupe
                    saucer100Rect.setX(saucer.getX() - 15);
                    saucer100Rect.setY(saucer.getY());
                    // On repositionne la soucoupe à sa position initiale
                    saucer.setX(Constants.X_POS_INIT_SAUCER);
                    saucer.setY(Constants.Y_POS_INIT_SAUCER);

                    // On affiche le score de 100 points à la dernière position de la soucoupe avant de l'abattre
                    Timer timerScoreSaucer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            // ... Puis, on l'affiche hors du board
                            saucer100Rect.setX(Constants.X_POS_INIT_SAUCER_SCORE);
                            saucer100Rect.setY(Constants.Y_POS_INIT_SAUCER_SCORE);

                        }
                    };
                    timerScoreSaucer.schedule(timerTask, 1000);


                }
            }
        }
        // Collision avec un tir alien
        for (AlienShot alienShot: alienShotList) {
            if (alienShot.getBoundsInParent().intersects(shipShot.getBoundsInParent())) {
                shipShot.setX(-10);
                shipShot.setY(-10);
//                board.getChildren().remove(alienShot);
                Group explosionAlienShoot = new Group(Explosion.explosionAlienShoot());
                explosionAlienShoot.setLayoutX(alienShot.getX() - 15);
                explosionAlienShoot.setLayoutY(alienShot.getY() - 10);
                // On sort l'alienShot du board
                alienShot.setX(Constants.WINDOW_WIDTH);
                alienShot.setY(Constants.WINDOW_HEIGHT);
                board.getChildren().addAll(explosionAlienShoot);
                // On met à jour le score
                score.set(score.get() + Constants.ALIEN_SHOT_POINTS);

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

        Animation.animateLogoSpaceInvaders(imgLogo,-500,0,600,0,1,1000);
    }

    // La partie ce termine si on gagne ou si on perd
    private void endGame() {
        // GAIN DE LA PARTIE
        // On gagne quand la totalité des aliens sont morts
        boolean result = Arrays.stream(aliens).allMatch(a -> Arrays.stream(a).allMatch(Alien::isDead));
        if (result == true) {
            timer.stop();
            lblEndGame.setText(Constants.WIN);
            board.getChildren().remove(ship);
        }

        // PERTE DE LA PARTIE
        // On perd quand un tir ennemi nous touche
        // On perd quand un alien nous touche
        // On perd quand un alien atteint la limite du board
        if (alienShotList.stream().anyMatch(shoot1 -> shoot1.getBoundsInParent().intersects(ship.getBoundsInParent()))
                || Arrays.stream(aliens).anyMatch(a -> Arrays.stream(a).anyMatch(alien -> alien.getBoundsInParent()
                .intersects(ship.getBoundsInParent())))
                || Arrays.stream(aliens).anyMatch(a -> Arrays.stream(a).anyMatch(alien -> alien.getY() >
                Constants.WINDOW_HEIGHT - Constants.WINDOW_MARGIN))) {
            Group groupExplosionShip = new Group(Explosion.explosionShip());
            groupExplosionShip.setLayoutX(ship.getX());
            groupExplosionShip.setLayoutY(ship.getY() - 40);
            board.getChildren().addAll(groupExplosionShip);
            ship.setX(-Constants.SHIP_WIDTH);
            ship.setY(0);
            board.getChildren().remove(ship);
            Audio.playSound(SHIP_DESTRUCTION);
            System.out.println("TTTTTT");
            lblEndGame.setText(Constants.LOOSE);
            timer.stop();
        }
    }

}