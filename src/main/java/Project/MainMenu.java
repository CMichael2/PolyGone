package Project;

import Framework.GameObject;
import java.awt.*;

public class MainMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;

    private Font font = new Font("Consolas", Font.BOLD, 30);
    private boolean isVisible = false;
    private int saveMenuState = 0;

    private final int buttonWidth = 350;
    private final int buttonHeight = 50;
    private final int buttonX;

    private final int saveFrameWidth = 300;
    private final int saveFrameHeight = 600;
    private final int saveFrameY = 400;
    private final int saveButtonWidth = 300;
    private int selectedSave = 0;

    private double animationSaveY = 1200;
    private final double animationSpeed = 0.1; //speed of animation, decrease to slow

    private final int newGameY = 505;
    private final int continueY = 570;
    private final int settingsY = 635;
    private final int creditsY = 700;
    private final int quitY = 765;

    public MainMenu(PolyGone game, Player player) {
        this.player = player;
        this.game = game;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
        buttonX = this.getWidth()/6;
    }

    public void setMainMenuVisible(boolean visible) {
        this.isVisible = visible;
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        //main menu buttons
        drawNewGameButton(g2d, buttonX, newGameY, GameMouseInput.mouseX, GameMouseInput.mouseY);
        drawContinueFromSaveButton(g2d, buttonX, continueY, GameMouseInput.mouseX, GameMouseInput.mouseY);
        drawSettingsButton(g2d, buttonX, settingsY, GameMouseInput.mouseX, GameMouseInput.mouseY);
        drawCreditsButton(g2d, buttonX, creditsY, GameMouseInput.mouseX, GameMouseInput.mouseY);
        drawQuitButton(g2d, buttonX, quitY, GameMouseInput.mouseX, GameMouseInput.mouseY);

        if (saveMenuState != 0) {
            int currentY = (int) animationSaveY;

            drawSaveFrame1(g2d, 650, currentY, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSaveFrame2(g2d, 1000, currentY, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSaveFrame3(g2d, 1350, currentY, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSavePlayButton(g2d, 750, currentY+350, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSaveDeleteButton(g2d, 1200, currentY+350, GameMouseInput.mouseX, GameMouseInput.mouseY);
        }
    }

    public void drawQuitButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "Quit";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSettingsButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "Settings";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawCreditsButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "Credits";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawNewGameButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "New Game";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawContinueFromSaveButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "Continue From Save";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSaveFrame1(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - saveFrameWidth/2;
        y = y - saveFrameHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + saveFrameWidth && mouseY >= y && mouseY <= y + saveFrameHeight;

        //inner button
        if (isHovered || selectedSave == 1) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, saveFrameWidth, saveFrameHeight);

        //outer border
        if (isHovered || selectedSave == 1) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, saveFrameWidth, 2); //top line
        g2d.fillRect(x, y + saveFrameHeight - 2, saveFrameWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, saveFrameHeight); //left line
        g2d.fillRect(x + saveFrameWidth - 2, y, 2, saveFrameHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered || selectedSave == 1) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        }

        //button text centering and creation
        String text = "Placeholder";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (saveFrameWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((saveFrameHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSaveFrame2(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - saveFrameWidth/2;
        y = y - saveFrameHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + saveFrameWidth && mouseY >= y && mouseY <= y + saveFrameHeight;

        //inner button
        if (isHovered || selectedSave == 2) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, saveFrameWidth, saveFrameHeight);

        //outer border
        if (isHovered || selectedSave == 2) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, saveFrameWidth, 2); //top line
        g2d.fillRect(x, y + saveFrameHeight - 2, saveFrameWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, saveFrameHeight); //left line
        g2d.fillRect(x + saveFrameWidth - 2, y, 2, saveFrameHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered || selectedSave == 2) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        }

        //button text centering and creation
        String text = "Placeholder";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (saveFrameWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((saveFrameHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSaveFrame3(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - saveFrameWidth/2;
        y = y - saveFrameHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + saveFrameWidth && mouseY >= y && mouseY <= y + saveFrameHeight;

        //inner button
        if (isHovered || selectedSave == 3) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, saveFrameWidth, saveFrameHeight);

        //outer border
        if (isHovered || selectedSave == 3) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, saveFrameWidth, 2); //top line
        g2d.fillRect(x, y + saveFrameHeight - 2, saveFrameWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, saveFrameHeight); //left line
        g2d.fillRect(x + saveFrameWidth - 2, y, 2, saveFrameHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered || selectedSave == 3) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, saveFrameHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + saveFrameHeight - 2 - thickness, saveFrameWidth - 4, thickness);
            g2d.fillRect(x + saveFrameWidth - 2 - thickness, y + 2, thickness, saveFrameHeight - 4);
        }

        //button text centering and creation
        String text = "Placeholder";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (saveFrameWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((saveFrameHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSavePlayButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - saveButtonWidth/2;
        y = y - buttonHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + saveButtonWidth && mouseY >= y && mouseY <= y + buttonHeight;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, saveButtonWidth, buttonHeight);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, saveButtonWidth, 2); //top line
        g2d.fillRect(x, y + buttonHeight - 2, saveButtonWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, buttonHeight); //left line
        g2d.fillRect(x + saveButtonWidth - 2, y, 2, buttonHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + saveButtonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + saveButtonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        }

        //button text centering and creation
        String text = "Play";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (saveButtonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    public void drawSaveDeleteButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
        x = x - saveButtonWidth/2;
        y = y - buttonHeight/2;

        boolean isHovered = mouseX >= x && mouseX <= x + saveButtonWidth && mouseY >= y && mouseY <= y + buttonHeight;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, saveButtonWidth, buttonHeight);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, saveButtonWidth, 2); //top line
        g2d.fillRect(x, y + buttonHeight - 2, saveButtonWidth, 2); //bottom line
        g2d.fillRect(x, y, 2, buttonHeight); //left line
        g2d.fillRect(x + saveButtonWidth - 2, y, 2, buttonHeight); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + saveButtonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, buttonHeight - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(x + 2, y + buttonHeight - 2 - thickness, saveButtonWidth - 4, thickness);
            g2d.fillRect(x + saveButtonWidth - 2 - thickness, y + 2, thickness, buttonHeight - 4);
        }

        //button text centering and creation
        String text = "Delete";
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (saveButtonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);
    }

    @Override
    public void act() {
        if (!isVisible) return;

        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        if (GameMouseInput.isMouseLeftClickPressed) {
            int nx = buttonX - buttonWidth / 2;
            int ny = newGameY - buttonHeight / 2;
            if (mouseX >= nx && mouseX <= nx + buttonWidth && mouseY >= ny && mouseY <= ny + buttonHeight) {
                game.closeMainMenu();
                System.out.println("Player began new playthrough");
                return;
            }

            int cx = buttonX - buttonWidth / 2;
            int cy = continueY - buttonHeight / 2;
            if (mouseX >= cx && mouseX <= cx + buttonWidth && mouseY >= cy && mouseY <= cy + buttonHeight) {
                if (saveMenuState == 0) {
                    saveMenuState = 1; //slides up
                    animationSaveY = this.getHeight() + 350;
                } else if (saveMenuState == 2) {
                    saveMenuState = 3; //slides down
                }

                GameMouseInput.isMouseLeftClickPressed = false;
                GameMouseInput.reset();
                System.out.println("Player has opened save selection screen");
                return;
            }

            int sx = buttonX - buttonWidth / 2;
            int sy = settingsY - buttonHeight / 2;
            if (mouseX >= sx && mouseX <= sx + buttonWidth && mouseY >= sy && mouseY <= sy + buttonHeight) {
                System.out.println("Player has opened settings menu");
                return;
            }

            int crx = buttonX - buttonWidth / 2;
            int cry = creditsY - buttonHeight / 2;
            if (mouseX >= crx && mouseX <= crx + buttonWidth && mouseY >= cry && mouseY <= cry + buttonHeight) {
                System.out.println("Player has opened credits screen");
                return;
            }

            int ex = buttonX - buttonWidth / 2;
            int ey = quitY - buttonHeight / 2;
            if (mouseX >= ex && mouseX <= ex + buttonWidth && mouseY >= ey && mouseY <= ey + buttonHeight) {
                game.exitGame();
                return;
            }

            int s1x = 650 - saveFrameWidth / 2;
            int s1y = saveFrameY - saveFrameHeight / 2;
            if (mouseX >= s1x && mouseX <= s1x + saveFrameWidth && mouseY >= s1y && mouseY <= s1y + saveFrameHeight) {
                if (selectedSave == 1) {
                    selectedSave = 0;
                } else {
                    selectedSave = 1;
                }
                GameMouseInput.isMouseLeftClickPressed = false;
                GameMouseInput.reset();
                System.out.println("Save 1 selected");
                return;
            }

            int s2x = 1000 - saveFrameWidth / 2;
            int s2y = saveFrameY - saveFrameHeight / 2;
            if (mouseX >= s2x && mouseX <= s2x + saveFrameWidth && mouseY >= s2y && mouseY <= s2y + saveFrameHeight) {
                if (selectedSave == 2) {
                    selectedSave = 0;
                } else {
                    selectedSave = 2;
                }
                GameMouseInput.isMouseLeftClickPressed = false;
                GameMouseInput.reset();
                System.out.println("Save 2 selected");
                return;
            }

            int s3x = 1350 - saveFrameWidth / 2;
            int s3y = saveFrameY - saveFrameHeight / 2;
            if (mouseX >= s3x && mouseX <= s3x + saveFrameWidth && mouseY >= s3y && mouseY <= s3y + saveFrameHeight) {
                if (selectedSave == 3) {
                    selectedSave = 0;
                } else {
                    selectedSave = 3;
                }
                GameMouseInput.isMouseLeftClickPressed = false;
                GameMouseInput.reset();
                System.out.println("Save 3 selected");
                return;
            }
        }

        if (saveMenuState == 1) { //slide up
            animationSaveY -= (animationSaveY - saveFrameY) * animationSpeed; //moves save screen
            if (animationSaveY - saveFrameY < 1) { //snapping
                animationSaveY = saveFrameY;
                saveMenuState = 2; //state 2 means visible
            }
        }
        else if (saveMenuState == 3) { //slide down
            double screenBottom = this.getHeight() + 350;
            animationSaveY += (screenBottom - animationSaveY) * animationSpeed + 1; //moves save screen
            if (animationSaveY >= screenBottom) { //snapping
                animationSaveY = screenBottom;
                saveMenuState = 0; //state 0 means hidden
            }
        }

        this.repaint(); //do not remove, very important
    }
}