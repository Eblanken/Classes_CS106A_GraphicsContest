/*
 * File: SkeletonMobile.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one skeleton. The skeleton
 * must be supplied with an up to date pathmap to make
 * intelligent decisions.
 */

import acm.graphics.*;

public class SkeletonMobile implements TorchBearerConstants{
		
		private GPoint skeletonPosition;
		private int[][][] textureMap;
		private int[][] pathMap;
		private double maxSpeed;
		private boolean alert;
		private String skeletonDirection = DEFAULT_ORIENTATION;
		private double skeletonDX = 0;
		private double skeletonDY = 0;
		private GPoint skeletonTarget = new GPoint(0,0);
		private double health;
		private int levelModifier;
		private int hitValue;
		private int mobVariant = 1;
		
	SkeletonMobile(GPoint skeletonPosition,int[][][] textureMap,int[][] pathMap,double newMaxSpeed, int level){
		this.skeletonPosition = skeletonPosition;
		this.textureMap = textureMap;
		this.pathMap = pathMap;
		maxSpeed = newMaxSpeed;
		levelModifier = level;
		this.health = (levelModifier*2.5)+40;
		hitValue = (level*2)+15;
	}
	
	/** This class defines the actions of the skeleton mob, target is typically the player but might be something else. Change alert status beforehand */
	public void takeTurn(int[][] pathMap,GPoint target){
		if(alert==true){
			// If alert but no direct visibility, will set next target to the center of the square with the next lowest pathmap value.
			if(!rayCastToPlayer(target.getX()*16,target.getY()*16,skeletonPosition.getX()*16,skeletonPosition.getY()*16)){
				if(Math.pow((Math.pow((skeletonPosition.getX()-skeletonTarget.getX()),2)+Math.pow((skeletonPosition.getY()-skeletonTarget.getY()),2)),0.5)<0.05){
					int top = pathMap[((int) (skeletonPosition.getY()))-1][(int) skeletonPosition.getX()];
					int left = pathMap[(int) (skeletonPosition.getY())][((int) (skeletonPosition.getX()))-1];
					int right = pathMap[(int) skeletonPosition.getY()][((int) (skeletonPosition.getX())+1)];
					int bottom = pathMap[((int) skeletonPosition.getY())+1][(int) (skeletonPosition.getX())];
					if(((top>=left)||(left==0))&&((top>=right)||(right==0))&&((top>=bottom)||(bottom==0))&&(top<0)) skeletonTarget.setLocation(((int) (skeletonPosition.getX()))+0.5,((int) (skeletonPosition.getY()))-0.5);
					if(((left>=top)||(top==0))&&((left>=right)||(right==0))&&((left>=bottom)||(bottom==0))&&(left<0)) skeletonTarget.setLocation(((int) (skeletonPosition.getX()))-0.5,((int) (skeletonPosition.getY()))+0.5);
					if(((right>=left)||(left==0))&&((right>=top)||(top==0))&&((right>=bottom)||(bottom==0))&&(right<0)) skeletonTarget.setLocation(((int) (skeletonPosition.getX()))+1.5,((int) (skeletonPosition.getY()))+0.5);
					if(((bottom>=left)||(left==0))&&((bottom>=right)||(right==0))&&((bottom>=top)||(top==0))&&(bottom<0)) skeletonTarget.setLocation(((int) (skeletonPosition.getX()))+0.5,((int) (skeletonPosition.getY()))+1.5);
				}
			//Otherwise the skeleton can directly see the player and will make a bee-line.
			}else{
				skeletonTarget.setLocation(target);
			}
			// Determines the DY and DX for the skeletons turn.
			skeletonDX = 0;
			skeletonDY = 0;
			if (skeletonPosition.getX()-0.1>skeletonTarget.getX()) skeletonDX = -maxSpeed;
			if (skeletonPosition.getX()+0.1<skeletonTarget.getX()) skeletonDX = maxSpeed;
			if (skeletonPosition.getY()-0.1>skeletonTarget.getY()) skeletonDY = -maxSpeed;
			if (skeletonPosition.getY()+0.1<skeletonTarget.getY()) skeletonDY = maxSpeed;
			// Sets the skeleton's apparent direction.
			if (skeletonPosition.getX()<skeletonTarget.getX()&&(Math.abs(skeletonPosition.getX()-skeletonTarget.getX())>Math.abs(skeletonPosition.getY()-skeletonTarget.getY()))){
				skeletonDirection = "Right";
			}else if (skeletonPosition.getX()>skeletonTarget.getX()&&(Math.abs(skeletonPosition.getX()-skeletonTarget.getX())>Math.abs(skeletonPosition.getY()-skeletonTarget.getY()))){
				skeletonDirection = "Left";
			}
			if (skeletonPosition.getY()<skeletonTarget.getY()&&(Math.abs(skeletonPosition.getX()-skeletonTarget.getX())<Math.abs(skeletonPosition.getY()-skeletonTarget.getY()))){
				skeletonDirection = "Down";
			}else if (skeletonPosition.getY()>skeletonTarget.getY()&&(Math.abs(skeletonPosition.getX()-skeletonTarget.getX())<Math.abs(skeletonPosition.getY()-skeletonTarget.getY()))){
				skeletonDirection = "Up";
			}
			// Checks collision
			if (textureMap[(int) skeletonPosition.getY()*TILE_DIMENSION][(int) (skeletonPosition.getX()*TILE_DIMENSION+skeletonDX*TILE_DIMENSION+((skeletonDX/Math.abs(skeletonDX))*4))][0]==READ_BLOCKED) skeletonDX = 0;
			if (textureMap[(int) (skeletonPosition.getY()*TILE_DIMENSION+skeletonDY*TILE_DIMENSION+((skeletonDY/Math.abs(skeletonDY))*4))][(int) skeletonPosition.getX()*TILE_DIMENSION][0]==READ_BLOCKED) skeletonDY = 0;
			// Makes the turn.
			skeletonPosition.translate(skeletonDX, skeletonDY);
		//If the skeleton is'nt alert and it sees the player it will become alert.
		}else{
			if(rayCastToPlayer(target.getX()*TILE_DIMENSION,target.getY()*TILE_DIMENSION,skeletonPosition.getX()*TILE_DIMENSION,skeletonPosition.getY()*TILE_DIMENSION)&&alert==false){
				alert = true;
			}
		}
	}
	
	/** Gets the damage caused by this skeleton */
	public double getDamage(){
		return hitValue;
	}
	
	/** Returns the skeleton variant */
	public int getVariant(){
		return mobVariant;
	}
	
	/** Returns the skeletons health */
	public double getHealth(){
		return health;
	}
	
	/** Attacks this skeleton */
	public void attacked(double d){
		health-=d;
		skeletonDirection = "Up";
	}
	
	/** Returns the player's current heading */
	public String getDirection(){
		return skeletonDirection;
	}
	
	/** Gives you the skeletons position */
	public GPoint getPosition(){
		return skeletonPosition;
	}

	/** returns skeleton dX value */
	public double getDX(){
		return skeletonDX;
	}
		
	/** returns skeleton dY value */
	public double getDY(){
		return skeletonDY;
	}
		
	/** Gives the skeleton a better idea of where the target is */
	public void updateSkeletonPathMap(int[][] newPathMap){
		pathMap = newPathMap;
	}
		
	/** Changes the alert status of the skeleton */
	public void updateSkeletonAlertStatus(boolean newAlert){
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
