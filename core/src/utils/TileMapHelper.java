package utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mbl.pinoscastle.GameScreen;
import objects.player.Player;

import static utils.Constants.PPM;

public class TileMapHelper {

    private TiledMap map;
    private GameScreen gameScreen;

    public TileMapHelper(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap() {
        map = new TmxMapLoader().load("maps/mappa.tmx");
        parseMapObjects(map.getLayers().get("Objects").getObjects());
        parseTileCollisions();
        return new OrthogonalTiledMapRenderer(map);
    }

    private void parseMapObjects(MapObjects objects){
        for(MapObject mapObject : objects) {
            if(mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }

            if(mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();
                if(rectangleName.equals("player")) {
                    Body body = BodyHelperService.createBody(
                            rectangle.getX() + rectangle.getY() /2,
                            rectangle.getY() + rectangle.getHeight() /2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, map, gameScreen));
                }
            }
        }
    }





private void parseTileCollisions() {
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                if (layer.getProperties().containsKey("collides")) {
                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                    boolean isOneWayLayer = layer.getName().equalsIgnoreCase("OneWay");
                    boolean isSlideLayer = layer.getName().equalsIgnoreCase("slide");

                    // Handle horizontal chains
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        int counter = 0;
                        int startX = -1; // Initialize start X position outside valid range
                        for (int x = 0; x <= tileLayer.getWidth(); x++) {
                            TiledMapTileLayer.Cell cell = x < tileLayer.getWidth() ? tileLayer.getCell(x, y) : null;
                            if (cell != null && cell.getTile() != null) {
                                if (startX == -1) {
                                    startX = x; // Set start X position at the beginning of a new tile chain
                                }
                                counter++;
                            } else if (counter > 0) {
                                float width = counter * tileLayer.getTileWidth();
                                float startXPosition = startX * tileLayer.getTileWidth();
                                createStaticBodyForTile(startXPosition, y, width, tileLayer.getTileHeight(), true, isOneWayLayer, isSlideLayer);
                                counter = 0;
                                startX = -1; // Reset start X for the next chain
                            }
                        }
                    }

                }
            }
        }
    }





    private void createStaticBodyForTile(float startPosition, float orthogonalPosition, float length, float thickness, boolean isHorizontal, boolean isOneWay, boolean isSlide) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        if(isSlide) {
            System.out.println("slide");
        }

        // Calculate position based on orientation
        float posX, posY;
        if (isHorizontal) {
            posX = (startPosition + length / 2) / PPM;
            posY = (orthogonalPosition * thickness + thickness / 2) / PPM;
        } else {
            posX = (orthogonalPosition * thickness + thickness / 2) / PPM;
            posY = (startPosition + length / 2) / PPM;
        }
        bodyDef.position.set(posX, posY);

        PolygonShape shape = new PolygonShape();
        // Set shape based on orientation
        if (isHorizontal) {
            shape.setAsBox(length / 2 / PPM, thickness / 2 / PPM);
        } else {
            shape.setAsBox(thickness / 2 / PPM, length / 2 / PPM);
        }

        Fixture fixture = gameScreen.getWorld().createBody(bodyDef).createFixture(shape, 0);
        if (isOneWay) {
            fixture.setUserData("oneWay"); // Mark this fixture for special collision handling.
        }
        if (isSlide) {
            fixture.setUserData("slide");
        }
        if(!isSlide && !isOneWay){
            fixture.setUserData("normal");
        }
        shape.dispose();
    }


    private void createStaticBody(PolygonMapObject polygonMapObject){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000).setUserData("player");
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        //array di vertici
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        //ogni vertice ha due coordinate, quindi la lunghezza dell'array è la metà
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length/2; i++) {
            Vector2 current = new Vector2(vertices[i*2] / PPM, vertices[i*2+1] / PPM);
            worldVertices[i] = current;
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(worldVertices);
        return polygonShape;

    }
}
