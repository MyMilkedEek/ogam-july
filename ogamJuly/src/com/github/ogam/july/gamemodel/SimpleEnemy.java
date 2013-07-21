package com.github.ogam.july.gamemodel;

import java.util.Random;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

/**
 * Class to use as a basis for other enemies.
 * The simple enemy is round, it walks into a random direction until it collides with the path, when it selects a new direction.
 * it sends a kill signal to the player when touching the cutline.
 * 
 * @author caranha
 *
 */
public class SimpleEnemy implements Enemy{

	Vector2 position; // current xy location
	Vector2 direction; // candidate xy location

	float heading; // direction the enemy is moving (degrees)
	float speed = 60; // movement speed of the enemy;
	
	float size = 10;
	
	Random dice;
	
	boolean wallcol = false;
	boolean alive = true;
	
	public SimpleEnemy()
	{
		
		position = new Vector2();
		direction = new Vector2();
	}
	
	public void init(Vector2 pos, LevelContext lc)
	{
		position.set(pos);
		dice = lc.dice;
		heading = dice.nextFloat()*360;
	}
	
	/**
	 * Try to apply the calculated move. Does all the collision detection and signalling appropriate.
	 * @param delta
	 */
	public void update(float delta, LevelContext lc)
	{
		// calculate the direction vector
		direction.x = (float) (position.x + (speed*delta)*Math.cos(Math.toDegrees((double)heading)));
		direction.y = (float) (position.y + (speed*delta)*Math.sin(Math.toDegrees((double)heading)));
		
		// test if the direction vector collides with the wall
		if (lc.catwlk.intersectionInPath(position, direction) == null)
		{ // does not collide with the wall
			position.set(direction);
		}
		else
		{
			wallcol = true;
		}
	}
	
	/**
	 * Calculates the next move for this enemy. Separate from update() so that 
	 * we can allow the AI to stop midway
	 * @param delta
	 */
	public void calcMove(float delta, LevelContext lc)
	{
		if (wallcol)
		{
			heading = dice.nextFloat()*360; // if a collision is detected, select a random direction
			wallcol = false;
		}
	}
	
	/**
	 * Mark this enemy to die
	 */
	public void kill()
	{
		alive = false;
	}
	
	/** 
	 * Returns true if this enemy is dead and good to be removed from the enemy list
	 * @return
	 */
	public boolean isDead()
	{
		// Here I can set up some funny thing with enemy internal states, to keep them in a dying animation 
		// after getting the kill signal, but for a simple enemy, he is dead as soon as he gets the kill signal.
		return !alive;
	}
	
	/**
	 * Returns true if this enemy can kill the player by touching the cut line.
	 * @return
	 */
	public boolean canKillPlayer()
	{
		return alive;
	}
	
	/**
	 * Tests if this enemy collides with this line segment
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean collidesWithSegment(Vector2 start, Vector2 end)
	{
		float dist = Intersector.distanceLinePoint(start, end, position);
		return dist < size;
	}

	/**
	 * Returns the position vector (not a copy)
	 */
	public Vector2 getPos() {
		return position;
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public void doZoningSignal(boolean outside) {
		if (outside)
			kill();
	}
}
