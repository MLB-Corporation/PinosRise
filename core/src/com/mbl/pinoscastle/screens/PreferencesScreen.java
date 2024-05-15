package com.mbl.pinoscastle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mbl.pinoscastle.GameClass;

public class PreferencesScreen implements Screen {

    private GameClass parent;
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private Slider gameMusicVolume;
    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOL = "sound";
    private static final String PREFS_NAME = "preferences";
    Stage stage;
    Skin skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));
    utils.Preferences preferencesData = new utils.Preferences();
    public PreferencesScreen(GameClass parent) {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        this.parent = parent;
        Table table = new Table();
        table.setFillParent(true);

        // Crea gli slider
        musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        gameMusicVolume = new Slider(0f, 1f, 0.1f, false, skin);

        // Imposta i valori iniziali degli slider
        musicVolumeSlider.setValue(preferencesData.gameMusicVolume);
        soundVolumeSlider.setValue(preferencesData.SFXVolume);
        gameMusicVolume.setValue(preferencesData.gameMusicVolume);

        // Aggiungi gli slider alla tabella
        table.add(new Label("Menu Music Volume", skin));
        table.add(musicVolumeSlider);
        table.row();
        table.add(new Label("Game Music Volume", skin));
        table.add(gameMusicVolume);
        table.row();
        table.add(new Label("Sound Volume", skin));
        table.add(soundVolumeSlider);
        Texture backgroundTexture = new Texture(Gdx.files.internal("skin/menu_background.png"));
        // Create image with background texture
        Image backgroundImage = new Image(backgroundTexture); // Set alpha to 0.5 for 50% transparency
        // adapt to screen size
        backgroundImage.setScaling(Scaling.fill);
// Set the position and size of the image
        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.getColor().a = 0.5f; // 50% transparency
// Add the background image to the stage
        stage.addActor(backgroundImage);

        // Aggiungi la tabella allo stage
        stage.addActor(table);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        if (musicVolumeSlider.isDragging()) {
            preferencesData.gameMusicVolume =  musicVolumeSlider.getValue();
            parent.getMenuScreen().menuMusic.setVolume(preferencesData.gameMusicVolume);
        }
        if (soundVolumeSlider.isDragging()) {
            setSoundVolume(soundVolumeSlider.getValue());
        }
    }

    protected Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled);
        getPrefs().flush();
    }

    public boolean isMusicEnabled() {
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean musicEnabled) {
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getPrefs().flush();
    }

    public float getMusicVolume() {
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    public float getSoundVolume() {
        return getPrefs().getFloat(PREF_SOUND_VOL, 0.5f);
    }

    public void setSoundVolume(float volume) {
        getPrefs().putFloat(PREF_SOUND_VOL, volume);
        getPrefs().flush();
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
