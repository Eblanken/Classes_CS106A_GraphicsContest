
/*
 * File: BossMobile.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one boss. The boss
 * phases through walls and has a huge amount of health
 */

import acm.graphics.*;

public class BossMobile implements TorchBearerConstants{
		
		private GPoint bossPosition;
		private double maxSpeed;
		private boolean alert;
		private String bossDirection = DEFAULT_ORIENTATION;
		private double bossDX = 0;
		private double bossDY = 0;
		private GPoint bossTarget = new GPoint(0,0);
		private double health;
		private int levelModifier;
		private int hitValue;
		private int mobVariant = 1;
		private int[][][] textureMap;
		
	BossMobile(int[][][] textureMap, GPoint bossPosition,double newMaxSpeed, int level){
		this.bossPosition = bossPosition;
		maxSpeed = newMaxSpeed;
		levelModifier = level;
		this.health = (levelModifier*2.5)+40;
		hitValue = (level*2)+15;
		this.textureMap = textureMap;
	}
	
	/** This class defines the actions of the boss mob, target is typically the player but might be something else. Change alert status beforehand */
	public void takeTurn(GPoint target){
		if(alert==true){
			bossTarget.setLocation(target);
			// Determines the DY and DX for the bosss turn.
			bossDX = 0;
			bossDY = 0;
			if (bossPosition.getX()-0.1>bossTarget.getX()) bossDX = -maxSpeed;
			if (bossPosition.getX()+0.1<bossTarget.getX()) bossDX = maxSpeed;
			if (bossPosition.getY()-0.1>bossTarget.getY()) bossDY = -maxSpeed;
			if (bossPosition.getY()+0.1<bossTarget.getY()) bossDY = maxSpeed;
			// Sets the boss's apparent direction.
			if (bossPosition.getX()<bossTarget.getX()&&(Math.abs(bossPosition.getX()-bossTarget.getX())>Math.abs(bossPosition.getY()-bossTarget.getY()))){
				bossDirection = "Right";
			}else if (bossPosition.getX()>bossTarget.getX()&&(Math.abs(bossPosition.getX()-bossTarget.getX())>Math.abs(bossPosition.getY()-bossTarget.getY()))){
				bossDirection = "Left";
			}
			if (bossPosition.getY()<bossTarget.getY()&&(Math.abs(bossPosition.getX()-bossTarget.getX())<Math.abs(bossPosition.getY()-bossTarget.getY()))){
				bossDirection = "Down";
			}else if (bossPosition.getY()>bossTarget.getY()&&(Math.abs(bossPosition.getX()-bossTarget.getX())<Math.abs(bossPosition.getY()-bossTarget.getY()))){
				bossDirection = "Up";
			}
			bossPosition.translate(bossDX, bossDY);
		//If the boss is'nt alert and it sees the player it will become alert.
		}else{
			if(rayCastToPlayer(target.getX()*TILE_DIMENSION,target.getY()*TILE_DIMENSION,bossPosition.getX()*TILE_DIMENSION,bossPosition.getY()*TILE_DIMENSION)&&alert==false){
				alert = true;
			}
		}
	}
	
	/** Gets the damage caused by this boss */
	public double getDamage(){
		return hitValue;
	}
	
	/** Returns the boss variant */
	public int getVariant(){
		return mobVariant;
	}
	
	/** Returns the bosss health */
	public double getHealth(){
		return health;
	}
	
	/** Attacks this boss */
	public void attacked(double d){
		health-=d;
		bossDirection = "Up";
	}
	
	/** Returns the player's current heading */
	public String getDirection(){
		return bossDirection;
	}
	
	/** Gives you the bosss position */
	public GPoint getPosition(){
		return bossPosition;
	}

	/** returns boss dX value */
	public double getDX(){
		return bossDX;
	}
		
	/** returns boss dY value */
	public double getDY(){
		return bossDY;
	}
		
	/** Changes the alert status of the boss */
	public void updateBossAlertStatus(boolean newAlert){
		alert = newAlert;
	}
		
	/* Verifies that there is visibility between two points */
	private boolean rayCastToPlayer(double xPoint,double yPoint,double xOrigin,double yOrigin){
		//Checks left and right X sector.
		if((xOrigin<xPoint)&&(Math.abs(yPoint-yOrigin)<=Math.abs(xPoint-xOrigin))){
			for(int i=(int) xOrigin;i<(int) (xPoint);i+=1){
				if(textureMap[(int) (yOrigin+((i-xOrigin)*((yPoint-yOrigin)/(xPoint-xOrigin))))][i][0]==-16777215){
					return false;
				}
			}
		}
		if((xOrigin>xPoint)&&(Math.abs(yPoint-yOrigin)<=Math.abs(xPoint-xOrigin))){
			for(int i=(int) xOrigin;i>(int) (xPoint);i-=1){
				if(textureMap[(int) (yOrigin+((i-xOrigin)*((yPoint-yOrigin)/(xPoint-xOrigin))))][i][0]==-16777215){
					return false;
				}
			}
		}
		//Checks upper and lower Z sector.
		if((yOrigin<yPoint)&&(Math.abs(yPoint-yOrigin)>=Math.abs(xPoint-xOrigin))){
			for(int i=(int) yOrigin;i<(int) (yPoint);i+=1){
				if(textureMap[i][(int) (xOrigin+((i-yOrigin)*((xPoint-xOrigin)/(yPoint-yOrigin))))][0]==-16777215){
					return false;
				}
			}
		}
		if((yOrigin>yPoint)&&(Math.abs(yPoint-yOrigin)>=Math.abs(xPoint-xOrigin))){
			for(int i=(int) yOrigin;i>(int) (yPoint);i-=1){
				if(textureMap[i][(int) (xOrigin+((i-yOrigin)*((xPoint-xOrigin)/(yPoint-yOrigin))))][0]==-16777215){
					return false;
				}
			}
		}
	return true;
	}
}
