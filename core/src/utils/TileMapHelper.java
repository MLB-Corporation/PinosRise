package utils;

import com.badlogic.gdx.maps.Map;
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
import com.badlogic.gdx.utils.Array;
import com.mbl.pinosrise.screens.GameScreen;
import objects.obstacles.MovableBox;
import objects.obstacles.MovingPlatform;
import objects.player.Player;

import static utils.Constants.PPM;

public class TileMapHelper {

    private TiledMap map;
    private GameScreen gameScreen;
    Array<Body> bodies = new Array<>();

    public TileMapHelper(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    // setup della mappa
    public OrthogonalTiledMapRenderer setupMap(String mappa) {
        map = new TmxMapLoader().load(mappa);
        parseMapObjects(map.getLayers().get("Objects").getObjects());
        parseTileCollisions();
        System.out.println("rend: " + new OrthogonalTiledMapRenderer(map).toString());
        return new OrthogonalTiledMapRenderer(map);
    }


    // parsing degli oggetti della mappa
    private void parseMapObjects(MapObjects objects){
        for(MapObject mapObject : objects) {
            if(mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }

            if(mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                String rectangleName = mapObject.getName();

                // se l'oggetto ha la proprietà "moving" allora è una piattaforma mobile
                if(mapObject.getProperties().containsKey("moving")) {
                    gameScreen.addMovingPlatform(new MovingPlatform(gameScreen.getWorld(), (RectangleMapObject) mapObject, mapObject.getProperties().get("time").toString()));
                }

                // se l'oggetto ha la proprietà "box" allora è una scatola mobile
                if(mapObject.getName().equals("box")) {
                    gameScreen.addBox(new MovableBox(gameScreen.getWorld(), (RectangleMapObject) mapObject, mapObject.getProperties().get("type").toString()));
                }

                // se l'oggetto ha la proprietà "player" allora è il player
                if(rectangleName.equals("player")) {
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(rectangle.getWidth()/2/PPM, rectangle.getHeight()/2/PPM);
                    Body body = BodyHelperService.createBody(
                            rectangle.getX() + rectangle.getY() /2,
                            rectangle.getY() + rectangle.getHeight() /2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            gameScreen.getWorld()
                    );
                    body.createFixture(shape, 1000).setUserData("player");

                    // rimuove il player precedente e ne crea uno nuovo in caso di cambio mappa / bug (seconda mappa non implementata)
                    gameScreen.removePlayer();
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, map, gameScreen, (RectangleMapObject) mapObject, gameScreen.getWorld()));
                }
            }
        }
    }


    // funzione per generare automaticamente i corpi statici per le collisioni con i tile (hitbox)
    private void parseTileCollisions() {
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                // se il layer ha la proprietà "collides" allora è un layer di collisione
                if (layer.getProperties().containsKey("collides")) {
                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

                    // definizione del tipo di layer
                    boolean isOneWayLayer = layer.getName().equalsIgnoreCase("OneWay");
                    boolean isSlideLayer = layer.getName().equalsIgnoreCase("slide");
                    boolean isVerticalWall = layer.getProperties().containsKey("vertical");

                    // catene di tile orizzontali
                    if(!isVerticalWall) {
                        for (int y = 0; y < tileLayer.getHeight(); y++) {
                            handleTileChains(tileLayer, y, true, isOneWayLayer, isSlideLayer, isVerticalWall);
                        }
                    }

                    // catece di tile verticali
                    if (isVerticalWall) {
                        for (int x = 0; x < tileLayer.getWidth(); x++) {
                            handleTileChains(tileLayer, x, false, isOneWayLayer, isSlideLayer, isVerticalWall);
                        }
                    }
                }
            }
        }
    }

    // funzione per gestire le catene di tile
    private void handleTileChains(TiledMapTileLayer tileLayer, int position, boolean isHorizontal, boolean isOneWay, boolean isSlide, boolean isVerticalWall) {
        int counter = 0;
        int start = -1; // inizzializzazione della posizione di inizio
        // ciclo per scorrere i tile
        for (int i = 0; i <= (isHorizontal ? tileLayer.getWidth() : tileLayer.getHeight()); i++) {

            TiledMapTileLayer.Cell cell = i < (isHorizontal ? tileLayer.getWidth() : tileLayer.getHeight()) ? (isHorizontal ? tileLayer.getCell(i, position) : tileLayer.getCell(position, i)) : null;
            if (cell != null && cell.getTile() != null) {
                if (start == -1) {
                    start = i; // Imposta la posizione di inizio della catena
                }
                counter++;
            } else if (counter > 0) {
                float length = counter * (isHorizontal ? tileLayer.getTileWidth() : tileLayer.getTileHeight());
                float startPosition = start * (isHorizontal ? tileLayer.getTileWidth() : tileLayer.getTileHeight());

                createStaticBodyForTile(startPosition, position, length, (isHorizontal ? tileLayer.getTileHeight() : tileLayer.getTileWidth()), isHorizontal, isOneWay, isSlide, isVerticalWall);
                counter = 0;
                start = -1; // Resetta la posizione di inizio
            }
        }
    }

    // funzione per creare i corpi statici per i tile
    private void createStaticBodyForTile(float startPosition, float orthogonalPosition, float length, float thickness, boolean isHorizontal, boolean isOneWay, boolean isSlide, boolean isVerticalWall) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Calcola la posizione del corpo
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
        // Imposta la forma del corpo in base all'orientamento
        if (isHorizontal) {
            shape.setAsBox(length / 2 / PPM, thickness / 2 / PPM);
        } else {
            shape.setAsBox(thickness / 2 / PPM, length / 2 / PPM);
        }

        // Creazione del corpo e della fixture, necessaria per la collisione e per la gestione delle proprietà
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Fixture fixture = body.createFixture(shape, 0);
        if(isVerticalWall) {
            fixture.setUserData("verticalWall");
        }
        if (isOneWay) {
            fixture.setUserData("oneWay");
        }
        if (isSlide) {
            fixture.setUserData("slide");
        }
        if(!isSlide && !isOneWay && !isVerticalWall){
            fixture.setUserData("normal");
        }

        // Aggiunta del corpo all'array per la rimozione in caso di cambio mappa (seconda mappa non implementata)
        bodies.add(body);

        shape.dispose();
    }


    // funzione per creare il player
    private void createStaticBody(PolygonMapObject polygonMapObject){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000).setUserData("player");
        shape.dispose();
    }

    // funzione per creare la forma del corpo
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

    public Map getMap() {
        return map;
    }
}
