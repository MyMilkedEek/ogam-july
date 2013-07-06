package com.github.ogam.july.gamemodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.ogam.july.util.Constants;

/** 
 * Contains the model for the ship in the world
 * The ship also contains the information about its current cutting line
 * @author caranha
 *
 */
public class Ship {

	public static final int SHIPSIZE = 16;
	
	
	
	Rectangle collisionBox;
	public Vector2 lastTapLocation; // largely used for debugging
	
	/* Cutting related variables */
	boolean cutting_state; // is the ship cutting now, or moving on the catwalk?
	Array<Vector2> cutLines; // current cutlines (during cutting)
	
	
	/* Movement related variables */
	CatWalk lane; // should this be here, or on a parent "context" object?? Ship needs to know where the lane is to snap its position
	
	Vector2 position; // center of the ship
	Vector2 speed;
	
	public Array<Vector2> goalList;
	
	
	public Ship()
	{
		position = new Vector2();
		speed = new Vector2();
		cutLines = new Array<Vector2>();
		cutting_state = false;
		collisionBox = new Rectangle(0,0,SHIPSIZE,SHIPSIZE);
		
		goalList = new Array<Vector2>(Vector2.class);
	}

	
	public void update(float delta)
	{
		
	}
	
	
	/**
	 * Send a tap from the input to the ship
	 * @param tap
	 */
	public void doTap(Vector2 tap)
	{
		lastTapLocation = tap;
		
		// If the ship itself is tapped, it stops (clear goal list) and switches on the cutting state
		if (collisionBox.contains(tap)) // TODO: might want to make this bigger
		{
			Gdx.app.log(Constants.LOG_TAG, "Ship body was tapped");
			goalList.clear(); // stops the ship
			startCutting(); // changes the state to "Cutting"
		}
		else
		{
			Gdx.app.log(Constants.LOG_TAG, "Movement Target was tapped");
			goalList.clear(); // clear current goal list
			calculateGoals(tap); // calculate new goals
		}
		
	}
	
	/**
	 * Calculate the list of movement targets for the ship.
	 * 
	 * TODO: Currently this happens at "input event" time. Might want to change this to 
	 * happen inside the update cycle for performance reasons.
	 * 
	 * @param finalGoal
	 */
	void calculateGoals(Vector2 finalGoal)
	{
		if (cutting_state) 
		{
			// If the ship is in the cutting stage, there are only two goals - it goes on the larger axis towards the tap, then the shorter axis.
			float dx = Math.abs(finalGoal.x - position.x);
			float dy = Math.abs(finalGoal.y - position.y);
			
			if (dx > dy) // Adding intermediate goal
				goalList.add(new Vector2(finalGoal.x, position.y));
			else
				goalList.add(new Vector2(position.x, finalGoal.y));
			
			goalList.add(new Vector2(finalGoal.x, finalGoal.y)); // Adding final goal;			
		}
		else
		{
			// calculates a list of subgoals in the path necessary to reach the goal position
			goalList.addAll(lane.calculateSubGoals(position, finalGoal));
		}
	}
	
	void startCutting()
	{
		cutting_state = true;
	}
	
	/* Getters */
	/**
	 * @return Returns the center of the ship in world coordinates;
	 */
	public Vector2 getPos()
	{
		return position;
	}
	
	/* Setters */
	
	
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
	

	

	
}
