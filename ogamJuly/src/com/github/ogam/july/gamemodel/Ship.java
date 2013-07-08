package com.github.ogam.july.gamemodel;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.ogam.july.util.Constants;
import com.github.ogam.july.util.OgamMath;

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
	boolean first_cut; // needed to get the ship out of the catwalk correctly;
	Array<Vector2> cutLines; // current cutlines (during cutting)
	
	
	/* Movement related variables */
	CatWalk lane; // should this be here, or on a parent "context" object?? Ship needs to know where the lane is to snap its position
	public Array<Vector2> goalList;
	
	Vector2 position; // center of the ship
	Vector2 direction; // Direction must be aligned with the axis, can be positive or negative.
	float speed; // speed of the ship's movement, in pixels/s
	
	
	
	public Ship()
	{
		position = new Vector2();
		direction = new Vector2();
		speed = 200;
		cutLines = new Array<Vector2>();
		cutting_state = false;
		collisionBox = new Rectangle(0,0,SHIPSIZE,SHIPSIZE);
		
		goalList = new Array<Vector2>(Vector2.class);
	}

	
	public void update(float delta)
	{
		
		
		
		// if the ship has an objective, move following the rules of the objective
		// else, move using the "free movement" (keyboard) rules
		
		if (goalList.size > 0)
			doGoalMove(delta);
		else
			doFreeMove(delta);
		
		// TODO: probably should move the "test to see if we are back at the lane" check here 
		// (from outside the "do move" code) to avoid code duplication???
		
	}
	
	public void doGoalMove(float delta)
	{
		boolean doEndCut = false;
		direction.x = position.x + Math.signum(goalList.first().x - position.x)*(speed*delta);
		direction.y = position.y + Math.signum(goalList.first().y - position.y)*(speed*delta);
		
		// Clamp movement if a sub-goal is passed.
		if (OgamMath.isPointInSegment(position, direction, goalList.first()))
		{
			direction.set(goalList.first());
			goalList.removeIndex(0);
		}
		
		// If Cutting, We need to test if the direction is valid, or if we are back to the path.
		// This costs Path size -> Can I do it cheaper -- I need to know in O(1) if I may have crossed the polygon
		if (cutting_state)
		{
			if (first_cut)
			{
				// if it is the first movement after cut, we just check if the ship entered the polygon or not.
				if (!OgamMath.isPointInPolygon(direction, lane.getPath())) 
				{
					direction.set(position);
					doEndCut = true;
				}
				first_cut = false; // whatever happens, the first cut ends.
			}
			else
			{
				Vector2 tmp = lane.intersectionInPath(position, direction);
				if (tmp != null) // crossed the treshold, cutting is over
				{
					direction = tmp;
					doEndCut = true;
				}
			}

			// if direction is different than position while cutting, 
			// we move, and we need to possibly add a point to the cutting line
			if (!direction.epsilonEquals(position, Constants.EPSILON))
			{

				if (cutLines.size == 1) // only one point, we need to add another one.
				{
					cutLines.add(direction.cpy());
				}
				else // more than one point exist. If last three points are not colinear, add a new one.
				{
					Vector2 old = cutLines.pop();
					if (!OgamMath.testCollinear(direction, old, cutLines.peek())) // points are not collinear, add the old back.
						cutLines.add(old);
					cutLines.add(direction.cpy());
				}
			}
		}
		
		
		
		setPos(direction);
		direction.set(0, 0);
		if (doEndCut)
			endCutting();
		
	}
	
	public void doFreeMove(float delta)
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
	
	/**
	 * Ship receives a signal that it must start cutting (key pressed or ship clicked). This 
	 * can happen while the ship is already cutting, in which case the method should be ignored. 
	 * If this happens while the ship is not cutting, then proper measures should be taken.
	 */
	void startCutting()
	{
		if (cutting_state == false)
		{
			// ship started cutting
			cutting_state = true;
			first_cut = true;
			cutLines.add(position.cpy());
			
		} // else ship is already cutting, and just received an extra cutting signal. 
			

		goalList.clear(); // this only matters for mouse input.
	}
	
	void endCutting()
	{
		if (cutLines.size > 1)
		{
			lane.pushCut(cutLines);
			Gdx.app.log(Constants.LOG_TAG, "Ship: Sent Cut Lines to Catwalk");
		}
		cutting_state = false;
		cutLines.clear();
		goalList.clear();
	}
	
	
	/* Getters */
	/**
	 * @return Returns the center of the ship in world coordinates;
	 */
	public Vector2 getPos()
	{
		return position;
	}
	
	public boolean isCutting()
	{
		return cutting_state;
	}
	
	public Array<Vector2> getCutLine()
	{
		return cutLines;
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
