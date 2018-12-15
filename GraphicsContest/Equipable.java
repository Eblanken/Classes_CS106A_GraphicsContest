/*
 * File: Equipable.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one equipable. It is capable of
 * returning a card based on it's values in comparison to another
 * item. The card's position is offset so that it is above the item.
 */

import java.util.StringTokenizer;

import acm.graphics.*;
import acm.util.RandomGenerator;

public class Equipable implements TorchBearerConstants {

	private int variant;
	private String type;
	private int damage;
	private double range;
	private double speed;
	private double delay;
	private double digBonus = 1;
	private GPoint itemPosition;
	private String direction = "null";
	private RandomGenerator rgen = RandomGenerator.getInstance();
	int[][] card;
	
	/** Initializes the equipable from the level*/
	Equipable(GPoint newItemPosition,int level){
		itemPosition = newItemPosition;
		card = new GImage("basicCard.png").getPixelArray();
		switch(rgen.nextInt(4)) {
			case 0:
				type = "Tool";
				damage = rgen.nextInt(10,10+level*2);
				range = rgen.nextDouble(1,1+level*0.125);
				delay = rgen.nextDouble(300,300+damage*40);
				speed = rgen.nextDouble(0.1,0.1+0.1/damage);
				if(damage<12){
					variant = 1;
					digBonus = 1.50;
				}else if(damage<18){
					variant = 2;
					digBonus = 1.75;
				}else{
					variant = 3;
					digBonus = 2.00;
				}
				break;
			case 1:
				type = "Blade";
				damage = rgen.nextInt(10,10+(int) (level*2.5));
				range = rgen.nextDouble(1,1+level*0.125);
				delay = rgen.nextDouble(200,200+damage*40);
				speed = rgen.nextDouble(0.1,0.1+0.1/damage);
				if(damage<15){
					variant = 1;
				}else if(damage<18){
					variant = 2;
				}else{
					variant = 3;
				}
				break;
			case 2:
				type = "Sling";
				damage = rgen.nextInt(10,10+level*3);
				range = rgen.nextDouble(2,4+level*0.5);
				delay = rgen.nextDouble(200,200+damage*40);
				speed = rgen.nextDouble(1,1+0.5/damage);
				if(damage<15){
					variant = 1;
					digBonus = 1.25;
				}else if(damage<20){
					variant = 2;
					digBonus = 1.30;
				}else{
					variant = 3;
					digBonus = 1.50;
				}
				break;
			case 3:
				type = "Musket";
				damage = rgen.nextInt(40,40+level*6);
				range = rgen.nextDouble(2,4+level*0.5);
				delay = rgen.nextDouble(300,300+damage*50);
				speed = rgen.nextDouble(1,1+0.5/damage);
				if(damage<50){
					variant = 1;
				}else if(damage<70){
					variant = 2;
				}else{
					variant = 3;
				}
				break;
				/////////////////////
			case 4:
				type = "Armor";
				////////////////////
		}
	}
	/** Initializes the equipable from a String*/
	Equipable(String dataLine){
		card = new GImage("basicCard.png").getPixelArray();
		StringTokenizer saveReader = new StringTokenizer(dataLine,",");
		variant = (Integer.parseInt(saveReader.nextToken()));
		type = saveReader.nextToken();
		damage = Integer.parseInt(saveReader.nextToken());
		range = Double.parseDouble(saveReader.nextToken());
		speed = Double.parseDouble(saveReader.nextToken());
		delay = Double.parseDouble(saveReader.nextToken());
		digBonus = Double.parseDouble(saveReader.nextToken());
	}
			
	/** Returns the delay between shots */
	public double getDelay(){
		return delay;
	}
	
	/** Returns the speed of the items projectile */
	public double getSpeed(){
		return speed;
	}
	
	/** Returns the range of the items projectile */
	public double getRange(){
		return range;
	}
	
	/** Returns the damage of the item */
	public int getDamage(){
		return damage;
	}
	
	/** Returns the dig bonus of the item */
	public double getDigBonus(){
		return digBonus;
	}
	
	/** Returns the variant of the item */
	public int getVariant(){
		return variant;
	}
	
	/** Returns the type of the item */
	public String getType(){
		return type;
	}
	
	/** Returns the position of the item */
	public GPoint getPosition(){
		return itemPosition;
	}
	
	/** Returns the direction of the item */
	public String getDirection(){
		return direction;
	}
	
	/** Returns a string of all of the items qualities */
	public String toString(){
		String itemString = getVariant()+","+getType()+","+getDamage()+","+getRange()+","+getSpeed()+","+getDelay()+","+getDigBonus();
		return itemString;
	}
	
	/** Builds a visualization of this weapon's stats */
	public int[][] getCard(double playerWeaponDamage,double playerWeaponRange,double playerDigBonus, double playerWeaponDelay){
		card = new GImage("basicCard.png").getPixelArray();
		//Builds the damage bar indicator
		int damageColor = -10203822;
		if(damage>playerWeaponDamage){
			damageColor = -18865;
		}else if(damage<playerWeaponDamage){
			damageColor = -558510;
		}
		for(int i=6;i<damage/2+5;i++){
			if(i<31){
				card[5][i] = damageColor;
				card[6][i] = damageColor;
			}
		}
		//Builds the range bar indicator
		int rangeColor = -10203822;
		if(range>playerWeaponRange){
			rangeColor = -18865;
		}else if(range<playerWeaponRange){
			rangeColor = -558510;
		}
		for(int i=6;i<range*2+5;i++){
			if(i<31){
				card[10][i] = rangeColor;
				card[11][i] = rangeColor;
			}
		}
		//Builds the delay bar
		int speedColor = -10203822;
		if(delay<playerWeaponDelay){
			speedColor = -18865;
		}else if(delay>playerWeaponDelay){
			speedColor = -558510;
		}
		for(int i=6;i<(32-(delay/1000)*26);i++){
			if(i<31){
				card[15][i] = speedColor;
				card[16][i] = speedColor;
			}
		}
		//Builds the bonus bar indicator
		int bonusColor = -10203822;
		if(digBonus>playerDigBonus){
			bonusColor = -18865;
		}else if(digBonus<playerDigBonus){
			bonusColor = -558510;
		}
		for(int i=6;i<(digBonus-1)*32;i++){
			if(i<31){
				card[20][i] = bonusColor;
				card[21][i] = bonusColor;
			}
		}
		return card;
	}
	
	/** Returns the location of the item card, which is placed above the items location */
	public GPoint getCardPosition(){
		double originX = itemPosition.getX();
		double originY = itemPosition.getY()-((card.length/2.0)/TILE_DIMENSION)-0.5;
		GPoint cardPoint = new GPoint(originX,originY);
		return cardPoint;
	}
	public void setPosition(GPoint newPosition) {
		itemPosition = newPosition;	
	}
}
