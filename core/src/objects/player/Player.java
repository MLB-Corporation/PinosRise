package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mbl.pinosrise.screens.GameScreen;
import com.badlogic.gdx.utils.Timer;
import utils.*;

import static com.mbl.pinosrise.screens.PreferencesScreen.PREF_SOUND_VOL;
import static utils.Constants.PPM;
import static utils.Constants.jumpSound;

public class Player extends GameEntity {
    // Sistema di contatto con il terreno
    private static int groundContacts = 1;

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



    // Attributi relativi al gioco
    private GameScreen gameScreen;
    private TiledMap tiledMap;

    // Attributi relativi al player
    private String state;

    // Attributi relativi all'animazione
    private Texture texture;
    private Sprite sprite;
    private AnimationLoader animationLoader;
    private int aniTick, aniIndex, aniSpeed = 5;

    // Attributi relativi al movimento
    private float speed = 2.5f;
    private float jumpCooldown = 0;
    private float jumpTimer = 0; // Add this line at the beginning of your class

    // Attributi relativi alla fisica
    private PlayerContactListener contactListener;

    // Impostazioni
    private Preferences preferencesData = Gdx.app.getPreferences("preferences");



    public Player(float width, float height, Body body, TiledMap tiledMap, GameScreen gameScreen, RectangleMapObject mapObject, World world) {
        super(width, height, body);
        this.speed = 2.5f;
        this.tiledMap = tiledMap;
        this.gameScreen = gameScreen;
        this.texture = new Texture(Gdx.files.internal("player/player.png"));
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width / PPM, height / PPM);
        this.sprite.setOrigin(width / (2 * PPM), height / (2 * PPM));
        this.contactListener =  new PlayerContactListener(this, world, gameScreen);
        this.animationLoader = new AnimationLoader();
        jumpSound.setVolume(preferencesData.getFloat(PREF_SOUND_VOL)/10);
        state = "idle";
    }





    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        checkUserInput();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        checkClimbable();
    }


    // metodo per controllare se il player è in grado di salire su una scala
    private void checkClimbable() {
        Vector2 playerPosition = new Vector2(body.getPosition().x, body.getPosition().y);
        MapObjects objects = tiledMap.getLayers().get("Objects").getObjects();

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
        Sprite sprite = new Sprite(texture);
        Sprite[] animation = animationLoader.getAnimation(state);
        animation[aniIndex].setPosition(body.getPosition().x * PPM - sprite.getWidth() / 2, body.getPosition().y * PPM - sprite.getHeight() / 2);
        animation[aniIndex].draw(batch);

    }


    // controlli di input e relative animazioni
    private void checkUserInput() {
        if (jumpCooldown > 0) {
            jumpCooldown -= Gdx.graphics.getDeltaTime();
        }
        // stato di idle
        if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)){
            state = "idle";
            if(aniIndex >= 10){
                aniIndex = 0;
            }
            aniTick++;
            if (aniTick >= 10) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= 10) {
                    aniIndex = 0;
                }
            }
        }

        // tutto ciò che riguarda il movimento del player verso destra, con animazioni
        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
            //salto in movimento verso destra
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
                if(contactListener.isTouchingVerticalWall() && !contactListener.checkContact() ) {
                    return;
                } else {
                    rightjump();
                }
            }
            //corsa verso destra
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
                // camminata verso destra
                state = "right";
                if (aniIndex >= 2) {
                    aniIndex = 0;
                }
                aniTick++;
                if (aniTick >= 10) {
                    aniTick = 0;
                    aniIndex++;
                    if (aniIndex >= 2) {
                        aniIndex = 0;
                    }
                }
                velX = 1;
            }
        }

        // tutto ciò che riguarda il movimento del player verso sinistra, con animazioni
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
                if(contactListener.isTouchingVerticalWall() && !contactListener.checkContact() ) {
                    return;
                } else {
                    if(isOnGround()) {
                        // salto verso sinistra
                        jumpSound.play();
                        aniIndex = 0;
                        state = "leftJump";
                        aniTick++;
                        if (aniTick >= 10) {
                            aniTick = 0;
                        }

                        groundContacts = 0;
                        float force = body.getMass() * 8;
                        body.setLinearVelocity(body.getLinearVelocity().x, 0);
                        body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);

                        // Reset the jump timer
                        jumpTimer = 0;
                    }
                }
            }
            // corsa verso sinistra
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
            } else {
                // camminata verso sinistra
                state = "left";
                if (aniIndex >= 2) {
                    aniIndex = 0;
                }
                aniTick++;
                if (aniTick >= 10) {
                    aniTick = 0;
                    aniIndex++;
                    if (aniIndex >= 2) {
                        aniIndex = 0;
                    }
                }
                velX = -1;
            }}

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);

        // Update the jump timer
        jumpTimer += Gdx.graphics.getDeltaTime();

        //salto da fermo
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ){
            if(contactListener.isTouchingVerticalWall() && !contactListener.checkContact() ) {
                return;
            } else {
                rightjump();
            }
        }

        // Se si è su una one way platform e si preme S, disabilita il sensore per un breve periodo in modo da poter passare attraverso
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            Vector2 playerPosition = body.getPosition();


            Array<Fixture> fixtures = new Array<>();
            gameScreen.getWorld().QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    fixtures.add(fixture);
                    return true;
                }
            }, playerPosition.x, playerPosition.y - 1, playerPosition.x, playerPosition.y);

            // Controllo se il player è su una piattaforma one way
            for (Fixture fixture : fixtures) {
                if (fixture.getUserData() != null && fixture.getUserData().toString().contains("oneWay")) {
                    // Disabilita il sensore
                    fixture.setSensor(true);

                    // Lo riabilita dopo 0.5 secondi
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            fixture.setSensor(false);
                        }
                    }, 0.5f);

                    break;
                }
            }
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 7 ? body.getLinearVelocity().y : 7);
    }

    // funzione right jump (salto verso destra) - Era ripetuta
    private void rightjump() {
        if (isOnGround()) {

            jumpSound.play();
            aniIndex = 0;
            state = "rightJump";
            aniTick++;
            if (aniTick >= 10) {
                aniTick = 0;
            }

            groundContacts = 0;
            float force = body.getMass()*8;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);

            // Reset the jump timer
            jumpTimer = 0;
        }
    }







}
