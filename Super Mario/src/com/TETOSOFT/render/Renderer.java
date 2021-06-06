package com.TETOSOFT.render;

import com.TETOSOFT.resource.ResourceManager;
import com.TETOSOFT.tilegame.TileMap;
import com.TETOSOFT.tilegame.objects.Enemy;
import com.TETOSOFT.tilegame.objects.Player;
import com.TETOSOFT.tilegame.objects.PowerUp;
import com.TETOSOFT.utils.stringDrawer;

import java.awt.*;

public class Renderer {
    public static Image background;

    public static final int TILE_SIZE = 64;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;

    /**
     * Converts a pixel position to a tile position.
     */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }

    /**
     * Converts a pixel position to a tile position.
     */
    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;
    }

    /**
     * Converts a tile position to a pixel position.
     */
    public static int tilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;
        // use this if the tile size isn't a power of 2:
        // return numTiles * TILE_SIZE;
    }

    public static void renderMainMenu(Graphics2D g, int screenWidth, int screenHeight) {

        //NOTE(Amine): First Try if you have any improvements go ahead
        //g.fillRect(0,0, screenWidth, screenHeight);
        Font font0 = new Font("arial", Font.PLAIN, 25);
        Rectangle welcomeRect = new Rectangle(screenWidth / 2 - 140, screenHeight / 2 - 250, 320, 50);
        Rectangle playBtn = new Rectangle(screenWidth / 2 - 100, screenHeight / 2 - 80, 250, 50);
        Rectangle exitBtn = new Rectangle(screenWidth / 2 - 100, screenHeight / 2 - 10, 250, 50);


        stringDrawer.drawCenteredString(g, "Welcome To Violetio", welcomeRect, font0,Color.BLACK);
        stringDrawer.drawCenteredString(g, "Play (Press Space)", playBtn, font0,Color.RED);
        stringDrawer.drawCenteredString(g, "Exit (Press Q)", exitBtn, font0,Color.RED);

    }

    public static void renderGameOverMenu(Graphics2D g, int screenWidth, int screenHeight, int score) {
        int alfa = 200;
        Color customColor = new Color(0, 0, 0, alfa);
        g.setColor(customColor);
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(Color.RED);
        g.drawString(String.format("Your score is :  %d", score), screenWidth / 2 - 200, screenHeight / 2 -200);

        Font font0 = new Font("arial", Font.PLAIN, 25);
        Rectangle GameOver = new Rectangle(screenWidth / 2 -200, screenHeight / 2 -150, 400, 50);
        stringDrawer.drawCenteredString(g, "Game Over , Try Again (Press R) ", GameOver, font0,Color.RED);
        Rectangle BackBtn = new Rectangle(screenWidth / 2 -150, screenHeight / 2 -70, 300, 50);
        stringDrawer.drawCenteredString(g, "Main Menu (Press ESC)", BackBtn, font0,Color.RED);
        Rectangle exitBtn = new Rectangle(screenWidth / 2 -125, screenHeight / 2 +10, 250, 50);
        stringDrawer.drawCenteredString(g, "Exit (Press Q)", exitBtn, font0,Color.RED);
    }

    static void renderEnemy(Graphics2D g, Enemy enemy, int screenWorldPositionX, int screenWorldPositionY, int screenWidth, int screenHeight, float newSpeed) {
        int enemyScreenX = Math.round(enemy.x) - screenWorldPositionX;
        int enemyScreenY = Math.round(enemy.y) - screenWorldPositionY;

        if (enemyScreenX + enemy.getWidth() >= 0 && enemyScreenX <= screenWidth
                && enemyScreenY + enemy.getHeight() >= 0 && enemyScreenY <= screenHeight) {
            //draw only if visible
            g.drawImage(enemy.getImage(), enemyScreenX, enemyScreenY, null);
            g.setColor(Color.BLUE);
            if (enemy.dx == 0) {
                //wake up
                enemy.dx = newSpeed;
            }
        }
    }

    public static void renderMap(Graphics2D g, TileMap map, int screenWidth, int screenHeight) {
        Player player = map.player;
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        int mapWorldWidth = tilesToPixels(mapWidth);
        int mapWorldHeight = tilesToPixels(mapHeight);
        //NOTE(Mouad): player should be always in the middle of the screen if there is a room for it
        // NOTE(Mouad): screenWorldPosition is the position of the screen in the game world
        int screenWorldPositionX = Math.round(player.x) - (screenWidth / 2);
        int screenWorldPositionY = Math.round(player.y) - (screenHeight / 2);
        screenWorldPositionX = Math.max(0, screenWorldPositionX);
        screenWorldPositionX = Math.min(screenWorldPositionX, mapWorldWidth - screenWidth);
        screenWorldPositionY = Math.max(0, screenWorldPositionY);
        screenWorldPositionY = Math.min(screenWorldPositionY, mapWorldHeight - screenHeight);
        //draw the background if exist
        if (background != null) {
            //parallax effect
            int backgroundWidth = background.getWidth(null);
            int backgroundHeight = background.getHeight(null);
            float scale = (float) screenHeight / backgroundHeight;
            backgroundWidth *= scale;
            //make the background move slower
            int backgroundWorldPositionX = (int) (screenWorldPositionX * -0.5);
            backgroundWorldPositionX %= backgroundWidth;
            g.drawImage(background, backgroundWorldPositionX,0,backgroundWidth, screenHeight, null);
            if (backgroundWorldPositionX + backgroundWidth < screenWorldPositionX + screenWidth)
            {
                g.drawImage(background, backgroundWorldPositionX + backgroundWidth,0,backgroundWidth, screenHeight, null);
            }
        } else {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        //draw tiles
        for (int i = 0; i < mapHeight; ++i) {
            int tileWorldY = tilesToPixels(i);
            for (int j = 0; j < mapWidth; ++j) {
                int tileWorldX = tilesToPixels(j);
                int tileScreenX = tileWorldX - screenWorldPositionX;
                int tileScreenY = tileWorldY - screenWorldPositionY;
                if (tileScreenX + TILE_SIZE >= 0 && tileScreenX <= screenWidth
                        && tileScreenY + TILE_SIZE >= 0 && tileScreenY <= screenHeight)
                    g.drawImage(map.tiles[i][j], tileScreenX, tileScreenY, TILE_SIZE, TILE_SIZE, null);
            }
        }
        //draw coins
        for (int i = 0; i < map.remainingCoins; ++i) {
            PowerUp coin = map.coins[i];
            int coinScreenX = Math.round(coin.x) - screenWorldPositionX;
            int coinScreenY = Math.round(coin.y) - screenWorldPositionY;
            g.drawImage(coin.getImage(), coinScreenX, coinScreenY, null);
        }
        //draw alive and dying grubs
        for (int i = 0; i < map.aliveShrooms + map.dyingShrooms; ++i) {
            renderEnemy(g, map.shrooms[i], screenWorldPositionX, screenWorldPositionY, screenWidth, screenHeight, Enemy.max_shroom_dx);
        }
        //draw alive and dying fly
        for (int i = 0; i < map.aliveFlies + map.dyingFlies; ++i) {
            renderEnemy(g, map.flies[i], screenWorldPositionX, screenWorldPositionY, screenWidth, screenHeight, Enemy.max_fly_dx);
        }
        //draw home
        PowerUp home = map.home;
        int homeScreenX = Math.round(home.x) - screenWorldPositionX;
        int homeScreenY = Math.round(home.y) - screenWorldPositionY;
        g.drawImage(home.getImage(), homeScreenX, homeScreenY, null);
        // draw player
        int playerScreenX = Math.round(player.x) - screenWorldPositionX;
        int playerScreenY = Math.round(player.y) - screenWorldPositionY;
        g.drawImage(player.getImage(),
                playerScreenX, playerScreenY,playerScreenX + player.getWidth(),playerScreenY+ player.getHeight(),
                0,0, player.getWidth(), player.getHeight(),
                null);
    }

    public static void renderHUD(Graphics2D g, int collectedStars, int numLives, int frameCount) {
        g.setColor(Color.WHITE);
        g.drawString("Press ESC for Main Menu.", 10.0f, 20.0f);
        g.setColor(Color.GREEN);
        g.drawString("Coins: " + collectedStars, 300.0f, 20.0f);
        g.setColor(Color.YELLOW);
        g.drawString("Lives: " + (numLives), 500.0f, 20.0f);
        g.setColor(Color.WHITE);
        g.drawString("Home: " + ResourceManager.currentMap, 700.0f, 20.0f);
        g.drawString("frames: " + frameCount, 500.f, 40.f);
    }

    public static void renderWinningGame(Graphics2D g, int screenWidth, int screenHeight, int score) {
        int alfa = 200;
        Color customColor = new Color(0, 0, 0, alfa);
        g.setColor(customColor);
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(Color.RED);
        g.drawString(String.format("Yayyyy You win, Your score is :  %d", score), screenWidth / 2 - 200, screenHeight / 2 -200);

        Font font0 = new Font("arial", Font.PLAIN, 25);
        Rectangle BackBtn = new Rectangle(screenWidth / 2 -150, screenHeight / 2 -70, 300, 50);
        stringDrawer.drawCenteredString(g, "Main Menu (Press ESC)", BackBtn, font0,Color.RED);
        Rectangle exitBtn = new Rectangle(screenWidth / 2 -125, screenHeight / 2 +10, 250, 50);
        stringDrawer.drawCenteredString(g, "Exit (Press Q)", exitBtn, font0,Color.RED);
    }
}
