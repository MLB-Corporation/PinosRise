package com.mbl.pinoscastle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameClass extends Game {


    public static GameClass INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera camera;

    public GameClass(int widthScreen, int heightScreen) {
        INSTANCE = this;
    }

    @Override
    public void create () {
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, widthScreen/3, heightScreen/3);

        setScreen(new GameScreen(camera));
    }
}
