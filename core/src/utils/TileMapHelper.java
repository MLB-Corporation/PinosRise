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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
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
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
                }
            }
        }
    }

    private void parseTileCollisions() {
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    for (int x = 0; x < tileLayer.getWidth(); x++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("collidable")) {
                            createStaticBodyForTile(x, y, tileLayer.getTileWidth(), tileLayer.getTileHeight());
                        }
                    }
                }
            }
        }
    }

    private void createStaticBodyForTile(float x, float y, float width, float height) {
        // Modifica questa funzione per adattarla al tuo modo di creare corpi fisici.
        // Ad esempio, potresti voler calcolare la posizione del centro del tile e usare quelle coordinate per posizionare il corpo fisico.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x * width + width / 2) / PPM, (y * height + height / 2) / PPM);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        gameScreen.getWorld().createBody(bodyDef).createFixture(shape, 0);
        shape.dispose();
    }

    private void createStaticBody(PolygonMapObject polygonMapObject){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000);
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
