package com.github.ogam.july.gamemodel;

/**
 * This class represents all the values of the game model for a level. This includes:
 * 
 * All gameplay actors: player, catwalk, etc.
 * Level parameters: Speed, difficulty, etc.
 * Level controllers: Timer, Random number Generator, etc.
 * @author caranha
 *
 */
public class LevelContext {

	public Ship player;
	public CatWalk catwlk;
	

	/** 
	 * Creates empty, unusable class.
	 */
	public LevelContext()
	{
	}

	/**
	 * Set the parameters for everything
	 */
	public void init()
	{
		// set parameters received "from above"

		// creates catwalk, player, etc, based on these parameters
		catwlk = new CatWalk();
		player = new Ship();
		player.setPos(catwlk.getPath()[0]);
		player.setLane(catwlk);
		
	}
	
	public void update(float delta)
	{
		catwlk.update(delta);
		player.update(delta);
	}
	
	
}
