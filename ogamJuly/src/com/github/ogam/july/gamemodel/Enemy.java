package com.github.ogam.july.gamemodel;

import com.badlogic.gdx.math.Vector2;

public interface Enemy {
	
	public void init(Vector2 pos, LevelContext lc);
	public void update(float delta, LevelContext lc);
	public void calcMove(float delta, LevelContext lc);
	public void kill();
	public boolean isDead();
	public boolean canKillPlayer();
	public boolean collidesWithSegment(Vector2 start, Vector2 end);
	
	public Vector2 getPos();
	
	/**
	 * Returns the importance weight of this enemy. This weight is taken into 
	 * account when calculating which area to be zoned (area with biggest 
	 * enemy weight remains.
	 * 
	 * @return
	 */
	public int getWeight();
	
	
	/**
	 * Sends a signal to this enemy when the play area was zoned.
	 * @param outside: true if the enemy is outside the final zone.
	 */
	public void doZoningSignal(boolean outside);
}
