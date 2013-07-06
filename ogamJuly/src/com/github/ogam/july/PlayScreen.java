package com.github.ogam.july;

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
import com.github.ogam.july.gamemodel.*;
import com.github.ogam.july.listener.PlayerKeyListener;
import com.github.ogam.july.listener.PlayerTouchListener;

public class PlayScreen implements Screen {

	OgamJulyMain g;
	
	CatWalk catwlk;
	Ship playership;
	ShapeRenderer lineDrawer;
	OrthographicCamera c;
	
	
	
	public PlayScreen(OgamJulyMain link)
	{
		g = link;
		lineDrawer = new ShapeRenderer();
		c = new OrthographicCamera();

		
		// Temporary
		c.setToOrtho(false, 800, 480);
		c.translate(new Vector2(-100,-100));
		c.update();
		
		// Probably needs to move this somewhere better (GameMain? On Show?)
		init();
	}
	
	public void init()
	{
		catwlk = new CatWalk();
		playership = new Ship();
		playership.setPos(catwlk.getPath()[0]);
		playership.setLane(catwlk);

		/* Input Initialization */
		// TODO: We probably should use a multiplexer to add the ability of a separate input processor for GUI
		
		// TODO: Keyboard/Mouse-Touch should probably be defined in the game options. The switch between mouse/kb input 
		//        should be made whenever the play screen is launched. 
		
		InputProcessor playerInputProcessor = null;
		
		// boolean isKeyboardPresent = Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);
		boolean isKeyboardPresent = false;

		if ( isKeyboardPresent ) {
            playerInputProcessor = new PlayerKeyListener(playership);
        } else {
            playerInputProcessor = new GestureDetector(new PlayerTouchListener(playership,c));
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
		
	}
	
	

	private void renderDebugCatwalk()
	{
		Vector2[] lines = catwlk.getPath();
		
		
		lineDrawer.begin(ShapeType.Line);
		lineDrawer.setColor(Color.WHITE);
		for (int i = 0; i < lines.length; i++)
			lineDrawer.line(lines[i].x, lines[i].y, lines[(i+1)%lines.length].x, lines[(i+1)%lines.length].y);
		lineDrawer.end();
	}
	
	private void renderDebugShip()
	{
		Vector2 shipcenter = playership.getPos();
		
		// NOTE TO MME: Do you have the most recent libGDX nightly build? I think "Filled Rectangle" has been replaced with "filled"
		lineDrawer.begin(ShapeType.Filled);
		lineDrawer.setColor(Color.BLUE);
		lineDrawer.rect(shipcenter.x - Ship.SHIPSIZE / 2, shipcenter.y - Ship.SHIPSIZE / 2, Ship.SHIPSIZE, Ship.SHIPSIZE);
		if (playership.lastTapLocation != null)
		{
			lineDrawer.setColor(Color.RED);
			lineDrawer.circle(playership.lastTapLocation.x, playership.lastTapLocation.y, 4);
		}
		
		lineDrawer.setColor(Color.WHITE);
		for (int i = 0; i < playership.goalList.size; i++)
		{
			lineDrawer.circle(playership.goalList.get(i).x, playership.goalList.get(i).y, 3);
		}
		lineDrawer.end();
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
