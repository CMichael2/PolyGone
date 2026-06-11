package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    PolyGone game;
    EnemyManager enemyManager;

    public int playerSpeed = 180; //change to determine player movement speed and coordinate change per key press

    //health variables
    public int playerMaxHealth = 100;
    public int playerCurrentHealth = playerMaxHealth;

    //xp & level variables
    public int startingPlayerLevel = 48; //starting player level
    public int playerLevel = startingPlayerLevel;
    public double currentPlayerXp = 0;
    public int totalPlayerXp = 0;
    public int playerXPBarMaxXP = 10 + (int)((Math.pow(playerLevel, 1.8)/4.0)+0.5); //base xp level up requirement
    public final int PLAYER_XP_BAR_MAX_XP_BASE = 10;

    //variables for bullet creation placed here inside class that creates the object
    public static int bulletWidth = 20; //please update width and height to the same values to prevent ellipse hitboxes
    public static int bulletHeight = 20;

    public static ArrayList<Weapon> weaponsList = new ArrayList<>();
    public int currentWeaponIndex = 0;
    Weapon activeWeapon;
    public boolean hasWeapon2 = false; //pistol
    public boolean hasWeapon3 = false; //rifle
    public boolean hasWeapon4 = false; //sniper
    public boolean hasWeapon5 = false; //miniGon
    public boolean hasWeapon6 = false; //revolver
    public boolean hasWeapon7 = false; //exo
    public boolean hasWeapon8 = false; //ICBM
    public boolean hasWeapon9 = false; //laser
    public boolean hasWeapon10 = false; //boomerang
    public boolean hasWeapon11 = false; //homing
    public boolean hasWeapon12 = false; //sentry

    public Player(PolyGone mainGame, EnemyManager enemyManager) { //sets attributes for player game object
        this.setSize(60, 60);
        this.setX((mainGame.getWidth() / 2) - (this.getHeight() / 2));
        this.setY((mainGame.getHeight() / 2) - (this.getHeight() / 2));
        this.setColor(Color.CYAN);
        this.game = mainGame;
        this.enemyManager = enemyManager;
        System.out.println(playerCurrentHealth);
        addWeapon(0); //starting weapon
        this.currentWeaponIndex = 0;
    }

    @Override
    public void act() {
        playerMovementUpdate(this.game);
        handlePlayerShooting(this.game, Bullets.getBulletsList());
        switchWeapon(this.game);
    }

    public void playerMovementUpdate(PolyGone mainGame) {
        Weapon activeWeapon = weaponsList.get(currentWeaponIndex);

        int moveAmount = (int) (playerSpeed * PolyGone.deltaTime);

        if (mainGame.isKeyPressed(KeyEvent.VK_W) || mainGame.isKeyPressed(KeyEvent.VK_UP)) {
            setY(getY()-moveAmount);
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_S) || mainGame.isKeyPressed(KeyEvent.VK_DOWN)) {
            setY(getY()+moveAmount);
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_A) || mainGame.isKeyPressed(KeyEvent.VK_LEFT)) {
            setX(getX()-moveAmount);
        }
        if (mainGame.isKeyPressed(KeyEvent.VK_D) || mainGame.isKeyPressed(KeyEvent.VK_RIGHT)) {
            setX(getX()+moveAmount);
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

        activeWeapon.updateAmmoRegen();
    }

    public void addWeapon(int weaponID) {
        Weapon newWeapon = null;

        switch (weaponID) {
            case 0:
                newWeapon = new Weapon(this.game, this, 720, 300, 4, 20, 2000, 400, false,"glock19");
                newWeapon.weaponName = "StartingGun"; //glock-19
                break;

            case 1:
                newWeapon = new Weapon(this.game, this, 600, 250, 3, 30, 2500, 550, false,"glock40");
                newWeapon.weaponName = "Pistol"; //glock-40
                break;

            case 2:
                newWeapon = new Weapon(this.game, this, 1080, 200, 4, 35, 4000, 850, false,"ar15");
                newWeapon.weaponName = "Rifle"; //AR-15
                break;

            case 3: //add periceing
                newWeapon = new Weapon(this.game, this, 1500, 1000, 20, 10, 5000, 1000, false,"m82");
                newWeapon.weaponName = "Sniper"; //M82 sniper
                break;

            case 4:
                newWeapon = new Weapon(this.game, this, 1200, 50, 2, 67, 15000, 200, false,"minigun");
                newWeapon.weaponName = "Minigun"; //minigun134
                break;

            case 5:
                newWeapon = new Weapon(this.game, this, 900, 250, 5, 10, 3000, 400, false,"44mag");
                newWeapon.weaponName = "Revolver"; //44-magnum
                break;

            case 6:
                //makes an explosion on impact that does splash damage
                newWeapon = new Weapon(this.game, this, 480, 400, 3, 15, 3000, 600, true, "exo");
                newWeapon.weaponName = "ExoGun"; //EXO
                break;

            case 7:
                //makes an explosion on impact that instantly kills nearby enemies
                //maybe also flies through enemies before impacting
                newWeapon = new Weapon(this.game, this, 300, 10000, 100, 3, 50000, 1500, true,"icbm");
                newWeapon.weaponName = "ICBM"; //intercontinental ballistic missile
                break;

            case 8:
                //looks like a beam but each section(bullet) does a bit of damage that adds up as dozens of bullets shoot out at once
                newWeapon = new Weapon(this.game, this, 1200, 25, 0.5, 100, 10000, 750, false,"laser");
                newWeapon.weaponName = "Laser";
                break;

            case 9:
                //boomerang that flies through enemies, damaging them and comes back
                break;

            case 10:
                //Homing missile launcher
                break;

            case 11:
                //Sentry that spawns in middle of map and auto shoots enemies for player (uses rifle stats)
                break;
        }

        if (newWeapon != null) {
            weaponsList.add(newWeapon);
        }
    }

    //handles bullet creation and targeting, related to bullet class(see Bullets.java)
    public void handlePlayerShooting(PolyGone mainGame, ArrayList<Bullets> gameBulletsList) {
        activeWeapon = weaponsList.get(currentWeaponIndex);

        //variables for bullets
        double bulletTargetX;
        double bulletTargetY;
        double distanceForBullets;

        //only shoots if the left click/space bar is held/clicked and the reload time is over
        if ((mainGame.isKeyPressed(KeyEvent.VK_SPACE) || GameMouseInput.isMouseLeftClickPressed) && (System.currentTimeMillis() - activeWeapon.lastShotTime) > activeWeapon.shotCooldown && activeWeapon.currentAmmo > 0) {

            //creating new bullet instance
            Bullets newBullet = new Bullets();
            newBullet.setSize(bulletWidth, bulletHeight);
            newBullet.setX(this.getX() + (this.getWidth() / 2));
            newBullet.setY(this.getY() + (this.getHeight() / 2));
            newBullet.setColor(Color.WHITE);
            newBullet.bulletDamage = activeWeapon.bulletDamage;

            //update bullet time of fire from bullets class
            newBullet.bulletTimeOfFire = System.currentTimeMillis();

            //setting coordinates to move to
            bulletTargetX = (double)GameMouseInput.mouseX - (double)newBullet.getX();
            bulletTargetY = (double)GameMouseInput.mouseY - (double)newBullet.getY();
            distanceForBullets = Math.sqrt(bulletTargetX * bulletTargetX + bulletTargetY * bulletTargetY); //PT calculations

            if (distanceForBullets > 0.01) {

                //moving bullet
                newBullet.bulletVelocityX = (bulletTargetX / distanceForBullets) * activeWeapon.bulletSpeed;
                newBullet.bulletVelocityY = (bulletTargetY / distanceForBullets) * activeWeapon.bulletSpeed;

                //adding bullet
                mainGame.add(newBullet);
                gameBulletsList.add(newBullet);

                //updating time of addition of bullet for bullet lifespan and firing cooldown/rate check
                activeWeapon.lastShotTime = System.currentTimeMillis();

                activeWeapon.currentAmmo--;
            }
        }
    }

    public void switchWeapon(PolyGone mainGame) {
        if (mainGame.isKeyPressed(KeyEvent.VK_1)) {
            if (!weaponsList.isEmpty() && currentWeaponIndex != 0) {
                currentWeaponIndex = 0;
                System.out.println("Player switched to: " + weaponsList.get(0).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_2)) {
            if (weaponsList.size() > 1 && currentWeaponIndex != 1) {
                currentWeaponIndex = 1;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_3)) {
            if (weaponsList.size() > 2 && currentWeaponIndex != 2) {
                currentWeaponIndex = 2;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_4)) {
            if (weaponsList.size() > 3 && currentWeaponIndex != 3) {
                currentWeaponIndex = 3;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_5)) {
            if (weaponsList.size() > 4 && currentWeaponIndex != 4) {
                currentWeaponIndex = 4;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_6)) {
            if (weaponsList.size() > 5 && currentWeaponIndex != 5) {
                currentWeaponIndex = 5;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_7)) {
            if (weaponsList.size() > 6 && currentWeaponIndex != 6) {
                currentWeaponIndex = 6;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_8)) {
            if (weaponsList.size() > 7 && currentWeaponIndex != 7) {
                currentWeaponIndex = 7;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_9)) {
            if (weaponsList.size() > 8 && currentWeaponIndex != 8) {
                currentWeaponIndex = 8;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }

        if (mainGame.isKeyPressed(KeyEvent.VK_0)) {
            if (weaponsList.size() > 9 && currentWeaponIndex != 9) {
                currentWeaponIndex = 9;
                System.out.println("Player switched to: " + weaponsList.get(1).weaponName);
            }
        }
    }

    public void updateHealth(int healthReduction) {
        playerCurrentHealth -= healthReduction;
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
        enemyManager.enemyCount = 0;
    }
}

