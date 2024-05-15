package com.mbl.pinosrise.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mbl.pinosrise.GameClass;

import static utils.Constants.*;

public class PreferencesScreen implements Screen {

    private GameClass parent;
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private Slider gameMusicVolume;
    private Button backButton;
    private CheckBox musicEnabled;
    private CheckBox soundEnabled;
    public static final String PREF_MENU_MUSIC_VOLUME = "volume";
    public static final String PREF_SOUND_VOL = "sound";
    public static final String PREFS_NAME = "preferences";
    public static final String PREF_GAME_MUSIC_VOLUME = "gameMusic";

    Stage stage;
    Skin skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));
    Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
    public PreferencesScreen(GameClass parent) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);


        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        this.parent = parent;
        Table table = new Table();
        table.setFillParent(true);

        backButton = new TextButton("BACK", skin, "bold");
        // Crea gli slider
        musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        gameMusicVolume = new Slider(0f, 1f, 0.1f, false, skin);


        // Imposta i valori iniziali degli slider
        musicVolumeSlider.setValue(prefs.getFloat(PREF_MENU_MUSIC_VOLUME, 0.5f));
        soundVolumeSlider.setValue(prefs.getFloat(PREF_SOUND_VOL, 0.5f));
        gameMusicVolume.setValue(prefs.getFloat(PREF_GAME_MUSIC_VOLUME, 0.5f));

        // Aggiungi gli slider alla tabella
        table.add(new Label("Menu Music Volume", skin));
        table.add(musicVolumeSlider);
        table.row();
        table.add(new Label("Game Music Volume", skin));
        table.add(gameMusicVolume);
        table.row();
        table.add(new Label("Sound Volume", skin));
        table.add(soundVolumeSlider);
        table.row().pad(10, 0, 10, 0);
        //add space
        table.add(backButton).colspan(2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("skin/menu_background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.fill);

        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.getColor().a = 0.5f;
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }
    @Override
    public void show() {
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                parent.changeScreen(parent.getPreviousScreen());

            }
        });


    };


    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        if (musicVolumeSlider.isDragging()) {
            preferences.putFloat(PREF_MENU_MUSIC_VOLUME, musicVolumeSlider.getValue());
            menuMusic.setVolume(musicVolumeSlider.getValue()/50);
        }
        if (soundVolumeSlider.isDragging()) {
            preferences.putFloat(PREF_SOUND_VOL, soundVolumeSlider.getValue());
            jumpSound.setVolume(soundVolumeSlider.getValue()/10);
            jumpSound.play();

        }
        if(gameMusicVolume.isDragging()){
            preferences.putFloat(PREF_GAME_MUSIC_VOLUME, gameMusicVolume.getValue());
            gameMusic.setVolume(gameMusicVolume.getValue()/50);
        }
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
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putFloat(PREF_MENU_MUSIC_VOLUME, musicVolumeSlider.getValue());
        prefs.putFloat(PREF_SOUND_VOL, soundVolumeSlider.getValue());
        prefs.putFloat(PREF_GAME_MUSIC_VOLUME, gameMusicVolume.getValue());
        prefs.flush(); // salva le preferenze
    }

    @Override
    public void dispose() {

    }




}
