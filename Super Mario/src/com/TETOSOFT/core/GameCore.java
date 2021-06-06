package com.TETOSOFT.core;

import java.awt.*;
import javax.swing.ImageIcon;

import com.TETOSOFT.graphics.ScreenManager;
import com.TETOSOFT.resource.ResourceManager;

/**
 * Simple abstract class used for testing. Subclasses should implement the
 * draw() method.
 */
public abstract class GameCore {
	public enum GameState{
		MAIN_MENU,
		GAME_RUNNING,
		PLAYER_DYING,
		GAME_OVER,
		WiningGame
	};
	protected static final int FONT_SIZE = 18;
	static private final int FRAME_TARGET = 150;

	private static final DisplayMode POSSIBLE_MODES[] = {
		new DisplayMode(800, 600, 32, 0),
		new DisplayMode(800, 600, 16, 0),
		new DisplayMode(800, 600, 24, 0),
		new DisplayMode(640, 480, 16, 0),
		new DisplayMode(640, 480, 32, 0),
		new DisplayMode(640, 480, 24, 0),
		new DisplayMode(1024, 768, 16, 0),
		new DisplayMode(1024, 768, 32, 0),
		new DisplayMode(1024, 768, 24, 0)
	};

	private boolean isRunning;
	protected ScreenManager screen;
	protected GameState state;

	//for debug purpose only
	protected int frameCount = 0;

	/**
	 * Signals the game loop that it's time to quit
	 */
	public void stop() {
		isRunning = false;
	}

	/**
	 * Calls init() and gameLoop()
	 */
	public void run() {
		try {
			init();
			gameLoop();
		} finally {
			screen.restoreScreen();
			lazilyExit();
		}
	}

	/**
	 * Exits the VM from a daemon thread. The daemon thread waits 2 seconds then
	 * calls System.exit(0). Since the VM should exit when only daemon threads are
	 * running, this makes sure System.exit(0) is only called if neccesary. It's
	 * neccesary if the Java Sound system is running.
	 */
	public void lazilyExit() {
		Thread thread = new Thread() {
			public void run() {
				// first, wait for the VM exit on its own.
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
				}
				// system is still running, so force an exit
				System.exit(0);
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Sets full screen mode and initiates and objects.
	 */
	public void init() {
		System.setProperty("sun.java2d.opengl", "true");
		screen = new ScreenManager();
		DisplayMode displayMode = screen.findFirstCompatibleMode(POSSIBLE_MODES);
		screen.setFullScreen(displayMode);

		Window window = screen.getFullScreenWindow();
		window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
		window.setBackground(Color.BLACK);
		window.setForeground(Color.BLACK);

		isRunning = true;
	}

	public Image loadImage(String fileName) {
		return new ImageIcon(fileName).getImage();
	}


	/**
	 * Runs through the game loop until stop() is called.
	 */
	public void gameLoop() {
		long startTime = System.currentTimeMillis();
		long currTime = startTime;
		long secondCountdown = 1000;
		int currentFrameCount = 0;
		long timePerFrame = 1000 / FRAME_TARGET;
		while (isRunning) {
			long elapsedTime = System.currentTimeMillis() - currTime;
			currTime = System.currentTimeMillis();
			switch (state)
			{
				case GAME_RUNNING:
					{
						update(elapsedTime);
						// draw the screen
						Graphics2D g = screen.getGraphics();
						drawGame(g);
						g.dispose();
						screen.update();
						//update the frame couting logic
						secondCountdown -= elapsedTime;
						currentFrameCount++;
						if (secondCountdown <= 0){
							secondCountdown = 1000;
							frameCount = currentFrameCount;
							currentFrameCount = 0;
						}
					} break;
				case PLAYER_DYING:
					{
						updateDying(elapsedTime);
						Graphics2D g = screen.getGraphics();
						drawGame(g);
						g.dispose();
						screen.update();
						//update the frame couting logic
						secondCountdown -= elapsedTime;
						currentFrameCount++;
						if (secondCountdown <= 0){
							secondCountdown = 1000;
							frameCount = currentFrameCount;
							currentFrameCount = 0;
						}
					} break;
				case GAME_OVER :
					{
						Graphics2D g = screen.getGraphics();
						checkMainMenuInput();
						drawGameOverMenu(g);
						g.dispose();
						screen.update();

					} break;
				case MAIN_MENU: {
					//DO menu stuff here
					Graphics2D g = screen.getGraphics();
					checkMainMenuInput();
					drawMainMenu(g);
					g.dispose();
					screen.update();
				}break;
				case WiningGame:{
					Graphics2D g = screen.getGraphics();
					checkMainMenuInput();
					drawWinningGame(g);
					g.dispose();
					screen.update();
				}break;
			}
			elapsedTime = System.currentTimeMillis() - currTime;


			// don't take a nap! run as fast as possible
			if (timePerFrame > elapsedTime) {
				try {
					Thread.sleep(timePerFrame - elapsedTime);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	protected abstract void drawWinningGame(Graphics2D g);


	/**
	 * Updates the state of the game/animation based on the amount of elapsed time
	 * that has passed, this function will be called only when the player is alive
	 */
	public void update(long elapsedTime) {
		// do nothing
	}

	/**
	 * this function will do the update logic when the player is dying
	 * */
	public void updateDying(long elapsedTime){
		// do nothing 
	}

	/**
	 * Draws to the screen. Subclasses must override this method.
	 */
	public abstract void drawGame(Graphics2D g);
	public abstract void drawGameOverMenu(Graphics2D g);
	public abstract void drawMainMenu(Graphics2D g);
	public abstract void checkMainMenuInput();
}
