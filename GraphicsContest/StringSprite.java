import acm.graphics.*;

/*
 * File: NumberSprite.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores and provides a sprite image for a string, 
 * typically used for damage indicators etc.
 */
public class StringSprite implements TorchBearerConstants {

	int[][] card;
	GPoint cardPosition;
	double dy;
	double health;
	
	/** Constructor takes in the actual string and the color */
	StringSprite(GPoint cardPosition,String data,String color,double dy,double health){
		this.dy = dy;
		this.cardPosition = cardPosition;
		this.health = health;
		card = new int[FONT_HEIGHT][FONT_WIDTH*data.length()];
		//Loads all of the characters in the string individually.
		for(int i=0;i<data.length();i++){
			int[][] tile = new GImage("Char_"+data.charAt(i)+"_"+color+".png").getPixelArray();
			//Reads in the character to the card.
			for(int j=0;j<tile.length;j++){
				for(int k=0;k<tile[0].length;k++){
					card[j][k+(i*FONT_WIDTH)]=tile[j][k];
				}
			}
		}
	}
	
	/** Allows the item card to move and eventually dissipates */
	public void takeTurn(){
		cardPosition.translate(0,dy);
		health-=1;
	}
	
	/** Returns the array that represents the image */
	public int[][] getCard(){
		return card;
	}
	
	/** Returns the internally stored position of the entity */
	public GPoint getCardPosition(){
		return cardPosition;
	}
	
	/** Returns the current health of the card */
	public double getHealth(){
		return health;
	}
}
