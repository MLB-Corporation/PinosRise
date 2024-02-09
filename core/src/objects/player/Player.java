package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;

import static utils.Constants.PPM;

public class Player extends GameEntity{

    private int jumpCount;
    private Sprite sprite;

    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 2.5f;
        this.jumpCount = 0;

        Texture texture = new Texture(Gdx.files.internal("player/player.png"));
        this.sprite = new Sprite(texture);

        // Adjust the size of the sprite to match the size of the hitbox
        this.sprite.setSize(width, height);

        // Set the origin of the sprite to its center (important for rotating)
        this.sprite.setOrigin(width / 2, height / 2);
    }
    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        sprite.setPosition(x-10, y-10);
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }


    private void checkUserInput() {
        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
            velX = 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            velX = -1;

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCount < 2 || Gdx.input.isKeyJustPressed(Input.Keys.W) && jumpCount < 2 || Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCount < 2){
            float force = body.getMass()*10;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCount++;
        }

        if(body.getLinearVelocity().y == 0) {
            jumpCount = 0;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 7 ? body.getLinearVelocity().y : 7);
    }
}
