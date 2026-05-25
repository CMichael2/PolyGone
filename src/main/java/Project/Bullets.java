package Project;

import Framework.GameObject;
import Framework.Game;

import java.awt.*;
/*
Handles bullet movement and existence
 */
public class Bullets extends GameObject {
    //other variables are located inside player class as bullets are created from the player

    //velocity variables for bullets
    public double bulletVelocityX = 0;
    public double bulletVelocityY = 0;
    private double exactX;
    private double exactY;
    private boolean isFirstFrame = true;

    public long bulletTimeOfFire = System.currentTimeMillis();

    public int maxBulletLifeSpan = 1000; //change to determine the distance the bullets fly in milliseconds

    //handles bullet lifespan and existence
    public boolean bulletUpdates(PolyGone game) {
        long bulletLifeSpan;

        //casts bullet coordinates to double on first frame
        if (isFirstFrame) {
            exactX = this.getX();
            exactY = this.getY();
            isFirstFrame = false;
        }

        //adds velocity to the decimal position tracking
        exactX += bulletVelocityX;
        exactY += bulletVelocityY;

        //casts to int only for rendering
        this.setX((int) Math.round(exactX));
        this.setY((int) Math.round(exactY));

        bulletLifeSpan = System.currentTimeMillis() - this.bulletTimeOfFire;

        if (bulletLifeSpan > maxBulletLifeSpan) {
            game.remove(this);
            return true; //passes value to main game class to remove the bullet from the bullet list array in main game class
        }
        return false;
    }

    @Override
    public void act() { }
}
