package Project;

import Framework.GameObject;
import java.awt.*;
import java.util.*;

public class Enemies extends GameObject{



    //velocity variables for enemies
    //update speed in main class
    public double enemyVelocityX = 0;
    public double enemyVelocityY = 0;

    public long enemyTimeOfSpawn = System.currentTimeMillis();

    public static int enemyWidth = 20; //please update width and height to the same values to prevent ellipse hitboxes
    public static int enemyHeight = 20;

    private int health = 3; //change enemy health here
    public int enemyDamage = 20;

    @Override
    public void act() {}

    //visible attributes
    //spawn location is updated inside enemy spawn position method in game class
    public Enemies() {
        this.setSize(enemyWidth,enemyHeight);
        this.setColor(Color.GREEN); //change color for enemies here
    }

    //reduces the enemy health by 1 when called
    public void takeDamage(int damage) {
        this.health -= damage;
    }

    //checks if enemy is dead
    public boolean isDead() {
        return this.health <= 0;
    }

//called by main game class to update enemy movement towards the player
//also handles enemy existence and collision update
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

