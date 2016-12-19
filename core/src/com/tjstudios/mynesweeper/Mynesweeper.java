package com.tjstudios.mynesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Mynesweeper extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    private static int WIN_HEIGHT;
    private static int WIN_WIDTH;

    // Blue Background
    private static float BKGD_RED = .22f;
    private static float BKGD_GREEN = .624f;
    private static float BKGD_BLUE = .761f;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

        WIN_HEIGHT = Gdx.graphics.getHeight();
        WIN_WIDTH = Gdx.graphics.getWidth();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(BKGD_RED, BKGD_GREEN, BKGD_BLUE, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.begin();
		batch.draw(img, WIN_WIDTH/2-img.getWidth()/2, WIN_HEIGHT/2-img.getHeight()/2);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
