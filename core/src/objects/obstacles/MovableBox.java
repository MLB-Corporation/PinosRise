package objects.obstacles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import utils.BodyHelperService;

import static utils.Constants.PPM;

public class MovableBox {
    private Body body;
    private Texture texture;
    private Rectangle rect;

    public MovableBox(World world, RectangleMapObject mapObject, String type) {
        this.texture = new Texture(Gdx.files.internal("structures/box_" + type+".png"));
        this.rect = mapObject.getRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((rect.x+rect.width/2)/PPM , (rect.y + rect.height /2 ) /PPM);
        bodyDef.fixedRotation = true; // Prevent the body from rotating

        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width/2/PPM, rect.height /2 /PPM);
        Fixture fixture = body.createFixture(shape, 1000f);
        fixture.setUserData("normal");
        fixture.setFriction(100.0f);
        shape.dispose();



    }

    public void render(SpriteBatch batch) {
        // Converti le dimensioni da metri a pixel
        float widthInPixels = rect.width;
        float heightInPixels = rect.height;
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(body.getPosition().x*PPM-rect.width/2, body.getPosition().y*PPM-heightInPixels/2);
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
