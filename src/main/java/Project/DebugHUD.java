package Project;

import Framework.GameObject;
import java.awt.*;
import java.util.ArrayList;

public class DebugHUD extends GameObject {

    Player player; //reference to object
    PolyGone game;

    private long lastTime = System.nanoTime();
    private double fps = 0.0; //do not change values
    private int frameCount = 0;
    private long fpsTimer = System.currentTimeMillis();

    private Font font = new Font("Arial", Font.BOLD, 30);
    private boolean isVisible = false;

    public DebugHUD(PolyGone game, Player player) {
        this.game = game;
        this.player = player;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets location and size respectively
    }

    public void setDebugHUDVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void paint(Graphics g) {
        calculateFPS();

        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int screenRightEdge = getWidth() - 10;

        //draws text
        g2d.setColor(Color.WHITE);
        drawRightAlignedString(g2d, "Health: " + player.playerCurrentHealth, screenRightEdge, getHeight()- 100, font);

        //location relative to debug hud bounding box location and aligned with right side
        drawRightAlignedString(g2d, "Player xy: " + player.getX() + ", " + player.getY(), screenRightEdge, getHeight()-10, font);

        int enemyCount = game.getEnemyCount();
        drawRightAlignedString(g2d, "Enemy count: " + enemyCount, screenRightEdge, getHeight()-40, font);

        drawRightAlignedString(g2d, "FPS: " + fps, screenRightEdge, getHeight()-70, font);
    }

    public void drawRightAlignedString(Graphics g, String text, int rightEdgeX, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);

        int textWidth = metrics.stringWidth(text); //gets the width of the string in pixels
        int x = rightEdgeX - textWidth; //finds the starting point location of the string

        //draws string
        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void calculateFPS() {
        frameCount++;

        //check if one second has passed
        if (System.currentTimeMillis() - fpsTimer >= 1000) {
            fps = frameCount; //sets fps for number of frames rendered in this second
            frameCount = 0;   //resets counter
            fpsTimer += 1000; //advances timer starting point
        }
    }

    @Override
    public void act() {
        if (isVisible) { this.repaint(); } //do not remove, very important
    }
}
