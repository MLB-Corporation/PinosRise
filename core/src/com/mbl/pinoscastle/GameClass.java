package com.mbl.pinoscastle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mbl.pinoscastle.screens.GameScreen;
import com.mbl.pinoscastle.screens.LoadingScreen;
import com.mbl.pinoscastle.screens.MenuScreen;
import com.mbl.pinoscastle.screens.PreferencesScreen;


public class GameClass extends Game {

    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;

    public static GameClass INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera camera;

    public void changeScreen(int screen){
        switch(screen){
            case MENU:
                if(menuScreen == null) menuScreen = new MenuScreen(this); // added (this)
                this.setScreen(menuScreen);
                break;
            case PREFERENCES:
                if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(this); // added (this)
                this.setScreen(preferencesScreen);
                break;
            case APPLICATION:
                if(gameScreen == null) gameScreen = new GameScreen(camera, this); //added (this)
                this.setScreen(gameScreen);
                break;
        }
    }

    public GameClass(int widthScreen, int heightScreen) {
        INSTANCE = this;
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
    }



    @Override
    public void create () {
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, widthScreen/2, heightScreen/2);

        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
        //setScreen(new GameScreen(camera));
    }
}
