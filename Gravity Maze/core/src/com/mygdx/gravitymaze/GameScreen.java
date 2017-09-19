package com.mygdx.gravitymaze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen extends com.badlogic.gdx.Game {
	public SpriteBatch batch;


	
	@Override
	public void create () {
		batch = new SpriteBatch();

	    setScreen(new GameScreen2(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
