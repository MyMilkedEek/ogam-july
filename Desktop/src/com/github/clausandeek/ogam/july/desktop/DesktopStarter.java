package com.github.clausandeek.ogam.july.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.clausandeek.ogam.july.core.Ogam;

/**
 * @author My Milked Eek
 * @author caranha
 */
public class DesktopStarter {
    
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        // TODO initialization settings will need to go to a property file/constants class
        cfg.title = "Title";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;

        new LwjglApplication(new Ogam(), cfg);
    }
}
