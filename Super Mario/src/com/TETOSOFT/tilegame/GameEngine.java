package com.TETOSOFT.tilegame;

import com.TETOSOFT.core.GameCore;
import com.TETOSOFT.input.InputManager;
import com.TETOSOFT.render.Renderer;
import com.TETOSOFT.resource.ResourceManager;
import com.TETOSOFT.tilegame.objects.Enemy;
import com.TETOSOFT.tilegame.objects.Player;
import com.TETOSOFT.tilegame.objects.PowerUp;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.TETOSOFT.resource.ResourceManager.MAP_COUNT;
import static com.TETOSOFT.resource.ResourceManager.currentMap;


/**
 * GameManager manages all parts of the game.
 */
public class GameEngine extends GameCore {

    public static void main(String[] args) {
        new GameEngine().run();
    }

    public static final float GRAVITY = 0.002f;

    private TileMap map;
    private InputManager inputManager;

    private int collectedStars = 0;

    private int realNumLives = 5;
    private int numLives = realNumLives;

    public void init() {
        super.init();
        // set up input manager
        initInput();
        ResourceManager.gc = screen.getFullScreenWindow().getGraphicsConfiguration();
        ResourceManager.InitImages();
        Renderer.background = ResourceManager.LoadBackground("sky_background.png");
        // load first map
        map = ResourceManager.LoadMap();
        state = GameState.MAIN_MENU;
    }

    /**
     * Closes any resurces used by the GameManager.
     */
    public void stop() {
        super.stop();
    }

    private void initInput() {
        inputManager = new InputManager(screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
    }

    private void checkInput() {
        boolean[] keys = inputManager.keyboardState;
        if (keys[KeyEvent.VK_ESCAPE]) {
            state = GameState.MAIN_MENU;
        }
        Player player = map.player;
        float velocityX = 0.f;
        if (keys[KeyEvent.VK_LEFT]) {
            velocityX -= player.max_dx;
            //update the
            if (Player.PlayerAnimation.currentFrames != Player.PlayerAnimation.movingLeftFrames) {
                player.moveLeft();
            }
        }
        else if (Player.PlayerAnimation.currentFrames == Player.PlayerAnimation.movingLeftFrames) {
                player.idle();
        }

        if (keys[KeyEvent.VK_RIGHT]) {
            velocityX += player.max_dx;
            if (Player.PlayerAnimation.currentFrames != Player.PlayerAnimation.movingRightFrames) {
                player.moveRight();
            }
        }
        else if (Player.PlayerAnimation.currentFrames == Player.PlayerAnimation.movingRightFrames) {
                player.idle();
        }

        if (keys[KeyEvent.VK_SPACE]) {
            player.jump();
        }
        player.dx = velocityX;
    }


    public void drawMainMenu(Graphics2D g) {
        Renderer.renderMap(g, map, screen.getWidth(), screen.getHeight());
        Renderer.renderMainMenu(g, screen.getWidth(), screen.getHeight());
    }


    @Override
    public void checkMainMenuInput() {
        boolean[] keys = inputManager.keyboardState;

        if (keys[KeyEvent.VK_SPACE] && state == GameState.MAIN_MENU) {
            state = GameState.GAME_RUNNING;
            inputManager.clearKeysState();
        }
        if (keys[KeyEvent.VK_Q]) {
            stop();
            inputManager.clearKeysState();
        }
        if(keys[KeyEvent.VK_ESCAPE] && state == GameState.GAME_OVER){
            state = GameState.MAIN_MENU;
            numLives=realNumLives;
            collectedStars=0;
            ResourceManager.currentMap=1;
            map = ResourceManager.LoadMap();
            inputManager.clearKeysState();
        }
        if(keys[KeyEvent.VK_R] && state==GameState.GAME_OVER){
            state = GameState.GAME_RUNNING;
            numLives=realNumLives;
            collectedStars=0;
            ResourceManager.currentMap=1;
            map = ResourceManager.LoadMap();
            inputManager.clearKeysState();
        }
        if(keys[KeyEvent.VK_ESCAPE] && state == GameState.WiningGame){
            state = GameState.MAIN_MENU;
            numLives=realNumLives;
            collectedStars=0;
            ResourceManager.currentMap=1;
            map = ResourceManager.LoadMap();
            inputManager.clearKeysState();
        }
    }
    public void drawWinningGame(Graphics2D g){
        Renderer.renderMap(g, map, screen.getWidth(), screen.getHeight());
        Renderer.renderWinningGame(g, screen.getWidth(), screen.getHeight(),collectedStars);
    }

    public void drawGameOverMenu(Graphics2D g) {
        Renderer.renderMap(g, map, screen.getWidth(), screen.getHeight());
        Renderer.renderGameOverMenu(g, screen.getWidth(), screen.getHeight(),collectedStars);
    }

    public void drawGame(Graphics2D g) {
        Renderer.renderMap(g, map, screen.getWidth(), screen.getHeight());
        Renderer.renderHUD(g, collectedStars, numLives, frameCount);
    }

    Point CheckTileCollision(Box box, float newX, float newY) {
        float fromX = Math.min(box.x, newX);
        float fromY = Math.min(box.y, newY);
        float toX = Math.max(box.x, newX);
        float toY = Math.max(box.y, newY);
        // get the tile locations
        int fromTileX = Renderer.pixelsToTiles(fromX);
        int fromTileY = Renderer.pixelsToTiles(fromY);
        int toTileX = Renderer.pixelsToTiles(toX + box.width - 1);
        int toTileY = Renderer.pixelsToTiles(toY + box.height - 1);
        // check each tile for a collision
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
                    // collision found, return the tile
                    return new Point(x, y);
                }
            }
        }
        // no collision found
        return null;
    }

    /**
     * @param enemy
     * @param elapsedTime
     * @return true if a horizontal collision happened
     */
    boolean ChangeEnemyX(Enemy enemy, long elapsedTime) {
        float newX = enemy.x + enemy.dx * elapsedTime;
        Box b = new Box();
        b.x = enemy.x;
        b.y = enemy.y;
        b.width = enemy.getWidth();
        b.height = enemy.getHeight();
        Point tileCollision = CheckTileCollision(b, newX, b.y);
        if (tileCollision == null) {
            enemy.x = newX;
            b.x = newX;
        } else {
            if (enemy.dx >= 0) {
                enemy.x = Renderer.tilesToPixels(tileCollision.x) - b.width;
            } else {
                enemy.x = Renderer.tilesToPixels(tileCollision.x + 1);
            }
            return true;
        }
        return false;
    }

    void ChangeEnemyY(Enemy enemy, long elapsedTime) {
        float newY = enemy.y + elapsedTime * enemy.dy;
        Box b = new Box();
        b.x = enemy.x;
        b.y = enemy.y;
        b.width = enemy.getWidth();
        b.height = enemy.getHeight();
        Point tileCollision = CheckTileCollision(b, b.x, newY);
        if (tileCollision == null) {
            enemy.y = newY;
        } else {
            if (enemy.dy > 0) {
                enemy.y = Renderer.tilesToPixels(tileCollision.y) - enemy.getHeight();
            } else {
                enemy.y = Renderer.tilesToPixels(tileCollision.y + 1);
            }
            enemy.dy = 0;
        }
    }

    void ChangeEnemyHorizontalDirection(Enemy enemy, long elapsedTime, Image[] left, Image[] right) {
        if (ChangeEnemyX(enemy, elapsedTime)) {
            if (enemy.dx < 0) {
                enemy.animation.currentFrames = right;
            } else {
                enemy.animation.currentFrames = left;
            }
            enemy.resetAnimation();
            enemy.dx *= -1;
        }
    }

    void updateAliveGrub(Enemy grub, long elapsedTime) {
        //apply gravity
        grub.dy += GRAVITY * elapsedTime;
        ChangeEnemyHorizontalDirection(grub, elapsedTime, Enemy.EnemyAnimation.shroomMovingLeftFrames, Enemy.EnemyAnimation.shroomMovingRightFrames);
        ChangeEnemyY(grub, elapsedTime);
        //update animation
        grub.update(elapsedTime);
    }

    void updateAliveFlies(Enemy fly, long elapsedTime) {
        //NOTE(Mouad): flies don't fall, so only change their X
        ChangeEnemyX(fly, elapsedTime);
        ChangeEnemyHorizontalDirection(fly, elapsedTime, Enemy.EnemyAnimation.flyMovingLeftFrames, Enemy.EnemyAnimation.flyMovingRightFrames);
        fly.update(elapsedTime);
    }

    /**
     * @return true if the player dies or the game is over, else false
     */
    boolean CheckPlayerCollision() {
        Player player = map.player;
        //check if collided with coin
        for (int i = 0; i < map.remainingCoins; ++i) {
            PowerUp coin = map.coins[i];
            if (player.x + player.getWidth() >= coin.x
                    && coin.x + coin.getWidth() >= player.x
                    && player.y + player.getHeight() >= coin.y
                    && coin.y + coin.getHeight() >= player.y) {
                map.collectCoin(i);
                collectedStars++;
            }
        }
        //check if collided with an alive grub
        for (int i = 0; i < map.aliveShrooms; ++i) {
            Enemy grub = map.shrooms[i];
            if (player.x + player.getWidth() >= grub.x
                    && grub.x + grub.getWidth() >= player.x
                    && player.y + player.getHeight() >= grub.y
                    && grub.y + grub.getHeight() >= player.y) {
                //collision
                if (player.y + player.weakSpotHeight >= grub.y) {
                    //player dies
                    numLives--;
                    player.die();
                    if (numLives > 0) {
                        state = GameState.PLAYER_DYING;
                    } else {
                        state = GameState.GAME_OVER;

                    }
                } else {
                    //player kills grub
                    map.killShroom(i);
                    player.ForceJump();
                }
                return true;
            }
        }
        // check if collided with an alive fly
        for (int i = 0; i < map.aliveFlies; ++i) {
            Enemy fly = map.flies[i];
            if (player.x + player.getWidth() >= fly.x
                    && fly.x + fly.getWidth() >= player.x
                    && player.y + player.getHeight() >= fly.y
                    && fly.y + fly.getHeight() >= player.y) {
                //collision
                if (player.y + player.weakSpotHeight >= fly.y) {
                    //player dies
                    numLives--;
                    player.die();
                    if (numLives > 0) {
                        state = GameState.PLAYER_DYING;

                    } else {
                        state = GameState.GAME_OVER;

                    }
                } else {
                    //player kills grub
                    map.killFly(i);
                    player.ForceJump();
                }
                return true;
            }
        }
        //check for collision with home

        PowerUp home = map.home;
        if (player.x + player.getWidth() >= home.x
                && home.x + home.getWidth() >= player.x
                && player.y + player.getHeight() >= home.y
                && home.y + home.getHeight() >= player.y) {
            if(currentMap<MAP_COUNT)
                currentMap++;
            else state=GameState.WiningGame;
            map = ResourceManager.LoadMap();

        }
        return false;
    }

    /**
     * @param fly
     * @param elapsedTime
     * @return true if the dyign fly finally died, else return false
     */
    boolean updateDyingFlies(Enemy fly, long elapsedTime) {
        //NOTE(Mouad): dying flies can't fly..  so apply gravity
        fly.dy += GRAVITY * elapsedTime;
        ChangeEnemyY(fly, elapsedTime);
        fly.animation.remainingDieTime -= elapsedTime;
        if (fly.animation.remainingDieTime <= 0) {
            return true;
        }
        //update animation
        fly.update(elapsedTime);
        return false;
    }

    /**
     * @param grub
     * @param elapsedTime
     * @return true if the dyign grub finally died, else return false
     */
    boolean updateDyingGrub(Enemy grub, long elapsedTime) {
        //NOTE(Mouad): dying grubs are not moving
        grub.animation.remainingDieTime -= elapsedTime;
        if (grub.animation.remainingDieTime <= 0) {
            return true;
        }
        //update animation
        grub.update(elapsedTime);
        return false;
    }

    void ChangePlayerX(long elapsedTime) {
        Player player = map.player;
        float newX = player.x + player.dx * elapsedTime;
        Box b = player.getBox();
        Point tileCollision = CheckTileCollision(b, newX, b.y);
        if (tileCollision == null) {
            player.x = newX;
            b.x = newX;
        } else {
            if (player.dx >= 0) {
                player.x = Renderer.tilesToPixels(tileCollision.x) - player.getWidth();
            } else {
                player.x = Renderer.tilesToPixels(tileCollision.x + 1);
            }
            b.x = player.x;
        }
    }

    void ChangePlayerY(long elapsedTime) {
        Player player = map.player;
        Box b = player.getBox();
        float newY = player.y + elapsedTime * player.dy;
        Point tileCollision = CheckTileCollision(b, b.x, newY);
        if (tileCollision == null) {
            player.y = newY;
        } else {
            if (player.dy > 0) {
                player.y = Renderer.tilesToPixels(tileCollision.y) - player.getHeight();
            } else {
                player.y = Renderer.tilesToPixels(tileCollision.y + 1);
            }
            if (player.dy > 0) {
                player.StopJump();
            }
            player.dy = 0;
        }
    }

    void updatePlayer(long elapsedTime) {
        //apply gravity
        Player player = map.player;
        player.dy += GRAVITY * elapsedTime;
        ChangePlayerX(elapsedTime);
        if (CheckPlayerCollision()) return;
        ChangePlayerY(elapsedTime);
        CheckPlayerCollision();
    }

    void updateDyingPlayer(long elapsedTime) {
        Player player = map.player;
        Player.PlayerAnimation.remainingDieTime -= elapsedTime;
        if (Player.PlayerAnimation.remainingDieTime <= 0) {
            Player.PlayerAnimation.currentFrameIndex = 0;
            state = GameState.GAME_RUNNING;
            //ResourceManager.currentMap = 1;
            map = ResourceManager.LoadMap();
            return;
        }
        //apply gravity
        player.dy += GRAVITY * elapsedTime;
        ChangePlayerY(elapsedTime);
    }

    public void update(long elapsedTime) {
        // NOTE(Mouad): player is still alive
        checkInput();
        updatePlayer(elapsedTime);
        updateWorld(elapsedTime);
    }

    public void updateDying(long elapsedTime) {
        updateDyingPlayer(elapsedTime);
        updateWorld(elapsedTime);
    }

    void updateWorld(long elapsedTime) {
        map.player.updateAnimation(elapsedTime);
        //update living enemies
        //NOTE(Mouad): we update the position and then we check for any collision with tiles,
        //we also update the animation
        //update living grubs
        for (int i = 0; i < map.aliveShrooms; ++i) {
            updateAliveGrub(map.shrooms[i], elapsedTime);
        }
        //update living flies
        for (int i = 0; i < map.aliveFlies; ++i) {
            updateAliveFlies(map.flies[i], elapsedTime);
        }
        //update the dying flies
        for (int i = map.aliveFlies; i < map.aliveFlies + map.dyingFlies; ) {
            //NOTE(Mouad): dying flies are subject to gravity
            if (updateDyingFlies(map.flies[i], elapsedTime)) {
                map.flyDied(i);
            } else {
                i++;
            }
        }
        for (int i = map.aliveShrooms; i < map.aliveShrooms + map.dyingShrooms; ) {
            //NOTE(Mouad): dying grubs are always on ground, so no need to apply gravity
            // or check for collision with the tiles
            if (updateDyingGrub(map.shrooms[i], elapsedTime)) {
                map.shroomDied(i);
            } else {
                i++;
            }
        }
        //update the remaining coins
        for (int i = 0; i < map.remainingCoins; ++i) {
            map.coins[i].update(elapsedTime);
        }
    }
}
