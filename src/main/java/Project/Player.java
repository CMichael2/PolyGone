package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    @Override
    public void act() {}

    public int playerSpeed = 6; //change to determine player movement speed and coordinate change per key press

    public int playerMaxHealth = 100;
    public int playerCurrentHealth = playerMaxHealth;

    public int startingPlayerLevel = 0; //starting player level
    public int playerLevel = startingPlayerLevel;
    public double currentPlayerXp = 0;
    public int totalPlayerXp = 0;
    public int playerXPBarMaxXP = 10 + (int)((Math.pow(playerLevel, 1.8)/4.0)+0.5); //base xp level up requirement
    public final int PLAYER_XP_BAR_MAX_XP_BASE = 10;
    PolyGone game;
    EnemyManager enemyManager;

    public Player(PolyGone mainGame, EnemyManager enemyManager) { //sets attributes for player game object
        this.setSize(40, 40);
        this.setX((mainGame.getWidth() / 2) - (this.getHeight() / 2));
        this.setY((mainGame.getHeight() / 2) - (this.getHeight() / 2));
        this.setColor(Color.CYAN);
        this.game = mainGame;
        this.enemyManager = enemyManager;
        System.out.println(playerCurrentHealth);
    }

    public int updateHealth(int healthReduction) {
        playerCurrentHealth -= healthReduction;
        return playerCurrentHealth;
    }

    public void updatePlayerXP(double playerXPIncrease, PolyGone game) {
        currentPlayerXp += playerXPIncrease;
        totalPlayerXp += (int)playerXPIncrease;
        if (currentPlayerXp >= playerXPBarMaxXP) {
            updatePlayerLevel(game);
        }
    }

    public void updatePlayerLevel(PolyGone game) {
        playerLevel += 1;
        if (playerLevel != 50) {
            game.openUpgradeMenu();
            System.out.println("Player leveled up to " + playerLevel);
        }
        currentPlayerXp = 0; //reset xp
        this.playerXPBarMaxXP = 10 + (int)((Math.pow(playerLevel, 1.8)/4.0)+0.5); //calculates new xp level up requirements
        enemyManager.enemySpawnRate = enemyManager.baseEnemySpawnRate - (playerLevel*25);
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

        this.updateAmmoRegen();
    }

    //variables for bullet creation placed here inside class that creates the object
    public double bulletSpeed = 12.0; //change to determine bullet speed
    public long lastShotTime = 0;
    public long shotCooldown = 200; //change to determine the firing rate/delay in milliseconds
    public static int bulletWidth = 10; //please update width and height to the same values to prevent ellipse hitboxes
    public static int bulletHeight = 10;

    //handles bullet creation and targeting, related to bullet class(see Bullets.java)
    public void handlePlayerShooting(PolyGone mainGame, ArrayList<Bullets> gameBulletsList) {

        //variables for bullets
        double bulletTargetX;
        double bulletTargetY;
        double distanceForBullets;

        //only shoots if the left click/space bar is held/clicked and the reload time is over
        if ((mainGame.isKeyPressed(KeyEvent.VK_SPACE) || GameMouseInput.isMouseLeftClickPressed) && (System.currentTimeMillis() - lastShotTime) > shotCooldown && currentAmmo > 0) {

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

                currentAmmo--;
            }
        }
    }

    public int maxAmmo = 25; //sets max ammo
    public int currentAmmo = maxAmmo;
    public long lastAmmoRegenTime = 0; //do not change
    public long ammoReloadCooldown = 2000; //sets ammo bar reload time
    public boolean isReloading = false;

    public void updateAmmoRegen() {
        if ((currentAmmo == 0 || game.isKeyPressed(KeyEvent.VK_R)) && !isReloading && currentAmmo!=maxAmmo) { //only regens ammo when needed
            isReloading = true;
            lastAmmoRegenTime = System.currentTimeMillis();
        }
        if (isReloading) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAmmoRegenTime >= ammoReloadCooldown) { //waits till the regeneration time is over to regen ammo
                currentAmmo = maxAmmo;
                isReloading = false;
            }
        }
    }

    public double getAmmoRegenProgress() {
        //if ammo is full, progress is 0
        if (currentAmmo >= maxAmmo) {
            return 0.0;
        }

        long timeElapsed = System.currentTimeMillis() - lastAmmoRegenTime;

        //calculate percentage of completion
        double progress = (double) timeElapsed / ammoReloadCooldown;

        return Math.max(0.0, Math.min(1.0, progress));
    }
}

