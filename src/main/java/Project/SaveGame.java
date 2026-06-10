package Project;
import java.io.*;

public class SaveGame implements Serializable {

    //class fields that are serialized and saved in the save files
    private static final long serialVersionUID = -3615957728628944243L;
    public int savedPlayerHealth;
    public int savedPlayerMaxHealth;
    public int savedPlayerLevel;
    public double savedPlayerXp;
    public int savedPlayerMaxXp;
    public int savedPlayerTotalXp;
    public int savedPlayerSpeed;
    public int savedEnemySpawnRate;
    public double savedEnemyDroppedXp;
    public boolean isFirstWin;

    /**
     * When called, it saves the value of specified objects/variables into a .ser save file that is named. The saved file can't be read by a human.
     * Pre: Player, Game, and MainMenu are not null and the method is called (the game data must be saved)
     * Post: A new file or overwritten file with the saved values/data
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     * @param mainMenu Parameter from MainMenu
     * @throws IOException In the event something goes wrong, it has a fallback and doesn't crash
     */
    public void saveData(PolyGone game, Player player, MainMenu mainMenu, EnemyManager enemyManager) throws IOException {
        savedPlayerHealth = player.playerCurrentHealth;
        savedPlayerMaxHealth = player.playerMaxHealth;
        savedPlayerLevel = player.playerLevel;
        savedPlayerXp = player.currentPlayerXp;
        savedPlayerMaxXp = player.playerXPBarMaxXP;
        savedPlayerTotalXp = player.totalPlayerXp;
        savedPlayerSpeed = player.playerSpeed;
        savedEnemySpawnRate = enemyManager.enemySpawnRate;
        savedEnemyDroppedXp = enemyManager.enemyDroppedXp;
        isFirstWin = game.firstWin;

        int saveNumber = game.saveSlotNumber;

        String filePath = "saves/save" + saveNumber + ".ser"; //file name formating

        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {

            objOut.writeObject(this);
            objOut.flush();

            System.out.println("Saved game to file");
        }
    }

    /**
     * Used to format the saved data into human-readable text
     * Pre: Data is not null
     * Post: A string of the data values
     * @return A string of the data values
     */
    @Override
    public String toString() {
        return "SaveGame{" +
                "savedPlayerHealth = " + savedPlayerHealth +
                ", savedPlayerMaxHealth = " + savedPlayerMaxHealth +
                ", savedPlayerLevel = " + savedPlayerLevel +
                ", savedPlayerXp = " + savedPlayerXp +
                ", savedPlayerMaxXp = " + savedPlayerMaxXp +
                ", savedPlayerTotalXp = " + savedPlayerTotalXp +
                ", savedPlayerSpeed = " + savedPlayerSpeed +
                ", savedEnemySpawnRate = " + savedEnemySpawnRate +
                ", savedEnemyDroppedXP = " + savedEnemyDroppedXp +
                '}';
    }

    /**
     * Loads data from a file with the same format and serial version UID
     * Pre: Data loading is requested
     * Post: Nothing if there is no file. The file data values saved as a SaveGame type object.
     * @param filePath Determines which file to read and load
     * @return Nothing if there is no file. The file data values saved as a SaveGame type object.
     */
    public static SaveGame loadData(String filePath) {
        File file = new File(filePath); //gets the file based on the file name/path

        if (!file.exists()) {
            System.out.println("No save file found at: " + filePath);
            return null;
        }

        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream objIn = new ObjectInputStream(fileIn)) {

            SaveGame loadedSave = (SaveGame) objIn.readObject(); //loads onto an object
            System.out.println("Successfully loaded save data from: " + filePath);
            return loadedSave;

        } catch (IOException | ClassNotFoundException e) { //try and catch fall-back
            System.err.println("Error reading save file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Applies the data to the game by updating the variables to what was stored
     * Pre: Pending save data, Player, Game, etc. is not null
     * Post: Updated data values as seen below
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     */
    public void applyDataToGame(PolyGone game, Player player, MainMenu mainMenu, EnemyManager enemyManager) {
        player.playerCurrentHealth = this.savedPlayerHealth;
        player.playerMaxHealth = this.savedPlayerMaxHealth;
        player.playerLevel = this.savedPlayerLevel;
        player.currentPlayerXp = this.savedPlayerXp;
        player.playerXPBarMaxXP = this.savedPlayerMaxXp;
        player.totalPlayerXp = this.savedPlayerTotalXp;
        player.playerSpeed = this.savedPlayerSpeed;
        enemyManager.enemySpawnRate = this.savedEnemySpawnRate;
        enemyManager.enemyDroppedXp = this.savedEnemyDroppedXp;
        game.firstWin = this.isFirstWin;

        System.out.println("Save data values applied");
    }
}
