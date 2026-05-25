package Project;

import java.awt.event.*;

public class GameKeyInput implements KeyListener {

    private PolyGone mainGame; //reference to main game class

    //constructor that references to main game class
    public GameKeyInput(PolyGone mainGame) {
        this.mainGame = mainGame;
    }

    @Override
    public void keyTyped(KeyEvent e) {} //unused but necessary for the listener interface that is implemented

    @Override
    public void keyPressed(KeyEvent e) {
        //calls setKeyState and tells it that a key has been pressed
        mainGame.setKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //calls setKeyState and tells it that a key has been released
        mainGame.setKeyState(e.getKeyCode(), false);
    }
}