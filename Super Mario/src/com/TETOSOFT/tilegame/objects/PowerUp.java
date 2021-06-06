package com.TETOSOFT.tilegame.objects;

import java.awt.Image;
import com.TETOSOFT.render.*;

public class PowerUp {
	public float x,y;

	public static class PowerUpAnimation{
		public static Image[] coinFrames;
		public static Image[] homeFrames;
		public Image[] currentFrames;
		public long frameDuration;
		public int currentFrameIndex = 0;
		public long currentFrameDuration;
	}

	public static PowerUp getCoin(int x, int y){
		PowerUp coin = getPowerUp(x + 1, y + 1);
		coin.anim.frameDuration = 250;
		coin.anim.currentFrames = PowerUpAnimation.coinFrames;
		coin.x -= coin.getWidth() / 2;
		coin.y -= coin.getHeight();
		return coin;
	}

	public static PowerUp getHome(int x, int y){
		PowerUp home = getPowerUp(x + 1, y + 1);
		home.anim.currentFrames = PowerUpAnimation.homeFrames;
		home.x -= home.getWidth() / 2;
		home.y -= home.getHeight();
		return home;
	}

	private static PowerUp getPowerUp(int x, int y){
		PowerUp p = new PowerUp();
		p.x = Renderer.tilesToPixels(x);
		p.y = Renderer.tilesToPixels(y);
		return p;
	}

	public PowerUpAnimation anim;

	public void update(long elapsedTime){
		int max_frames = anim.currentFrames.length;
		anim.currentFrameDuration-= elapsedTime;
		while (anim.currentFrameDuration <= 0){
			anim.currentFrameIndex++;
			anim.currentFrameDuration += anim.frameDuration;
			if (anim.currentFrameIndex == max_frames){
				anim.currentFrameIndex = 0;
			}
		}
	}

	public Image getImage(){
		return anim.currentFrames[anim.currentFrameIndex];
	}

	PowerUp(){
		anim = new PowerUpAnimation();
	}
	public int getWidth(){
		return getImage().getWidth(null);
	}

	public int getHeight(){
		return getImage().getHeight(null);
	}
}
