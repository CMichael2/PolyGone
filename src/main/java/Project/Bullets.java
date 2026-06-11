package Project;

import Framework.GameObject;

import java.util.ArrayList;

/*
Handles bullet movement and existence
Creation is handled in player class
 */
public class Bullets extends GameObject {

    //variables for bullet positioning
    public double bulletVelocityX = 0;
    public double bulletVelocityY = 0;
    private double exactX;
    private double exactY;
    private boolean isFirstFrame = true;

    public long bulletTimeOfFire = System.currentTimeMillis();

    private static ArrayList<Bullets> bulletsList = new ArrayList<>(); //creates arraylist of bullets
    public static double bulletDamage;

    /**
     * Getter method for array list of bullets
     * Pre: bullet list exists
     * Post: bullet list
     * @return bullet list
     */
    public static ArrayList<Bullets> getBulletsList() {
        return bulletsList;
    }

    /**
     * Deletes all bullets. Place here for better access to array list
     * Pre: none
     * Post: empty bullet list
     * @param game Parameter from PolyGone
     */
    public static void clearAllBullets(PolyGone game) {
        for (Bullets b : bulletsList) { //for each loop
            game.remove(b);
        }
        bulletsList.clear();
    }

    /**
     * Handles bullet behavior such as movement, collision, and range/lifespan
     * Pre: bullets list exists
     * Post: updated bullet behavior (collision, etc.)
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     * @param enemyManager Parameter from Enemy Manager
     */
    public static void bulletBehavior(PolyGone game, Player player, EnemyManager enemyManager) {
        for (int i = 0; i < bulletsList.size(); i++) {
            Bullets b = bulletsList.get(i);

            //bullet location before movement
            int bulletPrevX = b.getX();
            int bulletPrevY = b.getY();

            //determines if a bullet should be removed based on return value of bulletUpdates()
            if (b.bulletUpdates(game, player)) {
                bulletsList.remove(i);
                i--;
                continue; //to stop checking collision for removed bullets
            }

            //calls collision checking method of enemies colliding with bullets based on ray casting and removes bullets if necessary
            if (enemyAndBulletCollisionChecking(game, player, enemyManager, b, bulletPrevX, bulletPrevY)) {
                game.remove(b);
                bulletsList.remove(i);
                i--;
            }
        }
    }

    /**
     * Checks if a bullet collides with an enemy by default collision or ray casting collision
     * Pre: Objects are not null, bullets exist
     * Post: A true boolean value if the bullet collided with an enemy
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     * @param enemyManager Parameter from Enemy Manager
     * @param b The bullet that is being checked if it has collided
     * @param bulletPrevX The bullet's x value
     * @param bulletPrevY The bullet's y value
     * @return True or false value based on the given conditions
     */
    private static boolean enemyAndBulletCollisionChecking(PolyGone game, Player player, EnemyManager enemyManager, Bullets b, int bulletPrevX, int bulletPrevY) {
        ArrayList<Enemy> enemiesList = enemyManager.getEnemiesList();

        //loop to check if any bullet collides with any enemy
        for (int j = 0; j < enemiesList.size(); j++) { //cycles through all enemies
            Enemy e = enemiesList.get(j);

            //calls ray casting enemy collision method to check for collisions with bullets
            //or uses regular collision checking inherited from game object class
            if (e.collides(b) || bulletPathIntersectsEnemy(b, bulletPrevX, bulletPrevY, e)) {

                int explosionX = b.getX() + (b.getWidth() / 2);
                int explosionY = b.getY() + (b.getHeight() / 2);

                e.health -= bulletDamage; //updates enemy health

                if (player.activeWeapon.hasSplashDamage) {
                    triggerSplashDamage(game, player, enemyManager, explosionX, explosionY);
                }
                if (e.health <= 0) {
                    if (enemiesList.contains(e)) {
                        game.remove(e); //removes enemy when killed
                        enemiesList.remove(e);
                    }
                    player.updatePlayerXP(e.enemyDroppedXp, game);
                }
                game.repaint();

                return true;
            }
        }
        return false;
    }

    /**
     * Uses ray casting to determine if a bullet will collide with a selected enemy.
     * Works by finding the closest point in the bullet's trajectory in the frame to an enemy and checks if the 2 objects have/will collide at that point
     * Pre: Bullet and enemy exist
     * Post: Did the bullet collide with the enemy? Yes or no
     * @param b Bullet being checked
     * @param bulletPrevX Bullet x
     * @param bulletPrevY Bullet y
     * @param e Enemy being checked
     * @return True or false value based on the given conditions
     */
    private static boolean bulletPathIntersectsEnemy(Bullets b, int bulletPrevX, int bulletPrevY, Enemy e) {
        int bulletCurrX = b.getX();
        int bulletCurrY = b.getY();

        //only checks if the distance between the bullet and enemy is close enough that they might collide
        int buffer = (int) (300 * PolyGone.deltaTime) + 50; //bullet travel in one frame
        if (Math.abs(bulletCurrX - e.getX()) > buffer && Math.abs(bulletPrevX - e.getX()) > buffer) {
            return false;
        }
        if (Math.abs(bulletCurrY - e.getY()) > buffer && Math.abs(bulletPrevY - e.getY()) > buffer) {
            return false;
        }

        //gets radii
        double bulletRadius = Player.bulletWidth / 2.0;
        double enemyRadius = Enemy.enemySize / 2.0;

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
        return distanceSquared <= (combinedRadius * combinedRadius);
    }

    /**
     * Handles bullet range and movement
     * Pre: Bullet exists
     * Post: The bullet's existence
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     * @return True if the bullet exceeded its range and was removed, false otherwise
     */
    public boolean bulletUpdates(PolyGone game, Player player) {
        //casts bullet coordinates to double on first frame
        if (isFirstFrame) {
            exactX = this.getX();
            exactY = this.getY();
            isFirstFrame = false;
        }

        //adds velocity to the decimal position tracking
        exactX += bulletVelocityX * PolyGone.deltaTime;
        exactY += bulletVelocityY * PolyGone.deltaTime;

        //casts to int only for rendering
        this.setX((int) Math.round(exactX));
        this.setY((int) Math.round(exactY));

        double distanceFromPlayerX = Math.abs(exactX - player.getX());
        double distanceFromPlayerY = Math.abs(exactY - player.getY());

        int distance = (int)Math.sqrt((distanceFromPlayerX*distanceFromPlayerX) + (distanceFromPlayerY*distanceFromPlayerY));

        if (player.activeWeapon.range > 100) {
            if (distance > player.activeWeapon.range) {
                System.out.println(distance);
                game.remove(this);
                return true; //passes value to main game class to remove the bullet from the bullet list array in main game class
            }
        }
        return false;
    }

    private static void triggerSplashDamage(PolyGone game, Player player, EnemyManager enemyManager, int explosionX, int explosionY) {
        ExplosionEffect visualBlast = new ExplosionEffect(game, explosionX, explosionY);
        game.add(visualBlast);

        game.getContentPane().setComponentZOrder(visualBlast, game.getContentPane().getComponentCount() - 1); //sets explosion to below enemies

        ArrayList<Enemy> enemiesList = enemyManager.getEnemiesList();

        //splash damage properties
        double splashRadius = 150.0;
        double splashDamage = bulletDamage * 0.75; //set to 75% of damage

        //backwards loop to prevent errors
        for (int i = enemiesList.size() - 1; i >= 0; i--) {
            Enemy e = enemiesList.get(i);

            if (e.health <= 0) continue;

            //distance from enemy center to splash center
            double enemyCenterX = e.exactX + (e.getWidth() / 2.0);
            double enemyCenterY = e.exactY + (e.getHeight() / 2.0);

            double dx = enemyCenterX - explosionX;
            double dy = enemyCenterY - explosionY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= splashRadius) {  //if inside radius
                e.health -= splashDamage;

                if (e.health <= 0) {
                    game.remove(e);
                    enemiesList.remove(i);
                    player.updatePlayerXP(e.enemyDroppedXp, game);
                }
            }
        }
        game.repaint();
    }

    @Override
    public void act() { }
}
