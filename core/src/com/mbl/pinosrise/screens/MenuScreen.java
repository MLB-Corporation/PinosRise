package com.mbl.pinosrise.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mbl.pinosrise.GameClass;

import static utils.Constants.gameMusic;
import static utils.Constants.menuMusic;

public class MenuScreen implements Screen {

    private GameClass parent;
    private Stage stage;
    private Table table;
    Preferences preferencesData = Gdx.app.getPreferences("preferences");

    TextButton exit, newGame, preferences, loadGame;
    public MenuScreen(GameClass parent) {
        this.parent = parent;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        // Crea una tabella che riempie lo schermo. Tutto il resto andrà all'interno di questa tabella.
        table = new Table();
        table.setFillParent(true);
        table.setTransform(true);

        Texture backgroundTexture = new Texture(Gdx.files.internal("skin/background.png"));
        // Crea un'immagine con la texture di sfondo
        Image backgroundImage = new Image(backgroundTexture);


        stage.addActor(backgroundImage);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));
        newGame = new TextButton("NEW GAME", skin, "bold");
        preferences = new TextButton("OPTIONS", skin, "bold");
        exit = new TextButton("EXIT", skin, "bold");
        loadGame = new TextButton("LOAD GAME", skin, "bold");

        table.add(newGame).fillX().uniformX();

        //controlla se nel file di salvataggio c'è una partita salvata
        //se c'è, mostra il pulsante "LOAD GAME"
        //altrimenti, nasconde il pulsante "LOAD GAME"
        if (Gdx.files.local("savegame.json").exists() && !Gdx.files.local("savegame.json").readString().isEmpty()) {
            table.row().pad(10, 0, 10, 0);
            table.add(loadGame).fillX().uniformX();
            table.row().pad(0, 0, 10, 0);
            table.add(preferences).fillX().uniformX();
            table.row().pad(0, 0, 10, 0);
            table.add(exit).fillX().uniformX();
        }
        else {
            table.row().pad(10, 0, 10, 0);
            table.add(preferences).fillX().uniformX();
            table.row().pad(0, 0, 10, 0);
            table.add(exit).fillX().uniformX();
        }


        // controllo per evitare che la musica si sovrapponga
        if (!menuMusic.isPlaying()) {
            menuMusic.setLooping(true);
            System.out.println("Playing music");
            menuMusic.play();
        }



    }
    @Override
    public void show() {

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setNewGame(true);
                parent.getGameScreen().resetGame();
                parent.changeScreen(GameClass.APPLICATION);
                menuMusic.stop();
                gameMusic.play();
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(GameClass.PREFERENCES);
            }
        });

        loadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.getGameScreen().loadGame();
                parent.changeScreen(GameClass.APPLICATION);
                menuMusic.stop();
                gameMusic.play();

                }
            }
        );

    }

    @Override
    public void render(float delta) {

        menuMusic.setVolume(preferencesData.getFloat("volume") / 50);

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
