package com.mbl.pinosrise.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mbl.pinosrise.GameClass;

import static com.mbl.pinosrise.screens.PreferencesScreen.PREF_SOUND_VOL;
import static utils.Constants.gameMusic;
import static utils.Constants.saveSound;

public class PauseMenuScreen implements Screen {

    private GameClass parent;
    private Stage stage;
    private Table table;
    // Impostazioni
    private Preferences preferencesData = Gdx.app.getPreferences("preferences");

    private TextButton exit, resume, preferences, save;

    public PauseMenuScreen(GameClass parent) {
        this.parent = parent;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        // Crea una tabella che riempie lo schermo. Tutto il resto andrà all'interno di questa tabella.
        table = new Table();
        table.setFillParent(true);
        table.setTransform(true);

        Texture backgroundTexture = new Texture(Gdx.files.internal("skin/menu_background.png"));

        Image backgroundImage = new Image(backgroundTexture);

        backgroundImage.setScaling(Scaling.fill);

        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.getColor().a = 0.5f; // 50% di trasparenza per rendere il background più scuro
        stage.addActor(backgroundImage);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));
        resume = new TextButton("RESUME", skin, "bold");
        preferences = new TextButton("OPTIONS", skin, "bold");
        exit = new TextButton("MAIN MENU", skin, "bold");
        save = new TextButton("SAVE", skin, "bold");

        table.add(resume).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(preferences).fillX().uniformX();
        table.row().pad(0, 0, 10, 0);
        table.add(save).fillX().uniformX();
        table.row().pad(0, 0, 10, 0);
        table.add(exit).fillX().uniformX();
    }
    @Override
    public void show() {
        Gdx.input.setCursorCatched(false);

        resume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(GameClass.APPLICATION);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameMusic.stop();
                parent.changeScreen(GameClass.MENU);
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(GameClass.PREFERENCES);
            }
        });

        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setNewGame(false);
                parent.getGameScreen().saveGame();
                parent.changeScreen(GameClass.APPLICATION);
                saveSound.setVolume(preferencesData.getFloat(PREF_SOUND_VOL)/10);
                saveSound.play();
                String message = "Game saved!";
                parent.getGameScreen().showMessage(message);
            }
        });


    }

    @Override
    public void render(float delta) {


        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(i, i1, true);
        table.invalidateHierarchy();
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
        stage.dispose();
    }
}
