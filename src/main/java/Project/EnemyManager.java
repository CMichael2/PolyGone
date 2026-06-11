package Project;

import java.util.ArrayList;
import java.util.Random;

/*
Handles enemy spawning and behavior
 */
public class EnemyManager {
    private final PolyGone game;
    public Player player;

    //class enemy variables
    //placed here because enemies are spawned by the main game and follow code in enemies class
    public ArrayList<Enemy> enemiesList = new ArrayList<>();
    private double enemySpeed = 120.0; //used to determine enemy speed, 66% of default player speed
    private long lastEnemySpawnTime = 0;
    public int enemyWaveSpawnRate = 500; //used to determine the enemy spawn rate in milliseconds
    public int enemyDefaultSpawnRate = 1000;
    private boolean isFirstEnemy = true; //used to begin spawning of enemies
    public boolean isBoss = true;
    public double enemyDroppedXpMultiplier = 1.0;
    public double maxEnemyHealth;

    public int waveNum = 0;
    public int totalEnemiesPerWave;
    public int enemyCount = 0;

    /**
     * Constructor that initializes the game object and its attributes
     * Pre: none
     * Post: Object references
     * @param game Parameter from Game
     * @param player Parameter from Player
     */
    public EnemyManager(PolyGone game, Player player) {
        this.game = game;
        this.player = player;
    }

    /**
     * Getter method for array list of enemies
     * Pre: enemies list exists
     * Post: enemies list
     * @return enemies list
     */
    public ArrayList<Enemy> getEnemiesList() {
        return this.enemiesList;
    }

    /**
     * Helper method to clear enemies such as when the player dies or a save is reloaded
     * Pre: Enemies list exists
     * Post: Empty enemies list
     */
    public void clearEnemies() {
        for (Enemy e : enemiesList) {
            game.remove(e);
        }
        enemiesList.clear();
        isFirstEnemy = true;
        lastEnemySpawnTime = 0;
    }

    /**
     * Calls 3 other methods. Instead of having to separately call 3 methods, we only have to call one to update enemy info
     * Pre: none
     * Post: 3 called methods
     */
    public void update() {
        enemySpawning();
        enemyBehaviorUpdates();
        handleEnemyCollisions();
    }

    /**
     * Spawns an enemy at a random offscreen location
     * Pre:
     */
    private void enemySpawning() {
        waveNum = player.playerLevel;
        totalEnemiesPerWave = 5 + (int)((Math.pow(player.playerLevel, 1.6)/4.0)+0.5);

        if (((System.currentTimeMillis() - lastEnemySpawnTime > enemyWaveSpawnRate || isFirstEnemy) && enemyCount <= totalEnemiesPerWave) || (System.currentTimeMillis() - lastEnemySpawnTime > enemyDefaultSpawnRate))  {
            Enemy newEnemy = new Enemy(10, 40, 1 + enemyDroppedXpMultiplier, false, 20);
            newEnemy.enemyTimeOfSpawn = System.currentTimeMillis();

            enemySpawnPosition(newEnemy);
            game.add(newEnemy);
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
            enemyCount += 1;
            System.out.println(enemyCount);
        }

        if (player.playerLevel%5 == 0 && isBoss && player.playerLevel > 0) {
            Enemy newEnemy = new Enemy(20 * player.playerLevel, 200, 10 * enemyDroppedXpMultiplier, true, 75);
            newEnemy.enemyTimeOfSpawn = System.currentTimeMillis();

            enemySpawnPosition(newEnemy);
            game.add(newEnemy);
            enemiesList.add(newEnemy); //adds new element to array list

            double enemyTargetX = player.getX() - newEnemy.getX();
            double enemyTargetY = player.getY() - newEnemy.getY();
            double distanceForEnemies = Math.sqrt(enemyTargetX * enemyTargetX + enemyTargetY * enemyTargetY);

            if (distanceForEnemies > 1.0) {
                newEnemy.enemyVelocityX = (enemyTargetX / distanceForEnemies) * enemySpeed;
                newEnemy.enemyVelocityY = (enemyTargetY / distanceForEnemies) * enemySpeed;
            }
            isBoss = false;
        }
    }

    private void enemySpawnPosition(Enemy newEnemy) {
        Random r = new Random();
        final int ENEMY_SPAWN_POSITION_BUFFER = 100;
        int side = r.nextInt(4);

        switch (side) {
            case 0: //top of field spawning
                newEnemy.setX(r.nextInt(game.getWidth()));
                newEnemy.setY(-ENEMY_SPAWN_POSITION_BUFFER);
                break;

            case 1: //bottom of field spawning
                newEnemy.setX(r.nextInt(game.getWidth()));
                newEnemy.setY(game.getHeight() + ENEMY_SPAWN_POSITION_BUFFER);
                break;

            case 2: //left side of field spawning
                newEnemy.setX(-ENEMY_SPAWN_POSITION_BUFFER);
                newEnemy.setY(r.nextInt(game.getHeight()));
                break;

            case 3: //right side of field spawning
                newEnemy.setX(game.getWidth() + ENEMY_SPAWN_POSITION_BUFFER);
                newEnemy.setY(r.nextInt(game.getHeight()));
                break;
        }
    }

    private void enemyBehaviorUpdates() {
        //handles the creation and deletion of enemies based on enemies class
        for (int i = 0; i < enemiesList.size(); i++) {
            Enemy e = enemiesList.get(i);

            //calls method in enemies class for enemy movement and enemy default collision with PLAYER
            if (e.enemyMovementUpdates(game, player, enemySpeed)) {

                player.updateHealth(e.enemyDamage);

                //player death moved to main class

                enemiesList.remove(i);
                i--;
            }
        }
    }

    private void handleEnemyCollisions() {
        Random r = new Random();
        int randomOffSet = r.nextInt(3);

        for (int i = 0; i < enemiesList.size(); i++) {
            Enemy e1 = enemiesList.get(i);

            for (int j = i + 1; j < enemiesList.size(); j++) {
                Enemy e2 = enemiesList.get(j);

                //set min distance apart from centers, aka. the size of the enemy
                double r1 = e1.getWidth() / 2.0;
                double r2 = e2.getWidth() / 2.0;
                double minDistance = r1 + r2;

                double e1CenterX = e1.exactX + r1;
                double e1CenterY = e1.exactY + r1;
                double e2CenterX = e2.exactX + r2;
                double e2CenterY = e2.exactY + r2;

                //get distance from their centers
                double dx = e2CenterX - e1CenterX;
                double dy = e2CenterY - e1CenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                //when overlapping
                if (distance < minDistance) {
                    if (distance == 0.0) {
                        dx = 1.0;
                        dy = 0.0;
                        distance = 1.0;
                    }

                    //check how much the overlap is
                    double overlap = minDistance - distance;

                    double pushX = (dx / distance) * overlap * 0.5;
                    double pushY = (dy / distance) * overlap * 0.5;

                    //sets the amount to push each other away by with a random offset
                    e1.exactX -= pushX + randomOffSet;
                    e1.exactY -= pushY + randomOffSet;
                    e2.exactX += pushX + randomOffSet;
                    e2.exactY += pushY + randomOffSet;

                    //sets the new coordinates based on the push amount
                    e1.setX((int) Math.round(e1.exactX));
                    e1.setY((int) Math.round(e1.exactY));

                    e2.setX((int) Math.round(e2.exactX));
                    e2.setY((int) Math.round(e2.exactY));
                }
            }
        }
    }
}
