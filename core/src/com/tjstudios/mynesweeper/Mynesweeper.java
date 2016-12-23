package com.tjstudios.mynesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.toUpperCase;

public class Mynesweeper extends ApplicationAdapter implements InputProcessor {
    private static final float BKGD_RED = 255f/255f;                                                         // Blue Background
    private static final float BKGD_GREEN = 255f/255f;
    private static final float BKGD_BLUE = 255f/255f;
    private float sqSide = 0;
    private ShapeRenderer shape;
    private OrthographicCamera camera;
    private Viewport viewport;
	private SpriteBatch batch;                                                                          // Used for spriteLogo initialization
	private Texture img;
    private Sprite spriteLogo;
    private BitmapFont font;
    private ArrayList<buttonCheck> keypressArray = new ArrayList<buttonCheck>();                                  // Used for smoothing out directional key movement
    private int WIN_WIDTH = 0, WIN_HEIGHT = 0;
    private float posX, posY;
    private boolean isLeftTouchPressed = false;
    private boolean isRightTouchPressed = false;
    private String inputKey = "";
    private int bombCount, secTimer;
    private float timer;
    private boolean timerCheck;
	
	@Override
	public void create () {
		initSprite();                                                                                   // Create texture, image and then spriteLogo
        initFont();                                                                                     // Creates freetype font and sets its properties
        storeWindowAndPosition();                                                                       // Stores window size and position into their own variables

        bombCount = 20;
        secTimer = 0;
        timer = 0f;
        timerCheck = true;
        camera = new OrthographicCamera(WIN_WIDTH, WIN_HEIGHT);
        viewport = new ScreenViewport(camera);
        shape = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        incrementTimer();
        fontCheck();
		setBackground();                                                                                // Sets background color
        shapeProcess();
        batchProcess();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	private void batchProcess() {
        batch.begin();
//            spriteLogo.draw(batch);                                                                     // Draws spriteLogo (Logo)
//            font.draw(batch, inputKey, WIN_WIDTH/2, WIN_HEIGHT - 20);                                   // Draws alphanumeric char at top of screen
            font.draw(batch, bombCount + "", -WIN_WIDTH*6/13, WIN_HEIGHT*9/19);
            font.draw(batch, secTimer + "", WIN_WIDTH*3/10, WIN_HEIGHT*9/19);
        batch.end();
    }

    private void shapeProcess() {
        float btmRectHeight = WIN_HEIGHT/8;

        shape.setColor(Color.DARK_GRAY);    // Top-of-screen rectangle
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(0, btmRectHeight+sqSide*8, WIN_WIDTH, WIN_HEIGHT-btmRectHeight-sqSide*8);
        shape.end();

        shape.setColor(Color.DARK_GRAY);    // Bottom-of-screen rectangle
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(0, 0, WIN_WIDTH, btmRectHeight);
        shape.end();

        shape.setColor(new Color(200f/255f, 200f/255f, 0f, 0f));
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(30, btmRectHeight/10, WIN_WIDTH-60, btmRectHeight*8/10);
        shape.end();

        float sqPos;

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 8; j++){
                shape.setColor(Color.LIGHT_GRAY);
                shape.begin(ShapeRenderer.ShapeType.Filled);
                sqPos = sqSide;
                shape.rect(j*(sqPos), i*(sqPos)+btmRectHeight, sqSide, sqSide);
                shape.end();

                shape.setColor(Color.GRAY);
                shape.begin(ShapeRenderer.ShapeType.Filled);
                sqPos = (WIN_WIDTH-WIN_WIDTH*.9f)/8/2;
                shape.rect(j*(sqSide) + sqPos, i*(sqSide)+btmRectHeight + sqPos, sqSide*.9f, sqSide*.9f);
                shape.end();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

        posX -= scrollScale/2;
        posY -= scrollScale/2;

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
        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/digital_7.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = (int)(42 * Gdx.graphics.getDensity());
        param.color = Color.RED;
        param.borderWidth = (int)(3 * Gdx.graphics.getDensity());
        param.borderColor = Color.BLACK;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        font = fontGen.generateFont(param);
    }

    private void fontCheck(){

    }

    private void storeWindowAndPosition(){
        WIN_HEIGHT = Gdx.graphics.getHeight();
        WIN_WIDTH = Gdx.graphics.getWidth();
        posX = WIN_WIDTH/2-spriteLogo.getWidth()/2;
        posY = WIN_HEIGHT/2-spriteLogo.getHeight()/2;
        sqSide = WIN_WIDTH/8;
    }

    private void incrementTimer(){
        if(timerCheck && timer < 1000) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            timer += deltaTime;
            secTimer = (int)timer;
        }
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
