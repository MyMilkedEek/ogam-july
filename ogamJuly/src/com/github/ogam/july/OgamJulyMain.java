package com.github.ogam.july;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class OgamJulyMain extends Game {


	public SplashScreen s_splash;
	public MainScreen s_main;
	public PlayScreen s_play;
	
	AssetManager manager;
	
		
	@Override
	public void create() {		
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		queueAssets(); // List assets that need to be loaded 
		s_splash = new SplashScreen(this);
		s_main = new MainScreen(this);
		s_play = new PlayScreen(this);
		

		setScreen(s_main); 
	}

	
	private void queueAssets()
	{
		manager = new AssetManager();
	}
	
	@Override
	public void dispose() {
		
	}

}
