package Project;

import Framework.GameObject;
import java.awt.*;

public class ExplosionEffect extends GameObject {

    private final PolyGone game;
    private double currentRadius = 0;
    private final double maxRadius = 150.0; //set to the same as the explosion radius
    private final double expansionSpeed = 500.0; //in pixels per second

    private double exactX;
    private double exactY;
    private float opacity = 0.6f;
    private Color color;

    public ExplosionEffect(PolyGone game, int centerX, int centerY) {
        this.game = game;

        //centers the explosion where the bullet hit
        this.exactX = centerX;
        this.exactY = centerY;

        this.setSize(0, 0);
        this.setLocation(centerX, centerY);

        this.color = new Color(255, 150, 0, (int)(opacity * 255)); //orange-yellow tint
        this.setColor(color);
    }

    @Override
    public void act() {
        currentRadius += expansionSpeed * PolyGone.deltaTime;

        opacity = 0.6f * (float)(1.0 - (currentRadius / maxRadius)); //fade as increases

        if (opacity < 0.0f) opacity = 0.0f; //prevent out of bounds for color class errors
        if (opacity > 1.0f) opacity = 1.0f;

        int diameter = (int) Math.round(currentRadius * 2);
        this.setSize(diameter, diameter);

        //moves outwards from initial position
        this.exactX -= expansionSpeed * PolyGone.deltaTime;
        this.exactY -= expansionSpeed * PolyGone.deltaTime;
        this.setX((int) Math.round(exactX));
        this.setY((int) Math.round(exactY));

        this.color = new Color(255, 100, 0, (int)(opacity * 255));
        this.setColor(color);

        if (currentRadius >= maxRadius) {
            game.remove(this);
        }
    }
    @Override
    public void paint(Graphics g) {
        g.setColor(this.color);
        g.fillOval(0, 0, this.getWidth(), this.getHeight());
    }
}