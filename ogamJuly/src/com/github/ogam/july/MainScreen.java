package com.github.ogam.july;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainScreen implements Screen {

	OgamJulyMain g;
	
	BitmapFont debugtext;
	SpriteBatch batch;
	
	
	public MainScreen(OgamJulyMain maingame)
	{
		g = maingame;
		debugtext = new BitmapFont();
		batch = new SpriteBatch();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		debugtext.setColor(Color.WHITE);
		debugtext.draw(batch, "Click to Start", 200, 200);
		batch.end();
		
		if (Gdx.input.isTouched())
		{
			g.setScreen(g.s_play);
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
