package Project;

import Framework.GameObject;
import java.awt.*;

public class GUI extends GameObject {

    Player player; //reference to object

    private Font font = new Font("Arial", Font.BOLD, 30);

    public GUI(PolyGone game, Player player) {
        this.player = player;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);



        if (player != null) {
            drawHealthBar(g2d, 30, 30, player.playerCurrentHealth, player.playerMaxHealth); //draws health bar
        }
    }

    public void drawHealthBar(Graphics2D g2d, int x, int y, int currentHealth, int maxHealth) {
        //health bar dimensions
        int barWidth = 200;
        int barHeight = 25;

        //calculates the width of the health bar
        double healthPercentage = (double) currentHealth / maxHealth; //casts to double to avoid rounding to 0
        healthPercentage = Math.max(0.0, Math.min(1.0, healthPercentage)); //makes sure the value is between 0 and 1
        int fillWidth = (int) (barWidth * healthPercentage);

        //draws black border rectangle
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(x, y, barWidth, barHeight);

        //draws background health bar
        g2d.setColor(new Color(60, 20, 20)); //dark crimson color
        g2d.fillRect(x + 3, y + 3, barWidth - 6, barHeight - 6);

        //draws health bar
        if (fillWidth > 6) { //only draws if the thickness is valid
            g2d.setColor(new Color(200, 35, 45)); //dark red shadow bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 6);

            g2d.setColor(new Color(245, 50, 60)); //bright red health bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 10);
        }
    }

    @Override
    public void act() {
        this.repaint(); //do not remove, very important
    }
}
