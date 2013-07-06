package com.github.ogam.july.listener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.github.ogam.july.gamemodel.Ship;
import com.github.ogam.july.util.Constants;

/**
 * @author My Milked Eek
 */
public class PlayerKeyListener implements InputProcessor {

    private boolean acceptingInput;

    private Ship ship;

    public PlayerKeyListener(Ship ship) {
        acceptingInput = true;

        this.ship = ship;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("KeyboardListener - keyDown : kc %d", keycode));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("KeyboardListener - keyDown : kc %d", keycode));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("KeyboardListener - keyDown : char %c", character));
            return true;
        }
        return false;
    }


    /* IRRELEVANT OVERRIDES :( */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
