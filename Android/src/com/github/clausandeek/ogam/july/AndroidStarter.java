package com.github.clausandeek.ogam.july;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.clausandeek.ogam.july.core.Ogam;

public class AndroidStarter extends AndroidApplication {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

        // TODO initialization settings will need to go to a properties file/constants class
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useWakelock = true;
        cfg.useGL20 = true;
        
        initialize(new Ogam(), cfg);
    }
}
