package Project;

import Framework.GameObject;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;

    private final Font FONT = new Font("Consolas", Font.BOLD, 40);

    private boolean isVisible = false; //is this game object visible?

    //main menu button variables/class fields
    private final int BUTTON_WIDTH = 450;
    private final int BUTTON_HEIGHT = 70;
    private int buttonX; //should not be constant because its value is set in a constructor, not here

    //main menu button y coordinates variables
    private final int NEW_GAME_Y = 660;
    private final int CONTINUE_Y = 745;
    private final int SETTINGS_Y = 830;
    private final int CREDITS_Y = 915;
    private final int QUIT_Y = 1000;

    //save buttons/frames variables/class fields
    //many are constants because they do not need to be updated
    private final int SAVE_FRAME_WIDTH = 375;
    private final int SAVE_FRAME_HEIGHT = 800;
    private final int SAVE_FRAME_X = 800;
    private final int SAVE_FRAME_Y = 500; //default y coordinates for the save slots/frames
    private final int SAVE_BUTTON_WIDTH = 350;
    private final int SAVE_PLAY_BUTTON_X = 1000;
    private final int SAVE_DELETE_BUTTON_X = 1450;
    private final int SAVE_BUTTON_Y = 500;
    public int selectedSave = 0;
    public int unfilledSaveSlot = 0; //used to check where a game can be saved to if possible
    private String[][] slotLines = {
            {"Empty Slot", "", "", "", "", "", "", "", "", "", "", ""},
            {"Empty Slot", "", "", "", "", "", "", "", "", "", "", ""},
            {"Empty Slot", "", "", "", "", "", "", "", "", "", "", ""}
    };

    private final int CREDITS_X = 1300;
    private final int CREDITS_WIDTH = 1000;
    private final int CREDITS_HEIGHT = 1000;

    //animation variables
    public int saveMenuState = 0; //0 is closed, 1 is opening, 2 is open, 3 is closing
    private double animationSaveY = 1200; //starting position of the save screen animation
    private final double ANIMATION_SPEED = 0.1; //speed of save screen animation, decrease to slow down animation
    public int mainMenuState = 1; //0 is closed, 1 is opening, 2 is open, 3 is closing
    private double currentButtonX;
    private final int TARGET_OPEN_X; //default position of main menu buttons
    private final int OFF_SCREEN_LEFT_X; //animation starting position
    private float bgAlpha = 1.0f; //1 is visible, 0 is hidden
    private final float FADE_SPEED = 0.05f;
    public int creditsMenuState = 0; //0 is closed, 1 is opening, 2 is open, 3 is closing
    private final int CREDITS_TARGET_Y;
    private int creditsCurrentY;
    private double animationCreditY;

    private boolean wasNewGameHovered = false;
    private boolean wasContinueHovered = false;
    private boolean wasSettingsHovered = false;
    private boolean wasCreditsHovered = false;
    private boolean wasQuitHovered = false;
    private boolean wasPlayHovered = false;
    private boolean wasDeleteHovered = false;

    /**
     * Constructor that initializes the game object and fields
     * Pre: Game is initialized from the main method in PolyGone.java
     * Post: Sets the size of this game object to fill the screen, sets the X alignment of the main menu buttons, defines animation variables
     * @param game Parameter from PolyGone
     * @param player Parameter from Player
     */
    public MainMenu(PolyGone game, Player player) {
        this.player = player;
        this.game = game;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
        buttonX = this.getWidth()/6;

        //defines the values of the animation positions
        TARGET_OPEN_X = this.getWidth() / 6;
        OFF_SCREEN_LEFT_X = -(BUTTON_WIDTH / 2) - 50;
        currentButtonX = OFF_SCREEN_LEFT_X;
        CREDITS_TARGET_Y = 550;
    }

    /**
     * Helper method that sets the game object and its contents(buttons, etc.) to visible
     * Pre: Game is initialized from the main method in PolyGone.java
     * Post: isVisible boolean variable is set to true or false, resets mouse inputs to prevent instant clicking/selection
     * @param visible if it is true, this game object will become visible, and vice versa
     */
    public void setMainMenuVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            this.mainMenuState = 1; //begins animation
            this.currentButtonX = OFF_SCREEN_LEFT_X;
            this.bgAlpha = 1.0f; //resets transparency
        } else {
            this.mainMenuState = 0;
        }
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
    }

    /**
     * Draws all the buttons and sub menus/screens of the main menu
     * Pre: isVisible = true (main menu is visible)
     * Post: all the drawn buttons, etc.
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Composite originalComposite = g2d.getComposite(); //sets original composite
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgAlpha)); //updates composite for fading background

        //background color and size
        g2d.setColor(new Color(27, 26, 26));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2d.setComposite(originalComposite); //resets composite so buttons are not faded

        //main menu buttons
        boolean isNewGameNowHovered = drawMainMenuButtons(g2d, (int)currentButtonX, NEW_GAME_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "New Game");
        boolean isContinueNowHovered = drawMainMenuButtons(g2d, (int)currentButtonX, CONTINUE_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Resume From Save");
        boolean isSettingsNowHovered = drawMainMenuButtons(g2d, (int)currentButtonX, SETTINGS_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Settings");
        boolean isCreditsNowHovered = drawMainMenuButtons(g2d, (int)currentButtonX, CREDITS_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Credits");
        boolean isQuitNowHovered = drawMainMenuButtons(g2d, (int)currentButtonX, QUIT_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Quit");

        if (isNewGameNowHovered && !wasNewGameHovered) playHoverSound();
        if (isContinueNowHovered && !wasContinueHovered) playHoverSound();
        if (isSettingsNowHovered && !wasSettingsHovered) playHoverSound();
        if (isCreditsNowHovered && !wasCreditsHovered) playHoverSound();
        if (isQuitNowHovered && !wasQuitHovered) playHoverSound();

        wasNewGameHovered = isNewGameNowHovered;
        wasContinueHovered = isContinueNowHovered;
        wasSettingsHovered = isSettingsNowHovered;
        wasCreditsHovered = isCreditsNowHovered;
        wasQuitHovered = isQuitNowHovered;

        //only appears once continue from save button is clicked
        if (saveMenuState != 0) {
            int currentY = (int) animationSaveY; //constantly updates the buttons/frames y position so it looks animated

            drawSaveFrame(g2d, SAVE_FRAME_X, currentY, slotLines[0], 1, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSaveFrame(g2d, SAVE_FRAME_X + 425, currentY, slotLines[1], 2, GameMouseInput.mouseX, GameMouseInput.mouseY);
            drawSaveFrame(g2d, SAVE_FRAME_X + 850, currentY, slotLines[2], 3, GameMouseInput.mouseX, GameMouseInput.mouseY);

            boolean isPlayNowHovered = drawSaveScreenButtons(g2d, SAVE_PLAY_BUTTON_X, currentY + SAVE_BUTTON_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Play");
            boolean isDeleteNowHovered = drawSaveScreenButtons(g2d, SAVE_DELETE_BUTTON_X, currentY + SAVE_BUTTON_Y, GameMouseInput.mouseX, GameMouseInput.mouseY, "Delete");

            if (isPlayNowHovered && !wasPlayHovered) playHoverSound();
            if (isDeleteNowHovered && !wasDeleteHovered) playHoverSound();

            wasPlayHovered = isPlayNowHovered;
            wasDeleteHovered = isDeleteNowHovered;
        }

        if (creditsMenuState != 0) {
            int creditsCurrentY = (int) animationCreditY;

            drawCreditBox(g2d, CREDITS_X, creditsCurrentY);
        }
    }

    /**
     * Draws main menu buttons with dynamic graphics if the mouse is hovering
     * @param g2d Abstract class passing
     * @param x X coordinate of the button
     * @param y Y coordinate of the button
     * @param mouseX The player's mouses' current X location
     * @param mouseY The player's mouses' current Y location
     * @param text The specific text that is displayed on the button such as "New Game" or "Settings"
     */
    public boolean drawMainMenuButtons(Graphics2D g2d, int x, int y, int mouseX, int mouseY, String text) {
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
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
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
     * Draws save frames/slots with dynamic graphics if the mouse is hovering or clicked
     * @param g2d Abstract class passing
     * @param x X coordinate of the frame
     * @param y Y coordinate of the frame
     * @param lines The text to be displayed. In this case it is a list of the save details
     * @param slotID Which slot it is (1-3), used to determine if the header should say slot 1, slot 2, etc.
     * @param mouseX The player's mouses' current X location
     * @param mouseY The player's mouses' current Y location
     */
    public void drawSaveFrame(Graphics2D g2d, int x, int y, String[] lines, int slotID, int mouseX, int mouseY) {
        x = x - SAVE_FRAME_WIDTH / 2;
        y = y - SAVE_FRAME_HEIGHT / 2;

        boolean isHovered = mouseX >= x && mouseX <= x + SAVE_FRAME_WIDTH && mouseY >= y && mouseY <= y + SAVE_FRAME_HEIGHT;

        //background
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139));
        } else {
            g2d.setColor(new Color(148, 148, 148));
        }
        g2d.fillRect(x, y, SAVE_FRAME_WIDTH, SAVE_FRAME_HEIGHT);

        //outer borders
        if (isHovered || selectedSave == slotID) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.fillRect(x, y, SAVE_FRAME_WIDTH, 2);
        g2d.fillRect(x, y + SAVE_FRAME_HEIGHT - 2, SAVE_FRAME_WIDTH, 2);
        g2d.fillRect(x, y, 2, SAVE_FRAME_HEIGHT);
        g2d.fillRect(x + SAVE_FRAME_WIDTH - 2, y, 2, SAVE_FRAME_HEIGHT);

        int thickness = 4; //thickness of shadows

        //shadows
        if (isHovered || selectedSave == slotID) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, SAVE_FRAME_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, SAVE_FRAME_HEIGHT - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, SAVE_FRAME_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, SAVE_FRAME_HEIGHT - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
        }
        g2d.fillRect(x + 2, y + SAVE_FRAME_HEIGHT - 2 - thickness, SAVE_FRAME_WIDTH - 4, thickness);
        g2d.fillRect(x + SAVE_FRAME_WIDTH - 2 - thickness, y + 2, thickness, SAVE_FRAME_HEIGHT - 4);

        //header
        g2d.setFont(new Font("Consolas", Font.BOLD, 35));
        g2d.setColor(new Color(30, 30, 30));
        g2d.drawString("SLOT " + slotID, x + 25, y + 50);

        //save detail formatting
        g2d.setFont(new Font("Consolas", Font.PLAIN, 25));
        g2d.setColor(new Color(50, 50, 50));

        int startY = y + 100;
        int lineSpacing = 50;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null && !lines[i].isEmpty()) { //only draws lines with text
                g2d.drawString(lines[i], x + 25, startY + (i * lineSpacing));
            }
        }
    }

    /**
     * Draws save screen buttons with dynamic graphics if the mouse is hovering
     * @param g2d Abstract class passing
     * @param x X coordinate of the button
     * @param y Y coordinate of the button
     * @param mouseX The player's mouses' current X location
     * @param mouseY The player's mouses' current Y location
     * @param text The specific text that is displayed on the button such as "Play" or "Delete"
     */
    public boolean drawSaveScreenButtons(Graphics2D g2d, int x, int y, int mouseX, int mouseY, String text) {
        x = x - SAVE_BUTTON_WIDTH /2;
        y = y - BUTTON_HEIGHT /2;

        boolean isHovered = mouseX >= x && mouseX <= x + SAVE_BUTTON_WIDTH && mouseY >= y && mouseY <= y + BUTTON_HEIGHT;

        //inner button
        if (isHovered) {
            g2d.setColor(new Color(114, 119, 139)); //dark gray
        } else {
            g2d.setColor(new Color(148, 148, 148)); //light gray
        }
        g2d.fillRect(x, y, SAVE_BUTTON_WIDTH, BUTTON_HEIGHT);

        //outer border
        if (isHovered) {
            g2d.setColor(Color.WHITE); //white
        } else {
            g2d.setColor(Color.BLACK); //black
        }

        g2d.fillRect(x, y, SAVE_BUTTON_WIDTH, 2); //top line
        g2d.fillRect(x, y + BUTTON_HEIGHT - 2, SAVE_BUTTON_WIDTH, 2); //bottom line
        g2d.fillRect(x, y, 2, BUTTON_HEIGHT); //left line
        g2d.fillRect(x + SAVE_BUTTON_WIDTH - 2, y, 2, BUTTON_HEIGHT); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        if (isHovered) {
            //top and left shadows
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(x + 2, y + 2, SAVE_BUTTON_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, BUTTON_HEIGHT - 4);

            //bottom and right shadows
            g2d.setColor(new Color(57, 59, 70));
        } else {
            //top and left shadows
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(x + 2, y + 2, SAVE_BUTTON_WIDTH - 4, thickness);
            g2d.fillRect(x + 2, y + 2, thickness, BUTTON_HEIGHT - 4);

            //right and bottom shadows
            g2d.setColor(new Color(85, 85, 85));
        }
        g2d.fillRect(x + 2, y + BUTTON_HEIGHT - 2 - thickness, SAVE_BUTTON_WIDTH - 4, thickness);
        g2d.fillRect(x + SAVE_BUTTON_WIDTH - 2 - thickness, y + 2, thickness, BUTTON_HEIGHT - 4);

        //button text centering and creation
        g2d.setFont(FONT);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(FONT);
        int textX = x + (SAVE_BUTTON_WIDTH - metrics.stringWidth(text)) / 2;
        int textY = y + ((BUTTON_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);

        return isHovered;
    }

    public void drawCreditBox(Graphics2D g2d, int x, int y) {
        x = x - CREDITS_WIDTH /2;
        y = y - CREDITS_HEIGHT /2;

        //inner button
        g2d.setColor(new Color(148, 148, 148)); //light gray
        g2d.fillRect(x, y, CREDITS_HEIGHT, CREDITS_WIDTH);

        //outer border
        g2d.setColor(Color.BLACK);


        g2d.fillRect(x, y, CREDITS_WIDTH, 2); //top line
        g2d.fillRect(x, y + CREDITS_HEIGHT - 2, CREDITS_WIDTH, 2); //bottom line
        g2d.fillRect(x, y, 2, CREDITS_HEIGHT); //left line
        g2d.fillRect(x + CREDITS_WIDTH - 2, y, 2, CREDITS_HEIGHT); //right line

        int thickness = 4; //thickness of shadows
        //shadows
        //top and left shadows
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(x + 2, y + 2, CREDITS_WIDTH - 4, thickness);
        g2d.fillRect(x + 2, y + 2, thickness, CREDITS_HEIGHT - 4);

        //right and bottom shadows
        g2d.setColor(new Color(85, 85, 85));

        g2d.fillRect(x + 2, y + CREDITS_HEIGHT - 2 - thickness, CREDITS_WIDTH - 4, thickness);
        g2d.fillRect(x + CREDITS_WIDTH - 2 - thickness, y + 2, thickness, CREDITS_HEIGHT - 4);

        //button text centering and creation
        String text = "Credits";
        g2d.setFont(FONT);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(FONT);
        int textX = x + (CREDITS_WIDTH - metrics.stringWidth(text)) / 2;
        int textY = y + ((CREDITS_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent() + 7;
        g2d.drawString(text, textX, textY);
    }

    /**
     * Handles button logic and animations of main menu
     * Pre: Main menu is visible
     * Post: An action preformed based on the button/object clicked by the player or an animation
     */
    @Override
    public void act() {
        if (!isVisible) return;
        //mouse location variables
        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        if (GameMouseInput.isMouseLeftClickPressed) {
            //new game button
            int nx = (int)currentButtonX - BUTTON_WIDTH / 2;
            int ny = NEW_GAME_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= nx && mouseX <= nx + BUTTON_WIDTH && mouseY >= ny && mouseY <= ny + BUTTON_HEIGHT) {
                boolean allSlotsFull = false;
                //cycles through all the slots, trying to find an empty one that can be filled with the new save
                if (isSlotFilled(1)) {
                    if (isSlotFilled(2)) {
                        if (isSlotFilled(3)) {
                            allSlotsFull = true;
                        } else {
                            unfilledSaveSlot = 3;
                        }
                    } else {
                        unfilledSaveSlot = 2;
                    }
                } else {
                    unfilledSaveSlot = 1;
                }

                if (allSlotsFull) { //prevents player from starting new game if all save slots are full
                    System.out.println("All save slots filled");
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    return;
                }
                game.saveSlotNumber = unfilledSaveSlot; //selects the save slot to be used for the new game
                if (saveMenuState == 1 || saveMenuState == 2) { //if the save screen menu is open, close with the main menu
                    saveMenuState = 3;
                    this.mainMenuState = 3;
                } else if (creditsMenuState == 1 || creditsMenuState == 2){
                    creditsMenuState = 3;
                } else if (saveMenuState == 0 || creditsMenuState == 0) {
                    this.mainMenuState = 3;
                }
                game.prepareGameSession();
                playClickSound();
                System.out.println("Player began new playthrough");
                return;
            }
            //continue from save button
            int cx = (int)currentButtonX - BUTTON_WIDTH / 2;
            int cy = CONTINUE_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= cx && mouseX <= cx + BUTTON_WIDTH && mouseY >= cy && mouseY <= cy + BUTTON_HEIGHT) {
                if (saveMenuState == 0 || saveMenuState == 3) {
                    if (creditsMenuState == 2 || creditsMenuState == 1) { //if it is open it closes
                        creditsMenuState = 3; //slides down
                    }
                    loadSlotData(); //loads the data/details of save to be displayed on save frame/slot
                    saveMenuState = 1; //slides up
                    animationSaveY = this.getHeight() + 350;
                } else if (saveMenuState == 1 || saveMenuState == 2) { //if open or opening, close it
                    saveMenuState = 3;
                    selectedSave = 0;
                }
                playClickSound();
                GameMouseInput.isMouseLeftClickPressed = false; //reset mouse inputs
                GameMouseInput.reset();
                System.out.println("Player has opened save selection screen");
                return;
            }
            //settings button
            int sx = (int)currentButtonX - BUTTON_WIDTH / 2;
            int sy = SETTINGS_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= sx && mouseX <= sx + BUTTON_WIDTH && mouseY >= sy && mouseY <= sy + BUTTON_HEIGHT) {
                game.openSettingsMenu();
                System.out.println("Player has opened settings menu");
                return;
            }
            //credits button
            int crx = (int)currentButtonX - BUTTON_WIDTH / 2;
            int cry = CREDITS_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= crx && mouseX <= crx + BUTTON_WIDTH && mouseY >= cry && mouseY <= cry + BUTTON_HEIGHT) {
                if (creditsMenuState == 0 || creditsMenuState == 3) {
                    if (saveMenuState == 2 || saveMenuState == 1) { //if it is open it closes
                        saveMenuState = 3; //slides down
                        selectedSave = 0;
                    }
                    creditsMenuState = 1; //slides up
                    animationCreditY = this.getHeight() + 350;
                } else if (creditsMenuState == 1 || creditsMenuState == 2) { //if it is open it closes
                    creditsMenuState = 3; //slides down
                }
                playClickSound();
                GameMouseInput.isMouseLeftClickPressed = false; //reset mouse inputs
                GameMouseInput.reset();
                System.out.println("Player has opened credits screen");
                return;
            }
            //quit/exit button
            int ex = (int)currentButtonX - BUTTON_WIDTH / 2;
            int ey = QUIT_Y - BUTTON_HEIGHT / 2;
            if (mouseX >= ex && mouseX <= ex + BUTTON_WIDTH && mouseY >= ey && mouseY <= ey + BUTTON_HEIGHT) {
                game.exitGame();
                return;
            }

            //save selection screen button logic
            if (saveMenuState != 0) { //is visible
                int s1x = SAVE_FRAME_X - SAVE_FRAME_WIDTH / 2;
                int s1y = SAVE_FRAME_Y - SAVE_FRAME_HEIGHT / 2;
                if (mouseX >= s1x && mouseX <= s1x + SAVE_FRAME_WIDTH && mouseY >= s1y && mouseY <= s1y + SAVE_FRAME_HEIGHT) {
                    if (selectedSave == 1) { //deselects other saves and selects this one
                        selectedSave = 0;
                    } else {
                        selectedSave = 1;
                    }
                    playClickSound();
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    System.out.println("Save 1 selected");
                    return;
                }

                int s2x = (SAVE_FRAME_X + 425) - SAVE_FRAME_WIDTH / 2;
                int s2y = SAVE_FRAME_Y - SAVE_FRAME_HEIGHT / 2;
                if (mouseX >= s2x && mouseX <= s2x + SAVE_FRAME_WIDTH && mouseY >= s2y && mouseY <= s2y + SAVE_FRAME_HEIGHT) {
                    if (selectedSave == 2) { //deselects other saves and selects this one
                        selectedSave = 0;
                    } else {
                        selectedSave = 2;
                    }
                    playClickSound();
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    System.out.println("Save 2 selected");
                    return;
                }

                int s3x = (SAVE_FRAME_X + 850) - SAVE_FRAME_WIDTH / 2;
                int s3y = SAVE_FRAME_Y - SAVE_FRAME_HEIGHT / 2;
                if (mouseX >= s3x && mouseX <= s3x + SAVE_FRAME_WIDTH && mouseY >= s3y && mouseY <= s3y + SAVE_FRAME_HEIGHT) {
                    if (selectedSave == 3) { //deselects other saves and selects this one
                        selectedSave = 0;
                    } else {
                        selectedSave = 3;
                    }
                    playClickSound();
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    System.out.println("Save 3 selected");
                    return;
                }
                //play button
                int pX = SAVE_PLAY_BUTTON_X - SAVE_BUTTON_WIDTH / 2;
                int pY = (int) animationSaveY + SAVE_BUTTON_Y - BUTTON_HEIGHT / 2;
                if (mouseX >= pX && mouseX <= pX + SAVE_BUTTON_WIDTH && mouseY >= pY && mouseY <= pY + BUTTON_HEIGHT) {
                    if (selectedSave == 0) {
                        return;
                    }

                    String path = "saves/save" + selectedSave + ".ser"; //gets the path of the file to be read
                    SaveGame loadedProgress = SaveGame.loadData(path); //creates an object with all the data

                    if (loadedProgress != null) {
                        //passes data to main game class to load the data once the game objects are loaded
                        game.pendingSaveData = loadedProgress;
                        game.saveSlotNumber = selectedSave;

                        saveMenuState = 3; //slides down save screen
                        mainMenuState = 3;
                        game.prepareGameSession();
                        System.out.println("Save cached. Closing menu to initialize player...");
                    } else {
                        System.out.println("Could not launch game: Save slot file is missing or corrupted");
                        selectedSave = 0;
                    }
                    playClickSound();
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    return;
                }

                //delete button
                int dX = SAVE_DELETE_BUTTON_X - SAVE_BUTTON_WIDTH / 2;
                int dY = (int) animationSaveY + SAVE_BUTTON_Y - BUTTON_HEIGHT / 2;
                if (mouseX >= dX && mouseX <= dX + SAVE_BUTTON_WIDTH && mouseY >= dY && mouseY <= dY + BUTTON_HEIGHT) {
                    if (selectedSave == 0) {
                        return;
                    }

                    String filePath = "saves/save" + selectedSave + ".ser";
                    File file = new File(filePath);

                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("Save " + selectedSave + " was deleted");
                        } else {
                            System.out.println("File deletion error");
                        }
                    }
                    loadSlotData(); //refreshes graphics so that the save frame does not show the deleted save's data
                    selectedSave = 0; //deselects the slot

                    playClickSound();
                    GameMouseInput.isMouseLeftClickPressed = false;
                    GameMouseInput.reset();
                    return;
                }
            }
        }
        animateMainMenu();

        animateCredits();

        animateSaveScreen();

        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();

        this.repaint(); //do not remove, very important
    }

    /**
     * Loads saved data into text form for formatting in the save selection screen
     * Pre: Save selection screen (Resume from save) button is clicked
     * Post: String array is filled with the text that will be displayed
     */
    private void loadSlotData() {
        for (int i = 1; i <= 3; i++) {
            String filePath = "saves/save" + i + ".ser";
            java.io.File file = new java.io.File(filePath);

            if (file.exists()) {
                try (FileInputStream fileIn = new FileInputStream(file);
                     ObjectInputStream objIn = new ObjectInputStream(fileIn)) {

                    SaveGame save = (SaveGame) objIn.readObject();

                    //graphical formatting for in game graphics
                    slotLines[i - 1][0] = "Health: " + save.savedPlayerHealth + "/" + save.savedPlayerMaxHealth;
                    slotLines[i - 1][1] = "Level: " + save.savedPlayerLevel;
                    slotLines[i - 1][2] = "Xp: " + String.format("%.1f", save.savedPlayerXp) + "/" + save.savedPlayerMaxXp;
                    slotLines[i - 1][3] = "Total Xp: " + save.savedPlayerTotalXp;
                    slotLines[i - 1][4] = "Player Speed: " + save.savedPlayerSpeed;
                } catch (Exception e) {
                    slotLines[i - 1][0] = "Corrupted Save";
                    slotLines[i - 1][1] = "";
                    slotLines[i - 1][2] = "";
                    slotLines[i - 1][3] = "";
                    slotLines[i - 1][4] = "";
                    slotLines[i - 1][5] = "";
                    slotLines[i - 1][6] = "";
                    slotLines[i - 1][7] = "";
                    slotLines[i - 1][8] = "";
                }
            } else {
                slotLines[i - 1][0] = "Empty Slot";
                slotLines[i - 1][1] = "";
                slotLines[i - 1][2] = "";
                slotLines[i - 1][3] = "";
                slotLines[i - 1][4] = "";
                slotLines[i - 1][5] = "";
                slotLines[i - 1][6] = "";
                slotLines[i - 1][7] = "";
                slotLines[i - 1][8] = "";
            }
        }
    }

    /**
     * Helper method that checks if a save slot has a file that exists in that slot currently
     * Pre: New Game button is clicked
     * Post: Allows the game to select a free save slot or prevents the player from starting a new game unless there is a free slot
     * @param slotNumber The slot number to be checked
     * @return Is the slot filled or not filled (boolean)
     */
    public boolean isSlotFilled(int slotNumber) {
        String filePath = "saves/save" + slotNumber + ".ser";
        java.io.File file = new java.io.File(filePath);

        return file.exists() && file.isFile();
    }

    /**
     * Save screen opening animation
     * Pre: Save menu state = 1
     * Post: The save selection screen moves up with a nice animation
     */
    public void animateSaveScreen() {
        if (saveMenuState == 1) { //slide up
            animationSaveY -= (animationSaveY - SAVE_FRAME_Y) * ANIMATION_SPEED; //moves save screen
            if (animationSaveY - SAVE_FRAME_Y < 1) { //snapping at close distance
                animationSaveY = SAVE_FRAME_Y;
                saveMenuState = 2; //state 2 means visible
            }
        }
        if (saveMenuState == 3) { //slide down
            double screenBottom = this.getHeight() + 450;
            animationSaveY += (screenBottom - animationSaveY) * ANIMATION_SPEED + 1; //moves save screen

            if (animationSaveY >= screenBottom) {
                animationSaveY = screenBottom;
                saveMenuState = 0; //hidden
            }
        }
    }

    /**
     * Save screen closing animation
     * Pre: Save menu state = 3
     * Post: The save selection screen moves down with a nice animation and disappears
     */

    public void animateCredits() {
        if (creditsMenuState == 1) { //slide up
            animationCreditY -= (animationCreditY - CREDITS_TARGET_Y) * ANIMATION_SPEED; //moves save screen
            if (animationCreditY - CREDITS_TARGET_Y < 1) { //snapping at close distance
                animationCreditY = CREDITS_TARGET_Y;
                creditsMenuState = 2; //state 2 means visible
            }
        }
        if (creditsMenuState == 3) { //slide down
            double screenBottom = this.getHeight() + 600;
            animationCreditY += (screenBottom - animationCreditY) * ANIMATION_SPEED + 1; //moves save screen

            if (animationCreditY >= screenBottom) {
                animationCreditY = screenBottom;
                creditsMenuState = 0; //hidden
            }
        }
    }

    private void animateMainMenu() {
        if (mainMenuState == 1) { //opening
            currentButtonX += (TARGET_OPEN_X - currentButtonX) * ANIMATION_SPEED;
            if (TARGET_OPEN_X - currentButtonX < 1) {
                currentButtonX = TARGET_OPEN_X;
                mainMenuState = 2; //open
            }
        }
        if (mainMenuState == 3) { //closing
            bgAlpha -= FADE_SPEED; //fades background
            if (bgAlpha < 0.0f) {
                bgAlpha = 0.0f;
            }

            currentButtonX += (OFF_SCREEN_LEFT_X - currentButtonX) * ANIMATION_SPEED - 1;
            if (currentButtonX <= OFF_SCREEN_LEFT_X) {
                currentButtonX = OFF_SCREEN_LEFT_X;
                mainMenuState = 0; //closed
                game.activatePlayingState();
            }

        }
    }

    private void playHoverSound() {
        try {
            File soundFile = new File("Assets/hover.wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing hover sound: " + e.getMessage());
        }
    }

    private void playClickSound() {
        try {
            File soundFile = new File("Assets/click.wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing click sound: " + e.getMessage());
        }
    }
}