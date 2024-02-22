package utils;

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

        if (fixtureA.getBody() == player.getBody() || fixtureB.getBody() == player.getBody()) {
            player.hitGround();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getBody() == player.getBody() || fixtureB.getBody() == player.getBody()) {
            player.leaveGround();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}