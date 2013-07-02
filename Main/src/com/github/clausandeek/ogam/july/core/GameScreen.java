package com.github.clausandeek.ogam.july.core;

import com.badlogic.gdx.Screen;

/**
 * @author My Milked Eek
 * @author caranha
 */
public class GameScreen implements Screen {

    // game instance
    private Ogam game;


    public GameScreen(Ogam game) {
        this.game = game;
    }


    @Override
    public void render(float delta) {
        update(delta);
    }

    /**
     * Method to separate rendering from the game logic.
     *
     * @param delta
     */
    public void update(float delta) {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
