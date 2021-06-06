package com.TETOSOFT.input;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit()
			.createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible");

	public static final int MOUSE_MOVE_LEFT = 0;
	public static final int MOUSE_MOVE_RIGHT = 1;
	public static final int MOUSE_MOVE_UP = 2;
	public static final int MOUSE_MOVE_DOWN = 3;
	public static final int MOUSE_WHEEL_UP = 4;
	public static final int MOUSE_WHEEL_DOWN = 5;
	public static final int MOUSE_BUTTON_1 = 6;
	public static final int MOUSE_BUTTON_2 = 7;
	public static final int MOUSE_BUTTON_3 = 8;

	private static final int NUM_MOUSE_CODES = 9;

	private static final int NUM_KEY_CODES = 600;

	public boolean[] keyboardState = new boolean[NUM_KEY_CODES];
	private boolean[] mouseState = new boolean[NUM_MOUSE_CODES];

	private Point mouseLocation;
	private Point centerLocation;
	private Component comp;
	private Robot robot;
	private boolean isRecentering;

	public InputManager(Component comp) {
		this.comp = comp;
		mouseLocation = new Point();
		centerLocation = new Point();

		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		comp.addMouseWheelListener(this);

		comp.setFocusTraversalKeysEnabled(false);
	}

	public void setCursor(Cursor cursor) {
		comp.setCursor(cursor);
	}

	public void setRelativeMouseMode(boolean mode) {
		if (mode == isRelativeMouseMode()) {
			return;
		}

		if (mode) {
			try {
				robot = new Robot();
				recenterMouse();
			} catch (AWTException ex) {

				robot = null;
			}
		} else {
			robot = null;
		}
	}

	public boolean isRelativeMouseMode() {
		return (robot != null);
	}

	public int getMouseX() {
		return mouseLocation.x;
	}

	public int getMouseY() {
		return mouseLocation.y;
	}

	private synchronized void recenterMouse() {
		if (robot != null && comp.isShowing()) {
			centerLocation.x = comp.getWidth() / 2;
			centerLocation.y = comp.getHeight() / 2;
			SwingUtilities.convertPointToScreen(centerLocation, comp);
			isRecentering = true;
			robot.mouseMove(centerLocation.x, centerLocation.y);
		}
	}

	public static int getMouseButtonCode(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				return MOUSE_BUTTON_1;
			case MouseEvent.BUTTON2:
				return MOUSE_BUTTON_2;
			case MouseEvent.BUTTON3:
				return MOUSE_BUTTON_3;
			default:
				return -1;
		}
	}

	// from the KeyListener interface
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode < NUM_KEY_CODES){
			keyboardState[keyCode] = true;
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}

	// from the KeyListener interface
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode < NUM_KEY_CODES){
			keyboardState[keyCode] = false;
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}

	// from the KeyListener interface
	public void keyTyped(KeyEvent e) {
		// make sure the key isn't processed for anything else
		e.consume();
	}

	// from the MouseListener interface
	public void mousePressed(MouseEvent e) {
		int mouseCode = getMouseButtonCode(e);
		if (mouseCode >= 0){
			mouseState[mouseCode] = true;
		}
	}

	// from the MouseListener interface
	public void mouseReleased(MouseEvent e) {
		int mouseCode = getMouseButtonCode(e);
		if (mouseCode >= 0){
			mouseState[mouseCode] = false;
		}
	}

	// from the MouseListener interface
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	// from the MouseListener interface
	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	// from the MouseListener interface
	public void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}

	// from the MouseMotionListener interface
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	// from the MouseMotionListener interface
	public synchronized void mouseMoved(MouseEvent e) {
		if (isRecentering && centerLocation.x == e.getX() && centerLocation.y == e.getY()) {
			isRecentering = false;
		} else {
			if (isRelativeMouseMode()) {
				recenterMouse();
			}
		}

		mouseLocation.x = e.getX();
		mouseLocation.y = e.getY();

	}

	// from the MouseWheelListener interface
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	public void clearKeysState() {
		Arrays.fill(keyboardState, false);
		Arrays.fill(mouseState, false);
	}
}
