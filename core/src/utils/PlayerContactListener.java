package utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import objects.player.Player;

public class PlayerContactListener implements ContactListener {

    private Player player;

    public PlayerContactListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        System.out.println("Contact detected");
        System.out.println("Fixture A: " + fixtureA.getUserData());
        System.out.println("Fixture B: " + fixtureB.getUserData());

        // Check for player hitting or leaving the ground
        if (fixtureA.getBody() == player.getBody() || fixtureB.getBody() == player.getBody()) {
            player.hitGround();
        }


    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Check for player leaving the ground
        if (fixtureA.getBody() == player.getBody() || fixtureB.getBody() == player.getBody()) {
            player.leaveGround();
        }

        // Check for player ending contact with a sliding platform
        if ((fixtureA.getBody() == player.getBody() && "sliding".equals(fixtureB.getUserData())) ||
                (fixtureB.getBody() == player.getBody() && "sliding".equals(fixtureA.getUserData()))) {
            // Stop applying the sliding force to the player's body
            player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();



        // Determine which fixture is the player and which is the platform
        Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;
        Fixture platformFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("oneWay") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("oneWay") ? fixtureB : null;



            if (player.getBody().getLinearVelocity().y > 0 && fixtureB.getUserData().equals("oneWay")) {
                // Player is moving upwards; disable collision with the one-way platform
                contact.setEnabled(false);
            }

        if ("slide".equals(fixtureA.getUserData()) || "slide".equals(fixtureB.getUserData())) {
            System.out.println("Player is on a sliding platform");

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