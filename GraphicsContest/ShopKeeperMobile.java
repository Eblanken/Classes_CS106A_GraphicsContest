/*
 * File: PlayerMobile.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class creates a shop keeper whom can be interacted with by the player.
 */

import java.util.HashMap;
import java.util.Iterator;

import acm.graphics.GImage;
import acm.graphics.GPoint;

public class ShopKeeperMobile implements TorchBearerConstants{

	private GPoint position;
	private HashMap<Equipable, Integer> equipableInventory = new HashMap<Equipable, Integer>();
	private HashMap<Item, Integer> itemInventory = new HashMap<Item, Integer>();
	private int[][] card;
	private boolean isAlert = false;
	private String shopKeeperDirection = "Down";
	private Equipable selectedEquipable = null;
	private boolean coalSelected = false;
	private int coalRefillPrice = 0;
	private boolean shotSelected = false;
	private int shotRefillPrice = 0;
	
	/** Initializes the shopkeeper and its inventory based on level */
	ShopKeeperMobile(GPoint position, int level){
		this.position = position;
		//Adds the equipables to the shop's inventory
		for(int i=0;i<SHOP_EQUIPABLE_INVENTORY;i++){
			//Creates the equipable
			card = new GImage("ShopKeeperCard.png").getPixelArray();
			Equipable equip = new Equipable(position,level+SHOP_LEVEL_BUFF);
			int cost = equip.getDamage()*SHOP_ITEM_DAMAGETOCOST_MULTIPLIER;
			equipableInventory.put(equip,cost);
		}
	}
	
	/** Returns the card for the shopkeeper, rewrites the whole thing every time */
	public int[][] getCard(GPoint cursorPosition,GPoint playerPosition,int playerCoalCount,int playerMaxCoal){
		if(isAlert){
			coalSelected = false;
			shotSelected = false;
			selectedEquipable = null;
			card = new GImage("ShopKeeperCard.png").getPixelArray();
			GPoint relativePosition = new GPoint(cursorPosition.getX()-(getCardPosition().getX()-((card[0].length/TILE_DIMENSION)/2.0)),(cursorPosition.getY()-getCardPosition().getY())+((card.length/TILE_DIMENSION)/2.0));
			//Adds the equipables to the card
			Iterator equipIt = equipableInventory.keySet().iterator();
			int offsetCounter = 0;
			while(equipIt.hasNext()){
				Equipable equip = (Equipable) equipIt.next();
				if(equipableInventory.get(equip)!=0){
					int[][] equipIcon = new GImage(equip.getType()+"_"+equip.getVariant()+"_"+DEFAULT_VIEW_ICON_LIGHTLEVEL+"_null.png").getPixelArray();
					//Renders the icon for the equipable
					for(int i=0;i<equipIcon.length;i++){
						for(int j=0;j<equipIcon[0].length;j++){
							if(equipIcon[i][j]!=READ_TRANSPARENT){
							card[i+SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET][j+SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET+offsetCounter*(SHOP_GUI_EQUIPABLE_HORIZONTAL_ITERATIONOFFSET)]=equipIcon[i][j];
							}
						}
					}
					//Renders the price for the equipable
					String color = "Gold";
					if((relativePosition.getX()*TILE_DIMENSION>(SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET+offsetCounter*(SHOP_GUI_EQUIPABLE_HORIZONTAL_ITERATIONOFFSET)))&&(relativePosition.getX()*TILE_DIMENSION<(SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET+SHOP_GUI_EQUIPABLE_HORIZONTAL_REGIONWIDTH+offsetCounter*(SHOP_GUI_EQUIPABLE_HORIZONTAL_ITERATIONOFFSET)))&&(relativePosition.getY()*TILE_DIMENSION<(SHOP_GUI_EQUIPABLE_VERTICAL_OFFSET+SHOP_GUI_EQUIPABLE_VERTICAL_REGIONWIDTH))&&(relativePosition.getY()*TILE_DIMENSION<(SHOP_GUI_EQUIPABLE_VERTICAL_OFFSET+SHOP_GUI_EQUIPABLE_VERTICAL_REGIONWIDTH))){
						color = "Orange";
						selectedEquipable = equip;
					}else{
						color = "Gold";
					}
					StringSprite price = new StringSprite(position,equipableInventory.get(equip).toString(),color,0,0);
					int[][] numberIcon = price.getCard();
					for(int i=0;i<numberIcon.length;i++){
						for(int j=0;j<numberIcon[0].length;j++){
							if(numberIcon[i][j]!=READ_TRANSPARENT){
							card[i+SHOP_GUI_EQUIPABLE_TEXT_VERTICAL_OFFSET][j+(int) ((TILE_DIMENSION-numberIcon[0].length)/2)+SHOP_GUI_EQUIPABLE_HORIZONTAL_OFFSET+offsetCounter*(SHOP_GUI_EQUIPABLE_HORIZONTAL_ITERATIONOFFSET)]=numberIcon[i][j];
							}
						}
					}
				}
				offsetCounter+=1;
			}
			//Adds refill coal to the card
			String coalColor = "Gold";
			if((relativePosition.getX()*TILE_DIMENSION>(SHOP_GUI_COAL_HORIZONTAL_OFFSET))&&(relativePosition.getX()*TILE_DIMENSION<(SHOP_GUI_COAL_HORIZONTAL_OFFSET+SHOP_GUI_COAL_HORIZONTAL_REGIONWIDTH))&&(relativePosition.getY()*TILE_DIMENSION>(SHOP_GUI_COAL_VERTICAL_OFFSET))&&(relativePosition.getY()*TILE_DIMENSION<(SHOP_GUI_COAL_VERTICAL_OFFSET+SHOP_GUI_COAL_VERTICAL_REGIONWIDTH))){
				coalColor = "Orange";
				coalSelected = true;
			}else{
				coalColor = "Gold";
			}
			coalRefillPrice = (int) ((-playerCoalCount+playerMaxCoal)*SHOP_COAL_COST);
			StringSprite coalPrice = new StringSprite(position,""+coalRefillPrice,coalColor,0,0);
			int[][] coalNumberIcon = coalPrice.getCard();
			for(int i=0;i<coalNumberIcon.length;i++){
				for(int j=0;j<coalNumberIcon[0].length;j++){
					if(coalNumberIcon[i][j]!=READ_TRANSPARENT){
						card[i+SHOP_GUI_COAL_TEXT_VERTICAL_OFFSET][j+(int) ((SHOP_GUI_COAL_HORIZONTAL_REGIONWIDTH-coalNumberIcon[0].length)/2)+SHOP_GUI_COAL_HORIZONTAL_OFFSET]=coalNumberIcon[i][j];
					}
				}
			}
			//Adds ammo shop slot to the card
			String shotColor = "Gold";
			if((relativePosition.getX()*TILE_DIMENSION>(SHOP_GUI_SHOT_HORIZONTAL_OFFSET))&&(relativePosition.getX()*TILE_DIMENSION<(SHOP_GUI_SHOT_HORIZONTAL_OFFSET+SHOP_GUI_SHOT_HORIZONTAL_REGIONWIDTH))&&(relativePosition.getY()*TILE_DIMENSION>(SHOP_GUI_SHOT_VERTICAL_OFFSET))&&(relativePosition.getY()*TILE_DIMENSION<(SHOP_GUI_SHOT_VERTICAL_OFFSET+SHOP_GUI_SHOT_VERTICAL_REGIONWIDTH))){
				shotColor = "Orange";
				shotSelected = true;
			}else{
				shotColor = "Gold";
			}
			shotRefillPrice = SHOP_SHOT_INVENTORY*SHOP_SHOT_COST;
			StringSprite shotPrice = new StringSprite(position,""+shotRefillPrice,shotColor,0,0);
			int[][] shotNumberIcon = shotPrice.getCard();
			for(int i=0;i<shotNumberIcon.length;i++){
				for(int j=0;j<shotNumberIcon[0].length;j++){
					if(shotNumberIcon[i][j]!=READ_TRANSPARENT){
						card[i+SHOP_GUI_SHOT_TEXT_VERTICAL_OFFSET][j+(int) ((SHOP_GUI_SHOT_HORIZONTAL_REGIONWIDTH-shotNumberIcon[0].length)/2)+SHOP_GUI_SHOT_HORIZONTAL_OFFSET]=shotNumberIcon[i][j];
					}
				}
			}
		}else{
			card = new GImage("ShopKeeperAltCard.png").getPixelArray();
		}
		//Sets the shopKeeper's apparent direction.
		if (position.getX()<playerPosition.getX()&&(Math.abs(position.getX()-playerPosition.getX())>Math.abs(position.getY()-playerPosition.getY()))){
			shopKeeperDirection = "Right";
		}else if (position.getX()>playerPosition.getX()&&(Math.abs(position.getX()-playerPosition.getX())>Math.abs(position.getY()-playerPosition.getY()))){
			shopKeeperDirection = "Left";
		}
		if (position.getY()<playerPosition.getY()&&(Math.abs(position.getX()-playerPosition.getX())<Math.abs(position.getY()-playerPosition.getY()))){
			shopKeeperDirection = "Down";
		}else if (position.getY()>playerPosition.getY()&&(Math.abs(position.getX()-playerPosition.getX())<Math.abs(position.getY()-playerPosition.getY()))){
			shopKeeperDirection = "Up";
		}
		return card;
	}
	
	/** Returns the price of an item at the specified position */
	public int getSelectedEquipablePrice(){
		if(equipableInventory.get(selectedEquipable)!=null){
			return equipableInventory.get(selectedEquipable);
		}else{
			return 0;
		}

	}
	
	/** Returns the currently selected equipment from the shop and wipes it from inventory (sets price to zero)*/
	public Equipable getSelectedEquipable(){
		equipableInventory.put(selectedEquipable, 0);
		return selectedEquipable;
	}
	
	/** Returns whether coal is currently selected as an option */
	public boolean coalSelected(){
		return coalSelected;
	}
	
	/** Returns the price required to totally refill the player's coal */
	public int getCoalRefillPrice(){
		return coalRefillPrice;
	}
	
	/** Pays for some coal */
	public int payForCoal(int money){
		int coal = (int) (money/SHOP_COAL_COST);
		return coal;
	}
	
	/** Returns whether shots are currently selected as an option */
	public boolean shotSelected(){
		return shotSelected;
	}
	
	/** Returns the price required to pay for the number of shots sold by the shop */
	public int getShotPrice(){
		return shotRefillPrice;
	}
	
	/** Pays for some shots */
	public int payForShots(int money){
		int shots = (int) (money/SHOP_SHOT_COST);
		return shots;
	}
	
	/** Returns the location of the shopkeeper */
	public GPoint getPosition(){
		return position;
	}
	
	/** Returns whether the shopkeeper is currently active */
	public boolean isActive(){
		return isAlert;
	}
	
	/** Sets the active state of the shopkeeper */
	public void setActivity(boolean input){
		isAlert = input;
	}
	
	/** Gets the apparent direction of the shopkeeper */
	public String getDirection(){
		return shopKeeperDirection;
	}
	
	/** Returns the location of the shopkeeper's card in tiles */
	public GPoint getCardPosition(){
		GPoint cardPosition = new GPoint(position.getX(),position.getY()-((card.length/2.0)/TILE_DIMENSION)-0.5);
		return cardPosition;
	}
}
