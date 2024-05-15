package utils;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mbl.pinosrise.screens.GameScreen;

import objects.player.Player;

public class PlayerContactListener implements ContactListener {

    private Player player;

    private World world;
    private Body platform;






    public PlayerContactListener(Player player, World world, GameScreen gclass) {
        this.player = player;
        this.world = world;
    }

    // verifica se il player è a contatto con qualcosa di solido
    public boolean checkContact() {
        Array<Contact> contacts;
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


    // controllo se il player è a contatto con una parete verticale
    public boolean isTouchingVerticalWall() {
        Array<Contact> contacts;
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

    // inizio del contatto
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
            String userDataA = fixtureA.getUserData().toString();
            String userDataB = fixtureB.getUserData().toString();

            System.out.println("Fixture A: " + userDataA + "\nFixture B: " + userDataB + "\n\n");

            boolean isPlayerA = userDataA.equals("player");
            boolean isPlayerB = userDataB.equals("player");


            if (isPlayerA || isPlayerB) {
                // controllo se il player è a contatto con una parete verticale
                // se è vero il player non può saltare
                if (userDataA.equals("verticalWall") || userDataB.equals("verticalWall")) {
                    player.leaveGround();
                }
                // controllo se il player è a contatto con un pavimento
                if (player.isOnGround() && ((isPlayerA && (userDataB.equals("normal") || userDataB.toLowerCase().contains("oneway"))) || (isPlayerB && (userDataA.equals("normal") || userDataA.toLowerCase().contains("oneway"))))) {
                    player.hitGround();
                }

                System.out.println(Math.floor((isPlayerA ? fixtureA : fixtureB).getBody().getPosition().y));


                if (!userDataA.equalsIgnoreCase("verticalWall") && !userDataB.equalsIgnoreCase("verticalWall")) {
                    player.hitGround();
                }
                // controllo se il player è a contatto con una piattaforma mobile
                if (userDataA.contains("moving") || userDataB.contains("moving")) {
                    platform = userDataA.contains("moving") ? fixtureA.getBody() : fixtureB.getBody();
                }
            }
        }
    }

    public Body getPlatform() {
        return platform;
    }

    // fine del contatto
    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
            // controllo di fine contatto con il pavimento (salto)
            if(player.isOnGround()){
                if(((fixtureA.getUserData().toString().equals("player") && fixtureB.getUserData().toString().equals("normal") || fixtureB.getUserData().toString().toLowerCase().contains("oneway") )) || (fixtureB.getUserData().toString().equals("player") && fixtureA.getUserData().toString().equals("normal") || fixtureA.getUserData().toString().toLowerCase().contains("oneway") )){
                    player.leaveGround();
                }
            boolean isNotVert = !fixtureB.getUserData().toString().equals("verticalWall") && !fixtureA.getUserData().toString().equalsIgnoreCase("verticalWall");


            if ((isNotVert) && (fixtureA.getUserData().toString().equals("player") || fixtureB.getUserData().toString().equals("player"))) {
                player.leaveGround();
            }

            if (platform != null && ("moving".equals(fixtureA.getUserData()) || "moving".equals(fixtureB.getUserData()))) {
                platform = null;
            }
        }
    }
}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // distinguo tra player e secondo oggetto
        Fixture playerFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureA : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureB : null;
        Fixture secondFixture = fixtureA.getUserData() != null && fixtureA.getUserData().equals("player") ? fixtureB : fixtureB.getUserData() != null && fixtureB.getUserData().equals("player") ? fixtureA : null;

        // controllo se il player è a contatto con un pavimento, se è vero il player può saltare
        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null && playerFixture != null && secondFixture != null) {
                if(playerFixture.getBody().getPosition().y > secondFixture.getBody().getPosition().y) {
                    player.hitGround();
                }
            Body playerBody = fixtureA.getUserData().toString().equals("player") ? fixtureA.getBody() : fixtureB.getBody();

            Fixture platformFixture = fixtureA.getUserData().toString().equals("player") ? fixtureB : fixtureA;
            if (playerBody.getLinearVelocity().y > 0 && (fixtureA.getUserData().toString().contains("oneWay") || fixtureB.getUserData().toString().contains("oneWay"))) {
                // Se il player sta saltando e sta toccando una piattaforma oneWay, la piattaforma diventa un sensore per 0.3 secondi, in modo che il player possa passare attraverso
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
    }


}