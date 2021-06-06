package com.TETOSOFT.tilegame;

import java.awt.Image;

//import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.objects.*;
import com.TETOSOFT.render.*;

/**
 * The TileMap class contains the data for a tile-based map, including Sprites.
 * Each tile is a reference to an Image. Of course, Images are used multiple
 * times in the tile map.
 */
public class TileMap {

	public Player player;
	public Enemy[] shrooms;
	public Enemy[] flies;
	public PowerUp[] coins;
	public PowerUp home;

	public int aliveShrooms = 0;
	public int dyingShrooms = 0;
	public int aliveFlies = 0;
	public int dyingFlies = 0;
	public int remainingCoins = 0;

	public Image[][] tiles;

	/**
	 * Creates a new TileMap with the specified width and height (in number of
	 * tiles) of the map.
	 */
	public TileMap(int width, int height, int shroomCount, int flyCount, int coinCount) {
		tiles = new Image[height][width];
		flies = new Enemy[flyCount];
		shrooms = new Enemy[shroomCount];
		coins = new PowerUp[coinCount];
		AddPlayer();
	}


	public void AddShroom(int x, int y){
		shrooms[aliveShrooms++] = Enemy.getShroom(x,y);
	}

	public void AddFly(int x, int y){
		flies[aliveFlies++] = Enemy.getFly(x,y);
	}

	public void AddCoin(int x, int y){
		coins[remainingCoins++] = PowerUp.getCoin(x, y);
	}

	public void AddHome(int x, int y){
		home = PowerUp.getHome(x, y);
	}

	public void AddPlayer(){
		player = new Player();
		player.x = Renderer.tilesToPixels(3);
		player.y = 500;
	}

	/**
	* this function is called when an alive fly is dying
	* @param flyIndex
	 */
	public void killFly(int flyIndex){
		aliveFlies--;
		dyingFlies++;
		Enemy tmpFly = flies[flyIndex];
		flies[flyIndex] = flies[aliveFlies];
		flies[aliveFlies] = tmpFly;
		tmpFly.animation.remainingDieTime = Enemy.EnemyAnimation.DIE_TIME;
		tmpFly.dx = 0;
		//update th animation
		if (tmpFly.animation.currentFrames == Enemy.EnemyAnimation.flyMovingLeftFrames){
			tmpFly.animation.currentFrames = Enemy.EnemyAnimation.flyDyingLeftFrames;
		}
		else{
			tmpFly.animation.currentFrames = Enemy.EnemyAnimation.flyDyingRightFrames;
		}
		tmpFly.resetAnimation();
	}

	/**
	* this function is called when an alive grub is dying
	* @param grubIndex
	 */
	public void killShroom(int grubIndex){
		aliveShrooms--;
		dyingShrooms++;
		Enemy tmpGrub = shrooms[grubIndex];
		shrooms[grubIndex] = shrooms[aliveShrooms];
		shrooms[aliveShrooms] = tmpGrub;
		tmpGrub.animation.remainingDieTime = Enemy.EnemyAnimation.DIE_TIME;
		//update grub animation
		if (tmpGrub.animation.currentFrames == Enemy.EnemyAnimation.shroomMovingLeftFrames){
			tmpGrub.animation.currentFrames = Enemy.EnemyAnimation.shroomDyingLeftFrames;
		}
		else{
			tmpGrub.animation.currentFrames = Enemy.EnemyAnimation.shroomDyingRightFrames;
		}
	}

	/**
	* this function is called when a dying fly finally died
	* @param flyIndex
	 */
	public void flyDied(int flyIndex){
		dyingFlies--;
		Enemy tmpFly = flies[flyIndex];
		int lastDyingFlyIndx = aliveFlies + dyingFlies;
		flies[flyIndex] = flies[lastDyingFlyIndx];
		flies[lastDyingFlyIndx] = tmpFly;
	}

	/**
	* this function is called when a dying grub finally died
	* @param grubIndex
	 */
	public void shroomDied(int grubIndex){
		dyingShrooms--;
		Enemy tmpGrub = shrooms[grubIndex];
		int lastDyingGrubIndx = aliveShrooms + dyingShrooms;
		shrooms[grubIndex] = shrooms[lastDyingGrubIndx];
		shrooms[lastDyingGrubIndx] = tmpGrub;
	}

	public void collectCoin(int coinIndex){
		PowerUp tmpCoin = coins[coinIndex];
		remainingCoins--;
		coins[coinIndex] = coins[remainingCoins];
		coins[remainingCoins] = tmpCoin;
	}

	/**
	 * Gets the width of this TileMap (number of tiles across).
	 */
	public int getWidth() {
		return tiles[0].length;
	}

	/**
	 * Gets the height of this TileMap (number of tiles down).
	 */
	public int getHeight() {
		return tiles.length;
	}

	/**
	 * Gets the tile at the specified location. Returns null if no tile is at the
	 * location or if the location is out of bounds.
	 */
	public Image getTile(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return null;
		} else {
			return tiles[y][x];
		}
	}

	/**
	 * Sets the tile at the specified location.
	 */
	public void setTile(int x, int y, Image tile) {
		tiles[y][x] = tile;
	}
}
