package com.mygdx.gravitymaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import com.badlogic.gdx.utils.Array;

import java.awt.Point;
import java.util.Iterator;

/**
 * Created by Adam on 22/08/2014.
 */
public class Game implements Screen {
    GameScreen game;
    OrthographicCamera camera;
    World world;
    Box2DDebugRenderer debugRenderer;
    Array<BodyContainer> bodyList;
    Body player;
    int Unit = 50;
    Texture blueBox, redBox, blueTri, redTri, redTri2, redTri3, redTri4, blueTri2, blueTri3, blueTri4,
            playerTexture, gravTexture;
    Vector2 gravCentre;
    //ShapeRenderer shapeRenderer;

    public Game(GameScreen game) {
        this.game = game;

        world = new World(new Vector2(0f, -100f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
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
        playerDef.position.set((2*Unit), (2*Unit));
        bodyList = new Array<BodyContainer>();
        player = world.createBody(playerDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(Unit/2 - 5);

        FixtureDef playerFixture = new FixtureDef();
        playerFixture.shape = circleShape;
        playerFixture.density = 0.1f;
        playerFixture.friction = 0.05f;
        playerFixture.restitution = 0.2f;

        player.createFixture(playerFixture);


        BodyDef staticDef = new BodyDef();
        staticDef.position.set(0,0);

        PolygonShape box = new PolygonShape();
        box.setAsBox(1, 16*Unit);

        FixtureDef boxDef = new FixtureDef();
        boxDef.shape = box;

        Body Wall1 = world.createBody(staticDef);
        Wall1.createFixture(boxDef);

        staticDef.position.set(800, 0);
        Body Wall2 = world.createBody(staticDef);
        Wall2.createFixture(boxDef);

        box.dispose();

        BodyDef staticDef2 = new BodyDef();
        staticDef2.position.set(0,16*Unit);

        PolygonShape box2 = new PolygonShape();
        box.setAsBox(16*Unit, 1);

        FixtureDef boxDef2 = new FixtureDef();
        boxDef2.shape = box2;

        Body Wall12 = world.createBody(staticDef2);
        Wall12.createFixture(boxDef);

        staticDef2.position.set(0, 0);
        Body Wall22 = world.createBody(staticDef2);
        Wall22.createFixture(boxDef);

        box.dispose();


        circleShape.dispose();

        gravCentre = new Vector2(400, 0);

        populateLevel();


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        float x = Gdx.input.getAccelerometerY();
        //float y = Gdx.input.getAccelerometerX();
        Vector2 pos = player.getPosition();
        float diffX = gravCentre.x - pos.x;
        float modX = diffX/ Math.abs(diffX);
        Vector2 grav = world.getGravity();
        float Theta = (float) Math.atan((grav.y)/(grav.x));
        float gravMod = (float) Math.sqrt(grav.x*grav.x + grav.y * grav.y);
        if(x>= 0.1)
            player.applyLinearImpulse(new Vector2(200 *(float)Math.cos(Theta + 90), 200*(float)Math.sin(Theta + 90)), pos, true);
        if(x <= -0.1)
            player.applyLinearImpulse(new Vector2(200 *(float)Math.cos(Theta - 90), 200*(float)Math.sin(Theta - 90)), pos, true);


        //world.setGravity(new Vector2(x * 100, -y*100));

        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            gravCentre.x = touchPos.x;
            gravCentre.y = touchPos.y;

            float diffY = gravCentre.y - pos.y;
            float mod = (float) Math.sqrt(diffX* diffX + diffY*diffY);
            world.setGravity(new Vector2());
            world.setGravity(new Vector2(100 * diffX / mod,
                    100 * diffY / mod));

        }



       /* float diffY = gravCentre.y - pos.y;
        float mod = (float) Math.sqrt(diffX* diffX + diffY*diffY);
        world.setGravity(new Vector2(100 * diffX / mod,
                100 * diffY / mod));
                */
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();


        Iterator<BodyContainer> iter = bodyList.iterator();
        while(iter.hasNext()){
            BodyContainer bod = iter.next();
            switch (bod.type){
                case 1:
                    if(bod.colour == 1)
                        game.batch.draw(blueBox, bod.body.getPosition().x - Unit/2, bod.body.getPosition().y - Unit/2, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redBox, bod.body.getPosition().x - Unit/2, bod.body.getPosition().y - Unit/2, Unit, Unit);
                    break;
                case 2:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    break;
                case 3:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri2, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri2, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    break;
                case 4:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri3, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri3, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    break;
                case 5:
                    if(bod.colour == 1)
                        game.batch.draw(blueTri4, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    if(bod.colour == 2)
                        game.batch.draw(redTri4, bod.body.getPosition().x, bod.body.getPosition().y, Unit, Unit);
                    break;
            }
        }
        game.batch.draw(playerTexture, player.getPosition().x - Unit/2, player.getPosition().y - Unit/2);

        game.batch.draw(gravTexture, pos.x + 200 * grav.x/gravMod - Unit/2, pos.y + 200*grav.y / gravMod - Unit/2);
        game.batch.end();


        debugRenderer.render(world, camera.combined);

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

    public void populateLevel(){
        Map Level = new Level1();
        for(int i = 0; i<Level.BBox.length; i+=2){
            createBox(Level.BBox[i], Level.BBox[i+1], 1);
        }
        for(int i = 0; i<Level.BTri1.length; i+=2){
            createTriangle(Level.BTri1[i], Level.BTri1[i+1], 1, 1, 2);
        }
        for(int i = 0; i<Level.BTri2.length; i+=2){
            createTriangle(Level.BTri2[i], Level.BTri2[i+1], 2, 1, 3);
        }
        for(int i = 0; i<Level.BTri3.length; i+=2){
            createTriangle(Level.BTri3[i], Level.BTri3[i+1], 3, 1, 4);

        }
        for(int i = 0; i<Level.BTri4.length; i+=2){
            createTriangle(Level.BTri4[i], Level.BTri4[i+1], 4, 1, 5);
        }

        for(int i = 0; i<Level.RBox.length; i+=2){
            createBox(Level.RBox[i], Level.RBox[i+1], 2);
        }
        for(int i = 0; i<Level.RTri1.length; i+=2){
            createTriangle(Level.RTri1[i], Level.RTri1[i+1], 1, 2, 2);
        }
        for(int i = 0; i<Level.RTri2.length; i+=2){
            createTriangle(Level.RTri2[i], Level.RTri2[i+1], 2, 2, 3);
        }
        for(int i = 0; i<Level.RTri3.length; i+=2){
            createTriangle(Level.RTri3[i], Level.RTri3[i+1], 3, 2, 4);

        }
        for(int i = 0; i<Level.RTri4.length; i+=2){
            createTriangle(Level.RTri4[i], Level.RTri4[i+1], 4, 2, 5);
        }

    }

    public void createTriangle(float x, float y, int orientation, int C, int t){
        BodyContainer cont = new BodyContainer(C, t);
        BodyDef triDef = new BodyDef();
        triDef.type = BodyDef.BodyType.StaticBody;
        triDef.position.set(x*Unit, y*Unit);
        PolygonShape tri = new PolygonShape();
        switch (orientation) {
            case 1:
                tri.set(new float[]{0, 0, Unit, 0, Unit, Unit});
                break;
            case 2:
                tri.set(new float[]{0, 0, 0, Unit, Unit, Unit});
                break;
            case 3:
                tri.set(new float[]{0,0, 0, Unit, Unit, 0});
                break;
            case 4:
                tri.set(new float[]{Unit, 0, Unit, Unit, 0, Unit});
                break;
        }
        FixtureDef triFix = new FixtureDef();
        triFix.shape = tri;

        cont.body = world.createBody(triDef);
        cont.body.createFixture(triFix);
        tri.dispose();
        bodyList.add(cont);

    }
    public void createBox(float x, float y, int C){
        BodyContainer cont = new BodyContainer(C, 1);
        BodyDef triDef = new BodyDef();
        triDef.type = BodyDef.BodyType.StaticBody;
        triDef.position.set(x*Unit + Unit/2, y*Unit + Unit/2);
        PolygonShape tri = new PolygonShape();
        tri.setAsBox(Unit/2, Unit/2);
        FixtureDef triFix = new FixtureDef();
        triFix.shape = tri;

        cont.body = world.createBody(triDef);
        cont.body.createFixture(triFix);
        tri.dispose();
        bodyList.add(cont);

    }
    public float Func1(float x){
        return (480/800)* player.getPosition().x;
    }
    public float Func2(float x){
        return 480 - (480/800)* player.getPosition().x;
    }

    public class BodyContainer{
        public Body body;
        public int colour, type;
        //1=blue, 2=red;

        /*
        type:
        1, box
        2, tri1
        3, tri2
        4, tri3
        5, tri4
         */
        public BodyContainer(int c, int t){
            colour = c;
            type = t;
        }


    };
}
