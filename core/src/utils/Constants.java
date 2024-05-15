package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Constants {

    public static final float PPM = 32.0f;
    public static final int PLAYER_RUN_LENGHT = 3;
    public static final int PLAYER_IDLE_LENGHT = 10;
    public static final int PLAYER_JUMP_LENGHT = 2;
    public static final int PLAYER_FALL_LENGHT = 2;
    public static final int PLAYER_WALK_LENGHT = 2;

    public static Music menuMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/menu_music.mp3"));
    public static Music jumpSound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/jump.mp3"));
    public static Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/game_music.mp3"));
    public static Music saveSound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/save.mp3"));
}

