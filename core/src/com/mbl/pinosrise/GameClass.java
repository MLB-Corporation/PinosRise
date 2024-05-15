package com.mbl.pinosrise;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mbl.pinosrise.screens.*;


public class GameClass extends Game {

    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private FrameBuffer frameBuffer;
    private PauseMenuScreen pauseMenuScreen;
    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int PAUSE = 3;

    public static GameClass INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera camera;
    public Screen currentScreen;
    public Screen previousScreen;
    private boolean newGame = false;
    public void setNewGame(boolean newGame){
        this.newGame = newGame;
    }
    public void changeScreen(int screen){
        previousScreen = this.getScreen();
        switch(screen){
            case MENU:
                menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                currentScreen = menuScreen;
                break;
            case PREFERENCES:
                preferencesScreen = new PreferencesScreen(this);
                this.setScreen(preferencesScreen);
                currentScreen = preferencesScreen;
                break;
            case APPLICATION:
                if(gameScreen == null || newGame) gameScreen = new GameScreen(camera, this, frameBuffer);
                this.setScreen(gameScreen);
                currentScreen = gameScreen;
                break;
            case PAUSE:
                pauseMenuScreen = new PauseMenuScreen(this);
                this.setScreen(pauseMenuScreen);
                currentScreen = pauseMenuScreen;
                break;
        }
    }

    public GameClass(int widthScreen, int heightScreen) {
        INSTANCE = this;
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
    }



    public GameScreen getGameScreen() {
        return gameScreen;
    }


    @Override
    public void create () {
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, widthScreen/2, heightScreen/2);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        // Inizializza il MenuScreen e il GameScreen
        gameScreen = new GameScreen(camera, this, frameBuffer);
        menuScreen = new MenuScreen(this);


        // Imposta il MenuScreen come schermata iniziale
        setScreen(menuScreen);
    }



    public int getPreviousScreen() {
        return previousScreen == menuScreen ? MENU : previousScreen == preferencesScreen ? PREFERENCES : previousScreen == gameScreen ? APPLICATION : PAUSE;
    }
}
