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