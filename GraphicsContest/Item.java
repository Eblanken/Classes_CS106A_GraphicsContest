/*
 * File: Item.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for one item.
 */

import acm.util.RandomGenerator;
import acm.graphics.*;

public class Item {

	private int variant;
	private String type;
	private int quantity; //For coins only.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GPoint itemPosition;
	private String direction = "null";
	
	/** Constructor builds an item based on its type and the level. */
	Item(GPoint itemPosition,String type,int level){
		this.itemPosition = itemPosition;
		this.type = type;
		this.quantity = rgen.nextInt(1,(level*2)+2);
		switch(type) {
			case "Coins":
				if(quantity<8){
					variant = 1;
				}else if(quantity<12){
					variant = 2;
				}else{
					variant = 3;
				}
				break;
			case "Cell":
				quantity = 1;
				variant = 1;
				break;
			case "Stones":
				quantity=((quantity/2)+1);
				if(quantity<4){
					variant = 1;
				}else if(quantity<6){
					variant = 2;
				}else{
					variant = 3;
				}
				break;
			case "Fuel":
				quantity*=25;
				if(quantity<200){
					variant = 1;
				}else if(quantity<500){
					variant = 2;
				}else{
					variant = 3;
				}
				break;
		}
	}
	
	/** Returns the quantity of the item */
	public int getQuantity(){
		return quantity;
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
	
	public String getDirection(){
		return direction;
	}
}
