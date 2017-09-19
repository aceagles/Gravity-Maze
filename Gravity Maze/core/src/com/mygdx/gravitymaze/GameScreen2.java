package com.mygdx.gravitymaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Created by Adam on 22/08/2014.
 */
public class GameScreen2 implements Screen {
    GameScreen game;
    OrthographicCamera camera;
    World world;
    Box2DDebugRenderer debugRenderer;
    Array<BodyContainer> bodyList;
    Body player;
    int Unit = 50;
    Texture blueBox, redBox, blueTri, redTri, redTri2, redTri3, redTri4, blueTri2, blueTri3, blueTri4,
            playerTexture, gravTexture;

    boolean contacted = false;
    float dir = 1;
    long jumpGap;
    //ShapeRenderer shapeRenderer;

    public GameScreen2(GameScreen game) {
        this.game = game;

        world = new World(new Vector2(0f, -10f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        world.setContactListener(new MyListener());


        //shapeRenderer = new ShapeRenderer();
        blueBox = new Texture(Gdx.files.internal("BlueBox.png"));
        redBox = new Texture(Gdx.files.internal("RedBox.png"));
        blueTri = new Texture(Gdx.files.internal("BlueTri2.png"));
        redTri = new Texture(Gdx.files.internal("RedTri2.png"));
        redTri4 = new Texture(Gdx.files.internal("RedTri3.png"));
        redTri3 = new Texture(Gdx.files.internal("RedTri.png"));
        redTri2 = new Texture(Gdx.files.internal("RedTri4.png"));
        blueTri2 = new Texture(Gdx.files.internal("BlueTri4.png"));
        blueTri3 = new Texture(Gdx.files.internal("BlueTrie.png"));
        blueTri4 = new Texture(Gdx.files.internal("BlueTri3.png"));
        playerTexture = new Texture(Gdx.files.internal("hud_p1Alt.png"));
        gravTexture = new Texture(Gdx.files.internal("earth.png"));
        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(2, 2);
        bodyList = new Array<BodyContainer>();
        player = world.createBody(playerDef);



        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.46f);

        FixtureDef playerFixture = new FixtureDef();
        playerFixture.shape = circleShape;
        playerFixture.density = 0.1f;
        playerFixture.friction = 0.0f;
        playerFixture.restitution = 0.05f;
        player.createFixture(playerFixture);
        player.setFixedRotation(true);

        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(0.1f, 0.55f);
        FixtureDef footDef = new FixtureDef();
        footDef.isSensor = true;
        footDef.shape = footShape;
        Fixture foot = player.createFixture(footDef);
        foot.setUserData("Land");

        footShape.dispose();



        circleShape.dispose();

        LevelLoader loader = new LevelLoader(world, bodyList);


        loader.populateLevel(new TestLevel1());

        Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener()));
        jumpGap = TimeUtils.nanoTime();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(player.getPosition().x*Unit, player.getPosition().y*Unit, 0);
        camera.update();
        float x = Gdx.input.getAccelerometerY();
        Vector2 pos = player.getPosition();

            if (x >= 0.1 && player.getLinearVelocity().x<2)
                player.applyLinearImpulse(new Vector2(0.2f, 0), pos, true);
            if (x <= -0.1 && player.getLinearVelocity().x > -2)
                player.applyLinearImpulse(new Vector2(-0.2f, 0f), pos, true);


        //world.setGravity(new Vector2(x * 100, -y*100));

        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if(touchPos.x < pos.x*Unit - 250 ||
                    touchPos.x > pos.x*Unit + 250)
                jump();

        }



        renderSprites();


         //   debugRenderer.render(world, camera.projection);

        doPhysicsStep(Gdx.graphics.getDeltaTime());

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }


    float accumulator = 0;

    private void renderSprites(){
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();


        Iterator<BodyContainer> iter = bodyList.iterator();
        while(iter.hasNext()){
            BodyContainer bod = iter.next();
            switch (bod.type){
                case 1:
                    if(bod.colour == 1)
                        game.batch.draw(blueBox, bod.body.getPosition().x*Unit - Unit/2, bod.body.getPosition().y*Unit - Unit/2, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redBox, bod.body.getPosition().x*Unit - Unit/2, bod.body.getPosition().y*Unit - Unit/2, Unit, Unit);
                    break;
                case 2:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri, bod.body.getPosition().x*Unit, bod.body.getPosition().y*Unit, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri, bod.body.getPosition().x*Unit, bod.body.getPosition().y*Unit, Unit, Unit);
                    break;
                case 3:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri2, bod.body.getPosition().x*Unit, bod.body.getPosition().y*Unit, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri2, bod.body.getPosition().x*Unit, bod.body.getPosition().y*Unit, Unit, Unit);
                    break;
                case 4:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri3, bod.body.getPosition().x*Unit, bod.body.getPosition().y*Unit, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri3, bod.body.getPosition().x *Unit, bod.body.getPosition().y *Unit, Unit, Unit);
                    break;
                case 5:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri4, bod.body.getPosition().x *Unit, bod.body.getPosition().y *Unit, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri4, bod.body.getPosition().x *Unit, bod.body.getPosition().y *Unit, Unit, Unit);
                    break;
            }
        }
        game.batch.draw(playerTexture, player.getPosition().x *Unit - Unit/2, player.getPosition().y *Unit - Unit/2, Unit, Unit);

        //game.batch.draw(gravTexture, pos.x + 200 * grav.x/gravMod - Unit/2, pos.y + 200*grav.y / gravMod - Unit/2);
        game.batch.end();

    }


    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 7, 3);
            accumulator -= 1/60f;
        }
    }

    public void jump(){

        if(contacted && TimeUtils.nanoTime() - jumpGap >= 500000000){
            player.applyLinearImpulse(new Vector2(0, player.getMass()*10 * dir),player.getPosition(), true);
            jumpGap = TimeUtils.nanoTime();
        }


    }

/*    public class BodyContainer{
        public Body body;
        public int colour, type;
        //1=blue, 2=red;

        *//*
        type:
        1, box
        2, tri1
        3, tri2
        4, tri3
        5, tri4
         *//*
        public BodyContainer(int c, int t){
            colour = c;
            type = t;
        }


    }*/

    public class MyGestureListener implements GestureDetector.GestureListener{
        float X, Y;
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            if (contacted){
                if (velocityY >= 0.5) {
                    dir = 1;
                    //player.applyLinearImpulse(new Vector2(0,-200), player.getPosition(), true);
                    world.setGravity(new Vector2(0f, -10f));
                }
                else if (velocityY <= -0.5) {
                    dir = -1;
                    //player.applyLinearImpulse(new Vector2(0,0), player.getPosition(), true);
                    world.setGravity(new Vector2(0f, 10f));
                }
            }
            return true;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            X = x;
            Y = y;
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    }

    public class MyListener implements ContactListener{
        @Override
        public void beginContact(Contact contact) {
            Object userDataA = contact.getFixtureA().getUserData();
            Object userDataB = contact.getFixtureB().getUserData();
            if(userDataA != null){
            if(userDataA.equals("Land") && userDataB.equals("Floor"))
                contacted = true;
            }
            if(userDataB != null){
                if(userDataA.equals("Land") && userDataB.equals("Floor"))
                contacted = true;
            }
        }

        @Override
        public void endContact(Contact contact) {
            Object userDataA = contact.getFixtureA().getUserData();
            Object userDataB = contact.getFixtureB().getUserData();
            if(userDataA != null){
                if(userDataA.equals("Land") && userDataB.equals("Floor"))
                    contacted = false;
            }
            if(userDataB != null){
                if(userDataB.equals("Land") && userDataA.equals("Floor"))
                    contacted = false;
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
