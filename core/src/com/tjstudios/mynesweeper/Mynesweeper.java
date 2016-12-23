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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.toUpperCase;

public class Mynesweeper extends ApplicationAdapter implements InputProcessor {
    private static final int gridHeight = 11;
    private static final int gridWidth = 8;
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
    private BitmapFont clockFont, ubuntuFont;
    private ArrayList<buttonCheck> keypressArray = new ArrayList<buttonCheck>();                                  // Used for smoothing out directional key movement
    private int WIN_WIDTH = 0, WIN_HEIGHT = 0;
    private float posX, posY;
    private boolean isLeftTouchPressed = false;
    private boolean isRightTouchPressed = false;
    private String inputKey = "";
    private int bombCount, secTimer;
    private float timer;
    private boolean timerCheck;
    private String toggleButtonText = "BOMB";
    private float btmRectHeight;
    private GlyphLayout layout = new GlyphLayout();
    private button toggleButton;
	
	@Override
	public void create () {
		initSprite();                                                                                   // Create texture, image and then spriteLogo
        initFont();                                                                                     // Creates freetype font and sets its properties
        storeWindowAndPosition();                                                                       // Stores window size and position into their own variables

        layout.setText(ubuntuFont, toggleButtonText);
        btmRectHeight = WIN_HEIGHT/8;
        bombCount = 20;
        secTimer = 0;
        timer = 0f;
        timerCheck = true;
        camera = new OrthographicCamera(WIN_WIDTH, WIN_HEIGHT);
        viewport = new ScreenViewport(camera);
        shape = new ShapeRenderer();
        toggleButton = new buttonRounded(WIN_WIDTH*(.05f), btmRectHeight/10, WIN_WIDTH*(.9f), btmRectHeight*8/10, 5f);

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
            clockFont.draw(batch, "Bombs:" + bombCount + "", -WIN_WIDTH*6/13, WIN_HEIGHT*15/32);
            clockFont.draw(batch, "Time:" + secTimer + "", WIN_WIDTH/14, WIN_HEIGHT*15/32);
            ubuntuFont.draw(batch, layout, -layout.width/2, -WIN_HEIGHT/2 + btmRectHeight/2 + layout.height/2);
        batch.end();
    }

    private void shapeProcess() {
        shape.setColor(Color.DARK_GRAY);    // Top-of-screen rectangle
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(0, btmRectHeight+sqSide*gridHeight, WIN_WIDTH, WIN_HEIGHT-btmRectHeight-sqSide*gridHeight);
        shape.end();

        shape.setColor(Color.BLACK);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.line(WIN_WIDTH/2, btmRectHeight+sqSide*gridHeight, WIN_WIDTH/2, WIN_HEIGHT);
        shape.end();

        shape.setColor(Color.DARK_GRAY);    // Bottom-of-screen rectangle
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(0, 0, WIN_WIDTH, btmRectHeight);
        shape.end();

        shape.setColor(new Color(200f/255f, 200f/255f, 0f, 0f));
        shape.begin(ShapeRenderer.ShapeType.Filled);
        roundedRect(toggleButton.getX(), toggleButton.getY(), toggleButton.getXSize(), toggleButton.getYSize(), ((buttonRounded)toggleButton).getRadius());
        shape.end();

        float sqPos;

        for(int i = 0; i < gridHeight; i++){
            for(int j = 0; j < gridWidth; j++){
                shape.setColor(Color.LIGHT_GRAY);
                shape.begin(ShapeRenderer.ShapeType.Filled);
                sqPos = sqSide;
                shape.rect(j*(sqPos), i*(sqPos)+btmRectHeight, sqSide, sqSide);
                shape.end();

                shape.setColor(Color.GRAY);
                shape.begin(ShapeRenderer.ShapeType.Filled);
                sqPos = (WIN_WIDTH-WIN_WIDTH*.88f)/8/2;
                shape.rect(j*(sqSide) + sqPos, i*(sqSide)+btmRectHeight + sqPos, sqSide*.88f, sqSide*.88f);
                shape.end();
            }
        }
    }

    private void roundedRect(float x, float y, float width, float height, float radius){
        // Central rectangle
        shape.rect(x + radius, y + radius, width - 2*radius, height - 2*radius);

        // Four side rectangles, in clockwise order
        shape.rect(x + radius, y, width - 2*radius, radius);
        shape.rect(x + width - radius, y + radius, radius, height - 2*radius);
        shape.rect(x + radius, y + height - radius, width - 2*radius, radius);
        shape.rect(x, y + radius, radius, height - 2*radius);

        // Four arches, clockwise too
        shape.arc(x + radius, y + radius, radius, 180f, 90f);
        shape.arc(x + width - radius, y + radius, radius, 270f, 90f);
        shape.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        shape.arc(x + radius, y + height - radius, radius, 90f, 90f);
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
        clockFont = fontGen.generateFont(param);

        fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ubuntu_bold.ttf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = (int)(36 * Gdx.graphics.getDensity());
        param.color = Color.BLACK;
//        param.borderWidth = (int)(3 * Gdx.graphics.getDensity());
//        param.borderColor = Color.BLACK;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        ubuntuFont = fontGen.generateFont(param);
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

    private class button extends Mynesweeper {
        private float xPos, yPos, xSize, ySize;
        private float textX = 0, textY = 0;
        private String text;

        private button(float x, float y, float xSize, float ySize) {
            xPos = x;
            yPos = y;
            this.xSize = xSize;
            this.ySize = ySize;
        }

        private button(float x, float y, float xSize, float ySize, float xText, float yText, String theText) {
            xPos = x;
            yPos = y;
            this.xSize = xSize;
            this.ySize = ySize;
            textX = xText;
            textY = yText;
            text = theText;
        }

        private float getX() {
            return xPos;
        }

        private void setX(float x) {
            xPos = x;
        }

        private float getY() {
            return yPos;
        }

        private void setY(float y) {
            yPos = y;
        }

        private float getXSize() {
            return xSize;
        }

        private void setXSize(float x) {
            xSize = x;
        }

        private float getYSize() {
            return ySize;
        }

        private void setYSize(float y) {
            ySize = y;
        }

        private float getXText() {
            return textX;
        }

        private void setXText(float x) {
            textX = x;
        }

        private float getYText() {
            return textY;
        }

        private void setYText(float y) {
            textY = y;
        }

        private String getText() {
            return text;
        }

        private void setText(String s) {
            text = s;
        }
    }

    private class buttonRounded extends button {
        private float radius;

        private buttonRounded(float x, float y, float xSize, float ySize, float r) {
            super(x, y, xSize, ySize);
            radius = r;
        }

        private buttonRounded(float x, float y, float xSize, float ySize, float xText, float yText, String theText, float r) {
            super(x, y, xSize, ySize, xText, yText, theText);
            radius = r;
        }

        private float getRadius() {
            return radius;
        }

        private void setRadius(float r) {
            radius = r;
        }
    }
}
