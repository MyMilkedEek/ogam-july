package com.github.ogam.july.listener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.ogam.july.gamemodel.Ship;
import com.github.ogam.july.util.Constants;

/**
 * @author caranha
 * @author My Milked Eek
 */
public class PlayerTouchListener implements GestureDetector.GestureListener {

	
    private boolean acceptingInput;
    private Ship ship;
    private OrthographicCamera worldcam;
    private Vector3 rawtouch;

    public PlayerTouchListener(Ship ship, OrthographicCamera worldcam) {
        acceptingInput = true;
        this.ship = ship;
        this.worldcam = worldcam;
        rawtouch = new Vector3();
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - touchDown @ %f:%f p%d b%d", x, y, pointer, button));
            return true;
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (acceptingInput) {
        	
        	// Added by claus -- converting from screen coordinates to world coordinates
        	// TODO: since we use a 2D world, maybe a custom function would perform better?
			rawtouch.set(x,y,0);
			worldcam.unproject(rawtouch);        	
        	
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - tap @ %f:%f c%d b%d", rawtouch.x, rawtouch.y, count, button));
            ship.doTap(new Vector2(rawtouch.x,rawtouch.y));
            return true;
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - longpress @ %f:%f", x, y));
            return true;
        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - fling @ %f:%f b%d", velocityX, velocityY, button));
            return true;
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - pan @ %f:%f %f:%f", x, y, deltaX, deltaY));
            return true;
        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - zoom @ id%f dist%f", initialDistance, distance));
            return true;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (acceptingInput) {
            Gdx.app.log(Constants.LOG_TAG, String.format("TouchListener - pinch @ %f:%f %f:%f %f:%f %f:%f", initialPointer1.x, initialPointer1.y, initialPointer2.x, initialPointer2.y,
                    pointer1.x, pointer1.y, pointer2.x, pointer2.y));
            return true;
        }
        return false;
    }
}
