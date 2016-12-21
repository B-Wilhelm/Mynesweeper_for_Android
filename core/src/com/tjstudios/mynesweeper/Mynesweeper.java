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
    private static final float BKGD_RED = .22f;                                                         // Blue Background
    private static final float BKGD_GREEN = .624f;
    private static final float BKGD_BLUE = .761f;
	private SpriteBatch batch;                                                                          // Used for spriteLogo initialization
	private Texture img;
    private Sprite spriteLogo;
    private BitmapFont font;
    private ArrayList<buttonCheck> keypressArray = new ArrayList<buttonCheck>();                             // Used for smoothing out directional key movement
    private int WIN_WIDTH = 0, WIN_HEIGHT = 0;
    private float posX, posY;
    private boolean isLeftTouchPressed = false;
    private boolean isRightTouchPressed = false;
    private String inputKey = "";
	
	@Override
	public void create () {
		initSprite();                                                                                   // Create texture, image and then spriteLogo
        initFont();                                                                                     // Creates freetype font and sets its properties
        storeWindowAndPosition();                                                                       // Stores window size and position into their own variables
        Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		setBackground();                                                                                // Sets background color
//        collisionCheck();                                                                               // Ensures spriteLogo doesn't collide with window borders
//        spriteLogo.setPosition(posX, posY);                                                             // Positions spriteLogo
        batchProcess();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	private void batchProcess() {
        batch.begin();
            spriteLogo.draw(batch);                                                                     // Draws spriteLogo (Logo)
            font.draw(batch, inputKey, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() - 20);       // Draws alphanumeric char at top of screen
//            if(keypressArray.size() > 0){
//                moveLogoArrowKeys(keypressArray.get(keypressArray.size()-1).getKeycode());                       // If key is pressed, spriteLogo is moved
//            }
        batch.end();
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
            keypressArray.add(new buttonCheck(keycode, true));
        }

		return true;
	}

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT || keycode == Input.Keys.DOWN || keycode == Input.Keys.UP) {
            keypressArray.remove(keypressArray.indexOf(new buttonCheck(keycode, true)));
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
            posX = screenX - spriteLogo.getWidth()/2;
            posY = Gdx.graphics.getHeight() - screenY - spriteLogo.getHeight()/2;
            isLeftTouchPressed = true;
        }
        if(button == Input.Buttons.RIGHT){
            posX = Gdx.graphics.getWidth()/2 - spriteLogo.getWidth()/2;
            posY = Gdx.graphics.getHeight()/2 - spriteLogo.getHeight()/2;
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
            posX = screenX - spriteLogo.getWidth() / 2;
            posY = Gdx.graphics.getHeight() - screenY - spriteLogo.getHeight() / 2;
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

        spriteLogo.setSize(Math.max(spriteLogo.getWidth() + scrollScale, 1), Math.max(spriteLogo.getHeight() + scrollScale, 1));                            // Should scale image as scrolling occurs

        return true;
    }

    private void moveLogoArrowKeys(int keycode){
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

    private void collisionCheck(){
        posX = Math.min(posX, WIN_WIDTH - spriteLogo.getWidth());                                           // Ensures spriteLogo doesn't leave bounds of window
        posY = Math.min(posY, WIN_HEIGHT - spriteLogo.getHeight());
        posX = Math.max(posX, 0);
        posY = Math.max(posY, 0);
    }

    private void setBackground(){
        Gdx.gl.glClearColor(BKGD_RED, BKGD_GREEN, BKGD_BLUE, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void initSprite(){
        batch = new SpriteBatch();
        img = new Texture("minesweep.png");
        spriteLogo = new Sprite(img);
        spriteLogo.setSize(spriteLogo.getWidth()/2, spriteLogo.getHeight()/2);
        spriteLogo.setAlpha(0f);
    }

    private void initFont(){
        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ubuntu_bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        param.borderWidth = 4;
        param.borderColor = Color.BLACK;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        font = fontGen.generateFont(param);
    }

    private void storeWindowAndPosition(){
        WIN_HEIGHT = Gdx.graphics.getHeight();
        WIN_WIDTH = Gdx.graphics.getWidth();
        posX = WIN_WIDTH/2-spriteLogo.getWidth()/2;
        posY = WIN_HEIGHT/2-spriteLogo.getHeight()/2;
    }

    private class buttonCheck extends Mynesweeper {
        private int keycode;
        private boolean keypressArray;

        private buttonCheck(int key, boolean press) {
            keycode = key;
            keypressArray = press;
        }

        private int getKeycode(){
            return keycode;
        }

        public boolean equals(Object o) {
            if(!(o instanceof buttonCheck)) return false;
            buttonCheck other = (buttonCheck) o;
            return (this.keycode == other.keycode && this.keypressArray == other.keypressArray);
        }
    }
}
