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
	
	Vector2 position; // center of the ship
	Vector2 speed;
	
	Rectangle collisionBox;
	
	/* Is the ship cutting now? or moving on the catwalk */
	boolean cutting_state;
	
	/* when cutting, the current cut lines are stored here */
	Array<Vector2> cutLines;
	
	public Ship()
	{
		position = new Vector2();
		speed = new Vector2();
		cutLines = new Array<Vector2>();
		cutting_state = false;
		collisionBox = new Rectangle(0,0,SHIPSIZE,SHIPSIZE);
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
	
	public Vector2 getPos() // gets the center of the ship
	{
		return position;
	}
	
}
