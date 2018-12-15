import acm.graphics.GImage;
import acm.graphics.GPoint;

/*
 * File: GraphicsContest.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class represents an image, for sanity's sake, please remember
 * that pixels in the image directly correlate to pixels in the game.
 */
public class ImageSprite implements TorchBearerConstants {
	int[][] card;
	GPoint cardPosition;
	double dy;
	double health;
	
	/** Constructor takes in the actual string and the color */
	ImageSprite(GPoint cardPosition,String imageName,double dy,double health){
		this.dy = dy;
		this.cardPosition = cardPosition;
		this.health = health;
		card = new GImage(imageName+".png").getPixelArray();
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
