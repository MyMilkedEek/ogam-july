package com.github.clausandeek.ogam.july.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * @author My Milked Eek
 * @author caranha
 */
public class Ogam extends Game {

    // main game screen
    private Screen gameScreen;


    @Override
    public void create() {
        // TODO asset loading

        // screen initialization
        gameScreen = new GameScreen(this);

        setScreen(gameScreen);
    }

    public Screen getGameScreen() {
        return gameScreen;
    }
}