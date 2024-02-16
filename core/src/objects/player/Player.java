package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import static utils.Constants.PPM;

public class Player extends GameEntity {

    private int jumpCount;
    private Sprite sprite;
    private TiledMap tiledMap; // Reference to the TiledMap

    public Player(float width, float height, Body body, TiledMap tiledMap) {
        super(width, height, body);
        this.speed = 2.5f;
        this.jumpCount = 0;
        this.tiledMap = tiledMap; // Initialize the TiledMap

        Texture texture = new Texture(Gdx.files.internal("player/player.png"));
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width / PPM, height / PPM);
        this.sprite.setOrigin(width / (2 * PPM), height / (2 * PPM));
    }

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        checkTeleport();
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

    private void checkTeleport() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Vector2 playerPosition = new Vector2(body.getPosition().x, body.getPosition().y);
            MapObjects objects = tiledMap.getLayers().get("Objects").getObjects(); // Access objects from the "Objects" layer

            // Create a copy of objects for the second loop
            MapObjects objectsCopy = new MapObjects();
            for (MapObject object : objects) {
                objectsCopy.add(object);
            }

            for (MapObject object : objects) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String objectName = object.getName();
                    if (rect.contains(playerPosition.x * PPM, body.getPosition().y * PPM)) {
                        // Check if the current object is named "pipe_1A" or "pipe_1B"
                        if (objectName.endsWith("A")) {
                            teleportToDestination(objectsCopy, objectName.replace("A", "B"));
                        } else if (objectName.endsWith("B")) {
                            teleportToDestination(objectsCopy, objectName.replace("B", "A"));
                        }
                    }
                }
            }
        }
    }

    private void teleportToDestination(MapObjects objects, String destinationName) {
        for (MapObject destinationObject : objects) {
            if (destinationName.equals(destinationObject.getName())) {
                if (destinationObject instanceof RectangleMapObject) {
                    Rectangle destinationRect = ((RectangleMapObject) destinationObject).getRectangle();
                    Vector2 destination = new Vector2(destinationRect.x + destinationRect.width / 2, destinationRect.y + destinationRect.height / 2);
                    body.setTransform(destination.scl(1 / PPM), body.getAngle()); // Ensure to keep the player's current angle
                    return; // Exit after teleporting
                }
            }
        }
    }



}
