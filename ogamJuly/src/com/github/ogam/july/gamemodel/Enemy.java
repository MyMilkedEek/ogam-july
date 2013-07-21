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
}
