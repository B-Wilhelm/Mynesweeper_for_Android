package com.tjstudios.mynesweeper;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

public class Mynesweeper extends Game {
    private boolean lossCheck, winCheck;
    private int endDelay;
    private int gridWidth, gridHeight;
    private int WIN_WIDTH, WIN_HEIGHT;
    private int bombCount, curBombCount, secTimer;
    private int toggleButtonIndex;
    private int clusterNum;
    private int[][] mineVals;
    private float MINE_X_SIZE, MINE_Y_SIZE;
    private float sqSide;
    private float timer;
    private float btmRectHeight, topRectHeight;
    private boolean timerCheck;
    private String[] mineStatus, toggleButtonText;
    private ShapeRenderer shape;
    private OrthographicCamera camera;
    private Viewport viewport;
	private SpriteBatch batch;                                                                              // Used for spriteLogo initialization
	private Texture img;
    private BitmapFont clockFont, ubuntuFont;
    private Stage stage;
    private Skin toggleSkin;
    private GlyphLayout timerLayout, bombLayout;
    private MyButton toggleButton;
    private Color toggleButtonColor, toggleButtonShaded, toggleButtonClicked;
    private Color mineColor, mineColorShaded;
    private ArrayList<TextButton> mineField = new ArrayList<TextButton>();
    private ArrayList<MineButton> mineFieldValues = new ArrayList<MineButton>();

    @Override
	public void create() {
        storeWindowAndPosition();   // Stores window size and position into their own variables
        initFont();                 // Creates freetype font and sets its properties
        initVars();
        initButtonValues();
        initButtons();
        initGrid();
        setBackground();    // Sets background color

        Gdx.input.setInputProcessor(stage);
//        this.setScreen(new Mynesweeper());
//        stage.setDebugAll(true);
	}

	@Override
	public void render() {

        super.render();

        if(endDelay < 3) {

            if(lossCheck) {
                endDelay++;
            }
            cameraSetup();
            bombText();
            incrementTimer();
            shapeProcess();
            batchProcess();
            stageProcess();
            gridProcess();
        }
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
        toggleSkin.dispose();
        stage.dispose();
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

        shape.begin(ShapeRenderer.ShapeType.Filled);    // Bottom-of-screen rectangle
        shape.rect(0, 0, WIN_WIDTH, btmRectHeight);
        shape.end();

        shape.setColor(Color.BLACK);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.line(WIN_WIDTH/2, btmRectHeight+sqSide*gridHeight, WIN_WIDTH/2, WIN_HEIGHT);
        shape.end();

        shape.setColor(Color.LIGHT_GRAY);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(0, btmRectHeight, WIN_WIDTH, gridHeight * sqSide);
        shape.end();
    }

    private void stageProcess(){
        stage.act();
        stage.draw();
    }

    private void roundedRect(Pixmap toggleButtonPixmap, Color c, int x, int y, int width, int height, int radius){
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
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void initVars(){
        lossCheck = false;
        endDelay = 0;
        camera = new OrthographicCamera(WIN_WIDTH, WIN_HEIGHT);
        viewport = new ScreenViewport(camera);
        shape = new ShapeRenderer();
        stage = new Stage();
        batch = new SpriteBatch();
        img = new Texture("minesweep.png");
        mineStatus = new String[]{" ", "1", "2", "3", "4", "5", "6", "7", "8", "BOMB"};             // 0 is blank, 9 is bomb and 1-8 are adjacent bomb counts
        toggleButtonText = new String[]{"BOMB", "FLAG"};
        timerLayout = new GlyphLayout();
        bombLayout = new GlyphLayout();
        mineField.clear();
        mineFieldValues.clear();
        toggleButtonIndex = 0;
        gridWidth = 8;
        gridHeight = 11;
        sqSide = WIN_WIDTH/8;
        btmRectHeight = WIN_HEIGHT/8;
        topRectHeight = WIN_HEIGHT-btmRectHeight-sqSide*gridHeight;
        bombCount = 20;
        curBombCount = 20;
        secTimer = 0;
        timer = 0f;
        timerCheck = false;
        clusterNum = 0;
        MINE_X_SIZE = MINE_Y_SIZE = sqSide*.88f;
        toggleButtonColor = new Color(200f/255f, 200f/255f, 0, 1);
        toggleButtonShaded = new Color(179f/255f, 179f/255f, 0, 1);
        toggleButtonClicked = new Color(158f/255f, 158f/255f, 0, 1);
        mineColor = new Color(169f/255f, 169f/255f, 169f/255f, 1);
        mineColorShaded = new Color(150f/255f, 150f/255f, 150f/255f, 1);
    }

    private void initButtonValues() {
        toggleButton = new MyButtonRounded(WIN_WIDTH*(.05f), btmRectHeight/10, WIN_WIDTH*(.9f), btmRectHeight*8/10, 10f);
        float sqPos = (WIN_WIDTH-WIN_WIDTH*.88f)/8/2;

        for(int i = 0; i < gridHeight; i++) {
            for(int j = 0; j < gridWidth; j++) {
                mineFieldValues.add(new MineButton(j*(sqSide) + sqPos, i*(sqSide)+btmRectHeight + sqPos, sqSide*.88f, sqSide*.88f, mineStatus[9], j, i, false));
            }
        }
    }

    private void initButtons() {
        toggleSkin = new Skin();
        Pixmap toggleButtonPixmap = new Pixmap((int)toggleButton.getXSize(), (int)toggleButton.getYSize(), Pixmap.Format.RGBA8888);
        roundedRect(toggleButtonPixmap, toggleButtonColor, 0, 0, (int)toggleButton.getXSize(), (int)toggleButton.getYSize(), (int)(((MyButtonRounded)toggleButton).getRadius()*Gdx.graphics.getDensity()));
        toggleSkin.add("yellow", new Texture(toggleButtonPixmap));
        toggleSkin.add("default", ubuntuFont);

        TextButton.TextButtonStyle toggleStyle = new TextButton.TextButtonStyle();
        toggleStyle.up = toggleSkin.newDrawable("yellow", toggleButtonColor);
        toggleStyle.down = toggleSkin.newDrawable("yellow", toggleButtonClicked);
        toggleStyle.over = toggleSkin.newDrawable("yellow", toggleButtonShaded);
        toggleStyle.font = toggleSkin.getFont("default");
        toggleSkin.add("default", toggleStyle);

        final TextButton tB = new TextButton(toggleButtonText[toggleButtonIndex], toggleSkin);
        tB.setPosition(toggleButton.getX(), toggleButton.getY());
        tB.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                toggleButtonIndex = Math.abs(toggleButtonIndex - 1);
                tB.setText(toggleButtonText[toggleButtonIndex]);

                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {  }
        });

        tB.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                initVars();
                initFont();
                initButtonValues();
                initButtons();
                initGrid();

                return true;
            }
        });

        stage.addActor(tB);

        /*------------------------------------------------------------------------------------------------------------------------------------*/

        final Skin mineSkin = new Skin();
        Pixmap minePix = new Pixmap((int)MINE_X_SIZE, (int)MINE_Y_SIZE, Pixmap.Format.RGBA8888);
        TextButton.TextButtonStyle mineStyle;

        for(int i = 0; i < mineFieldValues.size(); i++) {
            minePix.setColor(mineColor);
            minePix.fillRectangle(0, 0, (int)MINE_X_SIZE, (int)MINE_Y_SIZE);
            mineSkin.add("gray", new Texture(minePix));
            minePix.setColor(Color.WHITE);
            mineSkin.add("white", new Texture(minePix));
            minePix.setColor(Color.RED);
            mineSkin.add("red", new Texture(minePix));
            mineSkin.add("default", ubuntuFont);

            mineStyle = new TextButton.TextButtonStyle();
            mineStyle.up = mineSkin.newDrawable("gray", mineColor);
            mineStyle.down = mineSkin.newDrawable("gray", mineColorShaded);
            mineStyle.font = mineSkin.getFont("default");

            mineSkin.add("default", mineStyle);

            final TextButton temp = new TextButton("", mineSkin);
            temp.setName("hidden");
            temp.setPosition((int)mineFieldValues.get(i).getX(), (int)mineFieldValues.get(i).getY());

            temp.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(!timerCheck) { timerCheck = true; }

                    if(temp.getName().equals("hidden") || temp.getName().equals("flagged")) {
                        if(toggleButtonIndex == 0 && temp.getName().equals("hidden")) {   // if ToggleButton's text is "BOMB"
                            temp.setName("revealed");
                            temp.getStyle().up = mineSkin.newDrawable("white", Color.WHITE);
                            temp.getStyle().down = mineSkin.newDrawable("white", Color.WHITE);
                        }
                        else if(toggleButtonIndex != 0) {  // if ToggleButton's text is "FLAG"
                            if(temp.getName().equals("flagged")) {
                                temp.setName("hidden");
                                curBombCount++;
                                temp.getStyle().up = mineSkin.newDrawable("gray", mineColor);
                                temp.getStyle().down = mineSkin.newDrawable("gray", mineColorShaded);
                            }
                            else {
                                temp.setName("flagged");
                                curBombCount--;
                                temp.getStyle().up = mineSkin.newDrawable("red", Color.FIREBRICK);
                                temp.getStyle().down = mineSkin.newDrawable("red", Color.FIREBRICK);
                            }
                        }
                    }

                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {  }
            });

            mineField.add(temp);
            stage.addActor(temp);
        }

        minePix.dispose();
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

    private void initGrid() {
        mineVals = new int[gridWidth][gridHeight];

        for(int i = 0; i < bombCount; i++) {
            mineVals[(int)(Math.random()*gridWidth)][(int)(Math.random()*gridHeight)] = 9;
        }

        for(int i = 0; i < gridHeight; i++) {
            for(int j = 0; j < gridWidth; j++) {
                if(mineVals[j][i] != 9) {
                    mineVals[j][i] = bombCheck(j, i);
                }
            }
        }
    }

    private int bombCheck(int x, int y) {
        int value = 0;
        int minX = -1, maxX = 1;
        int minY = -1, maxY = 1;

        if(x == 0) { minX = 0; }
        if(x == gridWidth-1) { maxX = 0; }
        if(y == 0) { minY = 0; }
        if(y == gridHeight-1) { maxY = 0; }

        for(int i = minY; i <= maxY; i++) {
            for(int j = minX; j <= maxX; j++) {
                if(mineVals[x+j][y+i] == 9) {
                    value++;
                }
            }
        }

        return value;
    }

    private void emptySpaceCheck(int x, int y) {

        emptySpaceCheckRec(x, y);
    }

    private void emptySpaceCheckRec(int x, int y) {

        mineField.get(y * gridWidth + x).setName("cluster " + clusterNum);

        if(x > 0) { emptySpaceCheckRec(x-1, y); }
        if(x > gridWidth) { emptySpaceCheckRec(x+1, y); }
        if(y > 0) { emptySpaceCheckRec(x, y-1); }
        if(y > gridHeight) { emptySpaceCheckRec(x, y+1); }
    }

    private void gridProcess() {
        String revealString, revealName;
        TextButton temp;

        for(int i = 0; i < gridHeight; i++) {
            for(int j = 0; j < gridWidth; j++) {
                temp = mineField.get(i * gridWidth + j);

                if(temp.getName().equals("revealed")) {
                    if(mineVals[j][i] == 0) {
                        revealString = " ";
                        revealName = "empty";
                    }
                    else if ((mineVals[j][i] == 9)){
                        revealString = "B";
                        revealName = "bomb";
                        lossCheck = true;
                    }
                    else {
                        revealString = mineVals[j][i] + "";
                        revealName = "value";
                    }

                    temp.setText(revealString);
                    temp.setName(revealName);
                }
            }
        }
    }

    private void bombText() {
        if(!lossCheck) {
            bombLayout.setText(clockFont, "Bombs:" + curBombCount);
            timerLayout.setText(clockFont, "Time:" + secTimer);
        }
        else if(!winCheck) {
            bombLayout.setText(clockFont, "YOU");
            timerLayout.setText(clockFont, "WIN");
        }
        else {
            bombLayout.setText(clockFont, "YOU");
            timerLayout.setText(clockFont, "WIN");
        }
    }

    private void cameraSetup() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
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
        private String text;

        private MyButton(float x, float y, float xSize, float ySize) {
            xPos = x;
            yPos = y;
            this.xSize = xSize;
            this.ySize = ySize;
        }

        private MyButton(float x, float y, float xSize, float ySize, String theText) {
            xPos = x;
            yPos = y;
            this.xSize = xSize;
            this.ySize = ySize;
            text = theText;
        }

        public float getX() {
            return xPos;
        }

        private void setX(float x) {
            xPos = x;
        }

        public float getY() {
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

        private MyButtonRounded(float x, float y, float xSize, float ySize, String theText, float r) {
            super(x, y, xSize, ySize, theText);
            radius = r;
        }

        public float getRadius() {
            return radius;
        }

        private void setRadius(float r) {
            radius = r;
        }
    }

    private class MineButton extends MyButton {
        private int xMinePos, yMinePos;
        private boolean revealed;

        private MineButton(float x, float y, float xSize, float ySize, String text, int xMinePos, int yMinePos, boolean revealed) {
            super(x, y, xSize, ySize, text);
            this.revealed = revealed;
            this.xMinePos = xMinePos;
            this.yMinePos = yMinePos;
        }

        public int getXMinePos() {
            return xMinePos;
        }

        private void setXMinePos(int x) {
            xMinePos = x;
        }

        public int getYMinePos() {
            return yMinePos;
        }

        private void setYMinePos(int y) {
            yMinePos = y;
        }

        public boolean getRevealed() {return revealed;}

        private void setRevealed(boolean revealed) {this.revealed = revealed;}
    }
}
