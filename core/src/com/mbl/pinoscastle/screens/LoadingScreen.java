package com.mbl.pinoscastle.screens;

import com.badlogic.gdx.Screen;
import com.mbl.pinoscastle.GameClass;

public class LoadingScreen implements Screen {

    private GameClass parent;

    public LoadingScreen(GameClass parent) {
        this.parent = parent;
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        parent.changeScreen(GameClass.MENU);

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
