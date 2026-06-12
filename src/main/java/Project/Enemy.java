package Project;

import Framework.GameObject;
import java.awt.*;
import java.util.Random;

/*
Handles enemy creation and initial targeting
This class is a blueprint for every new enemy so it's methods are called for each new enemy created, which is why some methods like takeDamage() are here
 */

public class Enemy extends GameObject{

    //velocity variables for enemies
    //update speed in enemy manager
    public double enemyVelocityX = 0;
    public double enemyVelocityY = 0;

    public long enemyTimeOfSpawn = System.currentTimeMillis(); //used to track enemy spawn time

    public int enemySize;

    public double maxHealth; //change to non static and update the upgrade card using enemymanger (todo);
    public double health;
    public int enemyDamage;
    public double enemyDroppedXp;

    public boolean isBoss;

    public double exactX;
    public double exactY;
    private boolean isFirstFrame = true;

    private int baseGreenColor;

    @Override
    public void act() {}

    /**
     * Constructor that initializes the game object and its attributes
     * Pre: none
     * Post: A new enemy instance with specified dimensions and colored green
     */
    public Enemy(int maxEnemyHealth, int enemySize, double enemyDroppedXp, boolean isBoss, int enemyDamage) {
        this.setSize(enemySize,enemySize);
        Random r = new Random();
        baseGreenColor = r.nextInt(40) + 130; //130-170
        this.setColor(new Color(0, baseGreenColor, 0)); //change color for enemies here
        this.maxHealth = maxEnemyHealth;
        this.health = maxEnemyHealth;
        this.enemyDroppedXp = enemyDroppedXp;
        this.isBoss = isBoss;
        if (isBoss) {
            this.maxHealth = 100;
            this.baseGreenColor = 50;
        }
        this.enemyDamage = enemyDamage;
    }

    /**
     * Moves enemy towards player and checks if it collides with player
     * Pre: Objects are not null
     * Post: Enemy moved, enemy removed if collided with player
     * @param mainGame Parameter from Game
     * @param player Parameter from Player
     * @param enemySpeed Parameter from Enemy Manager that sets the enemy speed used for movement calculations
     * @return True if enemy collided with player, false otherwise
     */
    public boolean enemyMovementUpdates(PolyGone mainGame, Player player, double enemySpeed) {
        //movement variables
        double enemyTargetX;
        double enemyTargetY;
        double distanceForEnemies;
        //color variables
        double percentOfHealth = this.health / this.maxHealth;
        int r;
        int g;

        if (isFirstFrame) {
            this.exactX = this.getX();
            this.exactY = this.getY();
            this.isFirstFrame = false;
        }

        //calculating new target location for enemy
        enemyTargetX = player.getX() - this.exactX;
        enemyTargetY = player.getY() - this.exactY;
        distanceForEnemies = Math.sqrt(enemyTargetX * enemyTargetX + enemyTargetY * enemyTargetY);

        if (distanceForEnemies > 1.0) {
            //updates the enemy velocity
            this.enemyVelocityX = (enemyTargetX / distanceForEnemies) * enemySpeed;
            this.enemyVelocityY = (enemyTargetY / distanceForEnemies) * enemySpeed;
        } else {
            //stops moving when they hit the player
            this.enemyVelocityX = 0;
            this.enemyVelocityY = 0;
        }

        this.exactX += this.enemyVelocityX * PolyGone.deltaTime;
        this.exactY += this.enemyVelocityY * PolyGone.deltaTime;

        //moves the enemies
        this.setX((int) Math.round(this.exactX));
        this.setY((int) Math.round(this.exactY));

        //health color
        if (percentOfHealth > 0.5) {
            r = (int) ((1.0 - percentOfHealth) * 2.0 * 255);
            g = baseGreenColor;
        } else {
            r = 255;
            g = (int) ((percentOfHealth) * 2.0 * 255);
        }
        if (r < 0) r = 0;
        if (r > 255) r = 255;
        if (g < 0) g = 0;
        if (g > 255) g = 255;

        this.setColor(new Color(r, g, 0));

        //removes enemy from array and viewport in main game class
        if (this.collides(player)) {
            player.enemyCollide = true;
            player.collideTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}

