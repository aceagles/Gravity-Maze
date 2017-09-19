package com.mygdx.gravitymaze;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;



/**
 * Created by Adam on 26/08/2014.
 */
public class LevelLoader {
    World world;
    Array<BodyContainer> bodyList;
    float Unit = 1;
    public LevelLoader(World w, Array<BodyContainer> C){
        world = w;
        bodyList = C;

    }

    public void populateLevel(Map level){
        Map Level = level;
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
        Fixture fixture = cont.body.createFixture(triFix);
        fixture.setUserData("Floor");
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
        Fixture fixture = cont.body.createFixture(triFix);
        fixture.setUserData("Floor");
        tri.dispose();
        bodyList.add(cont);

    }

}
