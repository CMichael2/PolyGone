package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject{

    @Override
    public void act() {}

    public static int playerSpeed = 10; //change to determine player movement speed and coordinate change per key press

    public int playerMaxHealth = 100;
    public int playerCurrentHealth = playerMaxHealth;

    public int playerLevel = 0;
    public int currentPlayerXp = 0;
    public int playerXPBarMaxXP = 10+ (int)(5 * Math.pow(playerLevel, 2)); //player max xp bar formula

    public Player(PolyGone mainGame) { //sets attributes for player game object
        this.setSize(40, 40);
        this.setX((mainGame.getWidth() / 2) - (this.getHeight() / 2));
        this.setY((mainGame.getHeight() / 2) - (this.getHeight() / 2));
        this.setColor(Color.CYAN);
    }

    public int updateHealth(int healthReduction) {
        playerCurrentHealth -= healthReduction;
        return playerCurrentHealth;
    }

    public void updatePlayerXP(int playerXPIncrease) {
        currentPlayerXp += playerXPIncrease;
        if (currentPlayerXp >= playerXPBarMaxXP) {
            updatePlayerLevel();
        }
    }

    public void updatePlayerLevel() {
        playerLevel += 1;
        currentPlayerXp = 0;
    }

    //movement for player
    public void moveLeft() {
        setX(getX()-playerSpeed);
    }
    public void moveRight() { setX(getX()+playerSpeed); }
    public void moveUp() {
        setY(getY()-playerSpeed);
    }
    public void moveDown() {
        setY(getY()+playerSpeed);
    }

    public void playerMovementUpdate(PolyGone mainGame) {
        // 1. EXECUTE MOVEMENT FIRST: Move the player based on inputs without checking borders yet
        if (mainGame.isKeyPressed(KeyEvent.VK_W) || mainGame.isKeyPressed(KeyEvent.VK_UP)) {
            this.moveUp();
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_S) || mainGame.isKeyPressed(KeyEvent.VK_DOWN)) {
            this.moveDown();
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_A) || mainGame.isKeyPressed(KeyEvent.VK_LEFT)) {
            this.moveLeft();
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_D) || mainGame.isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.moveRight();
        }

        //prevents player from exiting screen
        if (this.getX() < 0) {
            this.setX(0);
        }
        if (this.getX() > mainGame.getWidth() - this.getWidth()) {
            this.setX(mainGame.getWidth() - this.getWidth());
        }
        if (this.getY() < 0) {
            this.setY(0);
        }
        if (this.getY() > mainGame.getHeight() - this.getHeight()) {
            this.setY(mainGame.getHeight() - this.getHeight());
        }
    }

    //variables for bullet creation placed here inside class that creates the object
    private double bulletSpeed = 12.0; //change to determine bullet speed
    private long lastShotTime = 0;
    private long shotCooldown = 100; //change to determine the firing rate/delay in milliseconds
    public static int bulletWidth = 10; //please update width and height to the same values to prevent ellipse hitboxes
    public static int bulletHeight = 10;

    //handles bullet creation and targeting, related to bullet class(see Bullets.java)
    public void handlePlayerShooting(PolyGone mainGame, ArrayList<Bullets> gameBulletsList) {

        //variables for bullets
        double bulletTargetX;
        double bulletTargetY;
        double distanceForBullets;

        //only shoots if the left click/space bar is held/clicked and the reload time is over
        if ((mainGame.isKeyPressed(KeyEvent.VK_SPACE) || GameMouseInput.isMouseLeftClickPressed) && (System.currentTimeMillis() - lastShotTime) > shotCooldown) {

            //creating new bullet instance
            Bullets newBullet = new Bullets();
            newBullet.setSize(bulletWidth, bulletHeight);
            newBullet.setX(this.getX() + (this.getWidth() / 2));
            newBullet.setY(this.getY() + (this.getHeight() / 2));
            newBullet.setColor(Color.WHITE);

            //update bullet time of fire from bullets class
            newBullet.bulletTimeOfFire = System.currentTimeMillis();

            //setting coordinates to move to
            bulletTargetX = (double)GameMouseInput.mouseX - (double)newBullet.getX();
            bulletTargetY = (double)GameMouseInput.mouseY - (double)newBullet.getY();
            distanceForBullets = Math.sqrt(bulletTargetX * bulletTargetX + bulletTargetY * bulletTargetY); //PT calculations

            if (distanceForBullets > 0.01) {

                //moving bullet
                newBullet.bulletVelocityX = (bulletTargetX / distanceForBullets) * bulletSpeed;
                newBullet.bulletVelocityY = (bulletTargetY / distanceForBullets) * bulletSpeed;

                //adding bullet
                mainGame.add(newBullet);
                gameBulletsList.add(newBullet);

                //updating time of addition of bullet for bullet lifespan and firing cooldown/rate check
                lastShotTime = System.currentTimeMillis();
            }
        }
    }
}

