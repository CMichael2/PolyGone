package Project;
import java.io.*;

public class SaveGame implements Serializable {

    private static final long serialVersionUID = -3615957728628944243L;
    public int savedPlayerHealth;
    public int savedPlayerMaxHealth;
    public int savedPlayerLevel;
    public double savedPlayerXp;
    public int savedPlayerMaxXp;
    public int savedPlayerTotalXp;
    public int savedPlayerMaxAmmo;
    public int savedPlayerSpeed;
    public double savedBulletSpeed;
    public int savedEnemySpawnRate;
    public double savedEnemyDroppedXp;
    public int unfilledSlot;

    public void saveData(PolyGone game, Player player, MainMenu mainMenu) throws IOException {
        savedPlayerHealth = player.playerCurrentHealth;
        savedPlayerMaxHealth = player.playerMaxHealth;
        savedPlayerLevel = player.playerLevel;
        savedPlayerXp = player.currentPlayerXp;
        savedPlayerMaxXp = player.playerXPBarMaxXP;
        savedPlayerTotalXp = player.totalPlayerXp;
        savedPlayerMaxAmmo = player.maxAmmo;
        savedPlayerSpeed = player.playerSpeed;
        savedBulletSpeed = player.bulletSpeed;
        savedEnemySpawnRate = game.enemySpawnRate;
        savedEnemyDroppedXp = game.enemyDroppedXp;
        unfilledSlot = mainMenu.unfilledSlot;

        int saveNumber = game.saveSlotNumber;

        String filePath = "saves/save" + saveNumber + ".ser";

        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {

            objOut.writeObject(this);
            objOut.flush();

            System.out.println("Saved game to file");
        }
    }

    @Override
    public String toString() {
        return "SaveGame{" +
                "savedPlayerHealth = " + savedPlayerHealth +
                ", savedPlayerMaxHealth = " + savedPlayerMaxHealth +
                ", savedPlayerLevel = " + savedPlayerLevel +
                ", savedPlayerXp = " + savedPlayerXp +
                ", savedPlayerMaxXp = " + savedPlayerMaxXp +
                ", savedPlayerTotalXp = " + savedPlayerTotalXp +
                ", savedPlayerMaxAmmo = " + savedPlayerMaxAmmo +
                ", savedPlayerSpeed = " + savedPlayerSpeed +
                ", savedBulletSpeed = " + savedBulletSpeed +
                ", savedEnemySpawnRate = " + savedEnemySpawnRate +
                ", savedEnemyDroppedXP = " + savedEnemyDroppedXp +
                '}';
    }

    public static SaveGame loadData(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No save file found at: " + filePath);
            return null;
        }

        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream objIn = new ObjectInputStream(fileIn)) {

            SaveGame loadedSave = (SaveGame) objIn.readObject();
            System.out.println("Successfully loaded save data from: " + filePath);
            return loadedSave;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading save file: " + e.getMessage());
            return null;
        }
    }

    public void applyDataToGame(PolyGone game, Player player) {
        player.playerCurrentHealth = this.savedPlayerHealth;
        player.playerMaxHealth = this.savedPlayerMaxHealth;
        player.playerLevel = this.savedPlayerLevel;
        player.currentPlayerXp = this.savedPlayerXp;
        player.playerXPBarMaxXP = this.savedPlayerMaxXp;
        player.totalPlayerXp = this.savedPlayerTotalXp;
        player.maxAmmo = this.savedPlayerMaxAmmo;
        player.playerSpeed = this.savedPlayerSpeed;
        player.bulletSpeed = this.savedBulletSpeed;
        game.enemySpawnRate = this.savedEnemySpawnRate;
        game.enemyDroppedXp = this.savedEnemyDroppedXp;

        System.out.println("Save data values applied to live player and engine variables.");
    }
}
