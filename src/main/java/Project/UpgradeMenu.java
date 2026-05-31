package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage; //off-screen canvas
import java.awt.image.ConvolveOp; //used to scale up and down images
import java.awt.image.Kernel; //a matrix used for math
import java.util.Random;

public class UpgradeMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;
    DebugHUD debugHUD;

    private BufferedImage blurredSnapshot = null;
    private boolean needsBlurRefresh = false; //generates a new blur asset?

    private Font font = new Font("Consolas", Font.BOLD, 30);
    private boolean isVisible = false;

    //stores card variants
    private int[] cardRarities = new int[3]; //3 cards
    private int[] cardOptions = new int[3];

    private final int cardWidth = 350;
    private final int cardHeight = 500;

    private boolean hasMouseBeenReleasedSinceOpen = false;

    public UpgradeMenu(PolyGone game, Player player, DebugHUD debugHUD) {
        this.player = player;
        this.game = game;
        this.debugHUD = debugHUD;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
    }

    public void setUpgradeMenuVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            this.needsBlurRefresh = true; //tells game to blur background
            this.hasMouseBeenReleasedSinceOpen = false;
            rollUpgradeCards();
        } else {
            //avoids blurring when pause menu not open
            if (blurredSnapshot != null) {
                blurredSnapshot.flush();
                blurredSnapshot = null;
            }
        }
        this.repaint();
    }

    private void rollUpgradeCards() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int roll = random.nextInt(100) + 1;

            //percentage based rarity rolling
            if (roll <= 50) {
                cardRarities[i] = 0; //50%
            } else if (roll <= 75) {
                cardRarities[i] = 1; //25%
            } else if (roll <= 90) {
                cardRarities[i] = 2; //15%
            } else if (roll <= 98) {
                cardRarities[i] = 3; //8%
            } else {
                cardRarities[i] = 4; //2%
            }
            cardOptions[i] = random.nextInt(5); //number of card upgrade types
        }
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible) return; //determines if it should be drawn

        Graphics2D g2d = (Graphics2D) g; //cast to 2d graphics for antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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

        g2d.setFont(new Font("Consolas", Font.BOLD, 50));
        g2d.setColor(Color.WHITE);
        g2d.drawString("SELECT AN UPGRADE", this.getWidth() / 2 - 230, 150);

        int cardY = this.getHeight() / 2;
        //for spacing between cards
        int centerX = this.getWidth() / 2;
        int gap = 400;

        for (int i = 0; i < 3; i++) { //displays 3 options (change this value and the other for loop in act() for more options)
            int cardX = centerX + (i - 1) * gap; //for spacing between cards
            int rarity = cardRarities[i];
            int option = cardOptions[i];

            switch (rarity) {
                case 0: drawCommonUpgradeCard(g2d, cardX, cardY, GameMouseInput.mouseX, GameMouseInput.mouseY, option, rarity); break;
                case 1: drawUncommonUpgradeCard(g2d, cardX, cardY, GameMouseInput.mouseX, GameMouseInput.mouseY, option, rarity); break;
                case 2: drawRareUpgradeCard(g2d, cardX, cardY, GameMouseInput.mouseX, GameMouseInput.mouseY, option, rarity); break;
                case 3: drawEpicUpgradeCard(g2d, cardX, cardY, GameMouseInput.mouseX, GameMouseInput.mouseY, option, rarity); break;
                case 4: drawLegendaryUpgradeCard(g2d, cardX, cardY, GameMouseInput.mouseX, GameMouseInput.mouseY, option, rarity); break;
            }

        }
    }

    private boolean drawCardFrame(Graphics2D g2d, int x, int y, int w, int h, int mouseX, int mouseY, Color bg, Color shadowLight, Color shadowDark) {
        int cardLeft = x - w / 2;
        int cardTop = y - h / 2;
        boolean isHovered = mouseX >= cardLeft && mouseX <= cardLeft + w && mouseY >= cardTop && mouseY <= cardTop + h;

        Color currentBg;
        Color currentBorder;
        Color currentLightShadow;
        Color currentDarkShadow;

        //makes card become darker when hovered
        if (isHovered) {
            currentBg = bg.darker();
            currentBorder = Color.WHITE;
            currentLightShadow = shadowLight;
            currentDarkShadow = shadowDark;
        } else {
            currentBg = bg;
            currentBorder = new Color(50, 50, 50);
            currentLightShadow = Color.WHITE;
            currentDarkShadow = new Color(85, 85, 85);
        }

        //draw card background
        g2d.setColor(currentBg);
        g2d.fillRect(cardLeft, cardTop, w, h);

        int borderSize = 6;
        int shadowSize = 4;

        //draws light and dark shadows
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(currentLightShadow);
        g2d.fillRect(cardLeft + borderSize, cardTop + borderSize, w - (borderSize * 2), shadowSize);
        g2d.fillRect(cardLeft + borderSize, cardTop + borderSize, shadowSize, h - (borderSize * 2));
        g2d.setColor(currentDarkShadow);
        g2d.fillRect(cardLeft + borderSize, cardTop + h - borderSize - shadowSize, w - (borderSize * 2), shadowSize);
        g2d.fillRect(cardLeft + w - borderSize - shadowSize, cardTop + borderSize, shadowSize, h - (borderSize * 2));

        //draws border
        g2d.setColor(currentBorder);
        g2d.setStroke(new BasicStroke(borderSize)); //stroke is used for border size
        g2d.drawRect(cardLeft + borderSize/2, cardTop + borderSize/2, w - borderSize, h - borderSize);
        g2d.setStroke(new BasicStroke(1)); //reset border size so stroke can be used for other gui

        return isHovered;
    }

    private void drawCardText(Graphics2D g2d, int x, int y, int h, String rarityText, int option, int rarity) {
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics(font);

        //header
        g2d.drawString(rarityText, x - fm.stringWidth(rarityText) / 2, (y - h / 2) + 50);

        //upgrade options text
        g2d.setFont(new Font("Consolas", Font.PLAIN, 16));
        String text = switch (option) {
            case 0 -> "More Ammo";
            case 1 -> "More Bullet Speed & Range";
            case 2 -> "More Player Speed";
            case 3 -> "More Max Health";
            case 4 -> "More XP";
            default -> "Error in options text"; //if the option generated is invalid, it will output error message
        };

        int percentageIncrease = 0;

        switch (rarity) { //determines the percentage increase of the player/weapon attribute based on the rarity of the card
            case 0: percentageIncrease = 5; break;
            case 1: percentageIncrease = 10; break;
            case 2: percentageIncrease = 20; break;
            case 3: percentageIncrease = 40; break;
            case 4: percentageIncrease = 67; break;
        }

        String description = "+" + percentageIncrease + "% " + text;
        g2d.drawString(description, x - g2d.getFontMetrics().stringWidth(description) / 2, y);
    }

    private void applyUpgrade(int rarity, int option) {
        double multiplier = 1.0;
        switch (rarity) {
            case 0: multiplier = 1.05; System.out.println("Player selected a common upgrade"); break;
            case 1: multiplier = 1.10; System.out.println("Player selected a uncommon upgrade"); break;
            case 2: multiplier = 1.20; System.out.println("Player selected a rare upgrade"); break;
            case 3: multiplier = 1.40; System.out.println("Player selected a epic upgrade"); break;
            case 4: multiplier = 1.67; System.out.println("Player selected a legendary upgrade"); break;
        }

        switch (option) {
            case 0: player.maxAmmo = (int)((player.maxAmmo * multiplier) + 0.5); break; //rounds up
            case 1: player.bulletSpeed = (int)((player.bulletSpeed * multiplier) + 0.5); break;
            case 2: player.playerSpeed = (int)((player.playerSpeed * multiplier) + 0.5); break;
            case 3: player.playerMaxHealth = (int)((player.playerMaxHealth * multiplier) + 0.5); break;
            case 4: game.enemyDroppedXp = game.enemyDroppedXp*multiplier; break;
        }
        player.playerCurrentHealth = player.playerMaxHealth; //heals player
    }

    public void drawCommonUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        //calls method that draws the card frame and checks if the player mouse is in the frame
        drawCardFrame(g2d, x, y, cardWidth, cardHeight, mouseX, mouseY, new Color(148, 148, 148), new Color(171, 178, 209), new Color(57, 59, 70));
        drawCardText(g2d, x, y, cardHeight, "Common", cardOption, rarity);
    }

    public void drawUncommonUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, cardWidth, cardHeight, mouseX, mouseY, new Color(119, 179, 119), new Color(159, 219, 159), new Color(39, 99, 39));
        drawCardText(g2d, x, y, cardHeight, "Uncommon", cardOption, rarity);
    }

    public void drawRareUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, cardWidth, cardHeight, mouseX, mouseY, new Color(100, 149, 237), new Color(140, 189, 255), new Color(20, 69, 157));
        drawCardText(g2d, x, y, cardHeight, "Rare", cardOption, rarity);
    }

    public void drawEpicUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, cardWidth, cardHeight, mouseX, mouseY, new Color(160, 32, 240), new Color(200, 72, 255), new Color(80, 0, 160));
        drawCardText(g2d, x, y, cardHeight, "Epic", cardOption, rarity);
    }

    public void drawLegendaryUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, cardWidth, cardHeight, mouseX, mouseY, new Color(218, 165, 32), new Color(255, 205, 72), new Color(138, 85, 0));
        drawCardText(g2d, x, y, cardHeight, "Legendary", cardOption, rarity);
    }

    @Override
    public void act() {
        if (!isVisible) return;
        //prevents the immediate choosing of an option if the player holds down left-click on the location of a card
        if (!GameMouseInput.isMouseLeftClickPressed) {
            hasMouseBeenReleasedSinceOpen = true;
        }
        if (hasMouseBeenReleasedSinceOpen && GameMouseInput.isMouseLeftClickPressed) {
            int cardY = this.getHeight() / 2;
            int centerX = this.getWidth() / 2;
            int gap = 400; //for spacing between cards
            int mouseX = GameMouseInput.mouseX;
            int mouseY = GameMouseInput.mouseY;

            for (int i = 0; i < 3; i++) { //checks for all 3 cards
                int cardX = centerX + (i - 1) * gap; //for spacing
                int cardLeft = cardX - cardWidth / 2;
                int cardTop = cardY - cardHeight / 2;

                if (mouseX >= cardLeft && mouseX <= cardLeft + cardWidth && mouseY >= cardTop && mouseY <= cardTop + cardHeight) {
                    applyUpgrade(cardRarities[i], cardOptions[i]); //calls method that applies the chosen upgrade to the card
                    game.closeUpgradeMenu();
                    return;
                }
            }
        }
        this.repaint(); //do not remove, very important
    }

    //gets a pixel and that pixels surrounding pixels, mixes the colors together and outputs the result to make it look blurry
    //also down and upscales to save processing power and smooths result to avoid blockiness
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
        boolean oldDebugVisibility = false;
        boolean hasDebugMenu = (debugHUD != null);
        if (hasDebugMenu) {
            oldDebugVisibility = debugHUD.isVisible();
            debugHUD.setVisible(false); // Hide it from the blur snapshot
        }

        //draws the parent frame container components to the target texture
        targetCanvas.paint(containerGraphics);

        if (hasDebugMenu) {
            debugHUD.setVisible(oldDebugVisibility);
        }

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
