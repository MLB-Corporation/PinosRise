package utils;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.utils.Timer;
import com.mbl.pinoscastle.screens.GameScreen;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import objects.player.Player;

import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

public class PlayerContactListener implements ContactListener {

    private Player player;

    private World world; // Il mondo Box2D
    private WeldJoint joint;
    private Body platform;

    private float originalFriction;

    public boolean onGround = false;



    public boolean getOnGround() {
        return onGround;
    }



    GameScreen gscreen;

    public PlayerContactListener(Player player, World world, GameScreen gclass) {
        this.player = player;
        this.world = world;
        this.gscreen = gclass;

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {

            System.out.println("Fixture A: " + fixtureA.getUserData() + "\nFixture B: " + fixtureB.getUserData() + "\n\n");
            boolean isBox = fixtureB.getUserData().toString().equals("box") || fixtureA.getUserData().toString().equalsIgnoreCase("box");

            if(isBox) {

                player.hitGround();
            }

            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");
            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                Player.hitGround();

            }

            // Check for player beginning contact with a moving platform
            if (fixtureA.getUserData().toString().contains("moving") || fixtureB.getUserData().toString().contains("moving")) {
                // Store a reference to the platform's Body
                platform = fixtureA.getUserData().toString().contains("moving") ? fixtureA.getBody() : fixtureB.getBody();
            }


        }


    }

    public Body getPlatform() {
        return platform;
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
            boolean isBox = fixtureB.getUserData().toString().equals("box") || fixtureA.getUserData().toString().equalsIgnoreCase("box");

            if(isBox) {

                player.hitGround();
            }
            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");
            boolean isNotBox = !fixtureB.getUserData().toString().equals("box") && !fixtureA.getUserData().toString().equalsIgnoreCase("box");
        // Check for player leaving the ground
            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                Player.leaveGround();
            }

            // Check for player ending contact with a moving platform
            if (platform != null && ("moving".equals(fixtureA.getUserData()) || "moving".equals(fixtureB.getUserData()))) {
                // Set the platform reference to null
                platform = null;
            }
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();



        // Determine which fixture is the player and which is the platform
        Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;


        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {

            if((fixtureA.getUserData().toString().equals("player") && (fixtureB.getUserData().toString().equals("box") || fixtureB.getUserData().toString().equals("normal") || fixtureB.getUserData().toString().toLowerCase().contains("oneway") )) || (fixtureB.getUserData().toString().equals("player") && (fixtureA.getUserData().toString().equals("box") || fixtureA.getUserData().toString().equals("normal") || fixtureA.getUserData().toString().toLowerCase().contains("oneway") ))) {
                player.hitGround();
            }

            Body playerBody = fixtureA.getUserData().toString().equals("player") ? fixtureA.getBody() : fixtureB.getBody();
            Fixture platformFixture = fixtureA.getUserData().toString().equals("player") ? fixtureB : fixtureA;
            if (playerBody.getLinearVelocity().y > 0 && (fixtureA.getUserData().toString().contains("oneWay") || fixtureB.getUserData().toString().contains("oneWay"))) {
                // Player is moving upwards; disable collision with the one-way platform
                platformFixture.setSensor(true);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        platformFixture.setSensor(false);
                    }
                }, 0.3f);





            }
        }
    }



    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Implement any additional logic that needs to occur after the collision response
    }

    private boolean isOneWayPlatformCollision(Fixture a, Fixture b) {
        boolean aIsOneWay = a.getUserData() != null && a.getUserData().equals("oneWay");
        boolean bIsOneWay = b.getUserData() != null && b.getUserData().equals("oneWay");
        boolean aIsPlayer = a.getUserData() != null && a.getUserData().equals("player");
        boolean bIsPlayer = b.getUserData() != null && b.getUserData().equals("player");

        return (aIsOneWay && bIsPlayer) || (bIsOneWay && aIsPlayer);
    }


}