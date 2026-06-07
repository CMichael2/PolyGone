package Project;

import Framework.GameObject;
import java.awt.*;
import java.util.*;

/*
Handles enemy creation and initial targeting
This class is a blueprint for every new enemy so it's methods are called for each new enemy created, which is why some methods like takeDamage() are here
 */

public class Enemies extends GameObject{

    //velocity variables for enemies
    //update speed in enemy manager
    public double enemyVelocityX = 0;
    public double enemyVelocityY = 0;

    public long enemyTimeOfSpawn = System.currentTimeMillis(); //used to track enemy spawn time

    public static int enemyWidth = 20; //please update width and height to the same values to prevent ellipse hitboxes
    public static int enemyHeight = 20;

    public int health = 3; //change enemy health here
    public int enemyDamage = 20; //change enemy damage here

    @Override
    public void act() {}

    /**
     * Constructor that initializes the game object and its attributes
     * Pre: none
     * Post: A new enemy instance with specified dimensions and colored green
     */
    public Enemies() {
        this.setSize(enemyWidth,enemyHeight);
        this.setColor(Color.GREEN); //change color for enemies here
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
        double enemyTargetX;
        double enemyTargetY;
        double distanceForEnemies;

        //calculating new target location for enemy
        enemyTargetX = player.getX() - this.getX();
        enemyTargetY = player.getY() - this.getY();
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

        //moves the enemies
        this.setX((int)(this.getX() + this.enemyVelocityX));
        this.setY((int)(this.getY() + this.enemyVelocityY));

        if (this.health == 2) {
            this.setColor(Color.YELLOW);
        }

        if (this.health == 1) {
            this.setColor(Color.RED);
        }

        //removes enemy from array and viewport in main game class
        if (this.collides(player)) {
            mainGame.remove(this);
            return true;
        }
        return false;
    }
}

