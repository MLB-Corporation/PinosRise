package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbl.pinoscastle.screens.GameScreen;
import com.badlogic.gdx.utils.Timer;
import utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static utils.Constants.PPM;

public class Player extends GameEntity {

    private static int groundContacts = 1;

    // Other methods...

    public void hitGround() {
        if (groundContacts == 0) {
            groundContacts++;
        }
    }

    public void leaveGround() {
        if (groundContacts > 0) {
            groundContacts--;
        }
    }



    public boolean isOnGround() {
        return this.groundContacts > 0;
    }

    private BufferedImage[] run;

    private GameScreen gameScreen;

    private TileMapHelper tileMapHelper;

    private Texture texture;

    private Rectangle rect;

    private World world;

    private PlayerContactListener contactListener;

    private int jumpCount;
    private Sprite sprite;
    private TiledMap tiledMap; // Reference to the TiledMap

    private float jumpCooldown = 0; // Add this line at the beginning of your class

    Pixmap pixmap;
    Texture darkness;
    private int aniTick, aniIndex, aniSpeed = 5;

    //ANIMATIONS
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

    private String state;



    public Player(float width, float height, Body body, TiledMap tiledMap, GameScreen gameScreen, RectangleMapObject mapObject, World world) {
        super(width, height, body);

        this.tileMapHelper = new TileMapHelper(gameScreen);
        this.speed = 2.5f;
        this.jumpCount = 0;
        this.rect = mapObject.getRectangle();
        this.tiledMap = tiledMap; // Initialize the TiledMap
        this.gameScreen = gameScreen;

        this.texture = new Texture(Gdx.files.internal("player/player.png"));
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width / PPM, height / PPM);
        this.sprite.setOrigin(width / (2 * PPM), height / (2 * PPM));
        this.contactListener =  new PlayerContactListener(this, world, gameScreen);
        this.world = world;
        this.pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        darkness = new Texture(pixmap);


        this.animationLoader = new AnimationLoader();



        state = "idle";

    }





    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        checkTeleport();
        checkClimbable();

    }



    private void checkClimbable() {
        Vector2 playerPosition = new Vector2(body.getPosition().x, body.getPosition().y);
        MapObjects objects = tiledMap.getLayers().get("Objects").getObjects(); // Access objects from the "Objects" layer

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String objectName = object.getName();
                if (rect.contains(playerPosition.x * PPM, body.getPosition().y * PPM)) {
                    if (objectName.equals("ladder")) {
                        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                            body.setLinearVelocity(body.getLinearVelocity().x, 1 * speed);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {

        float widthInPixels = rect.width;
        float heightInPixels = rect.height;
        Sprite sprite = new Sprite(texture);
        //sprite.setPosition(body.getPosition().x*PPM-widthInPixels/2, body.getPosition().y*PPM-heightInPixels/2);
        //sprite.draw(batch);
        batch.draw(darkness, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Sprite[] animation = animationLoader.getAnimation(state);
        animation[aniIndex].setPosition(body.getPosition().x * PPM - sprite.getWidth() / 2, body.getPosition().y * PPM - sprite.getHeight() / 2);
        animation[aniIndex].draw(batch);

    }


    private float jumpTimer = 0; // Add this line at the beginning of your class

    private void checkUserInput() {

        if (jumpCooldown > 0) {
            jumpCooldown -= Gdx.graphics.getDeltaTime();
        }

        if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)){
            state = "idle";
            aniTick++;
            if (aniTick >= 10) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= 10) {
                    aniIndex = 0;
                }
            }
        }

        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
                if (isOnGround()) {
                    //start a 0.5s timer, then return false
                    aniIndex = 0;
                    state = "rightJump";
                    aniTick++;
                    if (aniTick >= 10) {
                        aniTick = 0;
                        aniIndex++;
                        if (aniIndex >= 10) {
                            aniIndex = 0;
                        }
                    }

                    groundContacts = 0;
                    jumpCount = 1;
                    float force = body.getMass()*10;
                    body.setLinearVelocity(body.getLinearVelocity().x, 0);
                    body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
                    jumpCount++;

                    // Reset the jump timer
                    jumpTimer = 0;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                state = "runRight";
                if (aniIndex >= 3) {
                    aniIndex = 0;
                }
                aniTick++;
                if (aniTick >= aniSpeed) {
                    aniTick = 0;
                    aniIndex++;
                    if (aniIndex >= 3) {
                        aniIndex = 0;
                    }
                }
                velX = (float) 1.5;
            }

            else {
                velX = 1;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
                if (isOnGround()) {
                    //start a 0.5s timer, then return false
                    aniIndex = 0;
                    state = "leftJump";
                    aniTick++;
                    if (aniTick >= 10) {
                        aniTick = 0;
                        aniIndex++;
                        if (aniIndex >= 10) {
                            aniIndex = 0;
                        }
                    }

                    groundContacts = 0;
                    jumpCount = 1;
                    float force = body.getMass()*10;
                    body.setLinearVelocity(body.getLinearVelocity().x, 0);
                    body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
                    jumpCount++;

                    // Reset the jump timer
                    jumpTimer = 0;
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                state = "runLeft";
                velX = (float) -1.5;
                if (aniIndex >= 3) {
                    aniIndex = 0;
                }
                aniTick++;
                if (aniTick >= aniSpeed) {
                    aniTick = 0;
                    aniIndex++;
                    if (aniIndex >= 3) {
                        aniIndex = 0;
                    }
                }
            } else
                velX = -1;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);

        // Update the jump timer
        jumpTimer += Gdx.graphics.getDeltaTime();

        // Check if the space key is pressed, the player is on the ground, and at least 1 second has passed since the last jump
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
            if(contactListener.isTouchingVerticalWall() && contactListener.checkContact() && !contactListener.isPlayerAboveGround()) {



            }

            if (isOnGround()) {
                //start a 0.5s timer, then return false
                aniIndex = 0;
                state = "rightJump";
                aniTick++;
                if (aniTick >= 10) {
                    aniTick = 0;
                    aniIndex++;
                    if (aniIndex >= 10) {
                        aniIndex = 0;
                    }
                }

                groundContacts = 0;
                jumpCount = 1;
                float force = body.getMass()*10;
                body.setLinearVelocity(body.getLinearVelocity().x, 0);
                body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
                jumpCount++;

                // Reset the jump timer
                jumpTimer = 0;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            System.out.println("onGround? " + groundContacts);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            Vector2 playerPosition = body.getPosition();

            // Get the fixtures under the player
            Array<Fixture> fixtures = new Array<>();
            gameScreen.getWorld().QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    fixtures.add(fixture);
                    return true;
                }
            }, playerPosition.x, playerPosition.y - 1, playerPosition.x, playerPosition.y);

            // Check if any of the fixtures are "oneWay" tiles
            for (Fixture fixture : fixtures) {
                if (fixture.getUserData() != null && fixture.getUserData().toString().contains("oneWay")) {
                    // Disable the tile's hitbox
                    fixture.setSensor(true);

                    // Use a Timer to re-enable the hitbox after a delay
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            fixture.setSensor(false);
                        }
                    }, 0.5f);  // Delay in seconds

                    // Exit the loop after finding a "oneWay" tile
                    break;
                }
            }
        }
        //if linear velocity is 0 and player is on the ground, reset jump count
        if(body.getLinearVelocity().y == 0){
            jumpCount = 0;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 7 ? body.getLinearVelocity().y : 7);
    }


    private void checkTeleport() {

        Vector2 playerPosition1 = new Vector2(body.getPosition().x, body.getPosition().y);
        MapObjects objects1 = tiledMap.getLayers().get("Objects").getObjects(); // Access objects from the "Objects" layer

        // Create a copy of objects for the second loop
        MapObjects objectsCopy1 = new MapObjects();
        for (MapObject object : objects1) {
            objectsCopy1.add(object);
        }

        for (MapObject object : objects1) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String objectName = object.getName();
                if (rect.contains(playerPosition1.x * PPM, body.getPosition().y * PPM)) {
                    // Check if the current object is named "pipe_1C" or "pipe_1D"
                    if (objectName.endsWith("C")) {
                        teleportToDestination(objectsCopy1, objectName.replace("C", "D"));
                    } else if (objectName.endsWith("D")) {
                        teleportToDestination(objectsCopy1, objectName.replace("D", "C"));
                    } else if (objectName.equals("door") && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        // load the new map when the player press E on the object named "porta"
                        TiledMap newMap = new TmxMapLoader().load("maps/map.tmx");
                        this.setPosition(new Vector2(0, 0)); // Imposta la posizione del giocatore
                        tileMapHelper.resetMap(newMap);
                        // Imposta la nuova mappa nella classe GameScreen
                        gameScreen.setTiledMap(newMap);
                        tileMapHelper.setupMap("maps/map.tmx");
                        // Spawn the player in front of the object "spawn"
                        teleportToDestination(newMap.getLayers().get("Objects").getObjects(), "spawn");

                    } else if (objectName.equals("zoom")) {
                        Timer.schedule(new Timer.Task() {
                            int counter = 0; // Contatore per tracciare il numero di secondi trascorsi

                            @Override
                            public void run() {
                                if (counter < 300) { // Se sono trascorsi meno di 10 secondi
                                    gameScreen.setCameraWidth(gameScreen.cameraWidth - 1);
                                    gameScreen.setCameraHeight(gameScreen.cameraHeight - 1);
                                    counter++; // Incrementa il contatore
                                } else {
                                    this.cancel(); // Annulla il task se sono trascorsi 10 secondi
                                }
                            }
                        }, 0, 0.001f);
                    } else if (objectName.equals("dezoom")) {
                        Timer.schedule(new Timer.Task() {
                            int counter = 0; // Contatore per tracciare il numero di secondi trascorsi

                            @Override
                            public void run() {
                                if (counter < 400) { // Se sono trascorsi meno di 10 secondi
                                    gameScreen.setCameraWidth(gameScreen.cameraWidth + 1);
                                    gameScreen.setCameraHeight(gameScreen.cameraHeight + 1);
                                    counter++; // Incrementa il contatore
                                } else {
                                    this.cancel(); // Annulla il task se sono trascorsi 10 secondi
                                }
                            }
                        }, 0, 0.001f);
                    } else if (object.getName().equals("darkness")) {
                        pixmap.setColor(new Color(0, 0, 0, 100f)); // Aumenta l'opacità
                        pixmap.fill();
                        darkness.dispose();
                        darkness = new Texture(pixmap); // Create a new texture from the pixmap

                        darkness.draw(pixmap,0, 0);


                    } else if (object.getName().equals("lightness")) {
                        pixmap.setColor(new Color(0, 0, 0, 0.5f)); // Riduce l'opacità
                        pixmap.fill();
                        darkness.draw(pixmap, 0, 0);
                    }



                }
            }
        }




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

    public void fixGround() {
        groundContacts = 1;
    }

    private void setPosition(Vector2 vector2) {
        body.setTransform(vector2, body.getAngle());
    }

    private void teleportToDestination(MapObjects objects, String destinationName) {


        System.out.println("Body: " + body.getUserData());


        for (MapObject destinationObject : objects) {
            if (destinationName.equals(destinationObject.getName())) {
                if (destinationObject instanceof RectangleMapObject) {
                    Rectangle destinationRect = ((RectangleMapObject) destinationObject).getRectangle();
                    Vector2 destination = new Vector2(destinationRect.x + destinationRect.width / 2, destinationRect.y + destinationRect.height / 2);
                    System.out.println("Body: " + body);
                    body.setTransform(destination.scl(1 / PPM), body.getAngle()); // Ensure to keep the player's current angle
                    return; // Exit after teleporting
                }
            }
        }
    }

    public void dispose() {
        texture.dispose();
        darkness.dispose();
        pixmap.dispose();

    }


    public Vector2 getPlayerPosition() {
        return new Vector2(body.getPosition().x, body.getPosition().y);
    }

    public int getPlayerWidth() {
        return (int) width;
    }

    public int getPlayerHeight() {
        return (int) height;
    }
}
