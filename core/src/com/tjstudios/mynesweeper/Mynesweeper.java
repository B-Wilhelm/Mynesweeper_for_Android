package com.tjstudios.mynesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Mynesweeper extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Texture img;
    private Sprite sprite;
    private static int WIN_HEIGHT, WIN_WIDTH;

    // Blue Background
    private static final float BKGD_RED = .22f;
    private static final float BKGD_GREEN = .624f;
    private static final float BKGD_BLUE = .761f;

    private float posX, posY;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("minesweep.png");
        sprite = new Sprite(img);

        WIN_HEIGHT = Gdx.graphics.getHeight();
        WIN_WIDTH = Gdx.graphics.getWidth();

        posX = WIN_WIDTH/2-img.getWidth()/2;
        posY = WIN_HEIGHT/2-img.getHeight()/2;

        Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(BKGD_RED, BKGD_GREEN, BKGD_BLUE, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        sprite.setPosition(posX, posY);

		batch.begin();
		sprite.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

	@Override
	public boolean keyDown(int keycode) {
		float moveAmount = 1.0f;
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
			moveAmount = 10.0f;
		if(keycode == Input.Keys.LEFT)
			posX-=moveAmount;
		if(keycode == Input.Keys.RIGHT)
			posX+=moveAmount;
		return true;
	}

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT){
            posX = screenX - sprite.getWidth()/2;
            posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight()/2;
        }
        if(button == Input.Buttons.RIGHT){
            posX = Gdx.graphics.getWidth()/2 - sprite.getWidth()/2;
            posY = Gdx.graphics.getHeight()/2 - sprite.getHeight()/2;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        posX = screenX - sprite.getWidth()/2;
        posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight()/2;

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
