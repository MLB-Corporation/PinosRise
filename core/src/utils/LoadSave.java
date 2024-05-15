package utils;


import com.badlogic.gdx.Gdx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class LoadSave{


    public static final String PLAYER_RUN_RIGHT = "player/right_run.png";
    public static final String PLAYER_RUN_LEFT = "player/left_run.png";
    public static final String PLAYER_IDLE = "player/idle.png";
    public static final String PLAYER_LEFT_JUMP = "player/left_jump.png";
    public static final String PLAYER_RIGHT_JUMP = "player/right_jump.png";
    public static final String PLAYER_FALL = "player/player_fall.png";
    public static final String PLAYER_RIGHT = "player/walk_right.png";
    public static final String PLAYER_LEFT = "player/walk_left.png";



    // carica un'immagine da un file
    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img = null;
        InputStream is = Gdx.files.internal(fileName).read();

        try{
            img = ImageIO.read(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return img;
    }




}