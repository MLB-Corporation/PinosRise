package com.mbl.pinoscastle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mbl.pinoscastle.GameClass;
import gameStates.GameState;
import objects.obstacles.MovableBox;
import objects.obstacles.MovingPlatform;
import objects.player.Player;
import utils.PlayerContactListener;
import utils.TileMapHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static utils.Constants.PPM;

public class GameScreen extends ScreenAdapter {

    // camera
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;

    public int cameraWidth = 640;
    public int cameraHeight = 640;
    private FrameBuffer frameBuffer;
    private Box2DDebugRenderer debugRenderer;

    private OrthogonalTiledMapRenderer renderer;
    private TileMapHelper tileMapHelper;

    private Array<MovingPlatform> movingPlatforms = new Array<>();

    private Array<MovableBox> movableBoxes = new Array<>();


    private PlayerContactListener contactListener; // Add this line
    boolean showMessage = false;
    String message;

    //game objects
    private Player player;
    List<Runnable> postStepActions = new ArrayList<>();

    private GameClass parent;
    Skin skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));

    // Crea un nuovo Label
    Label messageLabel = new Label("", skin);

// Posiziona il Label nella parte superiore dello schermo
BitmapFont font = new BitmapFont(); //or use alex answer to use custom font


    public GameScreen(OrthographicCamera camera, GameClass parent, FrameBuffer frameBuffer) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -25f), false);
        this.tileMapHelper = new TileMapHelper(this);
        this.renderer = tileMapHelper.setupMap("maps/map.tmx");
        this.contactListener = new PlayerContactListener(player, world, this);
        this.parent = parent;
        this.frameBuffer = frameBuffer;
        this.debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(contactListener);
        // Crea un nuovo Skin


    }

    public void setContactListener(PlayerContactListener contactListener) {
        this.contactListener = contactListener;
    }

    public void newPostAction(Runnable action) {
        postStepActions.add(action);
    }
    public void addMovingPlatform(MovingPlatform plat) {
        movingPlatforms.add(plat);
    }
    public void addBox(MovableBox box) { movableBoxes.add(box); }


    private void update(float delta) {

        world.step(1/60f, 6, 2);
        postStepActions.forEach(Runnable::run);
        postStepActions.clear();
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        player.update();

        for(MovingPlatform plat : movingPlatforms) {
            plat.update(delta);
            player.update();
        }


        if (contactListener.getPlatform() != null && contactListener.getPlatform().getPosition().y < player.getBody().getPosition().y) {
            Vector2 platformVelocity = contactListener.getPlatform().getLinearVelocity();
            // Add the platform's velocity to the player's current velocity
            Vector2 playerVelocity = player.getBody().getLinearVelocity();
            player.getBody().setLinearVelocity(playerVelocity.x + platformVelocity.x, playerVelocity.y + platformVelocity.y);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            GameClass.INSTANCE.changeScreen(GameClass.PAUSE);
        }
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10)/10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10)/10f;

        // Controllo dei limiti della mappa
        if (position.x < camera.viewportWidth / 2) {
            position.x = camera.viewportWidth / 2;
        } else if (position.x > getMapWidth(this.renderer.getMap()) - camera.viewportWidth / 2) {
            position.x = getMapWidth(this.renderer.getMap()) - camera.viewportWidth / 2;
        }

        if (position.y < camera.viewportHeight / 2) {
            position.y = camera.viewportHeight / 2;
        } else if (position.y > getMapHeight(this.renderer.getMap()) - camera.viewportHeight / 2) {
            position.y = getMapHeight(this.renderer.getMap()) - camera.viewportHeight / 2;
        }

        camera.position.set(position);

        camera.viewportWidth = cameraWidth;
        camera.viewportHeight = cameraHeight * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth());
        camera.update();
    }


    @Override
    public void render(float delta) {
        this.update(delta);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0); // Imposta lo sfondo trasparente
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Renderizza la GameScreen
        // Chiamata al metodo render della GameScreen
        frameBuffer.end();

        renderer.render();
        batch.begin();
        player.render(batch);
        //render di tutti gli oggetti
        for(MovingPlatform plat : movingPlatforms) {
            plat.render(batch);
            player.render(batch);
        }

        for(MovableBox box : movableBoxes) {
            box.render(batch);
            player.render(batch);
        }

        if(showMessage){
            font.draw(batch, message, 300, player.getBody().getPosition().y > 4.5 ? player.getBody().getPosition().y * PPM - 150 : 20);
        }
        batch.end();
        //debugRenderer.render(world, camera.combined.scl(PPM));
    }

    public void setCameraWidth(int cameraWidth) {
        this.cameraWidth = cameraWidth;
    }

    public void setCameraHeight(int cameraHeight) {
        this.cameraHeight = cameraHeight;
    }

    public World getWorld() {
        return world;
    }

    public int getMapWidth(TiledMap map) {
        int tileWidth = ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth();
        return tileWidth * map.getProperties().get("width", Integer.class);
    }

    public int getMapHeight(TiledMap map) {
        int tileHeight = ((TiledMapTileLayer) map.getLayers().get(0)).getTileHeight();
        return tileHeight * map.getProperties().get("height", Integer.class);
    }

    public void setPlayer(Player player){
        this.player = player;
        teleportToSpawn();
    }

    public void setTiledMap(TiledMap newMap) {
        this.renderer.setMap(newMap);
        camera.viewportWidth = getMapWidth(newMap);
        camera.viewportHeight = getMapHeight(newMap);
        camera.update();
    }

    public void teleportToSpawn() {
        this.player.getBody().setTransform(5.3f, 1, 0);
    }

    public void removePlayer() {
        this.player = null;
    }

    public void saveGame() {
        // Creare un oggetto Json per la serializzazione dei dati del gioco
        com.badlogic.gdx.utils.Json json = new com.badlogic.gdx.utils.Json();

        // Creare un oggetto Map per contenere i dati del gioco
        java.util.Map<String, Object> data = new java.util.HashMap<>();

        // Aggiungere i dati del gioco alla mappa
        // Ad esempio, potresti salvare la posizione del giocatore
        data.put("playerX", player.getBody().getPosition().x);
        data.put("playerY", player.getBody().getPosition().y);

        // Convertire la mappa in una stringa JSON
        String jsonData = json.toJson(data);

        // Scrivere la stringa JSON in un file
        com.badlogic.gdx.files.FileHandle file = Gdx.files.local("savegame.json");
        file.writeString(jsonData, false); // false indica che vogliamo sovrascrivere il file, non aggiungere alla fine
    }

    public void loadGame() {
        // Leggere il contenuto del file di salvataggio
        com.badlogic.gdx.files.FileHandle file = Gdx.files.local("savegame.json");
        String jsonData = file.readString();

        // Creare un oggetto Json per la deserializzazione dei dati del gioco
        com.badlogic.gdx.utils.Json json = new com.badlogic.gdx.utils.Json();

        // Convertire la stringa JSON in una mappa
        java.util.Map<String, Object> data = json.fromJson(java.util.HashMap.class, jsonData);

        // Leggere i dati del gioco dalla mappa
        float playerX = (Float) data.get("playerX");
        float playerY = (Float) data.get("playerY");

        // Impostare la posizione del giocatore
        player.getBody().setTransform(playerX, playerY, 0);
    }

    public void resetGame() {
        // Resetta il gioco al punto di spawn
        teleportToSpawn();
        com.badlogic.gdx.files.FileHandle file = Gdx.files.local("savegame.json");
        file.writeString("", false);



    }

    public void showMessage(String message) {
        // mostra un messaggio a schermo
        this.message = message;
        showMessage = true;
        messageLabel.setText(message);

        new Thread(() -> {
            try {
                Thread.sleep(1000); // Aspetta 5 secondi
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showMessage = false;
        }).start();
    }
}
