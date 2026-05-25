package Project;

import java.awt.event.*;

public class GameMouseInput implements MouseMotionListener, MouseListener, MouseWheelListener{

    public static int mouseX = 0;
    public static int mouseY = 0;
    public static boolean isMouseLeftClick = false;
    public static boolean isMouseRightClick = false;
    public static boolean isMouseLeftClickPressed = false;
    public static boolean isMouseLeftClickReleased = false;
    public static boolean isMouseScrolledUp = false;
    public static boolean isMouseScrolledDown = false;

    public static void reset() {
        isMouseLeftClick = false;
        isMouseRightClick = false;
        isMouseLeftClickReleased = false;
        isMouseScrolledUp = false;
        isMouseScrolledDown = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isMouseLeftClick = true;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            isMouseRightClick = true;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isMouseLeftClickPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isMouseLeftClickPressed = false;
            isMouseLeftClickReleased = true;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            isMouseScrolledUp = true;
        } else {
            isMouseScrolledDown = true;
        }
    }
}
