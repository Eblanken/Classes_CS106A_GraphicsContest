/*
 * File: ProjectileMobile.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one projectile instance.
 */

import acm.graphics.*;

public class ProjectileMobile implements TorchBearerConstants {
	
	private double remainingRange;
	private double dX;
	private double dY;
	private double speed;
	private double damage;
	private int variant;
	private GPoint position;
	private int[][][] textureMap;
	
	/** Creates the projectile */
	ProjectileMobile(GPoint startPosition, int[][][] textureMap, double speed, double range, double fireAngle, double damage, String type){
		remainingRange = range;
		this.speed = speed;
		dY = speed*Math.sin(fireAngle);
		dX = speed*Math.cos(fireAngle);
		this.textureMap = textureMap;
		position = startPosition;
		this.damage = damage;
		//Determines the projectile's appearance.
		switch (type){
		case "Tool":
			if(damage<50){
				variant = 3;
			}else{
				variant = 4;
			}
			break;
		case "Blade":
			if(damage<50){
				variant = 3;
			}else{
				variant = 4;
			}
			break;
		case "Sling":
			if(damage<50){
				variant = 1;
			}else{
				variant = 2;
			}
			break;
		case "Musket":
			if(damage<50){
				variant = 1;
			}else{
				variant = 2;
			}
			break;	
		}
	}
	
	/** Allows the projectile to move forward */
	public void takeTurn(){
		if(projectileFree()){
			position.translate(dX,dY);
			remainingRange-=speed;
		}
	}
	
	/** Returns null since this object currently has no direction */
	public String getDirection(){
		return "null";
	}
	
	/** Returns the damage dealt by the projectile */
	public double getDamage(){
		return damage;
	}
	
	/** Returns the position of the projectile */
	public GPoint getPosition(){
		return position;
	}
	
	/** Returns the variant of the projectile */
	public int getVariant(){
		return variant;
	}
	
	/** Returns true if the projectile can continue to move forward */
	public boolean projectileFree(){
		if((textureMap[(int) position.getY()*TILE_DIMENSION][(int) position.getX()*TILE_DIMENSION][0]!=READ_BLOCKED)&&(remainingRange>0)){
			return true;
		}else{
			return false;
		}
	}
	
}
