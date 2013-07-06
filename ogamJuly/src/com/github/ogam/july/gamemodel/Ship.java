package com.github.ogam.july.gamemodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** 
 * Contains the model for the ship in the world
 * The ship also contains the information about its current cutting line
 * @author caranha
 *
 */
public class Ship {

	public static final int SHIPSIZE = 16;
	
	
	
	Rectangle collisionBox;
	
	
	/* Cutting related variables */
	boolean cutting_state; // is the ship cutting now, or moving on the catwalk?
	Array<Vector2> cutLines; // current cutlines (during cutting)
	
	
	/* Movement related variables */
	CatWalk lane; // should this be here, or on a parent "context" object?? Ship needs to know where the lane is to snap its position
	
	Vector2 position; // center of the ship
	Vector2 speed;
	
	public Vector2 endTarget;
	public Vector2 currentTarget;
	
	
	public Ship()
	{
		position = new Vector2();
		speed = new Vector2();
		cutLines = new Array<Vector2>();
		cutting_state = false;
		collisionBox = new Rectangle(0,0,SHIPSIZE,SHIPSIZE);
		
		endTarget = null;
		currentTarget = null;
	}
	
	
	public void setLane(CatWalk l)
	{
		lane = l;
	}
	
	/**
	 * Forcefully changes the position of the ship
	 * @param newpos
	 */
	public void setPos(Vector2 newpos)
	{
		position.set(newpos);
		collisionBox.x = newpos.x - SHIPSIZE/2;
		collisionBox.y = newpos.y - SHIPSIZE/2;
	}
	
	/**
	 * @return Returns the center of the ship in world coordinates;
	 */
	public Vector2 getPos()
	{
		return position;
	}
	
	/**
	 * Send a tap from the input to the ship
	 * @param tap
	 */
	public void doTap(Vector2 tap)
	{
		// FIXME: This is just for quickly testing the taps
		endTarget = tap;
		currentTarget = lane.closestPointInPath(tap);
	}
	
}
