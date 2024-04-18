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
    private BufferedImage[] runAnimation;
    private Sprite[] runSprites;
    private BufferedImage[] idleAnimation;
    private Sprite[] idleSprites;
    private BufferedImage[] jumpAnimation;
    private Sprite[] jumpSprites;
    private BufferedImage[] fallAnimation;
    private Sprite[] fallSprites;
    private BufferedImage[] rightAnimation;
    private Sprite[] rightSprites;
    private BufferedImage[] leftAnimation;
    private Sprite[] leftSprites;
    private BufferedImage[] climbAnimation;
    private Sprite[] climbSprites;

    public AnimationLoader() {
        runAnimation = loadAnimation("run");
        idleAnimation = loadAnimation("idle");
        runSprites = new Sprite[runAnimation.length];
        for (int i = 0; i < runAnimation.length; i++) {
            runSprites[i] = new Sprite(bufferedImageToTexture(runAnimation[i]));
        }
        idleSprites = new Sprite[idleAnimation.length];
        for (int i = 0; i < idleAnimation.length; i++) {
            idleSprites[i] = new Sprite(bufferedImageToTexture(idleAnimation[i]));
        }
        /*jumpAnimation = loadAnimation("jump");
        jumpSprites = new Sprite[jumpAnimation.length];
        for (int i = 0; i < jumpAnimation.length; i++) {
            jumpSprites[i] = new Sprite(bufferedImageToTexture(jumpAnimation[i]));
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
        climbAnimation = loadAnimation("climb");
        climbSprites = new Sprite[climbAnimation.length];
        for (int i = 0; i < climbAnimation.length; i++) {
            climbSprites[i] = new Sprite(bufferedImageToTexture(climbAnimation[i]));
        }*/
    }

    BufferedImage sprite;

    public BufferedImage[] loadAnimation(String anim) {

        switch (anim) {
            case "run":
                runAnimation = new BufferedImage[Constants.PLAYER_RUN_LENGHT];
                for (int i = 0; i < runAnimation.length; i++) {
                    runAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_RUN).getSubimage(i * 12, 0, 12, 17);
                }
                return runAnimation;
            case "idle":
                idleAnimation = new BufferedImage[Constants.PLAYER_IDLE_LENGHT];
                for (int i = 0; i < idleAnimation.length; i++) {
                    idleAnimation[i] = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_IDLE).getSubimage(i * 12, 0, 12, 17);
                }
                return idleAnimation;

        }
        return null;
    }

    public Sprite[] getAnimation(String state) {
        switch(state) {
            case "run":
                return runSprites;
            case "idle":
                return idleSprites;
            case "jump":
                return jumpSprites;
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
