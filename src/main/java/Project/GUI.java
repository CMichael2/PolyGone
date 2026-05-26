package Project;

import Framework.GameObject;
import java.awt.*;

public class GUI extends GameObject {

    private int lives;

    private Font font = new Font("Arial", Font.PLAIN, 30);

    public GUI() {
        setSize(100, 100);
        setX(0);
        setY(0);
        setColor(Color.BLACK);
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("lives " + lives, 300, 300);
        System.out.println("Updated health");
    }

    @Override
    public void act() {

    }

    public void updatePlayerHealthGUI(int playerCurrentHealth) {
        lives = playerCurrentHealth;
    }
}
