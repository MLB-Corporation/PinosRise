package utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AnimationLoader {

    private AnimationLoader animationLoader;
    private BufferedImage[] runRightAnimation;
    private Sprite[] runRightSprites;
    private BufferedImage[] runLeftAnimation;
    private Sprite[] runLeftSprites;
    private BufferedImage[] idleAnimation;
    private Sprite[] idleSprites;
    private BufferedImage[] leftJumpAnimation;
    private Sprite[] leftJumpSprites;
    private BufferedImage[] rightJumpAnimation;
    private Sprite[] rightJumpSprites;
    private BufferedImage[] fallAnimation;
    private Sprite[] fallSprites;
    private BufferedImage[] rightAnimation;
    private Sprite[] rightSprites;
    private BufferedImage[] leftAnimation;
    private Sprite[] leftSprites;
    private BufferedImage[] climbAnimation;
    private Sprite[] climbSprites;

    public AnimationLoader() {
        // carica le animazioni e le trasforma in sprite
        runRightAnimation = loadAnimation("runRight");
        idleAnimation = loadAnimation("idle");
        runRightSprites = new Sprite[runRightAnimation.length];
        for (int i = 0; i < runRightAnimation.length; i++) {
            runRightSprites[i] = new Sprite(bufferedImageToTexture(runRightAnimation[i]));
        }

        runLeftAnimation = loadAnimation("runLeft");
        runLeftSprites = new Sprite[runLeftAnimation.length];
        for (int i = 0; i < runLeftAnimation.length; i++) {
            runLeftSprites[i] = new Sprite(bufferedImageToTexture(runLeftAnimation[i]));
        }

        idleSprites = new Sprite[idleAnimation.length];
        for (int i = 0; i < idleAnimation.length; i++) {
            idleSprites[i] = new Sprite(bufferedImageToTexture(idleAnimation[i]));
        }
        leftJumpAnimation = loadAnimation("leftJump");
        leftJumpSprites = new Sprite[leftJumpAnimation.length];
        for (int i = 0; i < leftJumpAnimation.length; i++) {
            leftJumpSprites[i] = new Sprite(bufferedImageToTexture(leftJumpAnimation[i]));
        }
        rightJumpAnimation = loadAnimation("rightJump");
        rightJumpSprites = new Sprite[rightJumpAnimation.length];
        for (int i = 0; i < rightJumpAnimation.length; i++) {
            rightJumpSprites[i] = new Sprite(bufferedImageToTexture(rightJumpAnimation[i]));
        }


        fallAnimation = loadAnimation("fall");
        fallSprites = new Sprite[fallAnimation.length];
        for (int i = 0; i < fallAnimation.length; i++) {
            fallSprites[i] = new Sprite(bufferedImageToTexture(fallAnimation[i]));
        }

        rightAnimation = loadAnimation("right");
        rightSprites = new Sprite[rightAnimation.length];
        for (int i = 0; i < rightAnimation.length; i++) {
            rightSprites[i] = new Sprite(bufferedImageToTexture(rightAnimation[i]));
        }
        leftAnimation = loadAnimation("left");
        leftSprites = new Sprite[leftAnimation.length];
        for (int i = 0; i < leftAnimation.length; i++) {
            leftSprites[i] = new Sprite(bufferedImageToTexture(leftAnimation[i]));
        }

    }

    BufferedImage sprite;

    public BufferedImage[] loadAnimation(String anim) {

        switch (anim) {
            // carica le immagini delle animazioni e le restituisce in un array
            case "runRight":
                runRightAnimation = new BufferedImage[Constants.PLAYER_RUN_LENGHT];
                for (int i = 0; i < runRightAnimation.length; i++) {
                    runRightAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_RUN_RIGHT).getSubimage(i * 12, 0, 12, 17);
                }
                return runRightAnimation;
            case "runLeft":
                runLeftAnimation = new BufferedImage[Constants.PLAYER_RUN_LENGHT];
                for (int i = 0; i < runLeftAnimation.length; i++) {
                    runLeftAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_RUN_LEFT).getSubimage(i * 12, 0, 12, 17);
                }
                return runLeftAnimation;

            case "idle":
                idleAnimation = new BufferedImage[Constants.PLAYER_IDLE_LENGHT];
                for (int i = 0; i < idleAnimation.length; i++) {
                    idleAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_IDLE).getSubimage(i * 12, 0, 12, 17);
                }
                return idleAnimation;
            case "leftJump":
                leftJumpAnimation = new BufferedImage[10];
                for(int i = 0; i < 10; i++)
                    leftJumpAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_LEFT_JUMP).getSubimage(0, 0, 12, 17);
                return leftJumpAnimation;
            case "rightJump":
                rightJumpAnimation = new BufferedImage[10];
                for(int i = 0; i < 10; i++)
                    rightJumpAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_RIGHT_JUMP).getSubimage(0, 0, 12, 17);
                return rightJumpAnimation;
            case "fall":
                fallAnimation = new BufferedImage[2];
                for(int i = 0; i < 2; i++)
                    fallAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_FALL).getSubimage(i*12, 0, 12, 17);
                return fallAnimation;
            case "right":
                rightAnimation = new BufferedImage[2];
                for(int i = 0; i < 2; i++)
                    rightAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_RIGHT).getSubimage(i*12, 0, 12, 17);
                return rightAnimation;
            case "left":
                leftAnimation = new BufferedImage[2];
                for(int i = 0; i < 2; i++)
                    leftAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_LEFT).getSubimage(i*12, 0, 12, 17);
                return leftAnimation;

        }
        return null;
    }

    public Sprite[] getAnimation(String state) {
        switch(state) {
            case "runRight":
                return runRightSprites;
            case "runLeft":
                return runLeftSprites;
            case "idle":
                return idleSprites;
            case "leftJump":
                return leftJumpSprites;
            case "rightJump":
                return rightJumpSprites;
            case "fall":
                return fallSprites;
            case "right":
                return rightSprites;
            case "left":
                return leftSprites;
            case "climb":
                return climbSprites;
            default:
                return null;

        }

    }

    // converte un BufferedImage in un Texture
    private Texture bufferedImageToTexture(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] byteArray = baos.toByteArray();

            Pixmap pixmap = new Pixmap(byteArray, 0, byteArray.length);
            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            return texture;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load texture", e);
        }
    }
}
