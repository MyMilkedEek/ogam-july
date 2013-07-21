package com.github.ogam.july;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.ogam.july.gamemodel.*;
import com.github.ogam.july.listener.PlayerKeyListener;
import com.github.ogam.july.listener.PlayerTouchListener;

public class PlayScreen implements Screen {

	OgamJulyMain g;
	

	ShapeRenderer lineDrawer;
	OrthographicCamera c;
	

	LevelContext lcontext;
	
	public PlayScreen(OgamJulyMain link)
	{
		g = link;
		lineDrawer = new ShapeRenderer();
		c = new OrthographicCamera();

		lcontext = new LevelContext();
		
		// Temporary
		c.setToOrtho(false, 800, 480);
		c.translate(new Vector2(-100,-100));
		c.update();
		
		// Probably needs to move this somewhere better (GameMain? On Show?)
		init();
	}
	
	public void init()
	{
		lcontext.init();
		
		/* Input Initialization */
		// TODO: We probably should use a multiplexer to add the ability of a separate input processor for GUI
		
		// TODO: Keyboard/Mouse-Touch should probably be defined in the game options. The switch between mouse/kb input 
		//        should be made whenever the play screen is launched. 
		
		InputProcessor playerInputProcessor = null;
		
		// boolean isKeyboardPresent = Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);
		boolean isKeyboardPresent = false;

		if ( isKeyboardPresent ) {
            playerInputProcessor = new PlayerKeyListener(lcontext.player);
        } else {
            playerInputProcessor = new GestureDetector(new PlayerTouchListener(lcontext.player,c));
        }
		
		// TODO: change this to an input Multiplexer to separate GUI input from Player input
        Gdx.input.setInputProcessor(playerInputProcessor);
        
	}
	
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		lineDrawer.setProjectionMatrix(c.combined);
		renderDebugCatwalk();
		renderDebugEnemy();
		renderDebugShip();
		
		// Might want to add a FPS limiter here in the future to
		// decouple rendering loops from update loops.
		update(delta);
	}
	
	
	/**
	 * Calls the update function on everything that is updateable
	 * @param delta
	 */
	public void update(float delta)
	{
		lcontext.update(delta);
	}
	
	private void renderDebugCatwalk()
	{
		Vector2[] lines = lcontext.catwlk.getPath();
		Vector2[] olines = lcontext.catwlk.getOriginalPath();
		
		lineDrawer.begin(ShapeType.Line);
		
		lineDrawer.setColor(Color.DARK_GRAY);
		for (int i = 0; i < olines.length; i++)
			lineDrawer.line(olines[i].x, olines[i].y, olines[(i+1)%olines.length].x, olines[(i+1)%olines.length].y);
		
		lineDrawer.setColor(Color.WHITE);
		for (int i = 0; i < lines.length; i++)
			lineDrawer.line(lines[i].x, lines[i].y, lines[(i+1)%lines.length].x, lines[(i+1)%lines.length].y);
		
// Candidate line rendering		
//		Color[]  c = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN};
//		for (int i = 0; i < p1.length; i++)
//		{
//			lineDrawer.setColor(c[i%c.length]);
//			lineDrawer.line(p1[i].x, p1[i].y, p1[(i+1)%p1.length].x, p1[(i+1)%p1.length].y);
//		}

		lineDrawer.end();
	}
	
	private void renderDebugShip()
	{
		Vector2 shipcenter = lcontext.player.getPos();
		
		// NOTE TO MME: Do you have the most recent libGDX nightly build? I think "Filled Rectangle" has been replaced with "filled"

		Array<Vector2> tmp = lcontext.player.getCutLine();
		if (tmp.size > 0) // draw the cutline
		{
			lineDrawer.begin(ShapeType.Line);
			lineDrawer.setColor(Color.RED);
			for (int i = 0; i < (tmp.size-1); i++)
			{
				lineDrawer.line(tmp.get(i).x, tmp.get(i).y, tmp.get(i+1).x, tmp.get(i+1).y);
			}
			lineDrawer.end();
		}
		
		lineDrawer.begin(ShapeType.Filled);
		
		
		if (lcontext.player.isCutting())
			lineDrawer.setColor(Color.RED);
		else
			lineDrawer.setColor(Color.BLUE);
		
		lineDrawer.rect(shipcenter.x - Ship.SHIPSIZE / 2, shipcenter.y - Ship.SHIPSIZE / 2, Ship.SHIPSIZE, Ship.SHIPSIZE);
		
		
		if (lcontext.player.lastTapLocation != null)
		{
			lineDrawer.setColor(Color.RED);
			lineDrawer.circle(lcontext.player.lastTapLocation.x, lcontext.player.lastTapLocation.y, 4);
		}
		
		lineDrawer.setColor(Color.WHITE);
		for (int i = 0; i < lcontext.player.goalList.size; i++)
		{
			lineDrawer.circle(lcontext.player.goalList.get(i).x, lcontext.player.goalList.get(i).y, 3);
		}
		
		
		lineDrawer.end();
	}
	
	private void renderDebugEnemy()
	{
		Iterator<Enemy> it = lcontext.enemylist.iterator();
		while (it.hasNext())
		{
			Enemy tmp = it.next();
			lineDrawer.begin(ShapeType.Filled);
			lineDrawer.setColor(Color.RED);
			lineDrawer.circle(tmp.getPos().x,tmp.getPos().y,10);
			lineDrawer.end();
		}
	}
	
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {

		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
