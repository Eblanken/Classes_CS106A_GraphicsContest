/*
 * File: GraphicsContest.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This is the actual Torch Bearer Program, this class coordinates
 * classes, displays the game's graphics, plays sounds, and takes in
 * commands from java swing components and hardware.
 */

import acm.program.*;
import acm.util.*;
import acm.graphics.*;
import acm.io.*;

import java.applet.AudioClip;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;

public class GraphicsContest extends GraphicsProgram implements TorchBearerConstants {
	
	//Loads window data
	//Please verify that your window is of these dimensions.
	/** Width and height of application window in pixels.  IMPORTANT NOTE:
	  * ON SOME PLATFORMS THESE CONSTANTS MAY **NOT** ACTUALLY BE THE DIMENSIONS
	  * OF THE GRAPHICS CANVAS.  Use getWidth() and getHeight() to get the 
	  * dimensions of the graphics canvas. */
		public static final int APPLICATION_WIDTH = APPLICATION_DIMENSION_PIXELS;
		public static final int APPLICATION_HEIGHT = APPLICATION_DIMENSION_PIXELS+40;
	
	//Various switches used in the program.
	private boolean teleportReady = false;
	private boolean pickupReady = false;
	private boolean readyToPlay = false;
	private double meleeLastFire = 0;
	private double rangedLastFire = 0;
	private double t = 0;
	
	//The current dungeon.
	private DungeonGenerator Dungeon;
	
	//Utility components
	private SaveGameReader save;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Java Swing Elements
	private JLabel nameEntryLabel;
	private JButton startGame;
	private JButton deleteEntry;
	private JButton saveAndExit;
	private JComboBox selectPlayer;
	private JTextField newPlayer;
	private JButton newPlayerCreate;
	
	//Lists of entitites.
	ArrayList<SkeletonMobile> mobs = new ArrayList<SkeletonMobile>();
	ArrayList<ProjectileMobile> projectiles = new ArrayList<ProjectileMobile>();
	ArrayList<Item> items = new ArrayList<Item>();
	ArrayList<Equipable> equipables = new ArrayList<Equipable>();
	ArrayList<StringSprite> popups = new ArrayList<StringSprite>();
	ArrayList<ShopKeeperMobile> shopKeepers = new ArrayList<ShopKeeperMobile>();
	ArrayList<LightMobile> lights = new ArrayList<LightMobile>();
 	ArrayList<BossMobile> boss = new ArrayList<BossMobile>();
	private PlayerMobile player;

	//Adds sounds
	//Soundtrack for pickups
	AudioClip teleportPickup = MediaTools.loadAudioClip("sfx_coin_double7.wav");
	AudioClip coalPickup = MediaTools.loadAudioClip("sfx_sounds_impact1.wav");
	AudioClip projectilePickup = MediaTools.loadAudioClip("sfx_menu_move4.wav");
	AudioClip money_1 = MediaTools.loadAudioClip("sfx_coin_single2.wav");
	AudioClip money_2 = MediaTools.loadAudioClip("sfx_coin_double2.wav");
	AudioClip money_3 = MediaTools.loadAudioClip("sfx_coin_cluster3.wav");
	
	//Soundtrack for damage and other effects
	AudioClip playerHit = MediaTools.loadAudioClip("sfx_sounds_damage3.wav");
	AudioClip skeletonHit = MediaTools.loadAudioClip("sfx_sounds_impact6.wav");
	AudioClip skeletonKilled = MediaTools.loadAudioClip("sfx_deathscream_human3.wav");
	AudioClip bossHit = MediaTools.loadAudioClip("sfx_deathscream_android1.wav");
	AudioClip bossKilled = MediaTools.loadAudioClip("sfx_deathscream_android6.wav");
	AudioClip toolAttack = MediaTools.loadAudioClip("sfx_wpn_punch4.wav");
	AudioClip slingAttack = MediaTools.loadAudioClip("sfx_wpn_dagger.wav");
	AudioClip bladeAttack = MediaTools.loadAudioClip("60013__qubodup__clean-whoosh-sound.wav");
	AudioClip musketAttack = MediaTools.loadAudioClip("sfx_wpn_cannon2.wav");
	AudioClip outOfProjectiles = MediaTools.loadAudioClip("sfx_wpn_noammo3.wav");
	AudioClip projectileLost = MediaTools.loadAudioClip("sfx_sounds_impact3.wav");
	AudioClip shopPurchase = MediaTools.loadAudioClip("sfx_sounds_fanfare2.wav");
	
	//Soundtrack for animations
	AudioClip teleportClip = MediaTools.loadAudioClip("sfx_movement_portal3.wav");
	AudioClip deadClip = MediaTools.loadAudioClip("FRUP_-_DarkVoid.wav");
	
	//Soundtrack for levels.
	AudioClip menuClip = MediaTools.loadAudioClip("06_Alone.wav");
	AudioClip level1Through3 = MediaTools.loadAudioClip("02_Im35.wav");
	AudioClip levels4Through7 = MediaTools.loadAudioClip("04_Haunted_Mansion.wav");
	AudioClip levels8Through11 = MediaTools.loadAudioClip("01_Dark_Galactica.wav");
	
	
	public void init(){
		//Initializes listeners and controls.
		addKeyListeners();
		addMouseListeners();
		save  = new SaveGameReader(SAVEGAME_FILE);
		loadMenu();
	}
	
	/** Plays the game */
	public void run(){
		chooseMusic(0);
		//The actual game loop.
		while(true){
			playMenuAnimation();
			//game runs as long as nothing interrupts readyToPlay
			if(readyToPlay==true){
				stopMusic();
				initializeLevel();
				while (true){
					chooseMusic(player.getLevel());
					while((teleportReady!=true)&&(player.getTorchFuel()>0)){
						renderTextureMap(Dungeon.getTextureMap(),player.getPosition(),APPLICATION_DIMENSION_TILES,t);
						t += GAME_DELAY;
						pause(GAME_DELAY);
						if(readyToPlay==false) break;
					}
					//If the loop was broken for the teleportation...
					if(teleportReady==true){
						player.incrementLevel();
						initializeLevel();
						player.setPosition(Dungeon.getPlayerStart());
						lights.get(0).setPosition(player.getPosition());
						teleportReady = false;
						playTeleportationAnimation();
					//Otherwise the player may have hit the "Quit" button...
					}else if(readyToPlay==false){
						chooseMusic(0);
						player.setDX(0);
						player.setDY(0);
						refreshMenu();
						break;
					}else{
						//Otherwise they died.
						chooseMusic(-1);
						saveAndExit.setVisible(false);
						save.deletePlayer(player);
						playDeathAnimation();
						refreshMenu();
						readyToPlay=false;
						break;
					}	
				}
			}
		}
	}
	
	//Java Swing methods.
	/* Creates and sets up the player selection menu */
	private void loadMenu(){
		nameEntryLabel = new JLabel("New hero");
		selectPlayer = new JComboBox();
		newPlayer = new JTextField(10);
		newPlayer.addActionListener(this);
		deleteEntry = new JButton("Delete");
		startGame = new JButton("Go!");
		saveAndExit = new JButton("Quit");
		saveAndExit.setVisible(false);
		refreshMenu();
		add(saveAndExit,NORTH);
		add(nameEntryLabel,NORTH);
		add(newPlayer,NORTH);
		add(selectPlayer,NORTH);
		add(deleteEntry,NORTH);
		add(startGame,NORTH);
		addActionListeners();
	}
	
	/* Sets all menu objects to be visible */
	private void refreshMenu(){
		selectPlayer.removeAllItems();
		for(int i=0;i<save.getSize();i++){
			selectPlayer.addItem(save.getEntry(i).simplifiedToString());
		}
		startGame.setVisible(true);
		deleteEntry.setVisible(true);
		newPlayer.setVisible(true);
		selectPlayer.setVisible(true);
		nameEntryLabel.setVisible(true);
		saveAndExit.setVisible(false);
	}
	
	/* Sets all menu objects to be invisible */
	private void hideMenu(){
		startGame.setVisible(false);
		deleteEntry.setVisible(false);
		newPlayer.setVisible(false);
		selectPlayer.setVisible(false);
		nameEntryLabel.setVisible(false);
		saveAndExit.setVisible(false);
	}
	
	//Methods for the current game
	
	/* Stops all music */
	private void stopMusic(){
		menuClip.stop();
		deadClip.stop();
		level1Through3.stop();
		levels4Through7.stop();
		levels8Through11.stop();
	}
	
	/* Chooses the music for the level */
	private void chooseMusic(int level){
		if(level==0){
			stopMusic();
			menuClip.play();
		}else if(level==1){	
			stopMusic();
			level1Through3.play();
		}else if(level==4){
			stopMusic();
			levels4Through7.play();
		}else if (level==7){
			stopMusic();
			levels8Through11.play();
		}else if (level==-1){
			stopMusic();
		}
	}
	
	/* Plays the animation for the menu */
	private void playMenuAnimation(){
		for(int j=1;j<=15;j++){
			if(readyToPlay!=true){
				GImage frame = new GImage("Menu_"+j+".png");
				frame.scale(SCALE_FACTOR);
				add(frame);
				pause(175);
				if(getElementCount()>1) remove(getElement(0));
			}
		}
	}
	
	/* Plays an animation for a teleportation between levels */
	private void playTeleportationAnimation(){
		teleportClip.play();
		for(int j=1;j<=5;j++){
			GImage frame = new GImage("Teleport_"+j+".png");
			frame.scale(SCALE_FACTOR);
			add(frame);
			pause(100);
			if(getElementCount()>1) remove(getElement(0));
		}
	}
	
	/* Plays the death animations */
	private void playDeathAnimation(){
		for(int j=1;j<=5;j++){
			GImage frame = new GImage("Lost_"+j+".png");
			frame.scale(SCALE_FACTOR);
			add(frame);
			if(j==2) deadClip.play();
			pause(3300);
			if(getElementCount()>1) remove(getElement(0));
		}
		pause(300);
	}
	
	/* Initializes a level */
	private void initializeLevel(){
		Dungeon = new DungeonGenerator(player.getLevel(), player.getSeed());
		if((player.getPosition().getX()==0)&&(player.getPosition().getY()==0)){
			player.setPosition(Dungeon.getPlayerStart());
		}
		player.setTextureMap(Dungeon.getTextureMap());
		player.clearCells();
		popups.clear();
		shopKeepers.clear();
		equipables.clear();
		items.clear();
		mobs.clear();
		lights.clear();
		boss.clear();
		lights.add(new LightMobile(0,player.getPosition(),3*player.getTorchFuelPercentage(),0.2,0.05,1));
		if(Dungeon.getMapType().equals("Dungeon")){
			generateMonsters();
			generateItems();
			generateEquipables();
		}else if(Dungeon.getMapType().equals("ExplodedCave")){
			shopKeepers.add(new ShopKeeperMobile(Dungeon.getRandomSpawn(),player.getLevel()));
			for(int i=0;i<shopKeepers.size();i++){
				lights.add(new LightMobile(0,shopKeepers.get(i).getPosition(),3.0,0.2,0.1,20));
			}
		}else if(Dungeon.getMapType().equals("Maze")){
			generateMonsters();
		}
	}
	
	/* Adds equipables to the level */
	private void generateEquipables(){
		int maxEquips = rgen.nextInt(player.getLevel());
		for(int i=0;i<maxEquips;i++){
			Equipable equip = new Equipable(Dungeon.getRandomSpawn(),player.getLevel());
			if(equip.getPosition()!=null) equipables.add(equip);
			
		}
	}
	
	/* Adds items to the level */
	private void generateItems(){
		for(int i=0;i<player.getLevel();i++){
			Item item = new Item(Dungeon.getRandomSpawn(),"Cell",player.getLevel());
			if(item.getPosition()!=null) items.add(item);
		}
		for(int i=0;i<player.getLevel()+5;i++){
			Item item = new Item(Dungeon.getRandomSpawn(),"Fuel",player.getLevel());
			if(item.getPosition()!=null) items.add(item);
		}
		for(int i=0;i<(player.getLevel()*0.5)+5;i++){
			Item item = new Item(Dungeon.getRandomSpawn(),"Coins",player.getLevel());
			if(item.getPosition()!=null) items.add(item);
		}
		for(int i=0;i<(player.getLevel()*0.5);i++){
			Item item = new Item(Dungeon.getRandomSpawn(),"Stones",player.getLevel());
			if(item.getPosition()!=null) items.add(item);
		}
	}
		
	/* Adds monsters to the level */
	private void generateMonsters(){
		//Generates the monsters
		if(Dungeon.getMapType().equals("Dungeon")){
			for(int i=0;i<(player.getLevel()*2)-1;i++){
			SkeletonMobile monster = new SkeletonMobile(Dungeon.getRandomSpawn(),Dungeon.getTextureMap(),Dungeon.generatePathFinder(player.getPosition()),rgen.nextDouble(0.01,DEFAULT_MONSTER_SPEED+(player.getLevel()*MONSTER_SPEED_BONUS)),player.getLevel());
			if(monster.getPosition()!=null) mobs.add(monster);
			}
			//Removes monsters if they are in your starting room
			for(int i=0;i<mobs.size();i++){
				if(rayCastToPlayer(Dungeon.getTextureMap(), mobs.get(i).getPosition().getX()*TILE_DIMENSION, mobs.get(i).getPosition().getY()*TILE_DIMENSION, Dungeon.getPlayerStart().getX()*TILE_DIMENSION, Dungeon.getPlayerStart().getY()*TILE_DIMENSION)){
					mobs.remove(i);
					i-=1;
				}
			}
		}else if(Dungeon.getMapType().equals("Maze")){
			mobs.clear();
			for(int i=0;i<((player.getLevel()/5)+1);i++){
				boss.add(new BossMobile(Dungeon.getTextureMap(),Dungeon.getRandomSpawn(),rgen.nextDouble(0.01,DEFAULT_MONSTER_SPEED+(player.getLevel()*MONSTER_SPEED_BONUS)),player.getLevel()));
				lights.add(new LightMobile(1,boss.get(i).getPosition(),3.0,0.2,0.1,20));
			}
		}
	}
	
	/* Actually renders the game */
	private void renderTextureMap(int[][][] textureMap,GPoint position,int APPLICATION_DIMENSION_TILES,double t){
		//Calculates the corner of the screen
		double yMin = (position.getY()*TILE_DIMENSION)-((APPLICATION_DIMENSION_TILES*TILE_DIMENSION)/2);
		double xMin = (position.getX()*TILE_DIMENSION)-((APPLICATION_DIMENSION_TILES*TILE_DIMENSION)/2);
		//Isolates a chunk of the whole textureMap for this function to use
		int[][][] smallChunk = generateSmallChunk(textureMap,yMin,xMin);
		//Allows the player to move.
		player.takeTurn();
		lights.get(0).setRadius(3*player.getTorchFuelPercentage());
		//Adds components requiring lighting calculations (Pre-Processing)
		renderItems(smallChunk,yMin,xMin);
		renderProjectiles(smallChunk,yMin,xMin);
		renderSprite(smallChunk,"Player",1,yMin,xMin,position,player.getDirection());
		renderMonsters(smallChunk,yMin,xMin);
		renderNPCS(smallChunk,yMin,xMin);
		renderEquipment(smallChunk,yMin,xMin);
		//Passes through and calculates brightness.
		int[][] screenArray = renderLighting(smallChunk,yMin,xMin);
		//Adds components that do not require lighting (Post Processing)
		renderGUI(screenArray,yMin,xMin);
		GImage screen = new GImage(screenArray);
		screen.scale(SCALE_FACTOR);
		add(screen);
		if(getElementCount()>1) remove(getElement(0));
	}
	
	/* Renders lighting in the map */
		private int[][] renderLighting(int[][][] inputArray,double yMin,double xMin){
			int lightValue = 0;
			int[][] screenArray = new int[APPLICATION_DIMENSION_TILES*TILE_DIMENSION][APPLICATION_DIMENSION_TILES*TILE_DIMENSION];
			for(int i= 0;i<APPLICATION_DIMENSION_TILES*TILE_DIMENSION;i++){
				for(int j=0;j<APPLICATION_DIMENSION_TILES*TILE_DIMENSION;j++){
					if(rayCastToPlayer(Dungeon.getTextureMap(),(int) (player.getPosition().getX()*TILE_DIMENSION),(int) (player.getPosition().getY()*TILE_DIMENSION),j+(int) (xMin),i+(int) (yMin))){
						lightValue = determineHighestLightValue(new GPoint((double) ((j+xMin)/TILE_DIMENSION),(double) ((i+yMin)/TILE_DIMENSION)));
					}else{
						lightValue = 0;
					}
					screenArray[i][j] = inputArray[i][j][lightValue];
				}
			}
			return screenArray;
		}
			
	
	/* Determines the highest light value for a given pixel */
		private int determineHighestLightValue(GPoint input){
			int lightValue = 0;
			int altLightValue = 5;
			for(int i=0;i<lights.size();i++){
				if(rayCastToPlayer(Dungeon.getTextureMap(),(int) (input.getX()*TILE_DIMENSION),(int) (input.getY()*TILE_DIMENSION),(int) (lights.get(i).getPosition().getX()*TILE_DIMENSION),(int) (lights.get(i).getPosition().getY()*TILE_DIMENSION))){
					int currentLightValue = lights.get(i).getLightValue(input,t);
					if((currentLightValue<5)&&(currentLightValue>lightValue)){
						lightValue = lights.get(i).getLightValue(input,t);
					}else if((currentLightValue>5&&currentLightValue>altLightValue)){
						altLightValue = currentLightValue;
					}
				}
			}
			int light = 0;
			if(lightValue>(altLightValue-5)){
				light = lightValue-(altLightValue-5);
			}else if(lightValue<(altLightValue-5)){
				light = altLightValue-lightValue;
			}else{
				light = 0;
			}
			if(light==5) light=0;
			return light;
		}
		
	/* Creates a small chunk of the array for the renderer to use */
		private int[][][] generateSmallChunk(int[][][] textureMap,double yInitial,double xInitial){
			int[][][] smallChunk = new int[APPLICATION_DIMENSION_TILES*TILE_DIMENSION][APPLICATION_DIMENSION_TILES*TILE_DIMENSION][NUMBER_COLORS];
			for(int i=0;i<APPLICATION_DIMENSION_TILES*TILE_DIMENSION;i++){
				for(int j=0;j<APPLICATION_DIMENSION_TILES*TILE_DIMENSION;j++){
					for(int k=0;k<NUMBER_COLORS;k++){
						smallChunk[i][j][k]=textureMap[(int) (yInitial)+i][(int) (xInitial)+j][k];
					}
				}
			}
			return smallChunk;
		}
	
	/* Allows for a brief flash of light */
	private void flash(double intensity){
		double oldLightValue = player.getTorchFuel();
		player.addTorchFuel(intensity-oldLightValue);
		renderTextureMap(Dungeon.getTextureMap(),player.getPosition(),APPLICATION_DIMENSION_TILES,t);
		player.setTorchFuel(oldLightValue);
	}
	
	/* Renders npc's */
	private void renderNPCS(int[][][] smallChunk,double yMin, double xMin){
		for(int k=0;k<shopKeepers.size();k++){
			shopKeepers.get(k).setActivity(false);
			//Renders the shopkeeper
			renderSprite(smallChunk,"ShopKeeper",1,yMin,xMin,shopKeepers.get(k).getPosition(),shopKeepers.get(k).getDirection());
			//If the shopkeeper is close enough, set it to be active.
			if((Math.pow((Math.pow((shopKeepers.get(k).getPosition().getX()-player.getPosition().getX()),2)+Math.pow((shopKeepers.get(k).getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5)){
				shopKeepers.get(k).setActivity(true);
			}
		}
	}
	
	/* Renders the monsters */
	private void renderMonsters(int[][][] smallChunk,double yMin,double xMin){
		//Lets the monsters take thier turn.
		for(int i=0;i<mobs.size();i++){
			mobs.get(i).takeTurn(Dungeon.generatePathFinder(player.getPosition()), player.getPosition());
			//Lets the skeleton attack, if applicable.
			if(Math.pow((Math.pow((mobs.get(i).getPosition().getX()-player.getPosition().getX()),2)+Math.pow((mobs.get(i).getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5){
				player.attack(mobs.get(i).getDamage());
				playerHit.play();
			}
			if(mobs.get(i).getHealth()<=0){
				skeletonKilled.play();
				boolean willDrop = rgen.nextBoolean(0.05+(player.getRanged().getDigBonus()-1));
				if(willDrop==true){
					items.add(new Item(mobs.get(i).getPosition(),"Coins",player.getLevel()));
				}
				if(i<mobs.size()) {
					mobs.remove(i);
					i -= 1;
				}
			}	
		}
		//Lets the bosses take thier turn.
		for(int i=0;i<boss.size();i++){
			boss.get(i).takeTurn(player.getPosition());
			//Lets the skeleton attack, if applicable.
			if(Math.pow((Math.pow((boss.get(i).getPosition().getX()-player.getPosition().getX()),2)+Math.pow((boss.get(i).getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5){
				player.attack(boss.get(i).getDamage());
				playerHit.play();
			}
			if(boss.get(i).getHealth()<=0){
				bossKilled.play();
				lights.remove(i+1);
				boolean willDrop = rgen.nextBoolean(0.05+(player.getRanged().getDigBonus()-1));
				items.add(new Item(boss.get(i).getPosition(),"Cell",player.getLevel()));
				if(willDrop==true){
					items.add(new Item(boss.get(i).getPosition(),"Coins",player.getLevel()+20));
				}
				if(i<boss.size()) {
					boss.remove(i);
					i -= 1;
				}
			}	
		}
		//Actually renders the monsters.
		for(int k=0;k<mobs.size();k++){
			SkeletonMobile mob = mobs.get(k);
			//Renders the skeleton
			renderSprite(smallChunk,"Skeleton",mob.getVariant(),yMin,xMin,mob.getPosition(),mob.getDirection());
		}
		//Renders the bosses.
		for(int l=0;l<boss.size();l++){
			BossMobile currentBoss = boss.get(l);
			renderSprite(smallChunk,"Boss",currentBoss.getVariant(),yMin,xMin,currentBoss.getPosition(),currentBoss.getDirection());
		}
	}
	
	/* Renders the items */
	private void renderItems(int[][][] smallChunk,double yMin,double xMin){
		for(int k=0;k<items.size();k++){
			Item item = items.get(k);
			//Renders the item
			renderSprite(smallChunk,item.getType(),item.getVariant(),yMin,xMin,item.getPosition(),item.getDirection());
			//Lets the player pick up item if applicable.
			if(Math.pow((Math.pow((item.getPosition().getX()-player.getPosition().getX()),2)+Math.pow((item.getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5){
				popups.add(new StringSprite(new GPoint(items.get(k).getPosition().getX(),items.get(k).getPosition().getY()-POPUP_PICKUP_HEIGHT_OFFSET),"+ "+items.get(k).getQuantity()+" "+items.get(k).getType(),"Orange",0.0125,10));
				switch (item.getType()){
				case "Coins":
					player.addMoney((int) (player.getMelee().getDigBonus()*item.getQuantity()));
					items.remove(items.get(k));
					switch(item.getVariant()){
					case 1:
						money_1.play();
						break;
					case 2:
						money_2.play();
						break;
					case 3:
						money_3.play();
						break;
					}
					break;
				case "Cell":
					teleportPickup.play();
					player.addCells(item.getQuantity());
					items.remove(items.get(k));
					break;
				case "Stones":
					projectilePickup.play();
					player.addStones(item.getQuantity());
					items.remove(items.get(k));
					break;
				case "Fuel":
					coalPickup.play();
					player.addTorchFuel((int) (player.getMelee().getDigBonus()*item.getQuantity()));
					items.remove(items.get(k));
					break;
				}
			}
		}
	}
	
	/* Renders equipment */
	private void renderEquipment(int[][][] smallChunk,double yMin,double xMin){
		for(int k=0;k<equipables.size();k++){
			Equipable equip = equipables.get(k);
				//Renders the item
				renderSprite(smallChunk,equip.getType(),equip.getVariant(),yMin,xMin,equip.getPosition(),equip.getDirection());
				//Determines if the item is in range
				if((Math.pow((Math.pow((equip.getPosition().getX()-player.getPosition().getX()),2)+Math.pow((equip.getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5)){
					//Picks up the item if applicable.
					if(pickupReady == true){
						switch(equip.getVariant()){
							case 1:
								money_1.play();
								break;
							case 2:
								money_2.play();
								break;
							case 3:
								money_3.play();
								break;
						}
						Equipable newItem = player.switchItem(equip);
						if(newItem!=null){
							newItem.setPosition(new GPoint(player.getPosition().getX(),player.getPosition().getY()));
							equipables.set(k,newItem);
						}else{
							equipables.remove(k);
						}
				pickupReady = false;
				}
			}
		}
	}
	
	/* Renders projectiles on the map */
	private void renderProjectiles(int[][][] smallChunk,double yMin,double xMin){
		//Actually renders the projectiles on the map.
		for(int i=0;i<projectiles.size();i++){
			if(projectiles.get(i).projectileFree()){
				projectiles.get(i).takeTurn();
				renderSprite(smallChunk,"Projectile",projectiles.get(i).getVariant(),yMin,xMin,projectiles.get(i).getPosition(),projectiles.get(i).getDirection());
			}else{
				projectiles.remove(i);	
				i=-1;
				projectileLost.play();
			}
		}
		//Allows the projectiles to attack mobs if applicable.
		for(int i=0;i<mobs.size();i++){
			for(int j=0;j<projectiles.size();j++){
				if((Math.pow((Math.pow((mobs.get(i).getPosition().getX()-projectiles.get(j).getPosition().getX()),2)+Math.pow((mobs.get(i).getPosition().getY()-projectiles.get(j).getPosition().getY()),2)),0.5)<0.5)){
				mobs.get(i).attacked(projectiles.get(j).getDamage());
				skeletonHit.play();
				popups.add(new StringSprite(new GPoint(mobs.get(i).getPosition().getX(),mobs.get(i).getPosition().getY()-DEFAULT_PLAYER_DIMENSION),new Integer((int) projectiles.get(j).getDamage()).toString(),"Orange",0.0125,20.0));
				//popups.add(new StringSprite(new GPoint(projectiles.get(j).getPosition().getX(),projectiles.get(j).getPosition().getY()-DEFAULT_PLAYER_DIMENSION),"!@#$^&*()_-+={[}]|;<>,.?\\~`","Gold",0.0125,2000.0));
				if(j<projectiles.size()&&projectiles.size()>0) projectiles.remove(projectiles.get(j));
				j-=1;
				}
			}
		}
		for(int i=0;i<boss.size();i++){
			for(int j=0;j<projectiles.size();j++){
				if((Math.pow((Math.pow((boss.get(i).getPosition().getX()-projectiles.get(j).getPosition().getX()),2)+Math.pow((boss.get(i).getPosition().getY()-projectiles.get(j).getPosition().getY()),2)),0.5)<0.5)){
				boss.get(i).attacked(projectiles.get(j).getDamage());
				bossHit.play();
				popups.add(new StringSprite(new GPoint(boss.get(i).getPosition().getX(),boss.get(i).getPosition().getY()-DEFAULT_PLAYER_DIMENSION),new Integer((int) projectiles.get(j).getDamage()).toString(),"Orange",0.0125,20.0));
				//popups.add(new StringSprite(new GPoint(projectiles.get(j).getPosition().getX(),projectiles.get(j).getPosition().getY()-DEFAULT_PLAYER_DIMENSION),"!@#$^&*()_-+={[}]|;<>,.?\\~`","Gold",0.0125,2000.0));
				if(j<projectiles.size()&&projectiles.size()>0) projectiles.remove(projectiles.get(j));
				j-=1;
				}
			}
		}
	}
	
	/* Renders the GUI for the game and any associated popups */
	private void renderGUI(int[][] screenArray,double yMin,double xMin){
		//Renders string popups
		for(int k=0;k<popups.size();k++){
			popups.get(k).takeTurn();
			renderGUIElement(screenArray,yMin,xMin,popups.get(k).getCardPosition(),popups.get(k).getCard());
			if(popups.get(k).getHealth()<0) popups.remove(k);
		}
		//Renders popups for shopKeepers
		for(int i = 0;i<shopKeepers.size();i++){
			if(this.getMousePosition()!=null){
				renderGUIElement(screenArray,yMin,xMin,shopKeepers.get(i).getCardPosition(),shopKeepers.get(i).getCard(new GPoint(((((this.getMousePosition())).getX()/(APPLICATION_DIMENSION_PIXELS))*APPLICATION_DIMENSION_TILES)+player.getPosition().getX()-(APPLICATION_DIMENSION_TILES/2.0),(((this.getMousePosition().getY()-SWING_BAR_HEIGHT)/(APPLICATION_DIMENSION_PIXELS))*APPLICATION_DIMENSION_TILES)+player.getPosition().getY()-(APPLICATION_DIMENSION_TILES/2.0)),player.getPosition(),(int) player.getTorchFuel(),(int) player.getTorchFuelMax()));
			}else{
				renderGUIElement(screenArray,yMin,xMin,shopKeepers.get(i).getCardPosition(),shopKeepers.get(i).getCard(new GPoint(0,0),player.getPosition(),(int) player.getTorchFuel(),(int) player.getTorchFuelMax()));
			}
		}
		//Renders stats for the player's current pickups.
		renderGUIElement(screenArray,yMin,xMin,player.getMeleeTabPosition(),player.getMeleeTab(t-meleeLastFire));
		renderGUIElement(screenArray,yMin,xMin,player.getRangedTabPosition(),player.getRangedTab(t-rangedLastFire));
		renderGUIElement(screenArray,yMin,xMin,player.getMoneyTabPosition(),player.getMoneyTab());
		//Renders item cards for the pickups on the map.
		for(int k=0;k<equipables.size();k++){
			Equipable equip = equipables.get(k);
			if((Math.pow((Math.pow((equip.getPosition().getX()-player.getPosition().getX()),2)+Math.pow((equip.getPosition().getY()-player.getPosition().getY()),2)),0.5)<0.5)){
				//Renders the item card for the player.
				switch(equip.getType()){
					case("Tool"):
						renderGUIElement(screenArray,yMin,xMin,equip.getCardPosition(),equip.getCard(player.getMelee().getDamage(), player.getMelee().getRange(), player.getMelee().getDigBonus(), player.getMelee().getDelay()));
						break;
					case("Blade"):
						renderGUIElement(screenArray,yMin,xMin,equip.getCardPosition(),equip.getCard(player.getMelee().getDamage(), player.getMelee().getRange(), player.getMelee().getDigBonus(), player.getMelee().getDelay()));
						break;
					case("Sling"):
						renderGUIElement(screenArray,yMin,xMin,equip.getCardPosition(),equip.getCard(player.getRanged().getDamage(), player.getRanged().getRange(), player.getRanged().getDigBonus(), player.getRanged().getDelay()));
						break;
					case("Musket"):
						renderGUIElement(screenArray,yMin,xMin,equip.getCardPosition(),equip.getCard(player.getRanged().getDamage(), player.getRanged().getRange(), player.getRanged().getDigBonus(), player.getRanged().getDelay()));
						break;
				}
			}
		}
		//Renders a notification, for when you are ready to teleport.
		if(player.getCells()>=player.getLevel()||(Dungeon.getMapType().equals("ExplodedCave")||((Dungeon.getMapType().equals("Maze"))&&(boss.size()==0)))){
			GImage teleportBarNotification = new GImage("nextLevel.png");
			GPoint elementPosition = new GPoint(player.getPosition().getX(),player.getPosition().getY()+(APPLICATION_DIMENSION_TILES/2)-1);
			renderGUIElement(screenArray,yMin,xMin,elementPosition,teleportBarNotification.getPixelArray());
		}
	}
	
	/* Renders a single GUI element on the finalized screenArray */
	private void renderGUIElement(int[][] screenArray,double yMin,double xMin,GPoint position,int[][] arrayElement){
		for(int i=0;i<arrayElement.length;i++){
			for(int j=0;j<arrayElement[0].length;j++){
				if((arrayElement[i][j]!=READ_TRANSPARENT)&&(withinBoundaries(0,0,screenArray[0].length,screenArray.length,((int) (((position.getX()-((arrayElement[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j),((int) (((position.getY()-((arrayElement.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i)))){
					screenArray[(int) ((((position.getY()-((arrayElement.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i)][(int) ((((position.getX()-((arrayElement[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j)] = arrayElement[i][j];
				}
			}
		}
	}
	
	/* Simple function that verifies that a point is within a set of bounds */
	private boolean withinBoundaries(double xMin,double yMin,double xMax,double yMax,double x,double y){
		if(y>yMin&&y<yMax&&x>xMin&&x<xMax){
			return true;
		}else{
			return false;
		}
	}
	
	/* Makes sure a given pixel is in the line of sight */
	private boolean rayCastToPlayer(int[][][] textureMap,double xPoint,double yPoint,double xOrigin,double yOrigin){
		//These processes iterate through the line y=mx+b pixel by pixel for wall where the axis "x" is the longest axis.
		//Conceptually divides the region into 4 sectors in an X shape and determines which quadrant (triangular shape) to calculate for based on axis length.
		//Attempts to divide region in half produced artifacts when input axis became small and slope approached infinity.
		//Checks left and right X sector.
		if((xOrigin<xPoint)&&(Math.abs(yPoint-yOrigin)<=Math.abs(xPoint-xOrigin))){
			for(int i=(int) xOrigin;i<(int) (xPoint);i+=1){
				if(textureMap[(int) (yOrigin+((i-xOrigin)*((yPoint-yOrigin)/(xPoint-xOrigin))))][i][0]==READ_BLOCKED){
					return false;
				}
			}
		}
		if((xOrigin>xPoint)&&(Math.abs(yPoint-yOrigin)<=Math.abs(xPoint-xOrigin))){
			for(int i=(int) xOrigin;i>(int) (xPoint);i-=1){
				if(textureMap[(int) (yOrigin+((i-xOrigin)*((yPoint-yOrigin)/(xPoint-xOrigin))))][i][0]==READ_BLOCKED){
					return false;
				}
			}
		}
		//Checks upper and lower Z sector.
		if((yOrigin<yPoint)&&(Math.abs(yPoint-yOrigin)>=Math.abs(xPoint-xOrigin))){
			for(int i=(int) yOrigin;i<(int) (yPoint);i+=1){
				if(textureMap[i][(int) (xOrigin+((i-yOrigin)*((xPoint-xOrigin)/(yPoint-yOrigin))))][0]==READ_BLOCKED){
					return false;
				}
			}
		}
		if((yOrigin>yPoint)&&(Math.abs(yPoint-yOrigin)>=Math.abs(xPoint-xOrigin))){
			for(int i=(int) yOrigin;i>(int) (yPoint);i-=1){
				if(textureMap[i][(int) (xOrigin+((i-yOrigin)*((xPoint-xOrigin)/(yPoint-yOrigin))))][0]==READ_BLOCKED){
					return false;
				}
			}
		}
	return true;
	}
	
	/* Puts the sprite on the screen */
	private void renderSprite(int[][][] smallChunk,String spriteType,int variant,double yMin,double xMin,GPoint position,String spriteDirection){
		if((position.getX()<(((smallChunk[0].length+xMin)/TILE_DIMENSION)+1))&&(position.getX()>((xMin/TILE_DIMENSION)-1))&&(position.getY()<(((smallChunk.length+yMin)/TILE_DIMENSION)+1))&&(position.getY()>((yMin/TILE_DIMENSION)-1))){
			//Initializes all of the sprite data for a real sprite.
			int[][] tileUltraDark = new GImage(spriteType+"_"+variant+"_UltraDark_"+spriteDirection+".png").getPixelArray();				
			int[][] tileDark = new GImage(spriteType+"_"+variant+"_Dark_"+spriteDirection+".png").getPixelArray();
			int[][] tileLowLight = new GImage(spriteType+"_"+variant+"_LowLight_"+spriteDirection+".png").getPixelArray();
			int[][] tileLight = new GImage(spriteType+"_"+variant+"_Light_"+spriteDirection+".png").getPixelArray();
			int[][] altTileUltraDark = new GImage(spriteType+"_"+variant+"_-UltraDark_"+spriteDirection+".png").getPixelArray();				
			int[][] altTileDark = new GImage(spriteType+"_"+variant+"_-Dark_"+spriteDirection+".png").getPixelArray();
			int[][] altTileLowLight = new GImage(spriteType+"_"+variant+"_-LowLight_"+spriteDirection+".png").getPixelArray();
			int[][] altTileLight = new GImage(spriteType+"_"+variant+"_-Light_"+spriteDirection+".png").getPixelArray();
			//Actually adds the sprite.
			for(int i=0;i<tileLight.length;i++){
				for(int j=0;j<tileLight[0].length;j++){
					if((tileLight[i][j]!=READ_TRANSPARENT)&&(withinBoundaries(0,0,smallChunk[0].length,smallChunk.length,((int) (((position.getX()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j),((int) (((position.getY()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i)))){
						//Selects which light level array to sample from based on a pixels light value.
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][0] = Color.BLACK.getRGB();
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][1] = tileUltraDark[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][2] = tileDark[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][3] = tileLowLight[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][4] = tileLight[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][6] = altTileUltraDark[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][7] = altTileDark[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][8] = altTileLowLight[i][j];
						smallChunk[(int) (((position.getY()-((tileLight[0].length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-yMin)+i][(int) (((position.getX()-((tileLight.length/TILE_DIMENSION)*0.5))*TILE_DIMENSION)-xMin)+j][9] = altTileLight[i][j];
					}
				}
			}
		}
	}
	
	/** Commands for GUI buttons */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newPlayer){
			newPlayer.setText(newPlayer.getText().trim());
			if((newPlayer.getText()!=null)&&(newPlayer.getText().length()>0)&&!(newPlayer.getText().contains(" "))){
				player = new PlayerMobile();
				player.setName(newPlayer.getText());
				save.newPlayer(player);
				refreshMenu();
				newPlayer.setText(null);
			}
		}else if(e.getSource()==deleteEntry){
			if(selectPlayer.getItemAt(0)!=null){
				save.deletePlayer(save.getEntry(selectPlayer.getSelectedIndex()));
				refreshMenu();
			}
		}else if(e.getSource()==startGame){
			if(selectPlayer.getItemAt(0)!=null){
				player = save.getEntry(selectPlayer.getSelectedIndex());
				readyToPlay = true;
				hideMenu();
				saveAndExit.setVisible(true);
			}
		}else if (e.getSource()==saveAndExit){
			save.saveFile();
			readyToPlay = false;
			saveAndExit.setVisible(false);
		}
			
	}

	/** Commands for pressed keys */
	public void keyPressed(KeyEvent e){
		switch (e.getKeyCode()) {
			// Movement
			case 'D':
				player.setDX(DEFAULT_PLAYER_SPEED);
				player.setDirection("Right");
				break;
			case 'A':
				player.setDX(-DEFAULT_PLAYER_SPEED);
				player.setDirection("Left");
				break;
			case 'S':
				player.setDY(DEFAULT_PLAYER_SPEED);
				player.setDirection("Down");
				break;
			case 'W':
				player.setDY(-DEFAULT_PLAYER_SPEED);
				player.setDirection("up");
				break;
			// Items
			case 'E':
				pickupReady = true;
				break;
			// Next level
			case ' ':
				if((player.getCells()>=player.getLevel())||(Dungeon.getMapType().equals("ExplodedCave"))||((Dungeon.getMapType().equals("Maze"))&&(boss.size()==0))) teleportReady=true;
				break;
			// Controls
			case 27: // Escape key
				save.saveFile();
				readyToPlay = false;
				saveAndExit.setVisible(false);
				break;
		}
	}	
	
	/** Commands for released keys */
	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()) {
			// Movement
			case 'D':
				player.setDX(0);
				break;
			case 'A':
				player.setDX(0);
				break;
			case 'S':
				player.setDY(0);
				break;
			case 'W':
				player.setDY(0);
				break;
			// Items
			case 'E':
				pickupReady = false;
				break;	
		}
		//Reverifies direction
		if(player.getDY()>0){
			player.setDirection("Down");
		}else if(player.getDY()<0){
			player.setDirection("Up");
		}
		if(player.getDX()<0){
			player.setDirection("Left");
		}else if (player.getDX()>0){
			player.setDirection("Right");
		}
	}
	
	/** Takes care of mouse events */
	public void mousePressed(MouseEvent e){
		int button = e.getButton();
		//Left mouse button
		if(button==1){
			for(int i=0;i<shopKeepers.size();i++){
				if(shopKeepers.get(i).isActive()){
					if(shopKeepers.get(i).coalSelected()){
						if(player.payMoney(shopKeepers.get(i).getCoalRefillPrice())){
							player.addTorchFuel(player.getTorchFuelMax()-player.getTorchFuel());
							shopPurchase.play();
						}else if(player.getMoney()>0){
							player.addTorchFuel(shopKeepers.get(i).payForCoal(player.getMoney()));
							player.payMoney(player.getMoney());
							shopPurchase.play();
						}else if(player.getMoney()==0){
							outOfProjectiles.play();
						}
					}else if(shopKeepers.get(i).shotSelected()){
						if(player.payMoney(shopKeepers.get(i).getShotPrice())){
							player.addStones(shopKeepers.get(i).payForShots(shopKeepers.get(i).getShotPrice()));
							shopPurchase.play();
						}else if(player.getMoney()>0){
							player.addStones(shopKeepers.get(i).payForShots(player.getMoney()));
							player.payMoney(player.getMoney());
							shopPurchase.play();
						}else if(player.getMoney()==0){
							outOfProjectiles.play();
						}
					}else{
						if(player.payMoney(shopKeepers.get(i).getSelectedEquipablePrice())&&(shopKeepers.get(i).getSelectedEquipablePrice()!=0)){
							Equipable newItem = player.switchItem(shopKeepers.get(i).getSelectedEquipable());
							if(newItem!=null){
								newItem.setPosition(new GPoint(player.getPosition().getX(),player.getPosition().getY()));
								equipables.add(newItem);
								shopPurchase.play();
							}
						}else if(shopKeepers.get(i).getSelectedEquipablePrice()!=0){
							outOfProjectiles.play();
						}
					}
				return;
				}
			}
			if(t>meleeLastFire+player.getMelee().getDelay()){
				GPoint start = new GPoint(player.getPosition().getX(),player.getPosition().getY());
				ProjectileMobile projectile = new ProjectileMobile(start,Dungeon.getTextureMap(),player.getMelee().getSpeed(),player.getMelee().getRange(),Math.atan2((e.getY()-(APPLICATION_DIMENSION_PIXELS/2)),(e.getX()-(APPLICATION_DIMENSION_PIXELS/2))),player.getMelee().getDamage(),player.getMelee().getType());
				projectiles.add(projectile);
				switch(player.getMelee().getType()){
				case "Tool":
					toolAttack.play();
					break;
				case "Blade":
					bladeAttack.play();	
					break;
				}
				meleeLastFire = t;
				return;
			}
		//Pressing down the mouse-wheel
		}else if(button==2){
			if(CHEAT_MODE){
				player.addTorchFuel(1000);
				renderTestMap(Dungeon.generatePathFinder(player.getPosition()));
				player.addCells(player.getLevel());
				player.addStones(100);
				player.addMoney(1000);
				
			}
		//Right mouse button
		}else if(button==3){
			if((t>rangedLastFire+player.getRanged().getDelay())&&(player.getStones()>0)){
				GPoint start = new GPoint(player.getPosition().getX(),player.getPosition().getY());
				ProjectileMobile projectile = new ProjectileMobile(start,Dungeon.getTextureMap(),player.getRanged().getSpeed(),player.getRanged().getRange(),Math.atan2((e.getY()-(APPLICATION_DIMENSION_PIXELS/2)),(e.getX()-(APPLICATION_DIMENSION_PIXELS/2))),player.getRanged().getDamage(),player.getRanged().getType());
				projectiles.add(projectile);
				player.subtractStone(1);
				switch(player.getRanged().getType()){
					case "Sling":
						slingAttack.play();
						break;
					case "Musket":
						musketAttack.play();
						flash(5000);
						break;
				}
				rangedLastFire = t;
			}else if (player.getStones()<=0){
				outOfProjectiles.play();
			}
		}
	}

	// Debugging functions
	/** Renders the world map for testing purposes. Specifically renders pathmap with -1 as the target position */
	public void renderTestMap(int[][] testMap){
		String currentString = "";
		for (int i=0;i<Dungeon.getSize();i++){
			currentString = "";
			String space = "";
			for(int j=0;j<Dungeon.getSize();j++){
				space = "";
				if(testMap[i][j]==0) currentString += "MWM";
				if(testMap[i][j]==1) currentString += "   ";
				if(testMap[i][j]==2) currentString += " X ";
				if(testMap[i][j]>-10) space = " ";
				if(testMap[i][j]<0) currentString += +testMap[i][j]+space;
			}
		println (currentString);
		}
	println();
	}
}

// Peppers - Monteray