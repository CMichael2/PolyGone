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
    public ArrayList<Enemies> enemiesList = new ArrayList<>();
    private double enemySpeed = 4.0; //used to determine enemy speed, 33% of default player speed
    private long lastEnemySpawnTime = 0;
    public int baseEnemySpawnRate = 2000;
    public int enemySpawnRate = 2000; //used to determine the enemy spawn rate in milliseconds
    private boolean isFirstEnemy = true; //used to begin spawning of enemies
    public double enemyDroppedXp = 300.0;

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
    public ArrayList<Enemies> getEnemiesList() {
        return this.enemiesList;
    }

    /**
     * Helper method to clear enemies such as when the player dies or a save is reloaded
     * Pre: Enemies list exists
     * Post: Empty enemies list
     */
    public void clearEnemies() {
        for (Enemies e : enemiesList) {
            game.remove(e);
        }
        enemiesList.clear();
        isFirstEnemy = true;
        lastEnemySpawnTime = 0;
        enemySpawnRate = baseEnemySpawnRate;
    }

    /**
     * Calls 2 other methods instead of having to call 2 methods, we only have to call one to update enemy info
     * Pre: none
     * Post: 2 called methods
     */
    public void update() {
        enemySpawning();
        enemyBehaviorUpdates();
    }

    /**
     * Spawns an enemy at a random offscreen location
     * Pre:
     */
    private void enemySpawning() {
        if (System.currentTimeMillis() - lastEnemySpawnTime > enemySpawnRate || isFirstEnemy) {
            Enemies newEnemy = new Enemies();
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
        }
    }

    private void enemySpawnPosition(Enemies newEnemy) {
        Random r = new Random();
        final int ENEMY_SPAWN_POSITION_BUFFER = 30;
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
            Enemies e = enemiesList.get(i);

            //calls method in enemies class for enemy movement and enemy default collision with PLAYER
            if (e.enemyMovementUpdates(game, player, enemySpeed)) {

                int currentPlayerHealth = player.updateHealth(e.enemyDamage);

                //player death moved to main class

                enemiesList.remove(i);
                i--;
            }
        }
    }
}
