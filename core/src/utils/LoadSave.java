package utils;


import com.badlogic.gdx.Gdx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class LoadSave{


    public static final String PLAYER_RUN = "player/run.png";
    public static final String PLAYER_IDLE = "player/idle.png";




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