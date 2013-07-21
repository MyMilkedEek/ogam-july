package com.github.ogam.july.gamemodel;

import java.util.Iterator;

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
	
	LevelContext lc; // FIXME: need this for tap detection, but it creates a circular list of references. fix "do Tap" or put a destructor here to remove the reference
	
	/* Cutting related variables */
	boolean cutting_state; // is the ship cutting now, or moving on the catwalk?
	boolean first_cut; // needed to get the ship out of the catwalk correctly;
	Array<Vector2> cutLines; // current cutlines (during cutting)
	
	
	/* Movement related variables */
	//CatWalk lane; // should this be here, or on a parent "context" object?? Ship needs to know where the lane is to snap its position
	public Array<Vector2> goalList;
	
	Vector2 position; // center of the ship
	Vector2 direction; // Direction must be aligned with the axis, can be positive or negative.
	float speed; // speed of the ship's movement, in pixels/s
	
	boolean dead = false;
	
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

	public void init(Vector2 pos, LevelContext lc)
	{
		this.lc = lc;
		setPos(pos);
		goalList.clear();
		cutLines.clear();
		cutting_state = false;
		dead = false;
	}
	

	
	public void update(float delta, LevelContext lc)
	{
		if (isDead()) // if player is dead, do nothing
			return;
		
		// if the ship has an objective, move following the rules of the objective
		// else, move using the "free movement" (keyboard) rules
		// TODO: Maybe separate: select dir, movement, validation?
		if (goalList.size > 0)
			doGoalMove(delta, lc);
		else
			doKeyMove(delta);		
		
		// test if enemies kill me
		// TODO: this can probably be improved
		if (cutLines.size > 1) // testing if there is a line for enemies to touch
		{
			Iterator<Enemy> danger = lc.enemylist.iterator();		
			while (danger.hasNext())
			{
				Enemy tmp = danger.next();
				if (tmp.canKillPlayer())
				{
					for (int i = 1; i < cutLines.size; i++)
					{
						if (tmp.collidesWithSegment(cutLines.get(i-1), cutLines.get(i)))
						{
							kill();
							return;
						}
					}
				}
			}
		}
	}

	/* 
	 * Sets this player to the "dead" state
	 */
	public void kill()
	{
		dead = true;
		cutLines.clear();
		goalList.clear();
	}
	
	
	/** 
	 * Calculates the new direction based on the goal and the delta-time, validates it, and put the ship in the new position.
	 * TODO: I should probably break this down into different functions to do each part of calculation/validation and operation.
	 * @param delta
	 */
	public void doGoalMove(float delta, LevelContext lc)
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
				if (!OgamMath.isPointInPolygon(direction, lc.catwlk.getPath())) 
				{
					direction.set(position);
					doEndCut = true;
				}
				first_cut = false; // whatever happens, the first cut ends.
			}
			else
			{
				Vector2 tmp = lc.catwlk.intersectionInPath(position, direction);
				if (tmp != null) // crossed the treshold, cutting is over
				{
					direction = tmp;
					doEndCut = true;
				}
			}
			
			// if direction is different than position while cutting (the move is valid if it got 
			// to this point and is not 0), we need to possibly add a cutting point
			if (!direction.epsilonEquals(position, Constants.EPSILON))
			{
				addCutPoint(position,direction);
			}
		}
		
		setPos(direction);
		direction.set(0, 0);
		if (doEndCut)
			endCutting(lc);
		
	}
	
	/** 
	 * Adds a new point to the cut vector based on the movement of the ship.
	 * This function assumes that the movement was already tested for validity.
	 * @param start
	 * @param end
	 */
	public void addCutPoint(Vector2 start, Vector2 end)
	{
		if (cutLines.size == 1) // only one point, we need to add another one.
		{
			cutLines.add(direction.cpy());
			
			return;
		}
		else // more than one point exist. If last three points are not colinear, add a new one.
		{
			Vector2 old = cutLines.pop();
			if (!OgamMath.testCollinear(direction, old, cutLines.peek())) // points are not collinear, add the old back.
				cutLines.add(old);
			cutLines.add(direction.cpy());
		}
		
		// Finally, we need to test if the new line crosses any of the old ones.
		if (cutLines.size > 4)
		{
			Vector2 lastStart = cutLines.get(cutLines.size-2);
			Vector2 lastEnd = cutLines.peek();	
			Vector2 intersect = new Vector2();
		

			int index = 1;
			while ((index < cutLines.size - 2)&&(!Intersector.intersectSegments(cutLines.get(index-1),cutLines.get(index),lastStart,lastEnd,intersect)))
			{
				index++;
			}
			if (index != cutLines.size-2)
			{
				while (cutLines.size > index)
					cutLines.pop();
				cutLines.add(intersect);
				cutLines.add(lastEnd);
			}
		}
	}
	
	
	public void doKeyMove(float delta)
	{
		
	}
	
	
	/**
	 * Send a tap from the input to the ship
	 * @param tap
	 */
	public void doTap(Vector2 tap)
	{
		if (isDead()) // if the player is dead, does not react to taps.
			return;
		
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
			calculateGoals(tap, lc); // calculate new goals
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
	void calculateGoals(Vector2 finalGoal, LevelContext lc)
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
			goalList.addAll(lc.catwlk.calculateSubGoals(position, finalGoal));
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
	
	void endCutting(LevelContext lc)
	{
		if (cutLines.size > 1)
		{
			lc.catwlk.pushCut(cutLines); // Attention! This clears the cutlines
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
	
	public boolean isDead()
	{
		return dead;
	}
	
	public Array<Vector2> getCutLine()
	{
		return cutLines;
	}
	
	/* Setters */
	
	
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
