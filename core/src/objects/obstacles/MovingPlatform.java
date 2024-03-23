package objects.obstacles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import static utils.Constants.PPM;

public class MovingPlatform {

    private Body body;
    private Texture texture;
    private float velocity = 2f; // Velocità della piattaforma
    private float moveTimer = 0;
    private float directionChangeInterval = 2; // Intervallo di cambio direzione
    private Rectangle rect;

    public MovingPlatform(World world, RectangleMapObject mapObject) {
        // Creazione della texture rossa
        this.texture = new Texture(Gdx.files.internal("structures/basicPlat.png"));
        this.rect = mapObject.getRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody; // Piattaforme mobili devono essere Kinematic
        bodyDef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height / 2) / PPM);
        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM);
        body.createFixture(shape, 0.0f).setUserData("moving");
        shape.dispose();

        this.body.setLinearVelocity(velocity, 0); // Inizia a muoversi orizzontalmente
    }

    public void update(float deltaTime) {
        moveTimer += deltaTime;
        if (moveTimer >= directionChangeInterval) {
            velocity = -velocity; // Cambia direzione
            body.setLinearVelocity(velocity, 0);
            moveTimer = 0;
        }
    }



    public void render(SpriteBatch batch) {
        // Converti le dimensioni da metri a pixel
        float widthInPixels = rect.width;
        float heightInPixels = rect.height;
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(body.getPosition().x*PPM-rect.width/2, body.getPosition().y*PPM-heightInPixels/2 + 2);
        sprite.draw(batch);

        // Calcola la posizione in pixel per il rendering, convertendo da metri a pixel
        // e centrando la texture sulla posizione del corpo Box2D
        float x = (body.getPosition().x * PPM) - widthInPixels/2;
        float y = (body.getPosition().y * PPM) - heightInPixels/2;


    }


    public void dispose() {
        texture.dispose();
    }



    public Body getBody() {
        return body;
    }
}
