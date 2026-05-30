package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GUI extends GameObject {

    Player player; //reference to object

    private Font font = new Font("OCR A Extended", Font.BOLD, 30);

    private BufferedImage heartImage;

    public GUI(PolyGone game, Player player) {
        this.player = player;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location

        //loads heart image with a try and catch to avoid crashing
        try {
            heartImage = ImageIO.read(new File("Assets/heart.png"));
        } catch (Exception e) {
            System.out.println("Error: Could not load GUI image file. Check file path.");
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (player != null) {
            drawXPBar(g2d, 25, 20, player.currentPlayerXp, player.playerXPBarMaxXP); //draws xp bar

            String playerLevel = "Level " + player.playerLevel;

            g2d.setFont(font);
            g2d.setColor(Color.WHITE);

            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(playerLevel);

            int x = (this.getWidth() - textWidth) / 2;
            int y = 48;

            g2d.drawString(playerLevel, x, y);

            drawHealthBar(g2d, 57, 70, player.playerCurrentHealth, player.playerMaxHealth); //draws health bar
            if (heartImage != null) {
                g2d.drawImage(heartImage, 28, 64, 40, 40, null); //adds heart sprite
            }

            drawAmmoBar(g2d, 57, this.getHeight() - 60, player.maxAmmo, player.currentAmmo);

            drawAmmoCooldownBar(g2d, 57, this.getHeight() - 30, player.lastShotTime, player.shotCooldown);

            int diameter = 40;
            drawAmmoRegenCircle(g2d, 28, this.getHeight()-70, player.lastAmmoRegenTime, player.ammoRegenCooldown, diameter);
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

    public void drawXPBar(Graphics2D g2d, int x, int y, int currentPlayerXP, int playerXPBarMaxXP) {
        //xp bar dimensions
        int barWidth = this.getWidth() - (2 * x);
        int barHeight = 35; //thickness of bar

        //calculates the width of the xp bar
        double xpPercentage = (double) currentPlayerXP / playerXPBarMaxXP; //casts to double to avoid rounding to 0
        xpPercentage = Math.max(0.0, Math.min(1.0, xpPercentage)); //makes sure the value is between 0 and 1
        int fillWidth = (int) (barWidth * xpPercentage);

        //draws black border rectangle
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(x, y, barWidth, barHeight);

        //draws background xp bar
        g2d.setColor(new Color(15, 55, 60)); //dark cyan color
        g2d.fillRect(x + 3, y + 3, barWidth - 6, barHeight - 6);

        //draws xp bar
        if (fillWidth > 6) { //only draws if the thickness is valid
            g2d.setColor(new Color(0, 170, 185)); //dark cyan shadow bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 6);

            g2d.setColor(new Color(0, 225, 235)); //bright cyan xp bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 10);
        }
    }

    public void drawAmmoBar(Graphics2D g2d, int x, int y, int maxAmmo, int currentAmmo) {
        //ammo bar dimensions
        int barWidth = 200;
        int barHeight = 25; //thickness of bar

        //calculates the width of the ammo bar
        double ammoBarPercentage = (double) currentAmmo / maxAmmo; //casts to double to avoid rounding to 0
        ammoBarPercentage = Math.max(0.0, Math.min(1.0, ammoBarPercentage)); //makes sure the value is between 0 and 1
        int fillWidth = (int) (barWidth * ammoBarPercentage);

        //draws black border rectangle
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(x, y, barWidth, barHeight);

        //draws background ammo bar
        g2d.setColor(new Color(60, 50, 15)); //dark yellow color
        g2d.fillRect(x + 3, y + 3, barWidth - 6, barHeight - 6);

        //draws ammo bar
        if (fillWidth > 6) { //only draws if the thickness is valid
            g2d.setColor(new Color(190, 160, 0)); //dark yellow shadow bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 6);

            g2d.setColor(new Color(245, 215, 0)); //bright yellow ammo bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 10);
        }
    }

    public void drawAmmoCooldownBar(Graphics2D g2d, int x, int y, long lastShotTime, long shotCooldown) {
        //ammo bar dimensions
        int barWidth = 200;
        int barHeight = 10; //thickness of bar
        double ammoCooldownBarPercentage;

        long timeElapsed = System.currentTimeMillis() - lastShotTime;

        //calculate percentage of completion
        double progress = (double) timeElapsed / shotCooldown;
        ammoCooldownBarPercentage = Math.max(0.0, Math.min(1.0, progress)); //makes sure value is between 0 and 1
        int fillWidth = (int) (barWidth * ammoCooldownBarPercentage);

        //draws black border rectangle
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(x, y, barWidth, barHeight);

        //draws background ammo bar
        g2d.setColor(new Color(60, 50, 15)); //dark yellow color
        g2d.fillRect(x + 3, y + 3, barWidth - 6, barHeight - 6);

        //draws ammo bar
        if (fillWidth > 6) { //only draws if the thickness is valid
            g2d.setColor(new Color(190, 160, 0)); //dark yellow shadow bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, barHeight - 6);

            g2d.setColor(new Color(245, 215, 0)); //bright yellow ammo bar
            g2d.fillRect(x + 3, y + 3, fillWidth - 6, 0);
        }
    }

    public void drawAmmoRegenCircle(Graphics2D g2d, int x, int y, long lastAmmoRegenTime, long ammoRegenCooldown, int diameter) {
        if (player.currentAmmo == player.maxAmmo) {
            return;
        }

        double ammoRegenBarPercentage;

        long timeElapsed = System.currentTimeMillis() - lastAmmoRegenTime;

        //calculate percentage of completion
        double progress = (double) timeElapsed / ammoRegenCooldown;
        ammoRegenBarPercentage = Math.max(0.0, Math.min(1.0, progress)); //makes sure value is between 0 and 1

        if (ammoRegenBarPercentage >= 1.0) {
            return;
        }

        int arcAngle = (int) (360 * ammoRegenBarPercentage);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //draws black border
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillOval(x, y, diameter, diameter);

        //draws background ammo reload bar
        g2d.setColor(new Color(60, 50, 15)); //dark yellow color
        g2d.fillOval(x + 3, y + 3, diameter - 6, diameter - 6);

        //draws ammo reload bar
        if (arcAngle > 6) { //only draws if the thickness is valid
            g2d.setColor(new Color(245, 215, 0)); //bright yellow
            g2d.fillArc(x + 3, y + 3, diameter - 6, diameter - 6, 90, -arcAngle);
        }
    }

    @Override
    public void act() {
        this.repaint(); //do not remove, very important
    }
}
