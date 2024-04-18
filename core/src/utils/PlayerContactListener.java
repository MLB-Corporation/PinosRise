package utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mbl.pinoscastle.screens.GameScreen;

import objects.player.Player;

import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

public class PlayerContactListener implements ContactListener {

    private Player player;

    private World world; // Il mondo Box2D
    private WeldJoint joint;
    private Body platform;

    private float originalFriction;

    public boolean onGround = true;



    public boolean getOnGround() {
        return onGround;
    }



    GameScreen gscreen;

    public PlayerContactListener(Player player, World world, GameScreen gclass) {
        this.player = player;
        this.world = world;
        this.gscreen = gclass;

    }

    public boolean checkContact() {
        Array<Contact> contacts = new Array<>();
        contacts = world.getContactList();

        for (Contact contact : contacts) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
                if ((fixtureA.getUserData().equals("player") &&
                        (fixtureB.getUserData().equals("box") || fixtureB.getUserData().equals("normal") || fixtureB.getUserData().equals("oneWay"))) ||
                        (fixtureB.getUserData().equals("player") &&
                                (fixtureA.getUserData().equals("box") || fixtureA.getUserData().equals("normal") || fixtureA.getUserData().equals("oneWay")))) {
                    System.out.println("contact");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPlayerAboveGround() {
        Array<Contact> contacts = new Array<>();
        contacts = world.getContactList();

        for (Contact contact : contacts) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;
            Fixture secondFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureB : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureA : null;

            if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
                if ((fixtureA.getUserData().equals("player") &&
                        (fixtureB.getUserData().equals("box") || fixtureB.getUserData().equals("normal") || fixtureB.getUserData().equals("oneWay"))) ||
                        (fixtureB.getUserData().equals("player") &&
                        (fixtureB.getUserData().equals("player") &&
                                (fixtureA.getUserData().equals("box") || fixtureA.getUserData().equals("normal") || fixtureA.getUserData().equals("oneWay"))))) {

                        if (playerFixture.getBody().getPosition().y > secondFixture.getBody().getPosition().y) {
                            return true;
                        } else {
                            return false;
                        }

                }
            }
        }
        return false;
    }

    public boolean isTouchingVerticalWall() {
        //if the player is touching a vertical wall, return true
        Array<Contact> contacts = new Array<>();
        contacts = world.getContactList();

        for (Contact contact : contacts) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
                if ((fixtureA.getUserData().equals("player") && fixtureB.getUserData().equals("verticalWall")) ||
                        (fixtureB.getUserData().equals("player") && fixtureA.getUserData().equals("verticalWall"))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {

            System.out.println("Fixture A: " + fixtureA.getUserData() + "\nFixture B: " + fixtureB.getUserData() + "\n\n");
            boolean isBox = fixtureB.getUserData().toString().equals("box") || fixtureA.getUserData().toString().equalsIgnoreCase("box");

            if(fixtureA.getUserData().equals("verticalWall") || fixtureB.getUserData().equals("verticalWall")) {
                if(fixtureA.getUserData().equals("player") || fixtureB.getUserData().equals("player")) {
                    player.leaveGround();
                }
            }

            if(player.isOnGround()){
                System.out.println("onGround");
                if(((fixtureA.getUserData().toString().equals("player") && (fixtureB.getUserData().toString().equals("normal") || fixtureB.getUserData().toString().toLowerCase().contains("oneway") ))) || (fixtureB.getUserData().toString().equals("player") && (fixtureA.getUserData().toString().equals("normal") || fixtureA.getUserData().toString().toLowerCase().contains("oneway")) )){
                    player.hitGround();
                }
            }

            if (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player")) {
                if(fixtureA.getUserData().toString().equals("player")) {
                    System.out.println(Math.floor(fixtureA.getBody().getPosition().y));
                } else {
                    System.out.println(Math.floor(fixtureB.getBody().getPosition().y));

                }
            }

            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");
            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                player.hitGround();

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

            if(player.isOnGround()){
                if(((fixtureA.getUserData().toString().equals("player") && fixtureB.getUserData().toString().equals("normal") || fixtureB.getUserData().toString().toLowerCase().contains("oneway") )) || (fixtureB.getUserData().toString().equals("player") && fixtureA.getUserData().toString().equals("normal") || fixtureA.getUserData().toString().toLowerCase().contains("oneway") )){
                    player.leaveGround();
                }
            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");
            boolean isNotBox = !fixtureB.getUserData().toString().equals("box") && !fixtureA.getUserData().toString().equalsIgnoreCase("box");
        // Check for player leaving the ground
            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                player.leaveGround();
            }

            // Check for player ending contact with a moving platform
            if (platform != null && ("moving".equals(fixtureA.getUserData()) || "moving".equals(fixtureB.getUserData()))) {
                // Set the platform reference to null
                platform = null;
            }
        }

    }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();



        // Determine which fixture is the player and which is the platform
        Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;
        Fixture secondFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureB : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureA : null;

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null && playerFixture != null && secondFixture != null) {



                if(playerFixture.getBody().getPosition().y > secondFixture.getBody().getPosition().y) {
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