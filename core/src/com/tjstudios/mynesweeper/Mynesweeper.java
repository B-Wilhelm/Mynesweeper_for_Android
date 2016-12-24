package com.tjstudios.mynesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.toUpperCase;

public class Mynesweeper extends ApplicationAdapter {
    private static final int gridHeight = 11;
    private static final int gridWidth = 8;
    private float sqSide = 0;
    private ShapeRenderer shape;
    private OrthographicCamera camera;
    private Viewport viewport;
	private SpriteBatch batch;                                                                          // Used for spriteLogo initialization
	private Texture img;
    private BitmapFont clockFont, ubuntuFont;
    private Stage stage;
    private Skin skin;
    private int WIN_WIDTH = 0, WIN_HEIGHT = 0;
    private int bombCount, secTimer;
    private float timer;
    private boolean timerCheck;
    private String toggleButtonText;
    private float btmRectHeight, topRectHeight;
    private GlyphLayout timerLayout = new GlyphLayout();
    private GlyphLayout bombLayout = new GlyphLayout();
    private MyButton toggleButton;
    private Pixmap toggleButtonPixmap;
	
	@Override
	public void create () {
        storeWindowAndPosition();                                                                       // Stores window size and position into their own variables
        initFont();                                                                                     // Creates freetype font and sets its properties
        initVars();
        initButtonValues();

        Gdx.input.setInputProcessor(stage);

        initButtons();
	}

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        bombLayout.setText(clockFont, "Bombs:" + bombCount);
        timerLayout.setText(clockFont, "Time:" + secTimer);

        incrementTimer();
		setBackground();                                                                                // Sets background color
        shapeProcess();
        batchProcess();
        stageProcess();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
        stage.dispose();
        skin.dispose();
	}

	private void batchProcess() {
        batch.begin();
            clockFont.draw(batch, bombLayout, -WIN_WIDTH/4 - bombLayout.width/2, (WIN_HEIGHT-topRectHeight+bombLayout.height)/2);
            clockFont.draw(batch, timerLayout, WIN_WIDTH/4 - timerLayout.width/2, (WIN_HEIGHT-topRectHeight+timerLayout.height)/2);
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

    private void stageProcess(){
        stage.act();
        stage.draw();
    }

    private void roundedRect(Color c, int x, int y, int width, int height, int radius){
        // Central rectangle
        toggleButtonPixmap.setColor(c);
        toggleButtonPixmap.fillRectangle(x + radius, y + radius, width - 2*radius, height - 2*radius);

        // Four side rectangles, in clockwise order
        toggleButtonPixmap.fillRectangle(x + radius, y, width - 2*radius, radius);
        toggleButtonPixmap.fillRectangle(x + width - radius, y + radius, radius, height - 2*radius);
        toggleButtonPixmap.fillRectangle(x + radius, y + height - radius, width - 2*radius, radius);
        toggleButtonPixmap.fillRectangle(x, y + radius, radius, height - 2*radius);

        // Four arches, clockwise too
        toggleButtonPixmap.fillCircle(x + radius, y + radius, radius);
        toggleButtonPixmap.fillCircle(x + width - radius, y + radius, radius);
        toggleButtonPixmap.fillCircle(x + width - radius, y + height - radius, radius);
        toggleButtonPixmap.fillCircle(x + radius, y + height - radius, radius);
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

    private void setBackground(){
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void initVars(){
        sqSide = WIN_WIDTH/8;
        btmRectHeight = WIN_HEIGHT/8;
        topRectHeight = WIN_HEIGHT-btmRectHeight-sqSide*gridHeight;
        bombCount = 20;
        secTimer = 0;
        timer = 0f;
        timerCheck = true;
        toggleButtonText = "BOMB";
        camera = new OrthographicCamera(WIN_WIDTH, WIN_HEIGHT);
        viewport = new ScreenViewport(camera);
        shape = new ShapeRenderer();
        stage = new Stage();
        batch = new SpriteBatch();
        img = new Texture("minesweep.png");
    }

    private void initButtonValues() {
        toggleButton = new MyButtonRounded(WIN_WIDTH*(.05f), btmRectHeight/10, WIN_WIDTH*(.9f), btmRectHeight*8/10, 10f);
    }

    private void initButtons() {
        skin = new Skin();
        toggleButtonPixmap = new Pixmap((int)toggleButton.getXSize(), (int)toggleButton.getYSize(), Pixmap.Format.RGBA8888);
        roundedRect(new Color(200f/255f, 200f/255f, 0, 1), 0, 0, (int)toggleButton.getXSize(), (int)toggleButton.getYSize(), (int)((MyButtonRounded)toggleButton).getRadius());
        skin.add("yellow", new Texture(toggleButtonPixmap));
        skin.add("default", ubuntuFont);

        TextButton.TextButtonStyle tBS = new TextButton.TextButtonStyle();
        tBS.up = skin.newDrawable("yellow", 200f/255f, 200f/255f, 0, 1);
        tBS.down = skin.newDrawable("yellow", 179f/255f, 179f/255f, 0, 1);
        tBS.over = skin.newDrawable("yellow", Color.LIGHT_GRAY);
        tBS.font = skin.getFont("default");
        skin.add("default", tBS);

        final TextButton tB = new TextButton(toggleButtonText, skin);
        tB.setPosition(toggleButton.getX(), toggleButton.getY());
        stage.addActor(tB);
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
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        ubuntuFont = fontGen.generateFont(param);
    }

    private void storeWindowAndPosition(){
        WIN_HEIGHT = Gdx.graphics.getHeight();
        WIN_WIDTH = Gdx.graphics.getWidth();
    }

    private void incrementTimer(){
        if(timerCheck && timer < 1000) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            timer += deltaTime;
            secTimer = (int)timer;
        }
    }

    private class MyButton extends Mynesweeper {
        private float xPos, yPos, xSize, ySize;
        private float textX = 0, textY = 0;
        private String text;

        private MyButton(float x, float y, float xSize, float ySize) {
            xPos = x;
            yPos = y;
            this.xSize = xSize;
            this.ySize = ySize;
        }

        private MyButton(float x, float y, float xSize, float ySize, float xText, float yText, String theText) {
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

    private class MyButtonRounded extends MyButton {
        private float radius;

        private MyButtonRounded(float x, float y, float xSize, float ySize, float r) {
            super(x, y, xSize, ySize);
            radius = r;
        }

        private MyButtonRounded(float x, float y, float xSize, float ySize, float xText, float yText, String theText, float r) {
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
