package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage; //off-screen canvas
import java.awt.image.ConvolveOp; //used to scale up and down images
import java.awt.image.Kernel; //a matrix used for math
import java.io.IOException;

public class SettingsMenu extends GameObject {

    PolyGone game;

    private BufferedImage blurredSnapshot = null;
    private boolean needsBlurRefresh = false; //generates a new blur asset?

    private Font font = new Font("Consolas", Font.BOLD, 40);
    public boolean isSettingsMenuVisible = false;
    public int settingsMenuState = 0;

    private final int buttonWidth = 500;
    private final int buttonHeight = 70;

    private final double ANIMATION_SPEED = 0.1;
    private double animationSaveY = 1200;
    private int settingsY = 600;
    private int currentY;

    private final int sliderWidth = 500;
    private final int sliderHeight = 50;
    private final int knobWidth = 35;
    private final int knobHeight = 75;

    private boolean isDraggingMusicSlider = false;
    private boolean isDraggingUXSlider = false;
    private boolean isDraggingMasterSlider = false;

    private boolean wasBackHovered = false;
    private boolean wasVSyncHovered = false;
    private boolean wasDebugHovered = false;

    public SettingsMenu(PolyGone game) {
        this.game = game;
        this.setBounds(0, 0, game.getWidth(), game.getHeight()); //sets gui size and location
    }

    public void setSettingsMenuVisible(boolean visible) {
        GameMouseInput.isMouseLeftClickPressed = false;
        GameMouseInput.reset();
        this.isSettingsMenuVisible = visible;
        System.out.println("Player paused game");
        if (visible) {
            this.needsBlurRefresh = true; //tells game to blur background
            settingsMenuState = 1;
            this.animationSaveY = this.getHeight()+350;
            this.settingsY = 550;
        } else {
            //avoids blurring when pause menu not open
            if (blurredSnapshot != null) {
                blurredSnapshot.flush();
                blurredSnapshot = null;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (!isSettingsMenuVisible) return; //determines if it should be drawn

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
        if (settingsMenuState != 0) {
            currentY = (int) animationSaveY;

            boolean isBackHovered = drawButton(g2d, this.getWidth() / 2, currentY - 180, GameMouseInput.mouseX, GameMouseInput.mouseY, "Back");
            boolean isVSyncHovered = drawButton(g2d, this.getWidth() / 2,  currentY - 90, GameMouseInput.mouseX, GameMouseInput.mouseY, "V-Sync: " + game.isVSyncEnabled);
            boolean isDebugHovered = drawButton(g2d, this.getWidth() / 2, currentY, GameMouseInput.mouseX, GameMouseInput.mouseY, "Show DebugHUD: " + game.showDebugHUD);

            if (isBackHovered && !wasBackHovered) MusicSoundEffectsController.playHoverSound();
            if (isVSyncHovered && !wasVSyncHovered) MusicSoundEffectsController.playHoverSound();
            if (isDebugHovered && !wasDebugHovered) MusicSoundEffectsController.playHoverSound();

            wasBackHovered = isBackHovered;
            wasVSyncHovered = isVSyncHovered;
            wasDebugHovered = isDebugHovered;

            drawSlider(g2d, 270, "Music Volume: ", MusicSoundEffectsController.musicVolume, isDraggingMusicSlider);
            drawSlider(g2d, 180, "SFX Volume: ", MusicSoundEffectsController.UXVolume, isDraggingUXSlider);
            drawSlider(g2d, 90, "Master Volume: ", MusicSoundEffectsController.masterVolume, isDraggingMasterSlider);
        }
    }

    public boolean drawButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY, String text) {
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
        g2d.setFont(font);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent() + 4;
        g2d.drawString(text, textX, textY);

        return isHovered;
    }

    public void drawSlider(Graphics2D g2d, int yOffset, String label, double volumeValue, boolean isCurrentlyDragging) {
        int sliderX = (this.getWidth() / 2) - (sliderWidth / 2);
        int sliderY = currentY + yOffset - (sliderHeight / 2);

        g2d.setColor(new Color(148, 148, 148));
        g2d.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(sliderX, sliderY, sliderWidth, 2);
        g2d.fillRect(sliderX, sliderY + sliderHeight - 2, sliderWidth, 2);
        g2d.fillRect(sliderX, sliderY, 2, sliderHeight);
        g2d.fillRect(sliderX + sliderWidth - 2, sliderY, 2, sliderHeight);

        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(sliderX + 2, sliderY + 2, sliderWidth - 4, 4);
        g2d.fillRect(sliderX + 2, sliderY + 2, 4, sliderHeight - 4);
        g2d.setColor(new Color(85, 85, 85));
        g2d.fillRect(sliderX + 2, sliderY + sliderHeight - 6, sliderWidth - 4, 4);
        g2d.fillRect(sliderX + sliderWidth - 6, sliderY + 2, 4, sliderHeight - 4);

        int progressWidth = (int)(volumeValue * (sliderWidth - 8));
        if (progressWidth > 0) {
            g2d.setColor(new Color(114, 119, 139));
            g2d.fillRect(sliderX + 4, sliderY + 6, progressWidth, sliderHeight - 12);
        }

        g2d.setFont(new Font("Consolas", Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        g2d.drawString(label + (int)(volumeValue * 100) + "%", sliderX + 20, sliderY + 32);

        int knobX = sliderX + (int)(volumeValue * (sliderWidth - knobWidth));
        int knobY = currentY + yOffset - (knobHeight / 2); // Corrected Y to match yOffset parameters

        boolean isKnobHovered = GameMouseInput.mouseX >= knobX && GameMouseInput.mouseX <= knobX + knobWidth &&
                GameMouseInput.mouseY >= knobY && GameMouseInput.mouseY <= knobY + knobHeight;

        if (isKnobHovered || isCurrentlyDragging) {
            g2d.setColor(new Color(114, 119, 139));
        } else {
            g2d.setColor(new Color(148, 148, 148));
        }
        g2d.fillRect(knobX, knobY, knobWidth, knobHeight);

        if (isKnobHovered || isCurrentlyDragging) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.fillRect(knobX, knobY, knobWidth, 2);
        g2d.fillRect(knobX, knobY + knobHeight - 2, knobWidth, 2);
        g2d.fillRect(knobX, knobY, 2, knobHeight);
        g2d.fillRect(knobX + knobWidth - 2, knobY, 2, knobHeight);

        if (isKnobHovered || isCurrentlyDragging) {
            g2d.setColor(new Color(171, 178, 209));
            g2d.fillRect(knobX + 2, knobY + 2, knobWidth - 4, 4);
            g2d.fillRect(knobX + 2, knobY + 2, 4, knobHeight - 4);
            g2d.setColor(new Color(57, 59, 70));
            g2d.fillRect(knobX + 2, knobY + knobHeight - 6, knobWidth - 4, 4);
            g2d.fillRect(knobX + knobWidth - 6, knobY + 2, 4, knobHeight - 4);
        } else {
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(knobX + 2, knobY + 2, knobWidth - 4, 4);
            g2d.fillRect(knobX + 2, knobY + 2, 4, knobHeight - 4);
            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(knobX + 2, knobY + knobHeight - 6, knobWidth - 4, 4);
            g2d.fillRect(knobX + knobWidth - 6, knobY + 2, 4, knobHeight - 4);
        }
    }

    @Override
    public void act() throws IOException {
        if (!isSettingsMenuVisible) return;
        int midX = this.getWidth() / 2;

        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        int musicSliderX = midX - (sliderWidth / 2);
        int musicSliderY = currentY + 270 - (sliderHeight / 2);

        int UXSliderX = midX - (sliderWidth / 2);
        int UXSliderY = currentY + 180 - (sliderHeight / 2);

        int masterSliderX = midX - (sliderWidth / 2);
        int masterSliderY = currentY + 90 - (sliderHeight / 2);

        //music slider
        if (GameMouseInput.isMouseLeftClickPressed && !isDraggingUXSlider) {
            if (mouseX >= musicSliderX && mouseX <= musicSliderX + sliderWidth &&
                    mouseY >= musicSliderY - 15 && mouseY <= musicSliderY + sliderHeight + 15) {
                isDraggingMusicSlider = true;
            }
        }

        if (isDraggingMusicSlider) {
            double relativeX = mouseX - musicSliderX;
            double newVolume = relativeX / (double) (sliderWidth - knobWidth);

            if (newVolume < 0.0) newVolume = 0.0;
            if (newVolume > 1.0) newVolume = 1.0;

            MusicSoundEffectsController.musicVolume = newVolume;

            if (MusicSoundEffectsController.backgroundMusicClip != null) {
                MusicSoundEffectsController.setClipVolume(
                        MusicSoundEffectsController.backgroundMusicClip,
                        (float) newVolume
                );
            }
        }

        //ux slider
        if (GameMouseInput.isMouseLeftClickPressed && !isDraggingMusicSlider) {
            if (mouseX >= UXSliderX && mouseX <= UXSliderX + sliderWidth &&
                    mouseY >= UXSliderY - 15 && mouseY <= UXSliderY + sliderHeight + 15) {
                isDraggingUXSlider = true;
            }
        }

        if (isDraggingUXSlider) {
            double relativeX = mouseX - UXSliderX;
            double newVolume = relativeX / (double) (sliderWidth - knobWidth);

            if (newVolume < 0.0) newVolume = 0.0;
            if (newVolume > 1.0) newVolume = 1.0;

            MusicSoundEffectsController.UXVolume = newVolume;

            MusicSoundEffectsController.updateRunningMusicVolume();
        }

        //master volume slider
        if (GameMouseInput.isMouseLeftClickPressed && !isDraggingMusicSlider && !isDraggingUXSlider) {
            if (mouseX >= masterSliderX && mouseX <= masterSliderX + sliderWidth &&
                    mouseY >= masterSliderY - 15 && mouseY <= masterSliderY + sliderHeight + 15) {
                isDraggingMasterSlider = true;
            }
        }

        if (isDraggingMasterSlider) {
            double relativeX = mouseX - masterSliderX;
            double newVolume = relativeX / (double) (sliderWidth - knobWidth);

            if (newVolume < 0.0) newVolume = 0.0;
            if (newVolume > 1.0) newVolume = 1.0;

            MusicSoundEffectsController.masterVolume = newVolume;

            MusicSoundEffectsController.updateRunningMusicVolume();
        }

        if (!GameMouseInput.isMouseLeftClickPressed) {
            isDraggingMusicSlider = false;
            isDraggingUXSlider = false;
            isDraggingMasterSlider = false;
        }

        boolean isDraggingAnySlider = isDraggingMusicSlider || isDraggingUXSlider || isDraggingMasterSlider;

        if (GameMouseInput.isMouseLeftClickPressed && !isDraggingAnySlider) {
            int rx = midX - buttonWidth / 2;
            int ry = (currentY -180) - buttonHeight / 2;
            if (mouseX >= rx && mouseX <= rx + buttonWidth && mouseY >= ry && mouseY <= ry + buttonHeight) {
                GameMouseInput.reset();
                GameMouseInput.isMouseLeftClickPressed = false;
                settingsMenuState = 3;
                MusicSoundEffectsController.playClickSound();
                System.out.println("Player exited settings");
                return;
            }

            int ex = midX - buttonWidth / 2;
            int ey = (currentY -90) - buttonHeight / 2;
            if (mouseX >= ex && mouseX <= ex + buttonWidth && mouseY >= ey && mouseY <= ey + buttonHeight) {
                game.setVSync(!game.isVSyncEnabled);
                MusicSoundEffectsController.playClickSound();
                GameMouseInput.reset();
                GameMouseInput.isMouseLeftClickPressed = false;
                return;
            }

            int emx = midX - buttonWidth / 2;
            int emy = (currentY) - buttonHeight / 2;
            if (mouseX >= emx && mouseX <= emx + buttonWidth && mouseY >= emy && mouseY <= emy + buttonHeight) {
                game.toggleDebugHUD();
                MusicSoundEffectsController.playClickSound();
                GameMouseInput.reset();
                GameMouseInput.isMouseLeftClickPressed = false;
                return;
            }
        }
        animateSettingsMenu();

        if (!isDraggingAnySlider) {
            GameMouseInput.isMouseLeftClickPressed = false;
            GameMouseInput.reset();
        }

        this.repaint(); //do not remove, very important
    }

    public void animateSettingsMenu() {
        if (settingsMenuState == 1) { //slide up
            animationSaveY -= (animationSaveY - settingsY) * ANIMATION_SPEED; //moves save screen
            if (animationSaveY - settingsY < 1) { //snapping
                animationSaveY = settingsY;
                settingsMenuState = 2; //state 2 means visible
            }
        }
        if (settingsMenuState == 3) { //slide down
            double screenBottom = this.getHeight() + 250;
            animationSaveY += (screenBottom - animationSaveY) * ANIMATION_SPEED + 1; //moves save screen
            if (animationSaveY >= screenBottom) { //snapping
                animationSaveY = screenBottom;
                settingsMenuState = 0; //state 0 means hidden
                game.closeSettingsMenu();
            }
        }
    }

    /**
     * Blurs the background of anything behind the open menu by getting a pixel and its surrounding pixels
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
        boolean oldVisibility = this.isSettingsMenuVisible;
        this.isSettingsMenuVisible = false;

        //draws the parent frame container components to the target texture
        targetCanvas.paint(containerGraphics);

        this.isSettingsMenuVisible = oldVisibility; //unhides pause menu overlay
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