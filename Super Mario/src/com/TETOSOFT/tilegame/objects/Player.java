package com.TETOSOFT.tilegame.objects;

import java.awt.Image;
import com.TETOSOFT.tilegame.Box;

public class Player {
	//position
	public float x,y;
	// velocity
	public float dx = 0, dy = 0;
	public boolean jumping = false;
	public final float max_dx = 0.5f, max_dy = -1f;
	public final int weakSpotWidth;
	public final int weakSpotHeight;
	public static int width;
	public static int height;


	public static class PlayerAnimation
	{
		//NOTE(Mouad): everything is static because we only gonna have one player
		static public Image[] idleLeftFrames;
		static public Image[] idleRightFrames;
		static public Image[] movingLeftFrames;
		static public Image[] movingRightFrames;
		static public Image[] jumpingLeftFrames;
		static public Image[] jumpingRightFrames;
		static public Image[] dyingLeftFrames;
		static public Image[] dyingRightFrames;
		static public Image[] currentFrames;
		static public long frameDuration = 100;
		static public long currentFrameDuration;
		static public int currentFrameIndex = 0;
		static public long remainingDieTime;
		static public long DIE_TIME = 1000;
	}

	public void jump(){
		if (dy != 0) jumping = true;
		if (!jumping) {
			jumping = true;
			ForceJump();
		}
	}

	public void ForceJump(){
		jumping = true;
		dy = max_dy;
		PlayerAnimation.currentFrameIndex = 0;
		if (PlayerAnimation.currentFrames == PlayerAnimation.movingRightFrames ||
			PlayerAnimation.currentFrames == PlayerAnimation.idleRightFrames
		   )
		{
			PlayerAnimation.currentFrames = PlayerAnimation.jumpingRightFrames;
		}
		else{
			PlayerAnimation.currentFrames = PlayerAnimation.jumpingLeftFrames;
		}
	}

	public void StopJump(){
		jumping = false;
		dy = 0;
		if (PlayerAnimation.currentFrames == PlayerAnimation.jumpingRightFrames){
			if(dx > 0) PlayerAnimation.currentFrames = PlayerAnimation.movingRightFrames;
			else PlayerAnimation.currentFrames = PlayerAnimation.idleRightFrames;
		}
		else if (PlayerAnimation.currentFrames == PlayerAnimation.jumpingLeftFrames){
			if (dx < 0) PlayerAnimation.currentFrames = PlayerAnimation.movingLeftFrames;
			else PlayerAnimation.currentFrames = PlayerAnimation.idleLeftFrames;
		}
	}

	public void updateAnimation(long elapsedTime){
		PlayerAnimation.currentFrameDuration -= elapsedTime;
		int max_frames = PlayerAnimation.currentFrames.length;
		while (PlayerAnimation.currentFrameDuration <= 0){
			PlayerAnimation.currentFrameDuration += PlayerAnimation.frameDuration;
			PlayerAnimation.currentFrameIndex++;
			if (PlayerAnimation.currentFrameIndex == max_frames){
				PlayerAnimation.currentFrameIndex = 0;
			}
		}
	}

	public Image getImage(){
		return PlayerAnimation.currentFrames[PlayerAnimation.currentFrameIndex];
	}

	public Player(){
		PlayerAnimation.currentFrames = PlayerAnimation.idleRightFrames;
		PlayerAnimation.currentFrameIndex = 0;
		weakSpotWidth = getWidth();
		weakSpotHeight = 8 * getHeight() / 10 ;
	}
	public void moveLeft(){
		PlayerAnimation.currentFrames = PlayerAnimation.movingLeftFrames;
		PlayerAnimation.currentFrameIndex = 0;
		PlayerAnimation.currentFrameDuration = PlayerAnimation.frameDuration;
	}
	public void moveRight(){
		PlayerAnimation.currentFrames = PlayerAnimation.movingRightFrames;
		PlayerAnimation.currentFrameIndex = 0;
		PlayerAnimation.currentFrameDuration = PlayerAnimation.frameDuration;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		//NOTE(Mouad): subtract 6 pixels to make the player's feet touch the ground
		return height - 6;
	}
	public Box getBox(){
		Box b = new Box();
		b.x = x;
		b.y = y;
		b.width = getWidth();
		b.height = getHeight();
		return b;
	}

	public void die(){
		PlayerAnimation.remainingDieTime = PlayerAnimation.DIE_TIME;
		PlayerAnimation.currentFrameIndex = 0;
		if (PlayerAnimation.currentFrames == PlayerAnimation.movingRightFrames ||
			PlayerAnimation.currentFrames == PlayerAnimation.idleRightFrames ||
			PlayerAnimation.currentFrames == PlayerAnimation.jumpingRightFrames
		   )
		{
			PlayerAnimation.currentFrames = PlayerAnimation.dyingRightFrames;
		}
		else{
			PlayerAnimation.currentFrames = PlayerAnimation.dyingLeftFrames;
		}
	}
	public void idle()
	{
		if (PlayerAnimation.currentFrames == PlayerAnimation.movingLeftFrames||
			PlayerAnimation.currentFrames == PlayerAnimation.jumpingLeftFrames)
		{
			PlayerAnimation.currentFrameIndex = 0;
			PlayerAnimation.currentFrames = PlayerAnimation.idleLeftFrames;
		}
		else if(PlayerAnimation.currentFrames == PlayerAnimation.movingRightFrames||
				PlayerAnimation.currentFrames == PlayerAnimation.jumpingRightFrames)
		{
			PlayerAnimation.currentFrameIndex = 0;
			PlayerAnimation.currentFrames = PlayerAnimation.idleRightFrames;
		}
	}
}
