/*
 * File: DungeonGenerator.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one map. Constructor creates a new map
 * from the given level and seed. Main rgen sets the seed for level and
 * spawn rgen. This enables greater control over the level's overall seed
 * making map saving and accessing easier, keeps low level nextInt commands
 * from interfering with overall scheme.
 */

import java.awt.*;
import java.util.*;

import acm.graphics.*;
import acm.util.*;

public class DungeonGenerator implements TorchBearerConstants{

	private String mapType = "Dungeon";
	private int mapDimension;
	private int seed;
	private int level;
	
	private int[][] worldMap;
	private int[][][] textureMap;
	
	private ArrayList<GPoint> spawnPoints = new ArrayList<GPoint>();
	private static GPoint playerStart;
	
	private RandomGenerator mainRgen = RandomGenerator.getInstance();
	private RandomGenerator levelRgen = RandomGenerator.getInstance();
	private RandomGenerator spawnRgen = RandomGenerator.getInstance();
	
	//For explosion cave based dungeon 
	private int explosionIterations = 20;
	private int blastMinimum = 2;
	private int blastMaximum = 5;
	
	//For room based dungeon
	private int roomAttempts;
	private int attemptsPerRoom = CONSTRUCTION_ATTEMPTS;
	

	
	/** Constructor requires the new level number and seed, essentially creates a new level */
	DungeonGenerator(int newLevel,int newSeed){
		mapDimension = (int) (Math.log(level/40+1)*30+20+MAP_BORDER*2);
		seed = newSeed;
		level = newLevel;
		mainRgen.setSeed(seed);
		//Catches up to the current level for save purposes
		for(int i=0;i<level;i++){
			levelRgen.setSeed(mainRgen.nextInt());
		}
		roomAttempts = level*3;
		//Might be a cave
		if(mainRgen.nextBoolean(EXPLODEDCAVE_CHANCE)){
			mapType = "ExplodedCave";
		}
		if(mainRgen.nextBoolean(MAZE_CHANCE)){
			mapType = "Maze";
			mapDimension = 20;
		}
		textureMap = generateMap();
		generateSpawnPoints();
	}
	
	/* Generates the possible spawnpoints in the map */
	private void generateSpawnPoints(){
		for(int i=0;i<mapDimension;i++){
			for(int j=0;j<mapDimension;j++){
				if((worldMap[i][j]!=0)&&worldMap[i][j]!=19){
					GPoint spawnPoint = new GPoint(j,i);
					spawnPoints.add(spawnPoint);
				}
			}
		}
	}
	
	/** Returns a random valid spawnpoint in the map */
	public GPoint getRandomSpawn(){
		if(spawnPoints.size()>0){
			int i = spawnRgen.nextInt(spawnPoints.size());
			GPoint spawn = spawnPoints.get(i);
			spawnPoints.remove(i);
			spawn.translate(0.5,0.5);
			return spawn;
		}else{
			return null;
		}
	}
	
	/** Returns the compiled textureMap for view and modification */
	public int[][][] getTextureMap(){
		return textureMap;
	}
	
	/** Returns the starting position for the player */
	public GPoint getPlayerStart(){
		return playerStart;
	}
	
	/** Returns the worldMap for pathfinding calculations */
	public int[][] getWorldMap(){
		return worldMap;
	}
	
	/* Creates the world map */
	private int[][][] generateMap(){
		worldMap = new int[mapDimension+(MAP_BORDER*2)][mapDimension+(MAP_BORDER*2)];
		if(mapType.equals("Dungeon")){
			worldMap = generateRooms(worldMap);
		}else if(mapType.equals("ExplodedCave")){
			worldMap = generateExplodedCave(worldMap);
		}else if(mapType.equals("Maze")){
			worldMap = generateMaze(worldMap);
		}
		int [][] detailMap = generateDetails(worldMap);
		int[][][] textureMap = generateTextures(detailMap);
		return textureMap;
	}
	
	/* Builds a room and makes sure not to overlap */
	private int[][] generateRooms(int[][] worldMap){
		int previousRoomRow = 0;
		int previousRoomColumn = 0;
		int currentRoomAttemptCounter = 0;
		for (int i=0;i<roomAttempts;i++){
			boolean roomGenerationSuccessfull = false;
			while (!roomGenerationSuccessfull&&currentRoomAttemptCounter<attemptsPerRoom){
				//Initializes room dimensions to try
				currentRoomAttemptCounter = currentRoomAttemptCounter+1;
				int centerOfRoomRow = levelRgen.nextInt(MAP_BORDER,(mapDimension-MAP_BORDER));
				int centerOfRoomColumn = levelRgen.nextInt(MAP_BORDER,mapDimension-MAP_BORDER);
				int halfHeightOfRoom = levelRgen.nextInt(((ROOM_MIN_DIMENSION)/2),((ROOM_MAX_DIMENSION)/2)); 
				int halfWidthOfRoom = levelRgen.nextInt(((ROOM_MIN_DIMENSION)/2),((ROOM_MAX_DIMENSION)/2));
				//Checks to see if these parameters are valid
				roomGenerationSuccessfull = checkBuildRoom(worldMap,centerOfRoomColumn,centerOfRoomRow,halfWidthOfRoom,halfHeightOfRoom);
				if (roomGenerationSuccessfull){
					//Builds the room
					buildRoom(worldMap,centerOfRoomColumn,centerOfRoomRow,halfWidthOfRoom,halfHeightOfRoom);
					//Builds a connecting hallway (if applicable)
					buildHallway(worldMap,previousRoomColumn,previousRoomRow,centerOfRoomColumn,centerOfRoomRow);
					//Setting up for next room
					previousRoomRow = centerOfRoomRow;
					previousRoomColumn = centerOfRoomColumn;
					worldMap[previousRoomRow][previousRoomColumn] = 2;
					playerStart = new GPoint(centerOfRoomColumn,centerOfRoomRow);
				}
			}
		}
	return worldMap;
	}
	
	/* Verifies space is available */
	private boolean checkBuildRoom(int[][] worldMap, int centerOfRoomColumn,int centerOfRoomRow,int halfWidthOfRoom,int halfHeightOfRoom){
		//First step, check if the theoretical room position and boundaries are good
		if ((worldMap[centerOfRoomRow][centerOfRoomColumn]==0)&&((centerOfRoomRow+halfHeightOfRoom)<(mapDimension-MAP_BORDER))&&(((centerOfRoomRow-halfHeightOfRoom)>MAP_BORDER))&&((centerOfRoomColumn+halfWidthOfRoom)<(mapDimension-MAP_BORDER)&&((centerOfRoomColumn-halfWidthOfRoom)>MAP_BORDER))){
			//Second step, check to see if the room is available.
			for (int i=((centerOfRoomColumn-halfWidthOfRoom)-1);i<(centerOfRoomColumn+halfWidthOfRoom+1);i++){
				for (int j=((centerOfRoomRow-halfHeightOfRoom)-1);j<(centerOfRoomRow+halfHeightOfRoom+1);j++){
					if (!(worldMap[j][i]==0)){
						return false;
					}
				}
			}
		return true;
		}else{
			return false;
		}
	}
	
	/* Builds the actual room if conditions are right.--This is good but might need work */
	private void buildRoom(int[][] worldMap,int centerOfRoomColumn,int centerOfRoomRow,int halfWidthOfRoom,int halfHeightOfRoom){
		for (int i=(centerOfRoomRow-halfHeightOfRoom);i<(centerOfRoomRow+halfHeightOfRoom);i++){
			for (int j=(centerOfRoomColumn-halfWidthOfRoom);j<(centerOfRoomColumn+halfWidthOfRoom);j++){
			worldMap[i][j]=1;
			}
		}
	}
	
	/* Creates hallways, tries to spice things up by alternating x axis first and y axis first */
	private void buildHallway(int[][] worldMap,int previousRoomColumn,int previousRoomRow,int centerOfRoomColumn,int centerOfRoomRow){
		if((!(previousRoomRow==0))&&(!(previousRoomColumn==0))){
			int hallOrientation = levelRgen.nextInt(1,2);
			switch (hallOrientation) {
			case 1:
				//builds horizontal bias
					if (centerOfRoomRow>=previousRoomRow){
						for (int i=previousRoomRow;i<centerOfRoomRow;i++){
							if (worldMap[i][centerOfRoomColumn] == 0){	
								worldMap[i][centerOfRoomColumn] = 1;
							}
						}
					}else if(centerOfRoomRow<previousRoomRow)
						for (int i=centerOfRoomRow;i<previousRoomRow;i++){
							if (worldMap[i][centerOfRoomColumn] == 0){	
								worldMap[i][centerOfRoomColumn] = 1;
							}
						}
					if (centerOfRoomColumn>previousRoomColumn){
						for (int j=previousRoomColumn;j<(centerOfRoomColumn+1);j++){
							if (worldMap[previousRoomRow][j] == 0){
								worldMap[previousRoomRow][j] = 1;
							}
						}
					}else if (centerOfRoomColumn<previousRoomColumn){
						for (int j=centerOfRoomColumn;j<previousRoomColumn;j++){
							if (worldMap[previousRoomRow][j] == 0){
								worldMap[previousRoomRow][j] = 1;
							}
						}
					}
					break;	
			//Builds vertical bias.
			case 2:
					if (centerOfRoomRow>=previousRoomRow){
						for (int i=previousRoomRow;i<(centerOfRoomRow+1);i++){
							if (worldMap[i][previousRoomColumn] == 0){	
								worldMap[i][previousRoomColumn] = 1;
							}
						}
					}else if(centerOfRoomRow<previousRoomRow)
						for (int i=centerOfRoomRow;i<previousRoomRow;i++){
							if (worldMap[i][previousRoomColumn] == 0){	
								worldMap[i][previousRoomColumn] = 1;
							}
						}
					if (centerOfRoomColumn>=previousRoomColumn){
						for (int j=previousRoomColumn;j<(centerOfRoomColumn+1);j++){
							if (worldMap[centerOfRoomRow][j] == 0){
								worldMap[centerOfRoomRow][j] = 1;
							}
						}
				}else if (centerOfRoomColumn<previousRoomColumn){
						for (int j=centerOfRoomColumn;j<previousRoomColumn;j++){
							if (worldMap[centerOfRoomRow][j] == 0){
								worldMap[centerOfRoomRow][j] = 1;
							}
						}
					}
				break;
				}	
			}
		}
	
	/* Generates a cavern by carving somewhat circular regions */
	private int[][] generateExplodedCave(int[][] mapArray){
		playerStart = new GPoint((int) (MAP_BORDER+mapDimension/2),(int) (MAP_BORDER+mapDimension/2));
		createExplosion(mapArray,playerStart,5);
		for(int i=0;i<explosionIterations;i++){
			boolean exploded = false;
			while(exploded!=true){
				int j = mainRgen.nextInt(MAP_BORDER,mapDimension+MAP_BORDER);
				int k = mainRgen.nextInt(MAP_BORDER,mapDimension+MAP_BORDER);
				int radius = mainRgen.nextInt(blastMinimum,blastMaximum);
				if((mapArray[j][k]==1)&&(j-radius>MAP_BORDER)&&(j+radius<mapDimension-MAP_BORDER)&&(k-radius>MAP_BORDER)&&(k+radius<mapDimension-MAP_BORDER)){
					createExplosion(mapArray,new GPoint(j,k),radius);
					exploded = true;
					break;
				}
			}
		}
		return mapArray;
	}
	
	/* Generates a maze with the hunter-killer algorith */
	private int[][] generateMaze(int[][] mapArray){
		GPoint currentCell = new GPoint(MAP_BORDER+mainRgen.nextInt(0,mapDimension/2)*2,MAP_BORDER+mainRgen.nextInt(0,mapDimension/2)*2);
		mapArray[(int) currentCell.getY()][(int) currentCell.getX()]=1;
		int dX = 0;
		int dY = 0;
		int roomWidth = 4;
		playerStart = new GPoint(MAP_BORDER+mapDimension/2,MAP_BORDER+mapDimension/2);
		System.out.println(playerStart.getX()+""+playerStart.getY());
		boolean done = false;
		int direction = 0;
		while(done == false){
			if(direction==0){
				direction = mainRgen.nextInt(0,4);
			}
			switch (direction){
				case 1:
					dX = 1;
					dY = 0;
					break;
				case 2:
					dX = -1;
					dY = 0;
					break;
				case 3:
					dX = 0;
					dY = 1;
					break;
				case 4:
					dX = 0;
					dY = -1;
					break;
			}
			if((mapArray[(int) currentCell.getY()+dY*2][(int) currentCell.getX()+dX*2]==0)&&(currentCell.getY()+dY*2>MAP_BORDER)&&(currentCell.getY()+dY*2<mapDimension+MAP_BORDER)&&(currentCell.getX()+dX*2>MAP_BORDER)&&(currentCell.getX()+dX*2<mapDimension+MAP_BORDER)){
				mapArray[(int) currentCell.getY()+dY][(int) currentCell.getX()+dX] = 1;
				mapArray[(int) currentCell.getY()+dY*2][(int) currentCell.getX()+dX*2] = 1;
				currentCell.translate(dX*2,dY*2);
				direction = 0;
			}else{
				done = true;
				for(int i=1;i<5;i++){
					if(done){
						switch (i){
							case 1:
								dX = 1;
								dY = 0;
								break;
							case 2:
								dX = -1;
								dY = 0;
								break;
							case 3:
								dX = 0;
								dY = 1;
								break;
							case 4:
								dX = 0;
								dY = -1;
								break;
						}
						if((mapArray[(int) currentCell.getY()+dY*2][(int) currentCell.getX()+dX*2]==0)&&(currentCell.getY()+dY*2>MAP_BORDER)&&(currentCell.getY()+dY*2<mapDimension-MAP_BORDER)&&(currentCell.getX()+dX*2>MAP_BORDER)&&(currentCell.getX()+dX*2<mapDimension-MAP_BORDER)){
							direction = i;
							done = false;
							break;
						}
					}
				}
			}
			if(done==true){
				for(int i=MAP_BORDER;i<mapDimension+MAP_BORDER;i+=2){
					for(int j=MAP_BORDER;j<mapDimension+MAP_BORDER;j+=2){
						if((mapArray[j][i]==0)&&((mapArray[j+2][i]==1)||(mapArray[j-2][i]==1)||(mapArray[j][i+2]==1)||(mapArray[j][i-2]==1))){
							currentCell = new GPoint(i,j);
							mapArray[j][i]=1;
							 for(int k=0;k<CONSTRUCTION_ATTEMPTS;k++){
								switch(mainRgen.nextInt(0,4)){
									case 1:
										if(mapArray[j][i+2]==1){
											mapArray[j][i+1] = 1;
											done = false;
											break;
										}
									case 2:
										if(mapArray[j][i-2]==1){
											mapArray[j][i-1] = 1;
											done = false;
											break;
										}
									case 3:
										if(mapArray[j+2][i]==1){
											mapArray[j+1][i] = 1;
											done = false;
											break;
										}
									case 4:
										if(mapArray[j-2][i]==1){
											mapArray[j-1][i] = 1;
											done = false;
											break;
									}
								}
								if(done==false) break;
							}
						if(done==false) break;
						}
					}
					if(done == false) break;
				}
			}
		}
		for(int i=0;i<roomWidth;i++){
			for(int j=0;j<roomWidth;j++){
				mapArray[(int) (MAP_BORDER+((mapDimension-roomWidth)/2.0)+j)][(int) (MAP_BORDER+((mapDimension-roomWidth)/2.0)+i)] = 1;
			}
		}
		//Prunes dead ends.
		for(int i=0;i<DEADEND_PRUNES;i++){
			for(int j=MAP_BORDER;j<MAP_BORDER+mapDimension;j++){
				for(int k=MAP_BORDER;k<MAP_BORDER+mapDimension;k++){
					int openNeighborCounter = 0;
					if(mapArray[j][k-1]==1) openNeighborCounter++;
					if(mapArray[j][k+1]==1) openNeighborCounter++;
					if(mapArray[j+1][k]==1) openNeighborCounter++;
					if(mapArray[j-1][k]==1) openNeighborCounter++;
					if(openNeighborCounter==1) mapArray[j][k]=0;
				}
			}
		}
		return mapArray;
	}
	
	/* Carves out a circular cavern region */
	private void createExplosion(int[][] mapArray,GPoint location,int radius){
		for(int j=0;j<mapArray.length;j++){
			for(int k=0;k<mapArray[0].length;k++){
				if((((location.getX()-j)*(location.getX()-j))+((location.getY()-k)*(location.getY()-k)))<radius){
					mapArray[j][k]=1;
				}
			}
		}
	}
	
	/* Copies a given array of ints */
	private int[][] copyArray(int[][] arrayToCopy){
		int[][] newArray = new int[arrayToCopy.length][arrayToCopy[0].length];
		for(int i=0;i<arrayToCopy.length;i++){
			for(int j=0;j<arrayToCopy[0].length;j++){
				newArray[i][j]=arrayToCopy[i][j];
			}
		}
		return newArray;
	}
	
	/* This builds all of the details on the map */
	private int[][] generateDetails (int[][] worldMap){
		int[][] detailMap = new int[mapDimension+MAP_BORDER*2][mapDimension+MAP_BORDER*2];
		//Establish distribution of filled floor type
		for (int i=0;i<mapDimension-1;i++){
			for (int j=0;j<mapDimension;j++){
				boolean k = levelRgen.nextBoolean(Math.pow(2.718, ((-0.4)/(level-0.90))));
				int switchValue;
				if (k==true){
					switchValue = 1;
				}else{
					switchValue = 2;
				}
				if(mapType.equals("ExplodedCave")){
					switchValue = 2;
				}
				if (worldMap[i][j]!=0){
					switch(switchValue){
						case 1:
							worldMap[i][j]=1;
							break;
						case 2:
							worldMap[i][j]=2;
							break;
					}
				}
			}
		}
		
	//Build connected tiles, treats walls as fellow floor tiles, dirt is (1)
		for (int y=0;y<mapDimension;y++){
			for (int j=0;j<mapDimension;j++){
				if ((worldMap[y][j]==1)||worldMap[y][j]==2){
					//Floor Tile surrounded by floor tiles (Value 3 a:21,22,23) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j] = 3;
					}
					//Floor Tile surrounded by floor on three sides (Empty Top) (Value 4,//a.24) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&(worldMap[y-1][j]==2)&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&(((worldMap[y][j-1]==1)||worldMap[y][j-1]==0))){
						detailMap[y][j] = 4;
					}
					//Floor Tile surrounded by floor tiles on three sides (Empty Bottom) (Value 5,//a.25) //Good
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j]=5;
					}				
					//Floor Tile surrounded by floor on three sides (Empty Left) (Value 6,//a.26) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=6;
					}
					//Floor Tile surrounded by floor on three sides (Empty Right) (Value 7,//a.27) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&(worldMap[y][j+1]==2)&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j]=7;
					}
					//Floor Tile surrounded by floor on two sides (Empty Left and Right) (Value 8,//a.28) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&(worldMap[y][j+1]==2)&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=8;
					}
					//Floor Tile surrounded by floor on two sides (Empty Bottom and Left) (Value 9,//a.29) //Good
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=9;
					}
					//Floor Tile surrounded by floor on two sides (Empty Bottom and Right) (Value 10,//a.30) //Good
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&((worldMap[y][j-1]==1)||(worldMap[y][j-1]==0))&&(worldMap[y][j+1]==2)){
						detailMap[y][j]=10;
					}
					//Floor Tile surrounded by floor on two sides (Empty Top and Right) (Value 11,//a.31) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&(worldMap[y-1][j]==2)&&(worldMap[y][j+1]==2)&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j]=11;
					}
					//Floor Tile surrounded by floor on two sides (Empty Top and Left) (Value 12,//a.32) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&(worldMap[y-1][j]==2)&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=12;
					}
					//Floor Tile surrounded by floor on two sides (Empty Top and Bottom) (Value 13,//a.33 //Good
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&(worldMap[y-1][j]==2)&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j]=13;
					}
					//Floor Tile surrounded by floor on one side (Floor Top) (Value 14,//a.34) //Good
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&((worldMap[y-1][j]==1)||(worldMap[y-1][j]==0))&&(worldMap[y][j+1]==2)&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=14;
					}
					//FLoor Tile surrounded by floor on one side (Floor Bottom) (Value 15,//a.35) //Good
					if ((worldMap[y][j]==1)&&((worldMap[y+1][j]==1)||(worldMap[y+1][j]==0))&&(worldMap[y-1][j]==2)&&(worldMap[y][j+1]==2)&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=15;
					}
					//Floor Tile surrounded by floor on one side (Floor Right) (Value 16,//a.36) 
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&(worldMap[y-1][j]==2)&&((worldMap[y][j+1]==1)||(worldMap[y][j+1]==0))&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=16;
					}
					//Floor Tile surrounded by floor on one side (Floor Left) (Value 17,//a.37)
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&(worldMap[y-1][j]==2)&&(worldMap[y][j+1]==2)&&((worldMap[y][j-1]==1)||worldMap[y][j-1]==0)){
						detailMap[y][j]=17;
					}
					//Floor Tile surrounded by empty on all sides (Value 18,//a.19,20)
					if ((worldMap[y][j]==1)&&(worldMap[y+1][j]==2)&&(worldMap[y-1][j]==2)&&(worldMap[y][j+1]==2)&&(worldMap[y][j-1]==2)){
						detailMap[y][j]=18;
					}
					if ((worldMap[y][j]==2)){
						boolean mushroom = levelRgen.nextBoolean(0.10);
						if (mushroom==true){
							detailMap[y][j]=19;
						}else{
							detailMap[y][j]=2;
						}
					}
				}
			}
		}
	return detailMap;
	}
			
	/* This method generates the pixel array of the map itself */
	private int[][][] generateTextures(int[][] detailMap){
		int[][][] textureMap = new int[(mapDimension+MAP_BORDER*2)*TILE_DIMENSION][(mapDimension+MAP_BORDER*2)*TILE_DIMENSION][NUMBER_COLORS];
		for (int y=0;y<mapDimension+MAP_BORDER*2;y++){
			for (int x=0;x<mapDimension+MAP_BORDER*2;x++){
				if(detailMap[y][x]==0){
					for(int i=0;i<TILE_DIMENSION;i++){
						for(int j=0;j<TILE_DIMENSION;j++){
							textureMap[(y*TILE_DIMENSION)+i][(x*TILE_DIMENSION)+j][0] = READ_BLOCKED;
							textureMap[(y*TILE_DIMENSION)+i][(x*TILE_DIMENSION)+j][1] = READ_BLOCKED;
							textureMap[(y*TILE_DIMENSION)+i][(x*TILE_DIMENSION)+j][2] = READ_BLOCKED;
							textureMap[(y*TILE_DIMENSION)+i][(x*TILE_DIMENSION)+j][3] = READ_BLOCKED;
							textureMap[(y*TILE_DIMENSION)+i][(x*TILE_DIMENSION)+j][4] = READ_BLOCKED;
						}
					}
				}else{
					int[][] tileUltraDark = new GImage("tile_"+detailMap[y][x]+"_UltraDark.png").getPixelArray();			
					int[][] tileDark = new GImage("tile_"+detailMap[y][x]+"_Dark.png").getPixelArray();	
					int[][] tileLowLight = new GImage("tile_"+detailMap[y][x]+"_LowLight.png").getPixelArray();	
					int[][] tileLight = new GImage("tile_"+detailMap[y][x]+"_Light.png").getPixelArray();	
					int[][] altTileUltraDark = new GImage("tile_"+detailMap[y][x]+"_-UltraDark.png").getPixelArray();			
					int[][] altTileDark = new GImage("tile_"+detailMap[y][x]+"_-Dark.png").getPixelArray();	
					int[][] altTileLowLight = new GImage("tile_"+detailMap[y][x]+"_-LowLight.png").getPixelArray();	
					int[][] altTileLight = new GImage("tile_"+detailMap[y][x]+"_-Light.png").getPixelArray();	
					for(int i=0;i<TILE_DIMENSION;i++){
						for(int j=0;j<TILE_DIMENSION;j++){
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][0] = Color.BLACK.getRGB();
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][1] = tileUltraDark[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][2] = tileDark[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][3] = tileLowLight[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][4] = tileLight[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][6] = altTileUltraDark[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][7] = altTileDark[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][8] = altTileLowLight[i][j];
							textureMap[y*TILE_DIMENSION+i][x*TILE_DIMENSION+j][9] = altTileLight[i][j];
						}
					}
				}
			}
		}
	return textureMap; 
	}
			
	/** Generates path-finding map where -1 is the target */
	public int[][] generatePathFinder(GPoint position){
		//Makes a copy of the map for the pathmap
		int[][] pathMap = new int[worldMap.length][worldMap[0].length];
		for(int x=0;x<worldMap[0].length;x++){
			for(int y=0;y<worldMap.length;y++){
				pathMap[y][x]=worldMap[y][x];
			}
		}
		//Builds the cells, each cell is one less than the lowest value of an adjacent calculated neighbor
		pathMap[(int) position.getY()][(int) position.getX()]=-1;
		for (int k=0;k<mapDimension+MAP_BORDER*2;k++){
			for (int i=MAP_BORDER;i<(mapDimension+MAP_BORDER);i++){
				for(int j=MAP_BORDER;j<(mapDimension+MAP_BORDER);j++){
					if((pathMap[i][j] == 1)||(pathMap[i][j] == 2)){
						int top = pathMap[i-1][j]; 
						int right = pathMap[i][j+1];
						int left = pathMap[i][j-1];
						int bottom = pathMap[i+1][j];
						if(((top>=left)||(left>-1))&&((top>=right)||(right>-1))&&((top>=bottom)||(bottom>-1))&&(top<0)) pathMap[i][j] = ((top)-1);
						if(((left>=top)||(top>-1))&&((left>=right)||(right>-1))&&((left>=bottom)||(bottom>-1))&&(left<0)) pathMap[i][j] = ((left)-1);
						if(((right>=left)||(left>-1))&&((right>=top)||(top>-1))&&((right>=bottom)||(bottom>-1))&&(right<0)) pathMap[i][j] = ((right)-1);
						if(((bottom>=left)||(left>-1))&&((bottom>=right)||(right>-1))&&((bottom>=top)||(top>-1))&&(bottom<0)) pathMap[i][j] = ((bottom)-1);
					}
				}
			}
		}
	return pathMap;
	}
		
	/** Returns what type of dungeon this is */
	public String getMapType(){
		return mapType;
	}
	
	/** Returns the size of the map */
	public int getSize(){
		return mapDimension+MAP_BORDER*2;
	}
}
