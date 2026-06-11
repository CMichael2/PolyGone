package Project;

import Framework.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage; //off-screen canvas
import java.awt.image.ConvolveOp; //used to scale up and down images
import java.awt.image.Kernel; //a matrix used for math
import java.util.ArrayList;
import java.util.Random;

public class UpgradeMenu extends GameObject {

    Player player; //reference to object
    PolyGone game;
    DebugHUD debugHUD;
    EnemyManager enemyManager;

    private BufferedImage blurredSnapshot = null;
    private boolean needsBlurRefresh = false; //generates a new blur asset?

    private final Font FONT = new Font("Consolas", Font.BOLD, 40);
    private boolean isVisible = false;

    //stores card variants
    private int[] cardRarities = new int[3]; //3 cards
    private int[] cardOptions = new int[3];

    private final int CARD_WIDTH = 450;
    private final int CARD_HEIGHT = 650;
    private final int BUTTON_WIDTH = 450;
    private final int BUTTON_HEIGHT = 70;

    private final int REROLL_Y = 950;
    public int numberOfRerollsLeft = 10;
    public int startingNumberOfRerolls = 100;

    private boolean hasMouseBeenReleasedSinceOpen = false;

    public UpgradeMenu(PolyGone game, Player player, DebugHUD debugHUD, EnemyManager enemyManager) {
        this.player = player;
        this.game = game;
        this.debugHUD = debugHUD;
        this.enemyManager = enemyManager;
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
        //(note to self) decrease 1.x to reduce op cards
        int commonChance = 70 - (int) (player.playerLevel * 1.4);
        int uncommonChance = 90 - (int) (player.playerLevel * 1.2);
        int rareChance = 96 - (int) (player.playerLevel * 0.9);
        int epicChance = 99 - (int) (player.playerLevel * 0.3);
        int legendaryChance = 100;

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            boolean isDuplicate;
            int rolledOption;

            do {
                isDuplicate = false;
                rolledOption = random.nextInt(10); //number of options

                for (int k = 0; k < i; k++) {
                    if (cardOptions[k] == rolledOption) {
                        isDuplicate = true;
                        break;
                    }
                }
            } while (isDuplicate);

            int roll = random.nextInt(100) + 1;

            //percentage based rarity rolling
            if (roll <= commonChance) {
                cardRarities[i] = 0; //common
            } else if (roll <= uncommonChance) {
                cardRarities[i] = 1; //uncommon
            } else if (roll <= rareChance) {
                cardRarities[i] = 2; //rare
            } else if (roll <= epicChance) {
                cardRarities[i] = 3; //epic
            } else if (roll <= legendaryChance){
                cardRarities[i] = 4; //legendary
            } else {
                cardRarities[i] = 1; //exception/fallback
            }
            cardOptions[i] = rolledOption;
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

        drawRerollButton(g2d, this.getWidth()/2, REROLL_Y, GameMouseInput.mouseX, GameMouseInput.mouseY);

        int cardY = this.getHeight() / 2;
        //for spacing between cards
        int centerX = this.getWidth() / 2;
        int gap = 550;

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

    public void drawRerollButton(Graphics2D g2d, int x, int y, int mouseX, int mouseY) {
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
        String text = "Rerolls Left: " + numberOfRerollsLeft;
        g2d.setFont(FONT);
        g2d.setColor(new Color(50, 50, 50));
        FontMetrics metrics = g2d.getFontMetrics(FONT);
        int textX = x + (BUTTON_WIDTH - metrics.stringWidth(text)) / 2;
        int textY = y + ((BUTTON_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent() + 7;
        g2d.drawString(text, textX, textY);
    }

    private void drawCardFrame(Graphics2D g2d, int x, int y, int mouseX, int mouseY, Color bg, Color shadowLight, Color shadowDark) {
        int cardLeft = x - CARD_WIDTH / 2;
        int cardTop = y - CARD_HEIGHT / 2;
        boolean isHovered = mouseX >= cardLeft && mouseX <= cardLeft + CARD_WIDTH && mouseY >= cardTop && mouseY <= cardTop + CARD_HEIGHT;

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
        g2d.fillRect(cardLeft, cardTop, CARD_WIDTH, CARD_HEIGHT);

        int borderSize = 6;
        int shadowSize = 4;

        //draws light and dark shadows
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(currentLightShadow);
        g2d.fillRect(cardLeft + borderSize, cardTop + borderSize, CARD_WIDTH - (borderSize * 2), shadowSize);
        g2d.fillRect(cardLeft + borderSize, cardTop + borderSize, shadowSize, CARD_HEIGHT - (borderSize * 2));
        g2d.setColor(currentDarkShadow);
        g2d.fillRect(cardLeft + borderSize, cardTop + CARD_HEIGHT - borderSize - shadowSize, CARD_WIDTH - (borderSize * 2), shadowSize);
        g2d.fillRect(cardLeft + CARD_WIDTH - borderSize - shadowSize, cardTop + borderSize, shadowSize, CARD_HEIGHT - (borderSize * 2));

        //draws border
        g2d.setColor(currentBorder);
        g2d.setStroke(new BasicStroke(borderSize)); //stroke is used for border size
        g2d.drawRect(cardLeft + borderSize/2, cardTop + borderSize/2, CARD_WIDTH - borderSize, CARD_HEIGHT - borderSize);
        g2d.setStroke(new BasicStroke(1)); //reset border size so stroke can be used for other gui

    }

    private void drawCardText(Graphics2D g2d, int x, int y, String rarityText, int option, int rarity) {
        g2d.setFont(FONT);
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics(FONT);

        //header
        g2d.drawString(rarityText, x - fm.stringWidth(rarityText) / 2, (y - CARD_HEIGHT / 2) + 75);

        //upgrade options text
        g2d.setFont(new Font("Consolas", Font.PLAIN, 25));
        String text1 = "";
        String text2 = "";
        String text3 = "";
        String text4 = "";

        switch (rarity) { //determines the percentage increase of the player/weapon attribute based on the rarity of the card
            case 0:
                switch (option) {
                    case 0:
                        text1 = "Improve durability";
                        text2 = "+5% Max Health";
                        break;
                    case 1:
                        text1 = "Collect more sides";
                        text2 = "+5% Xp Gain";
                        break;
                    case 2:
                        text1 = "Move faster";
                        text2 = "+5% Speed";
                        break;
                    case 3:
                        text1 = "Store more circles";
                        text2 = "+5% Max Ammo";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 4:
                        text1 = "Circles fly farther";
                        text2 = "+5% Range";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 5:
                        text1 = "Circles fly faster";
                        text2 = "+5% Projectile Speed" ;
                        text3 = "To CURRENT Weapon";
                        break;
                    case 6:
                        text1 = "Kill enemies faster";
                        text2 = "-5% Attack Cooldown";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 7:
                        text1 = "Kill enemies easier";
                        text2 = "+5% Weapon Damage";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 8:
                        text1 = "Reload Faster";
                        text2 = "-5% Reload Time";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 9:
                        if (!player.hasWeapon2) {
                            text1 = "Unlock new weapon";
                            text2 = "PistolGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-5% Enemy Health";
                        }
                        break;
                    default:
                        text1 = "Error in drawCardText method";
                        text2 = "Error in UpgradeMenu class";
                        break;
                }
                break;
            case 1:
                switch (option) {
                    case 0:
                        text1 = "Increase durability";
                        text2 = "+10% Max Health";
                        break;
                    case 1:
                        text1 = "Collect a lot more";
                        text2 = "sides";
                        text3 = "+10% Xp Gain";
                        break;
                    case 2:
                        text1 = "Move much faster";
                        text2 = "+10% Speed";
                        break;
                    case 3:
                        text1 = "Store many circles";
                        text2 = "+10% Max Ammo";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 4:
                        text1 = "Circles fly to the";
                        text2 = "far lands";
                        text3 = "+10% Range";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 5:
                        text1 = "Circles approach";
                        text2 = "terminal velocity";
                        text3 = "+10% Projectile Speed";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 6:
                        text1 = "Slice & Dice";
                        text2 = "-10% Attack Cooldown";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 7:
                        text1 = "Gain the strength";
                        text2 = "of many sides";
                        text3 = "+10% Weapon Damage";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 8:
                        text1 = "Have you tried";
                        text2 = "switching to another";
                        text3 = "weapon while reloading?";
                        text4 = "-10% Weapon Reload Time";
                        break;
                    case 9:
                        if (!player.hasWeapon3) {
                            text1 = "Unlock new weapon";
                            text2 = "RifleGon";
                        } else if (!player.hasWeapon6){
                            text1 = "Unlock new weapon";
                            text2 = "RevolverGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-10% Enemy Health";
                        }
                        break;
                    default:
                        text1 = "Error in drawCardText method";
                        text2 = "Error in UpgradeMenu class";
                        break;
                }
                break;
            case 2:
                switch (option) {
                    case 0:
                        text1 = "This enchanted apple..";
                        text2 = "..its top notch!";
                        text3 = "+25% Max Health";
                        break;
                    case 1:
                        text1 = "How much more do";
                        text2 = "you need???";
                        text3 = "+25% Xp Gain";
                        break;
                    case 2:
                        text1 = "Feel the wrath of";
                        text2 = "a thousand enemies";
                        text3 = "+25% More Enemies";
                        break;
                    case 3:
                        text1 = "Bottomless barrel";
                        text2 = "or Shulker box?";
                        text3 = "+25% Max Ammo";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 4:
                        text1 = "Circles fly into";
                        text2 = "the backrooms";
                        text3 = "+25% Range";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 5:
                        text1 = "Even Raymond can't";
                        text2 = "out run the circles now";
                        text3 = "+25% Projectile Speed ";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 6:
                        text1 = "MiniGon? No, but close...";
                        text2 = "-25% Weapon Attack Cooldown";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 7:
                        text1 = "Why do they seem so weak";
                        text2 = "+25% Damage";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 8:
                        text1 = "Lets go gambling!";
                        text2 = "+1 Rerolls";
                        break;
                    case 9:
                        if (!player.hasWeapon4) {
                            text1 = "Unlock new weapon";
                            text2 = "SniperGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-15% Enemy Health";
                        }
                        break;
                    default:
                        text1 = "Error in drawCardText method";
                        text2 = "Error in UpgradeMenu class";
                        break;
                }
                break;
            case 3:
                switch (option) {
                    case 0:
                        if (!player.hasWeapon7) {
                            text1 = "Unlock new weapon";
                            text2 = "ExoGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    case 1:
                        text1 = "You feel your shape ";
                        text2 = "growing stronger every kill...";
                        text3 = "+50% Xp Gain";
                        break;
                    case 2:
                        text1 = "Feel the wrath of ";
                        text2 = "a million enemies!";
                        text3 = "+50% More Enemies";
                        break;
                    case 3:
                        text1 = "All the circles in the world!";
                        text2 = "+50% Max Ammo";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 4:
                        if (!player.hasWeapon9) {
                            text1 = "Unlock new weapon";
                            text2 = "LaserGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    case 5:
                        text1 = "One iron, 4 planks";
                        text2 = "Get A Shield";
                        break;
                    case 6:
                        text1 = "Just use a MiniGon at ";
                        text2 = "this point";
                        text3 = "-50% Attack Cooldown";
                        text4 = "To CURRENT Weapon";
                        break;
                    case 7:
                        text1 = "One shot enemies";
                        text2 = "+50% Weapon Damage";
                        text3 = "To CURRENT Weapon";
                        break;
                    case 8:
                        text1 = "Lets go gambling! x3";
                        text2 = "+3 Rerolls";
                        break;
                    case 9:
                        if (!player.hasWeapon5) {
                            text1 = "Unlock new weapon";
                            text2 = "MiniGon";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    default:
                        text1 = "Error in drawCardText method";
                        text2 = "Error in UpgradeMenu class";
                        break;
                }
                break;
            case 4:
                switch (option) {
                    case 0:
                    case 1:
                    case 2:
                        if (!player.hasWeapon8) {
                            text1 = "Unlock new weapon";
                            text2 = "ICBM";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    case 3:
                    case 4:
                    case 5:
                        if (!player.hasWeapon12) {
                            text1 = "Unlock new weapon";
                            text2 = "Sentry";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    case 6:
                    case 7:
                    case 8:
                        if (!player.hasWeapon11) {
                            text1 = "Unlock new weapon";
                            text2 = "Homing missile";
                        } else {
                            text1 = "Weaken your enemies";
                            text2 = "-20% Enemy Health";
                        }
                        break;
                    case 9:
                        text1 = "Nuke";
                        text2 = "Instantly kill all enemies";
                        text3 = "on screen";
                        break;
                    default:
                        text1 = "Error in drawCardText method";
                        text2 = "Error in UpgradeMenu class";
                        break;
                }
                break;
        }

        g2d.drawString(text1, x - g2d.getFontMetrics().stringWidth(text1) / 2, y);
        g2d.drawString(text2, x - g2d.getFontMetrics().stringWidth(text2) / 2, y + 35);
        g2d.drawString(text3, x - g2d.getFontMetrics().stringWidth(text3) / 2, y + 80);
        g2d.drawString(text4, x - g2d.getFontMetrics().stringWidth(text4) / 2, y + 115);
    }

    private void applyUpgrade(int rarity, int option) {
        switch (rarity) {
            case 0:
                switch (option) {
                    case 0: player.playerMaxHealth = (int)((player.playerMaxHealth * 1.05) + 0.5); break;
                    case 1: enemyManager.enemyDroppedXpMultiplier = enemyManager.enemyDroppedXpMultiplier * 1.05; break;
                    case 2: player.playerSpeed = (int)((player.playerSpeed * 1.05) + 0.5); break;
                    case 3: player.activeWeapon.maxAmmo = (int)((player.activeWeapon.maxAmmo * 1.05) + 0.5); break;
                    case 4: player.activeWeapon.range = player.activeWeapon.range * 1.05; break;
                    case 5: player.activeWeapon.bulletSpeed = player.activeWeapon.bulletSpeed * 1.05; break;
                    case 6: player.activeWeapon.shotCooldown = (long)(player.activeWeapon.shotCooldown * 0.95); break;
                    case 7: player.activeWeapon.bulletDamage = player.activeWeapon.bulletDamage * 1.05; break;
                    case 8: player.activeWeapon.ammoReloadCooldown = (long)(player.activeWeapon.ammoReloadCooldown * 0.95); break;
                    case 9:
                        if (!player.hasWeapon2) {
                            player.addWeapon(1); //pistolGon
                            player.hasWeapon2 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.95;
                        }
                        break;

                    default: System.out.println("Error in choice selection");
                }
                break;
            case 1:
                switch (option) {
                    case 0: player.playerMaxHealth = (int)((player.playerMaxHealth * 1.1) + 0.5); break;
                    case 1: enemyManager.enemyDroppedXpMultiplier = enemyManager.enemyDroppedXpMultiplier * 1.1; break;
                    case 2: player.playerSpeed = (int)((player.playerSpeed * 1.1) + 0.5); break;
                    case 3: player.activeWeapon.maxAmmo = (int)((player.activeWeapon.maxAmmo * 1.1) + 0.5); break;
                    case 4: player.activeWeapon.range = player.activeWeapon.range * 1.1; break;
                    case 5: player.activeWeapon.bulletSpeed = player.activeWeapon.bulletSpeed * 1.1; break;
                    case 6: player.activeWeapon.shotCooldown = (long)(player.activeWeapon.shotCooldown * 0.9); break;
                    case 7: player.activeWeapon.bulletDamage = player.activeWeapon.bulletDamage * 1.1; break;
                    case 8: player.activeWeapon.ammoReloadCooldown = (long)(player.activeWeapon.ammoReloadCooldown * 0.9); break;
                    case 9:
                        if (!player.hasWeapon3) {
                            player.addWeapon(2); //rifleGon
                            player.hasWeapon3 = true;
                        } else if (!player.hasWeapon6) {
                            player.addWeapon(5); //revolverGon
                            player.hasWeapon6 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.9;
                        }
                        break;
                    default: System.out.println("Error in choice selection");
                }
                break;
            case 2:
                switch (option) {
                    case 0: player.playerMaxHealth = (int)((player.playerMaxHealth * 1.25) + 0.5); break;
                    case 1: enemyManager.enemyDroppedXpMultiplier = enemyManager.enemyDroppedXpMultiplier * 1.25; break;
                    case 2: break;
                    case 3: player.activeWeapon.maxAmmo = (int)((player.activeWeapon.maxAmmo * 1.25) + 0.5); break;
                    case 4: player.activeWeapon.range = player.activeWeapon.range * 1.25; break;
                    case 5: player.activeWeapon.bulletSpeed = player.activeWeapon.bulletSpeed * 1.25; break;
                    case 6: player.activeWeapon.shotCooldown = (long)(player.activeWeapon.shotCooldown * 0.75); break;
                    case 7: player.activeWeapon.bulletDamage = player.activeWeapon.bulletDamage * 1.25; break;
                    case 8: numberOfRerollsLeft += 1; break;
                    case 9:
                        if (!player.hasWeapon4) {
                            player.addWeapon(3); //sniperGon
                            player.hasWeapon4 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.85;
                        }
                        player.addWeapon(4); break; //sniperGon
                    default: System.out.println("Error in choice selection");
                }
                break;
            case 3:
                switch (option) {
                    case 0:
                        if (!player.hasWeapon7) {
                            player.addWeapon(6); //exoGon
                            player.hasWeapon7 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.8;
                        }
                        break;
                    case 1: enemyManager.enemyDroppedXpMultiplier = enemyManager.enemyDroppedXpMultiplier * 1.5; break;
                    case 2: break;
                    case 3: player.activeWeapon.maxAmmo = (int)((player.activeWeapon.maxAmmo * 1.5) + 0.5); break;
                    case 4:
                        if (!player.hasWeapon9) {
                            player.addWeapon(8); //laserGon
                            player.hasWeapon9 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.8;
                        }
                        break;
                    case 5: break;
                    case 6: player.activeWeapon.shotCooldown = (long)(player.activeWeapon.shotCooldown * 0.5); break;
                    case 7: player.activeWeapon.bulletDamage = player.activeWeapon.bulletDamage * 1.5; break;
                    case 8: numberOfRerollsLeft += 3; break;
                    case 9:
                        if (!player.hasWeapon5) {
                            player.addWeapon(4); //miniGon
                            player.hasWeapon5 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.8;
                        }
                        break;
                    default: System.out.println("Error in choice selection");
                }
                break;
            case 4:
                switch (option) {
                    case 0:
                    case 1:
                    case 2:
                        if (!player.hasWeapon8) {
                            player.addWeapon(7); //ICBM
                            player.hasWeapon8 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.75;
                        }
                        break;
                    case 3:
                    case 4:
                    case 5:
                        if (!player.hasWeapon12) {
                            player.addWeapon(11); //Sentry
                            player.hasWeapon12 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.75;
                        }
                        break;
                    case 6:
                    case 7:
                    case 8:
                        if (!player.hasWeapon11) {
                            player.addWeapon(10); //Homing missile
                            player.hasWeapon11 = true;
                        } else {
                            enemyManager.maxEnemyHealth = enemyManager.maxEnemyHealth * 0.75;
                        }
                        break;
                    case 9: enemyManager.clearEnemies(); break; //nuke
                }
                break;
        }

        player.playerCurrentHealth = player.playerMaxHealth; //heals player
    }

    public void drawCommonUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        //calls method that draws the card frame and checks if the player mouse is in the frame
        drawCardFrame(g2d, x, y, mouseX, mouseY, new Color(148, 148, 148), new Color(171, 178, 209), new Color(57, 59, 70));
        drawCardText(g2d, x, y, "Common", cardOption, rarity);
    }

    public void drawUncommonUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, mouseX, mouseY, new Color(119, 179, 119), new Color(159, 219, 159), new Color(39, 99, 39));
        drawCardText(g2d, x, y, "Uncommon", cardOption, rarity);
    }

    public void drawRareUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, mouseX, mouseY, new Color(100, 149, 237), new Color(140, 189, 255), new Color(20, 69, 157));
        drawCardText(g2d, x, y, "Rare", cardOption, rarity);
    }

    public void drawEpicUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, mouseX, mouseY, new Color(160, 32, 240), new Color(200, 72, 255), new Color(80, 0, 160));
        drawCardText(g2d, x, y, "Epic", cardOption, rarity);
    }

    public void drawLegendaryUpgradeCard(Graphics2D g2d, int x, int y, int mouseX, int mouseY, int cardOption, int rarity) {
        drawCardFrame(g2d, x, y, mouseX, mouseY, new Color(218, 165, 32), new Color(255, 205, 72), new Color(138, 85, 0));
        drawCardText(g2d, x, y, "Legendary", cardOption, rarity);
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
            int gap = 550; //for spacing between cards
            int mouseX = GameMouseInput.mouseX;
            int mouseY = GameMouseInput.mouseY;

            for (int i = 0; i < 3; i++) { //checks for all 3 cards
                int cardX = centerX + (i - 1) * gap; //for spacing
                int cardLeft = cardX - CARD_WIDTH / 2;
                int cardTop = cardY - CARD_HEIGHT / 2;

                if (mouseX >= cardLeft && mouseX <= cardLeft + CARD_WIDTH && mouseY >= cardTop && mouseY <= cardTop + CARD_HEIGHT) {
                    applyUpgrade(cardRarities[i], cardOptions[i]); //calls method that applies the chosen upgrade to the card
                    game.closeUpgradeMenu();
                    return;
                }
            }
        }
        int midX = this.getWidth() / 2;

        int mouseX = GameMouseInput.mouseX;
        int mouseY = GameMouseInput.mouseY;

        if (GameMouseInput.isMouseLeftClickPressed) {
            int rx = midX - BUTTON_WIDTH / 2;
            int ry = REROLL_Y - BUTTON_HEIGHT / 2;
            if ((mouseX >= rx && mouseX <= rx + BUTTON_WIDTH && mouseY >= ry && mouseY <= ry + BUTTON_HEIGHT) && (numberOfRerollsLeft > 0)) {
                numberOfRerollsLeft -= 1;
                setUpgradeMenuVisible(true);
                GameMouseInput.reset();
                GameMouseInput.isMouseLeftClickPressed = false;
                System.out.println("Player rerolled");
                return;
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

        //gets an off-screen canvas of game objects and everything behind the upgrade menu to blur
        BufferedImage rawSource = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D containerGraphics = rawSource.createGraphics();

        //hides upgrade menu overlay to prevent blurring of pause menu
        boolean oldVisibility = this.isVisible;
        this.isVisible = false;
        boolean oldDebugVisibility = false;
        boolean hasDebugMenu = (debugHUD != null);
        if (hasDebugMenu) {
            oldDebugVisibility = debugHUD.isVisible();
            debugHUD.setVisible(false);
        }

        //draws the parent frame container components to the target texture
        targetCanvas.paint(containerGraphics);

        if (hasDebugMenu) {
            debugHUD.setVisible(oldDebugVisibility);
        }

        this.isVisible = oldVisibility; //unhides upgrade menu overlay
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
