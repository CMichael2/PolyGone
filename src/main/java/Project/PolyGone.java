package Project;

import Framework.Game; //package containing the abstract class game where all methods are inherited from
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.*;

public class PolyGone extends Game {

    private boolean isGameFocused = true;

    Player player; //creates player variable that follows the code in Player class

    //gui and hud variables/objects
    private GUI gameUI; //object for referencing gui class
    private DebugHUD debugHUD;
    private boolean showDebugHUD = false;
    private boolean debugKeyWasPressedLastFrame = false;

    private ArrayList<Bullets> bulletsList = new ArrayList<>(); //creates arraylist of bullets

    //class enemy variables
    //placed here because enemies are spawned by the main game and follow code in enemies class
    private ArrayList<Enemies> enemiesList = new ArrayList<>();
    private double enemySpeed = Player.playerSpeed*0.3; //used to determine enemy speed, 50% of player speed
    private long lastEnemySpawnTime = 0;
    private long enemySpawnRate = 3000; //used to determine the enemy spawn rate in milliseconds
    private boolean isFirstEnemy = true; //used to begin spawning of enemies

    private final Set<Integer> activeKeys = new HashSet<>(); //arraylist to store unlimited active keys

    //method called from GameKeyInput class, adds a key code to the activeKeys array once for each unique key that is pressed
    public void setKeyState(int keyCode, boolean isPressed) {
        if (isPressed) {
            activeKeys.add(keyCode);
        } else {
            activeKeys.remove(keyCode);
        }
    }

    //searches the array to check if the key that is being pressed exists (is it pressed or not)
    public boolean isKeyPressed(int keyCode) {
        return activeKeys.contains(keyCode);
    }

    @Override
    public void setup() {
        setDelay(16); //tick rate (determines how often to run the act() method, currently set to approximately 60Hz screens)

        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                isGameFocused = true;
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                isGameFocused = false;
                //disables keys when not in focus to prevent player movement
                activeKeys.clear();
            }
        });

        this.addKeyListener(new GameKeyInput(this)); //key listener

        GameMouseInput mouseHandler = new GameMouseInput(); //creates a new variable that is from the mouse input class
        //mouse inputs from mouse input class
        this.addMouseMotionListener(mouseHandler);
        this.addMouseListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);

        //creates player game object
        player = new Player(this);
        add(player);

        //creates gui game object
        gameUI = new GUI(this, player);
        add(gameUI);

        //creates debug hud game object
        debugHUD = new DebugHUD(this, player);
        add(debugHUD);

        this.getContentPane().setComponentZOrder(debugHUD, 0); //moves hud to top layer of screen
        this.getContentPane().setComponentZOrder(gameUI, 1); //moves gui to 2nd top layer of screen
    }

    @Override
    public void act() {

        if (!isGameFocused) {
            //reset mouse inputs during pause
            GameMouseInput.reset();
            return;
        }
        //player interaction updates
        player.playerMovementUpdate(this); //calls player movement update method inside player class
        player.handlePlayerShooting(this, this.bulletsList); //calls player shooting method inside player class
        openDebugHUD(); //opens debug hud when f3 key is pressed

        //method for bullet creation and collision processing
        bulletBehavior();

        //method for enemy creation
        enemySpawning();

        //method for enemy updates including movement, collision handled in other methods and in enemies class
        enemyBehaviorUpdates();

        exitGame();

        //resets inputs in mouse input class
        GameMouseInput.reset();
    }

    private void openDebugHUD() {
        if (isKeyPressed(KeyEvent.VK_F3)) {
            //only toggles on first frame of being pressed
            if (!debugKeyWasPressedLastFrame) {
                showDebugHUD = !showDebugHUD; //changes the state of debug hud to the opposite

                if (debugHUD != null) { //checks if the debug hud has been created
                    debugHUD.setDebugHUDVisible(showDebugHUD); //calls method to toggle the debug hud
                }

                debugKeyWasPressedLastFrame = true; //prevents the toggle from activating again until the key is released
            }
        } else {
            debugKeyWasPressedLastFrame = false;
        }
    }

    private void bulletBehavior() {
        //array loop for creating and storing data of multiple bullets at once
        for (int i = 0; i < bulletsList.size(); i++) {
            Bullets b = bulletsList.get(i);

            //bullet location before movement
            int bulletPrevX = b.getX();
            int bulletPrevY = b.getY();

            //checks if the bullet should be removed based on conditions in bulletUpdates method in the bullets class
            if (b.bulletUpdates(this)) {
                remove(b);
                bulletsList.remove(i);
                i--;
                continue; //to stop checking collision for removed bullets
            }

            //calls collision checking method of enemies colliding with bullets based on ray casting
            if (enemyAndBulletCollisionChecking(b, bulletPrevX, bulletPrevY)) {
                bulletsList.remove(i);
                i--;
            }
        }
    }

    private boolean enemyAndBulletCollisionChecking(Bullets b, int bulletPrevX, int bulletPrevY) {
        //loop to check if any bullet collides with any enemy
        for (int j = 0; j < enemiesList.size(); j++) {
            Enemies e = enemiesList.get(j);

            //calls ray casting enemy collision method to check for collisions with bullets
            //or uses regular collision checking inherited from game object class
            if (e.collides(b) || bulletPathIntersectsEnemy(b, bulletPrevX, bulletPrevY, e)) {

                e.takeDamage(1); //updates enemy health info in player class

                remove(b); //removes bullet when collision happens

                //removes enemy when killed
                if (e.isDead()) {
                    remove(e);
                    enemiesList.remove(j);
                    this.repaint();
                }
                return true;
            }
        }
        return false;
    }

    private void enemySpawning() {
        if (System.currentTimeMillis() - lastEnemySpawnTime > enemySpawnRate || isFirstEnemy) {
            Enemies newEnemy = new Enemies();
            newEnemy.enemyTimeOfSpawn = System.currentTimeMillis();

            enemySpawnPosition(newEnemy);
            add(newEnemy);
            enemiesList.add(newEnemy); //adds new element to array list

            double enemyTargetX = player.getX() - newEnemy.getX();
            double enemyTargetY = player.getY() - newEnemy.getY();
            double distanceForEnemies = Math.sqrt(enemyTargetX * enemyTargetX + enemyTargetY * enemyTargetY);

            if (distanceForEnemies > 1.0) {
                newEnemy.enemyVelocityX = (enemyTargetX / distanceForEnemies) * enemySpeed;
                newEnemy.enemyVelocityY = (enemyTargetY / distanceForEnemies) * enemySpeed;
            }

            isFirstEnemy = false;
            lastEnemySpawnTime = System.currentTimeMillis();
        }
    }

    private void enemySpawnPosition(Enemies newEnemy) {
        Random r = new Random();
        final int ENEMY_SPAWN_POSITION_BUFFER = 30;
        int side = r.nextInt(4);

        switch (side) {
            case 0: //top of field spawning
                newEnemy.setX(r.nextInt(getWidth()));
                newEnemy.setY(-ENEMY_SPAWN_POSITION_BUFFER);
                break;

            case 1: //bottom of field spawning
                newEnemy.setX(r.nextInt(getWidth()));
                newEnemy.setY(getHeight() + ENEMY_SPAWN_POSITION_BUFFER);
                break;

            case 2: //left side of field spawning
                newEnemy.setX(-ENEMY_SPAWN_POSITION_BUFFER);
                newEnemy.setY(r.nextInt(getHeight()));
                break;

            case 3: //right side of field spawning
                newEnemy.setX(getWidth() + ENEMY_SPAWN_POSITION_BUFFER);
                newEnemy.setY(r.nextInt(getHeight()));
                break;
        }
    }

    private void enemyBehaviorUpdates() {
        //handles the creation and deletion of enemies based on enemies class
        for (int i = 0; i < enemiesList.size(); i++) {
            Enemies e = enemiesList.get(i);

            //calls method in enemies class for enemy movement and enemy default collision with PLAYER
            if (e.enemyMovementUpdates(this, player, enemySpeed)) {

                int currentPlayerHealth = player.updateHealth(e.enemyDamage);

                if (currentPlayerHealth <= 0) {
                    triggerGameOver();
                }

                enemiesList.remove(i);
                i--;
            }
        }
    }

    private void triggerGameOver() {
        System.out.println("Player died, resetting"); //console info

        player.playerCurrentHealth = player.playerMaxHealth; //resets player health

        //moves player back to middle of the screen
        player.setX((this.getWidth() / 2) - (player.getWidth() / 2));
        player.setY((this.getHeight() / 2) - (player.getHeight() / 2));
    }

    //ray casting to determine if bullets will collide with enemies
    //finds the closest point in the bullet's trajectory in the frame to an enemy and checks if the 2 objects have/will collide at that point
    private boolean bulletPathIntersectsEnemy(Bullets b, int bulletPrevX, int bulletPrevY, Enemies e) {
        int bulletCurrX = b.getX();
        int bulletCurrY = b.getY();

        //gets radius
        double bulletRadius = Player.bulletWidth / 2.0;
        double enemyRadius = Enemies.enemyWidth / 2.0;

        //gets center of enemy
        double enemyX = e.getX() + (e.getWidth() / 2.0);
        double enemyY = e.getY() + (e.getHeight() / 2.0);

        //gets the distance the bullet moved within one frame(16ms)
        double bulletXDisplacement = bulletCurrX - bulletPrevX;
        double bulletYDisplacement = bulletCurrY - bulletPrevY;
        double magnitudeOfBulletTravel = bulletXDisplacement * bulletXDisplacement + bulletYDisplacement * bulletYDisplacement; //PT calculations

        //if the enemy has not moved, the collision state is set to true to prevent division by zero in following lines of code
        if (magnitudeOfBulletTravel == 0) { return e.collides(b); }

        //finds the closest point in percentage from the enemy to the bullet line of travel
        double closestPoint = ((enemyX - bulletPrevX) * bulletXDisplacement + (enemyY - bulletPrevY) * bulletYDisplacement) / magnitudeOfBulletTravel;

        //restricts the max and min values to 0(bullet starting point) and 1(bullet end point) to create a line segment
        if (closestPoint < 0) { closestPoint = 0; }
        if (closestPoint > 1) { closestPoint = 1; }

        //conversion from percentage to x and y coordinates based on the bullet's previous coordinates and their projected displacement this frame
        double closestX = bulletPrevX + closestPoint * bulletXDisplacement;
        double closestY = bulletPrevY + closestPoint * bulletYDisplacement;

        //calculates distance from enemy center to the closest point on the bullet's trajectory
        double distX = enemyX - closestX;
        double distY = enemyY - closestY;

        double distanceSquared = (distX * distX) + (distY * distY);
        double combinedRadius = enemyRadius + bulletRadius;

        //checks if the distance from the center of the enemy to the center of the bullet at their closest point in the bullets trajectory
        //is less than their combined radius, meaning they have collided.
        if (distanceSquared <= (combinedRadius * combinedRadius)) {
            return true;
        }
        return false;
    }

    //for debugging purposes
    public int getEnemyCount() {
        return this.enemiesList.size();
    }

    //closes game if the escape key is pressed
    private void exitGame() {
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
    }

    //main method
    public static void main(String[] args) {
        PolyGone game = new PolyGone();

        //Removes the window elements
        game.setJMenuBar(null);
        game.setUndecorated(true);
        game.setResizable(false);

        //gets the size and moves window to top left to fill screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        game.setSize(screenSize);
        game.setLocation(0, 0);

        //launches window
        game.setVisible(true);
        //game.setBackground(java.awt.Color.BLACK);
        game.initComponents(); //such as game objects


    }
}

