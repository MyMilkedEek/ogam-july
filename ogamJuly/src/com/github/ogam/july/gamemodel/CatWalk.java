package com.github.ogam.july.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Catwalk contains the information about the "safe" area that can be walkable by the ship
 * @author caranha
 *
 */
public class CatWalk {
	
	Array<Vector2> path;
	
	public CatWalk()
	{
		
		path = new Array<Vector2>(Vector2.class);
		
		/* Initial Catwalk (on world coordinates) */
		path.add(new Vector2(0,0));
		path.add(new Vector2(300,0));
		path.add(new Vector2(300,300));
		path.add(new Vector2(0,300));				
	}
	
	public Vector2[] getPath()
	{
		return path.toArray();
	}

}
