package com.github.ogam.july.gamemodel;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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

	Random dice;
	public Ship player;
	public CatWalk catwlk;
	public Array<Enemy> enemylist;
	

	/** 
	 * Creates empty, unusable class.
	 */
	public LevelContext()
	{
		dice = new Random();
		player = new Ship();
		enemylist = new Array<Enemy>(Enemy.class);
	}

	/**
	 * Set the parameters for everything
	 */
	public void init()
	{
		// set parameters received "from above"

		// creates catwalk, player, etc, based on these parameters
		catwlk = new CatWalk();
		player.init(catwlk.getPath()[0], this);
		
		for (int i = 0; i < 3; i++)
		{
			SimpleEnemy tmp = new SimpleEnemy();
			tmp.init(new Vector2((dice.nextFloat()*100+100),(dice.nextFloat()*100+100)), this);
			enemylist.add(tmp);
		}
	}
	
	public void update(float delta)
	{
		catwlk.update(delta, this);
		
		Iterator<Enemy> it = enemylist.iterator();
		while (it.hasNext())
		{
			Enemy tmp = it.next();
			tmp.calcMove(delta, this);
			tmp.update(delta, this);
		}
		
		player.update(delta,this);
		
		// revive the player -- probably want to reduce lives and stuff here.
		if (player.isDead())
			player.init(catwlk.getPath()[0], this);
		
		// removes enemies that are dead from the level context
		bringOutYourDead();
	}
	
	/**
	 * Remove dead enemies from the enemy list.
	 */
	void bringOutYourDead()
	{
		Iterator<Enemy> it = enemylist.iterator();
		while (it.hasNext())
			if (it.next().isDead())
				it.remove();
	}
}
