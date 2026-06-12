package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage; //off-screen canvas
import java.awt.image.ConvolveOp; //used to scale up and down images
import java.awt.image.Kernel; //a matrix used for math
import java.io.IOException;

public class DeathMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;
    SaveGame saveGame;
    MainMenu mainMenu;
    EnemyManager enemyManager;

    private BufferedImage blurredSnapshot = null;
    private boolean needsBlurRefresh = false; //generates a new blur asset?

    private final Font FONT = new Font("Consolas", Font.BOLD, 40);
    private boolean isVisible = false;

    //button dimensions variables
    private final int BUTTON_WIDTH = 450;
    private final int BUTTON_HEIGHT = 70;
    private int buttonX;

    private final int EXIT_TO_MAIN_MENU_Y = 700;
    private final int PLAY_AGAIN_Y = 800;
    private final int QUIT_Y = 900;

    private boolean wasExitToMainHovered = false;
    private boolean wasPlayAgainHovered = false;
    private boolean wasQuitHovered = false;

    /**
     * Constructor that initializes the game object and fields
     * Pre: Game is initialized from the main method in PolyGone.java
     * Post: Sets the size of this game object to fill the screen
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     */
    public DeathMenu(PolyGone game, Player player, MainMenu mainMenu, EnemyManager enemyManager) {
        this.player = player;
        this.game = game;
        this.saveGame = new SaveGame();
        this.mainMenu = mainMenu;
        this.enemyManager = enemyManager;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
        this.buttonX = this.getWidth()/2;
    }

    /**
     * Helper method that sets the game object and its contents(buttons, etc.) to visible and blurs the background
     * Pre: Game is initialized from the main method in PolyGone.java
     * Post: isVisible boolean variable is set to true or false, resets mouse inputs to prevent instant clicking/selection, blurred background
     * @param visible if it is true, this game object will become visible, and vice versa
     */
    public void setDeathMenuVisible(boolean visible) {
        this.isVisible = visible;
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
        if (visible) {
            this.needsBlurRefresh = true; //tells game to blur background
        } else {
            //avoids blurring when pause menu not open
            if (blurredSnapshot != null) {
                blurredSnapshot.flush();
                blurredSnapshot = null;
            }
        }
    }

    /**
     * Draws all the buttons and text of the win menu
     * Pre: isVisible = true (main menu is visible)
     * Post: all the drawn buttons, text, etc.
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        //gets to be blurred background
        if (needsBlurRefresh || blurredSnapshot == null) {
            if (this.getParent() != null) {
                blurredSnapshot = generateBlurredImage(this.getParent());
            }
            needsBlurRefresh = false;
        }

        //draws blurred background
        if (blurredSnapshot != null) {
            g2d.drawImage(blurredSnapshot, 0, 0, null);
        } else {
            //fail option
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        drawLoseText(g2d);

        boolean isExitToMainNowHovered = drawButtons(g2d, buttonX, EXIT_TO_MAIN_MENU_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Exit To Main Menu");
        boolean isPlayAgainNowHovered = drawButtons(g2d, buttonX, PLAY_AGAIN_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Play Again");
        boolean isQuitNowHovered = drawButtons(g2d, buttonX, QUIT_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Quit");

        if (isExitToMainNowHovered && !wasExitToMainHovered) MusicSoundEffectsController.playHoverSound();
        if (isPlayAgainNowHovered && !wasPlayAgainHovered) MusicSoundEffectsController.playHoverSound();
        if (isQuitNowHovered && !wasQuitHovered) MusicSoundEffectsController.playHoverSound();

        wasExitToMainHovered = isExitToMainNowHovered;
        wasPlayAgainHovered = isPlayAgainNowHovered;
        wasQuitHovered = isQuitNowHovered;
    }

    /**
     * Draws the win text
     * @param g2d Abstract class passing
     */
    public void drawLoseText(Graphics2D g2d) {
        String text = "YOU DIED";

        g2d.setFont(new Font("OCR A Extended", Font.BOLD, 167));
        g2d.setColor(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);

        int x = (this.getWidth() - textWidth) / 2; //centers text
        int y = this.getHeight()/2 - 100;

        g2d.drawString(text, x, y);
    }

    /**
     * Draws win menu buttons with dynamic graphics if the mouse is hovering
     * @param g2d Abstract class passing
     * @param x X coordinate of the button
     * @param y Y coordinate of the button
     * @param mouseX The player's mouses' current X location
     * @param mouseY The player's mouses' current Y location
     * @param text The specific text that is displayed on the button such as "Play Again" or "Quit"
     */
    public boolean drawButtons(Graphics2D g2d, int x, int y, int mouseX, int mouseY, String text) {
        x = x - BUTTON_WIDTH /2;
        y = y - BUTTON_HEIGHT /2;

        boolean isHovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH && mouseY >= y && mouseY <= y + BUTTON_HEIGHT;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, BUTTON_WIDTH, 2); //top line
        g2d.fillRect(x, y + BUTTON_HEIGHT - 2, BUTTON_WIDTH, 2); //bottom line
        g2d.fillRect(x, y, 2, BUTTON_HEIGHT); //left line
        g2d.fillRect(x + BUTTON_WIDTH - 2, y, 2, BUTTON_HEIGHT); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, BUTTON_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, BUTTON_HEIGHT - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, BUTTON_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, BUTTON_HEIGHT - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
        }
        g2d.fillRect(x + 2, y + BUTTON_HEIGHT - 2 - thickness, BUTTON_WIDTH - 4, thickness);
        g2d.fillRect(x + BUTTON_WIDTH - 2 - thickness, y + 2, thickness, BUTTON_HEIGHT - 4);

        //button text centering and creation
        g2d.setFont(FONT);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(FONT);
        int textX = x + (BUTTON_WIDTH - metrics.stringWidth(text)) / 2;
        int textY = y + ((BUTTON_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent() + 7;
        g2d.drawString(text, textX, textY);

        return isHovered;
    }

    /**
     * Handles button logic of win menu
     * Pre: Win menu is visible
     * Post: An action preformed based on the button/object clicked by the player
     */
    @Override
    public void act() throws IOException {
        if (!isVisible) {
            return;
        }
        //mouse location variables
        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        if (GameMouseInput.isMouseLeftClickPressed) {
            //exit to main menu button
            int emx = buttonX - BUTTON_WIDTH / 2;
            int emy = EXIT_TO_MAIN_MENU_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= emx && mouseX <= emx + BUTTON_WIDTH && mouseY >= emy && mouseY <= emy + BUTTON_HEIGHT) {
                GameMouseInput.reset();
                GameMouseInput.isMouseLeftClickPressed = false;
                saveGame.saveData(game, player, mainMenu, enemyManager);
                game.closeUpgradeMenu();
                game.unpauseGame();
                game.gameReset();
                game.openMainMenu();
                mainMenu.selectedSave = 0;
                mainMenu.saveMenuState = 0;
                MusicSoundEffectsController.playClickSound();
                System.out.println("Exited to main menu");
                return;
            }

            //play again button
            int pax = buttonX - BUTTON_WIDTH / 2;
            int pay = PLAY_AGAIN_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= pax && mouseX <= pax + BUTTON_WIDTH && mouseY >= pay && mouseY <= pay + BUTTON_HEIGHT) {
                game.gameReset();
                MusicSoundEffectsController.playClickSound();
                System.out.println("Player restarted play through");
                return;
            }
            //quit game button
            int qx = buttonX - BUTTON_WIDTH / 2;
            int qy = QUIT_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= qx && mouseX <= qx + BUTTON_WIDTH && mouseY >= qy && mouseY <= qy + BUTTON_HEIGHT) {
                game.exitGame();
                return;
            }
        }

        this.repaint(); //do not remove, very important
    }

    /**
     * Blurs the background of anything behind the open menu by getting a pixel and that pixels surrounding pixels
     * Mixes the colors together and outputs the result to make it look blurry
     * Also down and upscales to save processing power and smooths result to avoid blockiness
     * Pre: Win menu is visible
     * Post: A blurred background
     * @param targetCanvas The canvas that is to be blurred
     * @return The blurred canvas
     */
    private BufferedImage generateBlurredImage(Component targetCanvas) {
        int w = targetCanvas.getWidth();
        int h = targetCanvas.getHeight();

        if (w <= 0 || h <= 0) return null;

        //gets an off-screen canvas of game objects and everything behind the pause menu to blur
        BufferedImage rawSource = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D containerGraphics = rawSource.createGraphics();

        //hides pause menu overlay to prevent blurring of pause menu
        boolean oldVisibility = this.isVisible;
        this.isVisible = false;

        //draws the parent frame container components to the target texture
        targetCanvas.paint(containerGraphics);

        this.isVisible = oldVisibility; //unhides pause menu overlay
        containerGraphics.dispose();

        //downscaling of canvas/snapshot of screen for optimization, reduced number of calculations by scale factor^2
        int scaleFactor = 2; //decrease for less blur, increase for more blur
        int targetW = Math.max(1, w / scaleFactor);
        int targetH = Math.max(1, h / scaleFactor);

        BufferedImage downscaledImage = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D downScaleG = downscaledImage.createGraphics();
        //blends pixel colors together when down scaling using math to avoid blockiness (bilinear interpolation)
        downScaleG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        downScaleG.drawImage(rawSource, 0, 0, targetW, targetH, null);
        downScaleG.dispose();
        rawSource.flush(); //clears memory of old not down scaled canvas

        //3x3 Box Blur Convolution Matrix Kernel, think of it as a computation that has to be applied to all pixels
        //basically it takes a pixel and gets 1/9th of the color of each pixel surrounding it in a 3x3 square and blends the colors together
        //the bigger the box/square, the more blurred it gets
        float[] blurMatrix = {
                1f/9f, 1f/9f, 1f/9f,
                1f/9f, 1f/9f, 1f/9f,
                1f/9f, 1f/9f, 1f/9f
        };
        Kernel blurKernel = new Kernel(3, 3, blurMatrix);

        //instructions for computation of blurring of all pixels
        //EDGE_NO_OP is to prevent it from reading black pixels off the screen when it is computing for edge screen case pixels
        ConvolveOp filterOperation = new ConvolveOp(blurKernel, ConvolveOp.EDGE_NO_OP, null);

        //runs computation of blurring of all pixels (convolution processing)
        //.filter starts the computation
        BufferedImage blurredOutput = filterOperation.filter(downscaledImage, null);
        downscaledImage.flush(); //.flush clears memory of old not down scaled canvas similar to above

        //up scales blurred canvas to screen resolution (using bilinear interpolation)
        //bilinear interpolation is using smooth blending when up scaling to avoid blockiness
        BufferedImage finalUpscaledBlur = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D finalUpscaleG = finalUpscaledBlur.createGraphics();
        finalUpscaleG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        finalUpscaleG.drawImage(blurredOutput, 0, 0, w, h, null);

        //tints blurred background
        finalUpscaleG.setColor(new Color(0, 0, 0, 80)); //black with 80/255 transparency
        finalUpscaleG.fillRect(0, 0, w, h);

        //cleans up memory usage of variables and objects used in the blurring process
        finalUpscaleG.dispose();
        blurredOutput.flush();

        return finalUpscaledBlur;
    }
}