package com.mygdx.gravitymaze;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Adam on 26/08/2014.
 */
public class BodyContainer {

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


    }
