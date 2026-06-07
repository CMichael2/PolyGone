package Project;

import Framework.GameObject;
import java.awt.*;

public class DebugHUD extends GameObject {

    Player player; //reference to object
    PolyGone game;
    EnemyManager enemyManager;

    //fps counter variables
    private double fps = 0.0; //do not change values
    private int frameCount = 0;
    private long fpsTimer = System.currentTimeMillis();

    //text display variables
    private final Font FONT = new Font("OCR A Extended", Font.PLAIN, 20);
    private boolean isVisible = false;

    /**
     * Constructor that initializes the game object and fields
     * Pre: PolyGone, Player, and EnemyManager instances are initialized and not null
     * Post: Sets the size of this game object to fill the screen
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     * @param enemyManager Parameter from Enemy Manager
     */
    public DebugHUD(PolyGone game, Player player, EnemyManager enemyManager) {
        this.game = game;
        this.player = player;
        this.enemyManager = enemyManager;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets location and size respectively
    }

    /**
     * Helper method that sets the game object and its contents(text, etc.) to visible
     * Pre: The DebugHUD instance has been instantiated
     * Post: isVisible boolean variable is set to true or false
     * @param visible if it is true, this game object will become visible, and vice versa
     */
    public void setDebugHUDVisible(boolean visible) {
        this.isVisible = visible;
        System.out.println("Player opened debug menu");
    }

    /**
     * Draws all the text in the debugHUD
     * Pre: isVisible = true (debugHUD is visible)
     * Post: all the text, etc.
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        calculateFPS();

        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int screenRightEdge = getWidth() - 10;

        g2d.setColor(Color.WHITE); //sets text color

        //location relative to debug hud bounding box location and aligned with right side
        drawRightAlignedString(g2d, "Player xy: " + player.getX() + ", " + player.getY(), screenRightEdge, getHeight()-10, FONT);

        int enemyCount = enemyManager.enemiesList.size();
        drawRightAlignedString(g2d, "Enemy count: " + enemyCount, screenRightEdge, getHeight()-25, FONT);

        drawRightAlignedString(g2d, "FPS: " + fps, screenRightEdge, getHeight()-45, FONT);

        drawRightAlignedString(g2d, "Health: " + player.playerCurrentHealth, screenRightEdge, getHeight()- 65, FONT);

        drawRightAlignedString(g2d, "Player total xp: " + player.totalPlayerXp, screenRightEdge, getHeight()- 85, FONT);

        drawRightAlignedString(g2d, "Player xp level up requirements: " + player.playerXPBarMaxXP, screenRightEdge, getHeight()- 105, FONT);

        drawRightAlignedString(g2d, "Current player xp: " + String.format("%.2f", player.currentPlayerXp), screenRightEdge, getHeight()-125, FONT);

        drawRightAlignedString(g2d, "Current ammo count: " + player.currentAmmo, screenRightEdge, getHeight()- 145, FONT);

        drawRightAlignedString(g2d, "Max ammo: " + player.maxAmmo, screenRightEdge, getHeight()- 165, FONT);

        drawRightAlignedString(g2d, "Enemy spawn rate: Every " + (double)(enemyManager.enemySpawnRate)/1000 + " seconds", screenRightEdge, getHeight() - 185, FONT);
    }

    /**
     * Draws text that is right aligned with the screen
     * Pre: A right aligned string needs to be drawn
     * Post: A right aligned string with specified text
     * @param g the <code>Graphics</code> context in which to paint
     * @param text The text to be displayed
     * @param rightEdgeX The spacing between the edge of the screen and the right edge of the text/string
     * @param y The y coordinates of the string
     * @param font The font of the string
     */
    public void drawRightAlignedString(Graphics g, String text, int rightEdgeX, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);

        int textWidth = metrics.stringWidth(text); //gets the width of the string in pixels
        int x = rightEdgeX - textWidth; //gets the starting point location of the string

        //draws string
        g.setFont(font);
        g.drawString(text, x, y);
    }

    /**
     * FPS calculator that uses a counter to check how many times the paint method in this class is run in one second
     * Pre: The game loop triggers a screen repaint frame tick
     * Post: The FPS count that is displayed in the debugHUD
     */
    private void calculateFPS() {
        frameCount++;

        //check if one second has passed
        if (System.currentTimeMillis() - fpsTimer >= 1000) {
            fps = frameCount; //sets fps for number of frames rendered in this second
            frameCount = 0;   //resets counter
            fpsTimer += 1000; //advances timer starting point
        }
    }

    /**
     * Act method to repaint(refresh what is displayed on screen)
     * Pre: none
     * Post: repainted screen
     */
    @Override
    public void act() {
        if (isVisible) { this.repaint(); } //do not remove, very important
    }
}
