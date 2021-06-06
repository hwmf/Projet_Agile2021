package com.TETOSOFT.resource;

import com.TETOSOFT.tilegame.TileMap;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import com.TETOSOFT.tilegame.objects.*;

//this class will be responsible to load sprites and levels
public class ResourceManager {
	public static int currentMap = 1;
	public static int MAP_COUNT = 4;
	public static GraphicsConfiguration gc;
	public static Image[] tiles;
	public static int tileImagesCount = 'K' - 'A' + 1;

	public static Image LoadImage(String name){
		String filename = "images/" + name;
		return new ImageIcon(filename).getImage();
	}

	public static Image getMirrorImage(Image image) {
		return getScaledImage(image, -1, 1);
	}

	public static Image getFlippedImage(Image image) {
		return getScaledImage(image, 1, -1);
	}

	public static Image LoadBackground(String name)
	{
		String filename = "Assets/backgrounds/" + name;
		return new ImageIcon(filename).getImage();
	}

	private static Image getScaledImage(Image image, float x, float y) {
		// set up the transform
		AffineTransform transform = new AffineTransform();
		transform.scale(x, y);
		transform.translate((x - 1) * image.getWidth(null) / 2, (y - 1) * image.getHeight(null) / 2);
		// create a transparent (not translucent) image
		Image newImage = gc.createCompatibleImage(image.getWidth(null),
												  image.getHeight(null),
												  Transparency.BITMASK);
		// draw the transformed image
		Graphics2D g = (Graphics2D) newImage.getGraphics();
		g.drawImage(image, transform, null);
		g.dispose();
		return newImage;
	}

	public static void InitImages(){
		//load player images
		int playerIdleFrameCount = 8;
		int playerDyingFrameCount = 13;
		int playerMovingFrameCount = 14;
		//NOTE: player will have fixed size
		Player.width = 35;
		Player.height = 70;
		Player.PlayerAnimation.idleLeftFrames = new Image[playerIdleFrameCount];
		Player.PlayerAnimation.idleRightFrames = new Image[playerIdleFrameCount];
		Player.PlayerAnimation.dyingLeftFrames = new Image[playerDyingFrameCount];
		Player.PlayerAnimation.dyingRightFrames = new Image[playerDyingFrameCount];
		Player.PlayerAnimation.movingLeftFrames = new Image[playerMovingFrameCount];
		Player.PlayerAnimation.movingRightFrames = new Image[playerMovingFrameCount];
		for (int frame = 0; frame < playerIdleFrameCount; ++frame)
		{
			String fileName = "Assets/player/idle/" + (frame + 1) + ".png";

			Image playerImage = new ImageIcon(fileName).getImage();
			Image newImage = gc.createCompatibleImage(Player.width, Player.height,Transparency.BITMASK);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(playerImage, 0,0, Player.width,Player.height, null);
			g.dispose();

			Player.PlayerAnimation.idleRightFrames[frame] = newImage; 
			Player.PlayerAnimation.idleLeftFrames[frame] =getMirrorImage(newImage);
		}
		//Dying animation
		for (int frame = 0; frame < playerDyingFrameCount; ++frame)
		{
			String fileName = "Assets/player/death/" + (frame + 1) + ".png";

			Image playerImage = new ImageIcon(fileName).getImage();
			Image newImage = gc.createCompatibleImage(Player.width, Player.height,Transparency.BITMASK);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(playerImage, 0,0, Player.width,Player.height, null);
			g.dispose();

			Player.PlayerAnimation.dyingRightFrames[frame] = newImage;
			Player.PlayerAnimation.dyingLeftFrames[frame] =getMirrorImage(newImage);
		}
		Player.PlayerAnimation.DIE_TIME = Player.PlayerAnimation.frameDuration * playerDyingFrameCount;

		//Moving animation
		for (int frame = 0; frame < playerMovingFrameCount; ++frame)
		{
			String fileName = "Assets/player/walk/" + (frame + 1) + ".png";

			Image playerImage = new ImageIcon(fileName).getImage();
			Image newImage = gc.createCompatibleImage(Player.width, Player.height,Transparency.BITMASK);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(playerImage, 0,0, Player.width,Player.height, null);
			g.dispose();

			Player.PlayerAnimation.movingRightFrames[frame] = newImage;
			Player.PlayerAnimation.movingLeftFrames[frame] =getMirrorImage(newImage);
		}
		Player.PlayerAnimation.jumpingLeftFrames = Player.PlayerAnimation.idleLeftFrames;
		Player.PlayerAnimation.jumpingRightFrames = Player.PlayerAnimation.idleRightFrames;
		//load grub images
		int shroomsMovingFrameCount = 6;
		Enemy.EnemyAnimation.shroomMovingLeftFrames = new Image[shroomsMovingFrameCount];
		for (int frame = 0; frame < shroomsMovingFrameCount; ++frame)
		{
			String fileName = "Assets/enemies/Shroom/blue/" + (frame + 1) + ".png";
			Image shroomImage = new ImageIcon(fileName).getImage();
			int width  = 50;
			int height = 50;
			Image newImage = gc.createCompatibleImage(width, height,Transparency.BITMASK);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(shroomImage, 0,0, width,height, null);
			g.dispose();

			Enemy.EnemyAnimation.shroomMovingLeftFrames[frame] = newImage;
		}

		Enemy.EnemyAnimation.shroomMovingRightFrames = new Image[shroomsMovingFrameCount];
		Enemy.EnemyAnimation.shroomDyingLeftFrames = new Image[shroomsMovingFrameCount];
		Enemy.EnemyAnimation.shroomDyingRightFrames = new Image[shroomsMovingFrameCount];
		for (int i = 0; i < shroomsMovingFrameCount; ++i){
			Enemy.EnemyAnimation.shroomMovingRightFrames[i] =
				getMirrorImage(Enemy.EnemyAnimation.shroomMovingLeftFrames[i]);
			Enemy.EnemyAnimation.shroomDyingLeftFrames[i] =
				getFlippedImage(Enemy.EnemyAnimation.shroomMovingLeftFrames[i]);
			Enemy.EnemyAnimation.shroomDyingRightFrames[i] =
				getFlippedImage(Enemy.EnemyAnimation.shroomMovingRightFrames[i]);
		}
		//load fly images
		Enemy.EnemyAnimation.flyMovingLeftFrames = new Image[] {
			LoadImage("fly1.png"),
			LoadImage("fly2.png")
		};
		int flyAnimSize = Enemy.EnemyAnimation.flyMovingLeftFrames.length;
		Enemy.EnemyAnimation.flyMovingRightFrames = new Image[flyAnimSize];
		Enemy.EnemyAnimation.flyDyingLeftFrames = new Image[flyAnimSize];
		Enemy.EnemyAnimation.flyDyingRightFrames = new Image[flyAnimSize];
		for (int i = 0; i < flyAnimSize; ++i){
			Enemy.EnemyAnimation.flyMovingRightFrames[i] =
				getMirrorImage(Enemy.EnemyAnimation.flyMovingLeftFrames[i]);
			Enemy.EnemyAnimation.flyDyingLeftFrames[i] =
				getFlippedImage(Enemy.EnemyAnimation.flyMovingLeftFrames[i]);
			Enemy.EnemyAnimation.flyDyingRightFrames[i] =
				getFlippedImage(Enemy.EnemyAnimation.flyMovingRightFrames[i]);
		}
		//load coin images
		int coinCount = 4;
		PowerUp.PowerUpAnimation.coinFrames = new Image[coinCount];
		for (int coin = 0; coin < coinCount; ++coin)
		{
			String fileName = "images/coin" + (coin + 1) + ".png";
			Image coinImage = new ImageIcon(fileName).getImage();
			int width  = 40;
			int height = 40;
			Image newImage = gc.createCompatibleImage(width, height,Transparency.BITMASK);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(coinImage, 0,0, width,height, null);
			g.dispose();

			PowerUp.PowerUpAnimation.coinFrames[coin] = newImage;
		}
		int homeWidth = 100;
		int homeHeight = 100;
		String fileName = "images/heart.png";
		Image home = new ImageIcon(fileName).getImage();
		Image newImage = gc.createCompatibleImage(homeWidth, homeHeight,Transparency.BITMASK);
		Graphics2D g = (Graphics2D) newImage.getGraphics();
		g.drawImage(home, 0,0, homeWidth,homeHeight, null);
		g.dispose();
		//load home image
		PowerUp.PowerUpAnimation.homeFrames = new Image[] {
			newImage
		};
		//load tiles images
		tiles = new Image[tileImagesCount];
		for (char ch = 'A'; ch <= 'K'; ++ch){
			String name = ch + ".png";
			tiles[ch - 'A'] = LoadImage(name);
		}
	}


	/**
	* load the currentMap
	* @return
	 */
	public static TileMap LoadMap()
	{
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		int coinCount = 0;
		int grubCount = 0;
		int fliesCount = 0;
		String filename = "maps/map" + currentMap + ".txt";
		// read every line in the text file into the list
		String line;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				for (int i = 0; i < line.length(); ++i){
					switch(line.charAt(i)){
						case 'o': coinCount++; break;
						case '1': grubCount++; break;
						case '2': fliesCount++; break;
					}
				}
				lines.add(line);
				width = Math.max(width, line.length());
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		// parse the lines to create a TileEngine
		height = lines.size();
		TileMap newMap = new TileMap(width, height, grubCount, fliesCount, coinCount);
		for (int y = 0; y < height; y++) {
			line = (String) lines.get(y);
			for (int x = 0; x < line.length(); ++x) {
				char ch = line.charAt(x);
				// check if the char represents tile A, B, C etc.
				int tile = ch - 'A';
				if (tile >= 0 && tile < tileImagesCount) {
					newMap.setTile(x, y, tiles[tile]);
				}
				else {
					switch(ch){
						case 'o': newMap.AddCoin(x,y); break;
						case '*': newMap.AddHome(x,y); break;
						case '1': newMap.AddShroom(x,y); break;
						case '2': newMap.AddFly(x,y); break;
					}
				}
			}
		}

		return newMap;
	}
}
