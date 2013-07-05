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
import com.github.ogam.july.listener.KeyboardListener;
import com.github.ogam.july.listener.TouchListener;

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

        // Binds the ship to the touch/mouse input
        boolean isKeyboardPresent = Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);
        InputProcessor inputProcessor = null;

        if ( isKeyboardPresent ) {
            inputProcessor = new KeyboardListener(playership);
        } else {
            inputProcessor = new GestureDetector(new TouchListener(playership));
        }

        Gdx.input.setInputProcessor(inputProcessor);
	}
	
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		lineDrawer.setProjectionMatrix(c.combined);
		renderDebugCatwalk();
		renderDebugShip();
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
		
		lineDrawer.begin(ShapeType.FilledRectangle);
		lineDrawer.setColor(Color.BLUE);
		lineDrawer.filledRect(shipcenter.x - Ship.SHIPSIZE / 2, shipcenter.y - Ship.SHIPSIZE / 2, Ship.SHIPSIZE, Ship.SHIPSIZE);
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
