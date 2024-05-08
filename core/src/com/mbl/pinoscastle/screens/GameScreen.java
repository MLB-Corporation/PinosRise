package com.mbl.pinoscastle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.mbl.pinoscastle.GameClass;
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

    private Box2DDebugRenderer debugRenderer;

    private OrthogonalTiledMapRenderer renderer;
    private TileMapHelper tileMapHelper;

    private Array<MovingPlatform> movingPlatforms = new Array<>();

    private Array<MovableBox> movableBoxes = new Array<>();


    private PlayerContactListener contactListener; // Add this line


    //game objects
    private Player player;
    List<Runnable> postStepActions = new ArrayList<>();

    private GameClass parent;


    public GameScreen(OrthographicCamera camera, GameClass parent) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -25f), false);
        this.tileMapHelper = new TileMapHelper(this);
        this.renderer = tileMapHelper.setupMap("maps/map.tmx");
        this.contactListener = new PlayerContactListener(player, world, this); // Modify this line
        this.parent = parent;

        this.debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(contactListener); // Modify this line

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
            Gdx.app.exit();
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


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
    }

    public void setTiledMap(TiledMap newMap) {
        this.renderer.setMap(newMap);
        camera.viewportWidth = getMapWidth(newMap);
        camera.viewportHeight = getMapHeight(newMap);
        camera.update();
    }

    public void resetRenderer(TiledMap newMap) {
        this.renderer = new OrthogonalTiledMapRenderer(newMap);
    }

    public void removePlayer() {
        this.player = null;
    }
}