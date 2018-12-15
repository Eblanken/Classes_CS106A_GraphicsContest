/*
 * File: TorchBearerConstants.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores constants for the TorchBearer game.
 */

public interface TorchBearerConstants {

	/** The name of the saveGame file */
	public static final String SAVEGAME_FILE = "Saves.txt";
	
	/** The size of the rendered screen in terms of tiles rendered in game (constrained by pixels dimension) */
		public static final int APPLICATION_DIMENSION_TILES = 10;

	/** The size of the application window in pixels */
		public static final int APPLICATION_DIMENSION_PIXELS = 640;
		
	/** The dimension of a single tile in pixels */
		//This is actually assumed for all of the art for the game, please do not change, original 16.
		public static final int TILE_DIMENSION = 16;
			
	/** The scale factor for the game */
		public static final double SCALE_FACTOR = (APPLICATION_DIMENSION_PIXELS/(double) (APPLICATION_DIMENSION_TILES*TILE_DIMENSION));

	/** The height in pixels of the top border bar */
		public static final int SWING_BAR_HEIGHT = 38;
		
	/** Controls timing of the game */
		public static final double GAME_DELAY = 25;
		
	/** Utility constants */
		public static final double PI = 3.14;
		
	/** Constants for defaults in the game for the player */
		public static final double DEFAULT_PLAYER_DIMENSION = 0.5;
		public static final double DEFAULT_TORCHFUEL_START = 1000;
		public static final double DEFAULT_BURN_RATE = 0.2;
		public static final double BURN_RATE_POWER = 4;
		public static final double DEFAULT_DAMAGE = 15;
		public static final int DEFAULT_VARIANT = 1;
		public static final double DEFAULT_RANGE = 1;
		public static final double DEFAULT_SPEED = 0.15;
		public static final double DEFAULT_DIG_BONUS = 1;
		public static final double DEFAULT_MELEE_DELAY = 200;
		public static final String DEFAULT_MELEE_TYPE = "Tool";
		public static final double DEFAULT_RANGED_DELAY = 300;
		public static final String DEFAULT_RANGED_TYPE = "Sling";
		public static final double DEFAULT_PLAYER_SPEED = 0.05;
		public static final String DEFAULT_ORIENTATION = "Down";
		
	/** Constants for debugging */
		public static final int SEVENTY_TWO = 71; //Casual Lecture reference.
		public static final boolean CHEAT_MODE = false; //Turn to true for free items, level skip, and pathmap render in console on center mouse click (hit mouse wheel).
		
	/** Constants for defaults in the game for the enemies */
		public static final double DEFAULT_MONSTER_SPEED = 0.04;
		public static final double MONSTER_SPEED_BONUS = 0.002;
		
	/** Item display constants */
		public static final String DEFAULT_VIEW_ICON_LIGHTLEVEL = "Light";
		/* Not actually the number of colors, its the number of colors and a gap color */
		public static final int NUMBER_COLORS = 10;
		
	/** Constants for the shopkeepers */
		public static final int SHOP_EQUIPABLE_INVENTORY = 3;
		public static final int SHOP_ITEM_INVENTORY = 1;
		public static final int SHOP_LEVEL_BUFF = 5;
		public static final int SHOP_ITEM_DAMAGETOCOST_MULTIPLIER = 2;
		public static final double SHOP_COAL_COST = 0.1;
		public static final int SHOP_SHOT_COST = 2;
		public static final int SHOP_SHOT_INVENTORY = 5;
		
		//Constants for shopkeeper GUI
		//Equipable constants
		public static final int SHOP_GUI_EQUIPABLE_VERTICAL_OFFSET = 4;
		public static final int SHOP_GUI_EQUIPABLE_VERTICAL_REGIONWIDTH = 26;
		public static final int SHOP_GUI_EQUIPABLE_TEXT_VERTICAL_OFFSET = 22;
		public static final int SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET = 4;
		public static final int SHOP_GUI_EQUIPABLE_HORIZONTAL_REGIONWIDTH = 18;
		public static final int SHOP_GUI_EQUIPABLE_HORIZONTAL_ITERATIONOFFSET=20;
		public static final int SHOP_GUI_EQUIPABLE_ICON_DIMENSION = 16;
		//Coal Refill Constants
		public static final int SHOP_GUI_COAL_HORIZONTAL_OFFSET = 3;
		public static final int SHOP_GUI_COAL_HORIZONTAL_REGIONWIDTH = 25;
		public static final int SHOP_GUI_COAL_VERTICAL_OFFSET = 32;
		public static final int SHOP_GUI_COAL_VERTICAL_REGIONWIDTH = 25;
		public static final int SHOP_GUI_COAL_TEXT_VERTICAL_OFFSET = 51;
		//Shots Refill Constants
		public static final int SHOP_GUI_SHOT_HORIZONTAL_OFFSET = 36;
		public static final int SHOP_GUI_SHOT_HORIZONTAL_REGIONWIDTH = 25;
		public static final int SHOP_GUI_SHOT_VERTICAL_OFFSET = 32;
		public static final int SHOP_GUI_SHOT_VERTICAL_REGIONWIDTH = 26;
		public static final int SHOP_GUI_SHOT_TEXT_VERTICAL_OFFSET = 51;
		
	/** Color markers in the game */
		//READ_TRANSPARENT is integral to the art of the game, you need to update art if you change it.
		public static final int READ_TRANSPARENT = -16776961;
		public static final int READ_BLOCKED = -16777215;
		
	/** Text Constants */
		public static final int FONT_WIDTH = 4;
		public static final int FONT_HEIGHT = 6;
		
		public static final double POPUP_PICKUP_HEIGHT_OFFSET = 1.5;
		
	/** Constants for dungeon map generation */
		public static final int MAP_BORDER = (APPLICATION_DIMENSION_TILES/2)+2;
		public static final int DEFAULT_START_LEVEL = 1;
		public static final int ROOM_MAX_DIMENSION = 7;
		public static final int ROOM_MIN_DIMENSION = 4;
		public static final int CONSTRUCTION_ATTEMPTS = 2500;
		
	/** Constants for explodedcave map generation */
		public static final double EXPLODEDCAVE_CHANCE = 0.20;
		public static final double MAZE_CHANCE = 0.15;
		public static final int DEADEND_PRUNES = 2;
}

/////////////////////////////////////////////////////////////
////////////////-BUGS TO FIX-////////////////////////////////
/////////////////////////////////////////////////////////////

// > Guns result in errors related to both monsters and bullets
// > Weird graphical rounding errors need to be resolved



////////////////-To Do List-//////////////////////////////////

// > Maze generation rework & boss rework
// > Additional enemy skins and types
// > Enemy projectiles
// > Better child-parent relationship for lights
// > 
// > Treasure chests?
// > New Weapons?


// > Upgraded graphics?
