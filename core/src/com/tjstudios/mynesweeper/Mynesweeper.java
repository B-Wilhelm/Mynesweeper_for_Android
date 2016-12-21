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

import java.util.ArrayList;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.toUpperCase;

public class Mynesweeper extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Texture img;
    private Sprite sprite;
    private BitmapFont font;
    private FreeTypeFontGenerator fontGen;
    private ArrayList<buttonCheck> keyPress = new ArrayList<buttonCheck>();

    // Blue Background
    private static final float BKGD_RED = .22f;
    private static final float BKGD_GREEN = .624f;
    private static final float BKGD_BLUE = .761f;

    private float posX, posY;
    private boolean isLeftTouchPressed = false;
    private boolean isRightTouchPressed = false;

    private String inputKey = "";
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("minesweep.png");
        sprite = new Sprite(img);

        fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ubuntu_bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        param.borderWidth = 4;
        param.borderColor = Color.BLACK;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        font = fontGen.generateFont(param);

        final int WIN_HEIGHT = Gdx.graphics.getHeight();
        final int WIN_WIDTH = Gdx.graphics.getWidth();
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
            font.draw(batch, inputKey, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() - 20);
            if(keyPress.size() > 0){
                moveLogo(keyPress.get(keyPress.size()-1).getKeycode());
            }
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
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT || keycode == Input.Keys.DOWN || keycode == Input.Keys.UP){
            keyPress.add(new buttonCheck(keycode, true));
        }

		return true;
	}

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT || keycode == Input.Keys.DOWN || keycode == Input.Keys.UP) {
            keyPress.remove(keyPress.indexOf(new buttonCheck(keycode, true)));
        }

        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        if(isLetterOrDigit(c)){
            inputKey = toUpperCase(c) + "";
        }

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

        sprite.setSize(sprite.getWidth() + scrollScale, sprite.getHeight() + scrollScale);                            // Should scale image as scrolling occurs

        return true;
    }

    private void moveLogo(int keycode){
        float moveAmount = 8.0f;

        if(keycode == Input.Keys.LEFT)
            posX-=moveAmount;
        if(keycode == Input.Keys.RIGHT)
            posX+=moveAmount;
        if(keycode == Input.Keys.DOWN)
            posY-=moveAmount;
        if(keycode == Input.Keys.UP)
            posY+=moveAmount;
    }

    private class buttonCheck extends Mynesweeper {
        private int keycode;
        private boolean keyPress;

        private buttonCheck(int key, boolean press) {
            keycode = key;
            keyPress = press;
        }

        private int getKeycode(){
            return keycode;
        }

        private boolean getKeyPress(){
            return keyPress;
        }

        public boolean equals(Object o) {
            if(!(o instanceof buttonCheck)) return false;
            buttonCheck other = (buttonCheck) o;
            return (this.keycode == other.keycode && this.keyPress == other.keyPress);
        }
    }
}
