/*
Michael Cao
6/5/2026
ICS3U1 Final Project
 */

package Project;

import Framework.Game; //package containing the abstract class game where all methods are inherited from

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;

public class PolyGone extends Game {

    private boolean isGameFocused = true;

    EnemyManager enemyManager;

    Player player; //creates player variable that follows the code in Player class

    public boolean isVSyncEnabled = false;
    private int monitorRefreshRate = 60;
    private int targetDelay = 16; //60Hz

    //gui and hud variables/objects
    private GUI gameUI; //object for referencing gui class

    private PauseMenu pauseMenu;
    private boolean pauseMenuKeyWasPressedLastFrame = false;
    private long pauseMenuOpenTime = 0;
    private GameState stateBeforePause = GameState.PLAYING;

    private SettingsMenu settingsMenu;
    private GameState previousState;

    private DebugHUD debugHUD;
    public boolean showDebugHUD = false;
    private boolean debugKeyWasPressedLastFrame = false;

    private UpgradeMenu upgradeMenu;
    private long upgradeMenuOpenTime = 0;

    public long totalTimeSpentPaused = 0;

    public int saveSlotNumber = 0;

    private DeathMenu deathMenu;

    private WinMenu winMenu;
    public boolean firstWin = true; //makes sure that win screen only shows once

    private MainMenu mainMenu;
    public boolean isGameInitialStart = true;

    private SaveGame saveGame;
    public SaveGame pendingSaveData = null;

    private GameState currentState = GameState.MAIN_MENU;
    public GameState getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(GameState newState) {
        this.currentState = newState;
    }

    private Clip backgroundMusicClip;

    private final Set<Integer> activeKeys = new HashSet<>(); //arraylist to store unlimited active keys

    //method called from GameKeyInput class, adds a key code to the activeKeys array once for each unique key that is pressed
    public void setKeyState(int keyCode, boolean isPressed) {
        if (isPressed) {
            activeKeys.add(keyCode);
        } else {
            activeKeys.remove(keyCode);
        }
    }

    //searches the array to check if the key that is being pressed exists (is it pressed or not)
    public boolean isKeyPressed(int keyCode) {
        return activeKeys.contains(keyCode);
    }

    @Override
    public void setup() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            DisplayMode mode = ge.getDefaultScreenDevice().getDisplayMode();
            int refreshRate = mode.getRefreshRate();

            //fall back to 60
            if (refreshRate != DisplayMode.REFRESH_RATE_UNKNOWN) {
                monitorRefreshRate = refreshRate;
            }
        } catch (Exception e) {
            monitorRefreshRate = 60;
        }

        setVSync(true);

        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                isGameFocused = true;
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                isGameFocused = false;
                if (pauseMenu != null) {
                    if (!pauseMenu.isPauseMenuVisible) {
                        pauseGame(true);
                    }
                }
                //disables keys when not in focus to prevent player movement
                activeKeys.clear();
                System.out.println("Game window is no longer focused");
            }
        });

        this.addKeyListener(new GameKeyInput(this)); //key listener

        GameMouseInput mouseHandler = new GameMouseInput(); //creates a new variable that is from the mouse input class
        //mouse inputs from mouse input class
        this.addMouseMotionListener(mouseHandler);
        this.addMouseListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);

        mainMenu = new MainMenu(this, player);
        add(mainMenu);

        settingsMenu = new SettingsMenu(this);
        add(settingsMenu);

        //moves game objects to front or back
        this.getContentPane().setComponentZOrder(mainMenu, 0);

        openMainMenu();
        this.isGameInitialStart = false;

        playBackgroundMusic("Assets/menu_theme.wav");
    }

    @Override
    public void act() throws IOException {
        openPauseMenu();
        openDebugHUD();

        switch (currentState) {
            case SETTINGS_MENU:
                if (settingsMenu != null) {
                    settingsMenu.act();
                }
                return;

            case MAIN_MENU:
                if (mainMenu != null) {
                    mainMenu.act();
                }
                return;

            case PAUSED:
                if (pauseMenu != null) {
                    pauseMenu.act();
                    GameMouseInput.reset();
                    return;
                }
                break;

            case DEATH_SCREEN:
                if (deathMenu != null) {
                    deathMenu.act();
                    GameMouseInput.reset();
                    return;
                }
                break;

            case WIN_SCREEN:
                if (winMenu != null) {
                    winMenu.act();
                }
                GameMouseInput.reset();
                return;

            case UPGRADE_MENU:
                if (upgradeMenu != null) {
                    upgradeMenu.act();
                }
                GameMouseInput.reset();
                return;

            case PLAYING:
                gameLost();
                gameWon();
                if (player == null) {
                    return;
                }

                player.act();

                Bullets.bulletBehavior(this, player, enemyManager);
                enemyManager.update();
                break;
        }

        //resets inputs in mouse input class
        GameMouseInput.reset();
    }

    public void setVSync(boolean enable) {
        this.isVSyncEnabled = enable;
        System.out.println("VSync changed to: " + isVSyncEnabled + " (" + monitorRefreshRate + "Hz)");
        if (isVSyncEnabled) {
            //match delay to refresh rate
            targetDelay = 1000 / monitorRefreshRate;
        }
        setDelay(targetDelay);
    }

    public void openMainMenu() {
        this.currentState = GameState.MAIN_MENU;
        if (mainMenu != null) mainMenu.setMainMenuVisible(true);
        this.getContentPane().setComponentZOrder(settingsMenu, 0);
        this.getContentPane().setComponentZOrder(mainMenu, 1);
        if (debugHUD != null) {
            this.getContentPane().setComponentZOrder(debugHUD, 2);
        }
        GameMouseInput.reset();
        this.repaint();
    }

    public void prepareGameSession() {
        if (player == null && gameUI == null) {
            enemyManager = new EnemyManager(this, player);

            player = new Player(this, enemyManager);
            add(player);

            enemyManager.player = player;

            if (mainMenu != null) mainMenu.player = player;
            if (pauseMenu != null) pauseMenu.player = player;
            if (winMenu != null) winMenu.player = player;
            if (deathMenu != null) deathMenu.player = player;

            gameUI = new GUI(this, player);
            add(gameUI);

            if (debugHUD == null) {
                debugHUD = new DebugHUD(this, player, enemyManager);
                add(debugHUD);

                debugHUD.setDebugHUDVisible(showDebugHUD);
            }
            if (upgradeMenu == null) {
                upgradeMenu = new UpgradeMenu(this, player, debugHUD, enemyManager);
                add(upgradeMenu);
            }
            if (pauseMenu == null) {
                pauseMenu = new PauseMenu(this, player, mainMenu, enemyManager);
                add(pauseMenu);
            }
            if (winMenu == null) {
                winMenu = new WinMenu(this, player, mainMenu, enemyManager);
                add(winMenu);
            }
            if (deathMenu == null) {
                deathMenu = new DeathMenu(this, player, mainMenu, enemyManager);
                add(deathMenu);
            }

            this.getContentPane().setComponentZOrder(mainMenu, 0);
            this.getContentPane().setComponentZOrder(debugHUD, 1);
            this.getContentPane().setComponentZOrder(settingsMenu, 2);
            this.getContentPane().setComponentZOrder(pauseMenu, 3);
            this.getContentPane().setComponentZOrder(deathMenu, 4);
            this.getContentPane().setComponentZOrder(winMenu, 5);
            this.getContentPane().setComponentZOrder(upgradeMenu, 6);
            this.getContentPane().setComponentZOrder(gameUI, 7);
            this.getContentPane().setComponentZOrder(player, 8);
        } else {
            player.setVisible(true);
            gameUI.setVisible(true);

            enemyManager.clearEnemies();
            Bullets.clearAllBullets(this);
        }

        if (this.pendingSaveData != null) {
            this.pendingSaveData.applyDataToGame(this, this.player, mainMenu, enemyManager);
            this.pendingSaveData = null;
            System.out.println("Save loaded in background");
        } else {
            if (player != null) {
                gameReset();
            }
            System.out.println("New game started");
        }
    }

    public void activatePlayingState() {
        this.currentState = GameState.PLAYING;

        if (mainMenu != null) {
            mainMenu.setMainMenuVisible(false);
        }
        playBackgroundMusic("Assets/playing_theme.wav");

        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    public void openSettingsMenu() {
        previousState = currentState;
        this.currentState = GameState.SETTINGS_MENU;
        if (settingsMenu != null) {
            settingsMenu.setSettingsMenuVisible(true);
        }
        GameMouseInput.reset();
        GameMouseInput.isMouseLeftClickPressed = false;
        this.repaint();
    }

    public void closeSettingsMenu() {
        this.currentState = previousState;
        if (settingsMenu != null) {
            settingsMenu.setSettingsMenuVisible(false);
        }
        if (previousState == GameState.PAUSED) {
            pauseMenu.setPauseMenuVisible(true);
        }
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    private void openPauseMenu() {
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            //only toggles on first frame of being pressed
            if (!pauseMenuKeyWasPressedLastFrame) {
                if (currentState == GameState.PAUSED) {
                    if (pauseMenu != null) {
                        if (pauseMenu.pauseMenuState == 2 || pauseMenu.pauseMenuState == 1) {
                            pauseMenu.pauseMenuState = 3;
                        }
                    }
                } else {
                    if (currentState != GameState.PAUSED && currentState == GameState.UPGRADE_MENU) {
                        long now = System.currentTimeMillis();
                        long timeSpentInUpgradeSoFar = now - upgradeMenuOpenTime;
                        for (Bullets b : Bullets.getBulletsList()) {
                            b.bulletTimeOfFire += timeSpentInUpgradeSoFar;
                        }
                    }
                    pauseGame(true);
                }
                pauseMenuKeyWasPressedLastFrame = true; //prevents the toggle from activating again until the key is released
            }
        } else {
            pauseMenuKeyWasPressedLastFrame = false;
        }
    }

    private void pauseGame(boolean shouldPause) {
        if (shouldPause && this.currentState != GameState.PAUSED) {
            this.stateBeforePause = this.currentState;
        }

        this.currentState = GameState.PAUSED;

        if (pauseMenu != null) {
            pauseMenu.setPauseMenuVisible(shouldPause);
        }
        if (shouldPause) {
            this.pauseMenuOpenTime = System.currentTimeMillis();
        } else {
            long timeSpentPaused = System.currentTimeMillis() - pauseMenuOpenTime;

            for (Bullets b : Bullets.getBulletsList()) {
                b.bulletTimeOfFire += timeSpentPaused;
            }
            GameMouseInput.isMouseLeftClickPressed = false;
        }
        GameMouseInput.reset();
    }

    //unpauses game
    public void unpauseGame() {
        this.currentState = this.stateBeforePause;

        long timeSpentPaused = System.currentTimeMillis() - pauseMenuOpenTime;

        for (Bullets b : Bullets.getBulletsList()) {
            b.bulletTimeOfFire += timeSpentPaused;
        }

        if (pauseMenu != null) {
            pauseMenu.setPauseMenuVisible(false);
        }

        if (this.currentState == GameState.UPGRADE_MENU) {
            this.upgradeMenuOpenTime = System.currentTimeMillis();
        }
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    public void openUpgradeMenu() {
        this.currentState = GameState.UPGRADE_MENU;
        this.upgradeMenuOpenTime = System.currentTimeMillis();
        if (upgradeMenu != null) upgradeMenu.setUpgradeMenuVisible(true);
        GameMouseInput.reset();
        this.repaint();
    }

    public void closeUpgradeMenu() {
        this.currentState = GameState.PLAYING;

        long timeSpentInMenu = System.currentTimeMillis() - upgradeMenuOpenTime;

        for (Bullets b : Bullets.getBulletsList()) {
            b.bulletTimeOfFire += timeSpentInMenu;
        }

        if (upgradeMenu != null) {
            upgradeMenu.setUpgradeMenuVisible(false);
        }
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    private void openDebugHUD() {
        if (isKeyPressed(KeyEvent.VK_F3)) {
            //only toggles on first frame of being pressed
            if (!debugKeyWasPressedLastFrame) {
                showDebugHUD = !showDebugHUD; //changes the state of debug hud to the opposite

                if (debugHUD != null) { //checks if the debug hud has been created
                    debugHUD.setDebugHUDVisible(showDebugHUD); //calls method to toggle the debug hud
                }

                debugKeyWasPressedLastFrame = true; //prevents the toggle from activating again until the key is released
            }
        } else {
            debugKeyWasPressedLastFrame = false;
        }
    }

    public void toggleDebugHUD() {
        showDebugHUD = !showDebugHUD;
        if (debugHUD != null) {
            debugHUD.setDebugHUDVisible(showDebugHUD);
        }
        System.out.println("Debug HUD toggled via menu: " + showDebugHUD);
    }

    public void playBackgroundMusic(String filePath) {
        //checks if music is playing to avoid overlays
        stopBackgroundMusic();

        try {
            File musicFile = new File(filePath);
            if (musicFile.exists()) {
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(AudioSystem.getAudioInputStream(musicFile));

                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusicClip.start();
            } else {
                System.out.println("C418 Music file not found: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }

    public void gameLost() {
        if (player.playerCurrentHealth <= 0) {
            this.currentState = GameState.DEATH_SCREEN;
            if (deathMenu != null) {
                deathMenu.setDeathMenuVisible(true);
            }
            System.out.println("Player has died");
        }
    }

    public void gameWon() {
        if (player.playerLevel == 50 && firstWin) {
            this.currentState = GameState.WIN_SCREEN;
            firstWin = false;
            if (winMenu != null) {
                winMenu.setWinMenuVisible(true);
            }
            System.out.println("Player has won game");
        }
    }

    public void gameReset() {
        if (winMenu != null) {
            winMenu.setWinMenuVisible(false);
        }
        if (deathMenu != null) {
            deathMenu.setDeathMenuVisible(false);
        }

        for (Enemy e : enemyManager.getEnemiesList()) {
            remove(e);
        }
        for (Bullets b : Bullets.getBulletsList()) {
            remove(b);
        }

        enemyManager.clearEnemies();
        Bullets.clearAllBullets(this);

        player.playerCurrentHealth = player.playerMaxHealth; //resets player health

        //moves player back to middle of the screen
        player.setX((this.getWidth() / 2) - (player.getWidth() / 2));
        player.setY((this.getHeight() / 2) - (player.getHeight() / 2));
        player.playerLevel = player.startingPlayerLevel;
        player.playerCurrentHealth = player.playerMaxHealth;
        player.playerXPBarMaxXP = 10 + (int)((Math.pow(player.playerLevel, 1.8)/4.0)+0.5);;
        player.currentPlayerXp = 0;
        player.totalPlayerXp = 0;
        player.currentWeaponIndex = 0;

        player.weaponsList.clear();
        player.addWeapon(0);
        player.currentWeaponIndex = 0;

        upgradeMenu.numberOfRerollsLeft = upgradeMenu.startingNumberOfRerolls;
        firstWin = true;

        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();

        this.currentState = GameState.PLAYING;

        this.repaint();
    }

    //closes game if the escape key is pressed
    public void exitGame() {
        System.out.println("Player quit game");
        System.exit(67);
    }

    //main method
    public static void main(String[] args) {
        PolyGone game = new PolyGone();

        ImageIcon icon = new ImageIcon("Assets/Polygon.png");
        game.setIconImage(icon.getImage());

        game.setJMenuBar(null);
        game.setUndecorated(true);
        game.setResizable(false);

        //gets the size and moves window to top left to fill screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        game.setSize(screenSize);
        game.setLocation(0, 0);

        //launches window
        game.setVisible(true);
        game.initComponents(); //such as game objects
    }
}

