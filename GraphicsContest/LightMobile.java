import acm.graphics.GPoint;

/*
 * File: TorchBearerConstants.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class stores data for a light source that changes over time.
 */

public class LightMobile implements TorchBearerConstants {
	
	private int type;
	private GPoint position;
	private double radius;
	private double timeOffset;
	private double period;
	private double radiusVariance;
	
  /** Constructor builds a new light given it's position, maximum radius, and period
	* Type refers to light type, 0 is a standard light, 1 is inverted
    */
	
	LightMobile(int type,GPoint position,double radius,double radiusVariance,double period, double timeOffset){
		this.radius = radius;
		this.radiusVariance = radiusVariance;
		this.position = position;
		this.radius = radius;
		this.period = period;
		this.timeOffset = timeOffset;
		this.type = type;
	}
	
	/** This method sets the current amount of fuel for the light source, otherwise it is stable */
	public void setRadius(double newRadius){
		radius = newRadius;
	}
	
	/** This method gets the current radius of the torch in tiles */
	public int getLightValue(GPoint targetPosition,Double time){
		double distance = Math.sqrt(((targetPosition.getX()-position.getX())*(targetPosition.getX()-position.getX()))+((targetPosition.getY()-position.getY())*(targetPosition.getY()-position.getY())));
		for(int i=4;i>0;i--){
			double newRadius = ((radius*((double) (5-i)/4)+radiusVariance*Math.sin(((time+timeOffset)/1000)*(1/((PI*2)*period)))));
			if(newRadius>distance) return (i+type*5);
		}
		return 0;
	}
	
	/** This method returns the position of the light */
	public GPoint getPosition(){
		return position;
	}
	
	/** This method sets the position of the light */
	public void setPosition(GPoint newPosition){
		position = newPosition;
	}
}
