package utils;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.mbl.pinoscastle.screens.GameScreen;
import objects.player.Player;

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
        System.out.println(fixtureA.getBody().getPosition().toString()+ fixtureB);

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
            System.out.println(fixtureA.getUserData().toString() + " " + fixtureB.getUserData().toString());
            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");
            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                System.out.println("OAOOOOAOOA");
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
            System.out.println(fixtureA.getUserData().toString() + " " + fixtureB.getUserData().toString());
            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");

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


        boolean isPlayerA = true;
        boolean isMovingPlatformB= false;

        if(fixtureA.getUserData() != null && fixtureB.getUserData()!= null) {
            isPlayerA = "player".equals(fixtureA.getUserData());
            isMovingPlatformB = fixtureB.getUserData().toString().contains("moving");
        }

        Body playerBody = isPlayerA ? fixtureA.getBody() : fixtureB.getBody();
        Body platformBody = isMovingPlatformB ? fixtureB.getBody() : fixtureA.getBody();



        // Determine which fixture is the player and which is the platform
        Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;
        Fixture platformFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("oneWay") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("oneWay") ? fixtureB : null;


        if (fixtureB.getUserData() != null) {
            if (player.getBody().getLinearVelocity().y > 0 && fixtureB.getUserData().toString().contains("oneWay")) {
                // Player is moving upwards; disable collision with the one-way platform
                contact.setEnabled(false);
            }
        }

        if ("slide".equals(fixtureA.getUserData()) || "slide".equals(fixtureB.getUserData())) {

            // Set the player's linear velocity to a constant value in the right direction
            player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x + 1, player.getBody().getLinearVelocity().y);

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