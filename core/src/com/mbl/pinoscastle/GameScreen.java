package com.mbl.pinoscastle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import objects.player.Player;
import utils.TileMapHelper;

import static utils.Constants.PPM;

public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;

    private Box2DDebugRenderer debugRenderer;

    private OrthogonalTiledMapRenderer renderer;
    private TileMapHelper tileMapHelper;

    //game objects
    private Player player;

    public GameScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -25f), false);
        this.debugRenderer = new Box2DDebugRenderer();
        this.tileMapHelper = new TileMapHelper(this);
        this.renderer = tileMapHelper.setupMap();

    }


    private void update() {
        world.step(1/60f, 6, 2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        player.update();
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10)/10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10)/10f;
        camera.position.set(position);
        camera.update();
    }


    @Override
    public void render(float delta) {
        this.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        renderer.render();
        batch.begin();
        //render di tutti gli oggetti


        batch.end();
        debugRenderer.render(world, camera.combined.scl(PPM));
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}