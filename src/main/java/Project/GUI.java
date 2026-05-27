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

        //draws text
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);
        g2d.drawString("Lives: " + player.playerCurrentHealth, 0, 35);
    }

    @Override
    public void act() {
        this.repaint(); //do not remove, very important
    }
}
