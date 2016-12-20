package com.tjstudios.mynesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import static java.lang.Character.toUpperCase;

public class Mynesweeper extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Texture img;
    private Sprite sprite, spriteBorder;
    private BitmapFont font;
    FreeTypeFontGenerator fontGen;
    FreeTypeFontGenerator.FreeTypeFontParameter param;

    private static int WIN_HEIGHT, WIN_WIDTH;

    // Blue Background
    private static final float BKGD_RED = .22f;
    private static final float BKGD_GREEN = .624f;
    private static final float BKGD_BLUE = .761f;

    private float posX, posY;
    private boolean isKeyPressed = false;
    private boolean isLeftTouchPressed = false;
    private boolean isRightTouchPressed = false;

    private String inputKey = "";
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("minesweep.png");
        spriteBorder = new Sprite(img);
        spriteBorder.setScale(1.02f, 1.02f);
        spriteBorder.setColor(Color.BLACK);
        sprite = new Sprite(img);

        fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ubuntu_bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        param.borderWidth = 4;
        param.borderColor = Color.BLACK;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        font = fontGen.generateFont(param);

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
        spriteBorder.setPosition(posX, posY);

		batch.begin();
        spriteBorder.draw(batch);
		sprite.draw(batch);
        font.draw(batch, inputKey, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() - 20);
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

		if(keycode == Input.Keys.LEFT)
			posX-=moveAmount;
		if(keycode == Input.Keys.RIGHT)
			posX+=moveAmount;

        isKeyPressed = true;

		return true;
	}

    @Override
    public boolean keyUp(int keycode) {
        isKeyPressed = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        inputKey = toUpperCase(character) + "";

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT){
            posX = screenX - sprite.getWidth()/2;
            posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight()/2;
            isLeftTouchPressed = true;
        }
        if(button == Input.Buttons.RIGHT){
            posX = Gdx.graphics.getWidth()/2 - sprite.getWidth()/2;
            posY = Gdx.graphics.getHeight()/2 - sprite.getHeight()/2;
            isRightTouchPressed = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isLeftTouchPressed = false;
        isRightTouchPressed = false;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(isLeftTouchPressed) {
            posX = screenX - sprite.getWidth() / 2;
            posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight() / 2;
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float scrollScale = 5f * amount;

        if(isRightTouchPressed) { scrollScale = 50f * amount; }

        posX = posX - scrollScale/2;
        posY = posY - scrollScale/2;
        spriteBorder.setSize(spriteBorder.getWidth() + scrollScale, spriteBorder.getHeight() + scrollScale);          // Should scale image as scrolling occurs
        sprite.setSize(sprite.getWidth() + scrollScale, sprite.getHeight() + scrollScale);                            // Should scale image as scrolling occurs

        return true;
    }
}
