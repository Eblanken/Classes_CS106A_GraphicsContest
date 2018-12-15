/*
 * File: PlayerMobile.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for a player. This class acts as both a save
 * with the toString function acting as the entry and as an active entity.
 * It is in many ways the brain behind the actual game.
 */

import java.awt.Color;
import java.util.*;

import acm.graphics.*;
import acm.util.RandomGenerator;

public class PlayerMobile implements TorchBearerConstants {
		//The player's environment stats.
		private int[][][] textureMap;
		private int level = DEFAULT_START_LEVEL;
		private int seed;
	
		//Player property stats.
		private String playerName = "Hello";
		private GPoint playerPosition;
		private double playerDX = 0;
		private double playerDY = 0;
		private String direction ="Down";
		
		
		//Player supply stats.
		private double torchFuel = DEFAULT_TORCHFUEL_START;
		private double torchFuelMax = DEFAULT_TORCHFUEL_START;
		private int money = 0;
		private int cells = 0;
		private int stones = 0;
		
		//Player inventory. 
		private Equipable melee = null;
		private Equipable ranged = null;
		
		
		//Stats about the player.
		public static final double DEFAULT_PLAYER_DIMENSION = 0.5;
		private int variant = 1;
	
		//Random generator used for the seed
		private RandomGenerator rgen = RandomGenerator.getInstance();
		
	/** Blank constructor builds a player from scratch */
	PlayerMobile(){
		playerPosition = new GPoint(0,0);
		seed = rgen.nextInt();
		while(true){
			melee = new Equipable(new GPoint(0,0),1);
			if((melee.getType().equals("Tool"))||(melee.getType().equals("Blade"))) break;
		}
		//Reads in the ranged weapon.
		while(true){
			ranged = new Equipable(new GPoint(0,0),1);
			if((ranged.getType().equals("Sling"))||(melee.getType().equals("Musket"))) break;
		}
	}
	
	/** String constructor builds a player from a data line identical to a toString */
	PlayerMobile(String dataLine){
		StringTokenizer saveReader = new StringTokenizer(dataLine);
		//Initializes level information
		level = Integer.parseInt(saveReader.nextToken());
		seed = Integer.parseInt(saveReader.nextToken());
		//Initializes player stats
		playerName = saveReader.nextToken();
		playerPosition = new GPoint (Double.parseDouble(saveReader.nextToken()),Double.parseDouble(saveReader.nextToken()));
		//Initializes player supplies
		torchFuel = Double.parseDouble(saveReader.nextToken());
		torchFuelMax = Double.parseDouble(saveReader.nextToken());
		money = Integer.parseInt(saveReader.nextToken());
		cells = Integer.parseInt(saveReader.nextToken());
		stones = Integer.parseInt(saveReader.nextToken());
		//Initializes player equipment
		//Reads in the melee weapon.
		melee = new Equipable(saveReader.nextToken());
		//Reads in the ranged weapon.
		ranged = new Equipable(saveReader.nextToken());
	}
	
	/** Creates a save file for the player as an entry line */
	public String toString(){
		String saveString = ""+getLevel();
		saveString+=" "+getSeed();
		saveString+=" "+playerName;
		saveString+=" "+playerPosition.getX();
		saveString+=" "+playerPosition.getY();
		saveString+=" "+torchFuel;
		saveString+=" "+torchFuelMax;
		saveString+=" "+money;
		saveString+=" "+cells;
		saveString+=" "+stones;
		saveString+=" ,"+melee.toString();
		saveString+=" ,"+ranged.toString();
		return saveString;
	}
	
	/** Returns a simplified summary of the player */
	public String simplifiedToString(){
		String simpleString = playerName+", level "+getLevel();
		return simpleString;
	}
	
	/** Returns the name of this player */
	public String getName(){
		return playerName;
	}
	
	/** Returns the name of this player */
	public void setName(String newName){
		playerName = newName;
	}
	
	/** Increments the level */
	public void incrementLevel(){
		level++;
	}
	
	/** Allows the player to take their turn */
	public void takeTurn(){
		if (checkNewPosition() == true){
			playerPosition.translate(playerDX,playerDY);
		}
		if(torchFuel<torchFuelMax){
			torchFuel -= DEFAULT_BURN_RATE;
		}else{
			torchFuel -= (Math.pow((torchFuel/torchFuelMax),BURN_RATE_POWER)*DEFAULT_BURN_RATE);
		}
	}
	
	/** Switches out a players weapon with what is on the ground */
	public Equipable switchItem (Equipable newWeapon){
		if(newWeapon!=null){
			Equipable oldWeapon = null;
			switch (newWeapon.getType()){
				case "Tool":
					oldWeapon = melee;
					melee = newWeapon;
					break;
				case "Blade":
					oldWeapon = melee;
					melee = newWeapon;
					break;
				case "Sling":
					oldWeapon = ranged;
					ranged = newWeapon;
					break;
				case "Musket":
					oldWeapon = ranged;
					ranged = newWeapon;
					break;
			}
		return oldWeapon;
		}
	return null;
	}
	
	/** Returns the seed of the level the player currently is in */
	public int getSeed(){
		return seed;
	}
	/** Sets the seed of the level the player currently is in */
	public void setSeed(int newSeed){
		seed = newSeed;
	}
	/** Returns the level number the player currently is in */
	public int getLevel(){
		return level;
	}
	/** Sets the level number the player currently is in */
	public void setLevel(int newLevel){
		level = newLevel;
	}
	
	/** Returns any item collection bonus the player might have */
	public Equipable getMelee(){
		return melee;
	}
	
	/** Returns any item collection bonus the player might have */
	public Equipable getRanged(){
		return ranged;
	}
	
	/** Adds money to the player's wallet */
	public void addMoney(int value){
		money+=value;
	}
	
	/** Spends money from the player's wallet */
	public boolean payMoney(int value){
		if(money>=value&&value!=0){
			money-=value;
			return true;
		}else{
			return false;	
		}
	}
	
	/** Returns the value of the players wallet */
	public int getMoney(){
		return money;
	}
	
	/** Adds money to the player's wallet */
	public void addCells(int value){
		cells+=value;
	}
		
	/** Clear's the player's reserve of cells */
	public void clearCells(){
		cells=0;
	}
		
	/** Returns the number of cells a player has */
	public int getCells(){
		return cells;
	}
	
	/** Adds stones to the player's reserve */
	public void addStones(int value){
		stones+=value;
	}
		
	/** Uses up a stone from the player's reserve */
	public void subtractStone(int value){
		stones-=value;
	}
		
	/** Returns the quantity of the player's stones */
	public int getStones(){
		return stones;
	}
	
	/** Allows other entities to attack the player, reducing their light level substantially */
	public void attack(double d){
		torchFuel-=d;
	}
	
	/** Allows other sources to increase the amount of torchfuel the player has */
	public void addTorchFuel(double d){
		torchFuel += d;
	}
	
	/** Returns the player's texture variant */
	public int getVariant(){
		return variant;
	}
	
	/** Returns the players position */
	public GPoint getPosition(){
		return playerPosition;
	}
	
	/** Teleports the player */
	public void setPosition(GPoint position){
		playerPosition = position;
	}
	
	/** Updates the intMap */
	public void setTextureMap(int[][][] newTextureMap){
		this.textureMap = newTextureMap;
	}
	
	/** Returns the player's current heading */
	public String getDirection(){
		return direction;
	}
	
	/** Returns the current reserve of fuel */
	public double getTorchFuel(){
		return torchFuel;
	}
	
	/** Sets the player's current torch reservoir */
	public void setTorchFuel(double newFuel){
		torchFuel = newFuel;
	}
	
	/** Returns the max reserve of fuel */
	public double getTorchFuelMax(){
		return torchFuelMax;
	}
	
	/** Sets the player's sprite's direction */
	public void setDirection(String newDirection){
		direction = newDirection;
	}
	
	/** Gets the Y direction of movement */
	public double getDY(){
		return playerDY;
	}
	
	/** Gets the X direction of movement */
	public double getDX(){
		return playerDX;
	}
	
	/** Sets the Y direction of movement */
	public void setDY(double newDY){
		playerDY = newDY;
	}
	
	/** Sets the X direction of movement */
	public void setDX(double newDX){
		playerDX = newDX;
	}
	
	/* Makes sure the new position is not out of bounds */
	private boolean checkNewPosition(){
		if((textureMap[(int) ((((playerPosition.getY())+(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDY)][(int) ((((playerPosition.getX())+(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDX)][1]==READ_BLOCKED)||
				(textureMap[(int) ((((playerPosition.getY())+(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDY)][(int) ((((playerPosition.getX())-(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDX)][1]==READ_BLOCKED)||
				(textureMap[(int) ((((playerPosition.getY())-(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDY)][(int) ((((playerPosition.getX())+(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDX)][1]==READ_BLOCKED)||
				(textureMap[(int) ((((playerPosition.getY())-(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDY)][(int) ((((playerPosition.getX())-(DEFAULT_PLAYER_DIMENSION/2)))*TILE_DIMENSION+40*playerDX)][1]==READ_BLOCKED)){
			return false;
		}
		return true;
	}
	
	/** Returns the money tab for the player */
	public int[][] getMoneyTab(){
		int[][] moneyTab = new GImage("moneyTab.png").getPixelArray();
		int[][] moneyTabNumbers = new StringSprite(playerPosition,money+"$","Gold",0,0).getCard();
		//Reads the money value onto the tab
		for(int i=0;i<moneyTabNumbers.length;i++){
			for(int j=0;j<moneyTabNumbers[0].length;j++){
				if(moneyTabNumbers[i][j]!=READ_TRANSPARENT){
					moneyTab[i+2][j+1] = moneyTabNumbers[i][j];
				}
			}
		}
		return moneyTab;
	}
	
	/** Returns the location of the money tab for the player */
	public GPoint getMoneyTabPosition(){
		double xOrigin=playerPosition.getX()-APPLICATION_DIMENSION_TILES/2+1;
		double yOrigin=playerPosition.getY()-APPLICATION_DIMENSION_TILES/2+1;
		return new GPoint(xOrigin,yOrigin);
	}
	
	/** Returns the melee tab for the player */
	public int[][] getMeleeTab(double meleeLastFire){
		GImage meleeTabImage = new GImage("meleeTab.png");
		int[][] meleeTab = meleeTabImage.getPixelArray();
		//Adds the image of the current melee weapon
		GImage meleeWeaponImage = new GImage(melee.getType()+"_"+melee.getVariant()+"_"+DEFAULT_VIEW_ICON_LIGHTLEVEL+"_null.png");
		int[][] meleeWeaponArray = meleeWeaponImage.getPixelArray();
		for(int i=0;i<meleeWeaponArray.length;i++){
			for(int j=0;j<meleeWeaponArray[0].length;j++){
				if((meleeWeaponArray[i][j]!=READ_TRANSPARENT)){
					meleeTab[6+i][2+j] = meleeWeaponArray[i][j];
				}
			}
		}
		//Builds the melee weapon's attack readiness bar.
		if(meleeLastFire<melee.getDelay()){
			for(int i=3;i<17;i++){
				meleeTab[23][i]=Color.BLACK.getRGB();
				meleeTab[24][i]=Color.BLACK.getRGB();
			}
			for(int i=3;i<3+(meleeLastFire/melee.getDelay())*15;i++){
				if(i<17)
				meleeTab[23][i]=-558510;
				meleeTab[24][i]=-558510;
			}
		}
		//Builds the melee weapon's bonus bar.
		for(int i=3;i<3+(melee.getDigBonus()-1)*15;i++){
			if(i<17)
			meleeTab[28][i]=-18865;
			meleeTab[29][i]=-18865;
		}	
		return meleeTab;
	}
	
	/** Returns the melee tab position for the player */
	public GPoint getMeleeTabPosition(){
		double xOrigin=playerPosition.getX()-APPLICATION_DIMENSION_TILES/2+1;
		double yOrigin=playerPosition.getY()+APPLICATION_DIMENSION_TILES/2-1;
		return new GPoint(xOrigin,yOrigin);
	}
	
	/** Returns the ranged tab for the player */
	public int[][] getRangedTab(double rangedLastFire){
		GImage rangedTabImage = new GImage("rangedTab.png");
		int[][] rangedTab = rangedTabImage.getPixelArray();
		//Adds an image of the current ranged weapon
		GImage rangedWeaponImage = new GImage(ranged.getType()+"_"+ranged.getVariant()+"_"+DEFAULT_VIEW_ICON_LIGHTLEVEL+"_null.png");
		int[][] rangedWeaponArray = rangedWeaponImage.getPixelArray();
		for(int i=0;i<rangedWeaponArray.length;i++){
			for(int j=0;j<rangedWeaponArray[0].length;j++){
				if((rangedWeaponArray[i][j]!=READ_TRANSPARENT)){
					rangedTab[5+i][15+j] = rangedWeaponArray[i][j];
				}
			}
		}
		//Builds the ranged weapon's attack readiness bar.
		if(rangedLastFire<ranged.getDelay()){
			for(int i=17;i<30;i++){
				rangedTab[23][i]=Color.BLACK.getRGB();
				rangedTab[24][i]=Color.BLACK.getRGB();
			}
			for(int i=16;i<16+(rangedLastFire/ranged.getDelay())*15;i++){
				if(i<30){
					rangedTab[23][i]=-558510;
					rangedTab[24][i]=-558510;
				}
			}
		}
		//Builds the ranged weapon's bonus bar.
		for(int i=16;i<16+(ranged.getDigBonus()-1)*15;i++){
			if(i<30){
			rangedTab[28][i]=-18865;
			rangedTab[29][i]=-18865;
			}
		}
		//Builds the ranged weapon's ammo bar.
		for(int i=29;i>29-stones;i--){
			if(i>12){
				rangedTab[i][7]=-18865;
				rangedTab[i][8]=-18865;
			}
		}
		return rangedTab; 
	}
	
	/** Returns the ranged tab position for the player */
	public GPoint getRangedTabPosition(){
		double xOrigin=playerPosition.getX()+APPLICATION_DIMENSION_TILES/2-1;
		double yOrigin=playerPosition.getY()+APPLICATION_DIMENSION_TILES/2-1;
		return new GPoint(xOrigin,yOrigin);
	}
	
	/** Returns the player's current torch fuel percentage */
	public double getTorchFuelPercentage(){
		return torchFuel/torchFuelMax;
	}
	
}
