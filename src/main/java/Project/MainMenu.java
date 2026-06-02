package Project;

import Framework.GameObject;
import java.awt.*;

public class MainMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;

    private Font font = new Font("Consolas", Font.BOLD, 30);
    private boolean isVisible = false;

    private final int buttonWidth = 350;
    private final int buttonHeight = 50;

    public MainMenu(PolyGone game, Player player) {
        this.player = player;
        this.game = game;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
    }

    public void setMainMenuVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawExitButton(g2d, this.getWidth()/2, 700, GameMouseInput.mouseX, GameMouseInput.mouseY);

        drawResumeButton(g2d, this.getWidth()/2, 500, GameMouseInput.mouseX, GameMouseInput.mouseY);
    }

    public void drawExitButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - buttonWidth/2;
        y = y - buttonHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, buttonWidth, buttonHeight);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, buttonWidth, 2); //top line
        g2d.fillRect(x, y + buttonHeight - 2, buttonWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, buttonHeight); //left line
        g2d.fillRect(x + buttonWidth - 2, y, 2, buttonHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, buttonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, buttonWidth - 4, thickness);
            g2d.fillRect(x + buttonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, buttonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, buttonWidth - 4, thickness);
            g2d.fillRect(x + buttonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        }

        //button text centering and creation
        String text = "Exit";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawResumeButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - buttonWidth/2;
        y = y - buttonHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, buttonWidth, buttonHeight);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, buttonWidth, 2); //top line
        g2d.fillRect(x, y + buttonHeight - 2, buttonWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, buttonHeight); //left line
        g2d.fillRect(x + buttonWidth - 2, y, 2, buttonHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, buttonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, buttonWidth - 4, thickness);
            g2d.fillRect(x + buttonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, buttonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, buttonWidth - 4, thickness);
            g2d.fillRect(x + buttonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        }

        //button text centering and creation
        String text = "Start Game";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    @Override
    public void act() {
        if (!isVisible) return;
        int midX = this.getWidth() / 2;
        int resumeY = 500;
        int exitY = 700;

        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        if (GameMouseInput.isMouseLeftClickPressed) {
            int rx = midX - buttonWidth / 2;
            int ry = resumeY - buttonHeight / 2;
            if (mouseX >= rx && mouseX <= rx + buttonWidth && mouseY >= ry && mouseY <= ry + buttonHeight) {
                game.closeMainMenu();
                System.out.println("Player began new playthrough");
                return;
            }

            int ex = midX - buttonWidth / 2;
            int ey = exitY - buttonHeight / 2;
            if (mouseX >= ex && mouseX <= ex + buttonWidth && mouseY >= ey && mouseY <= ey + buttonHeight) {
                game.exitGame();
                return;
            }
        }
        this.repaint(); //do not remove, very important
    }
}