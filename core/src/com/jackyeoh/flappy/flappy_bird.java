package com.jackyeoh.flappy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class flappy_bird extends ApplicationAdapter {

    enum GameState {
        START, ONGOING, STOP
    }
    GameState gameState = GameState.START;

    private static final int NUMOFTUBES = 4;
    private static final float TUBEVELOCITY = 4;
    private static final float GAP = 400;

    SpriteBatch batch;
    Texture background;
    Texture gameover;
    TextureAtlas birdAtlas;
    Animation<TextureRegion> animation;

    Texture topTube;
    Texture bottomTube;

    float timePassed;
    float birdYPos ;
    float birdVelocity;
    float distanceBetweenTubes;

    float[] tubeX = new float[NUMOFTUBES];
    float[] tubeOffset = new float[NUMOFTUBES];

    int score;
    int scoringTube;

    BitmapFont font;
    Random randomGenerator;

    Circle birdCircle;
    Rectangle[] topTubeRectangle;
    Rectangle[] bottomTubeRectangle;

    //ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        //shapeRenderer = new ShapeRenderer();

        batch = new SpriteBatch();
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        birdAtlas = new TextureAtlas(Gdx.files.internal("bird.atlas"));
        animation = new Animation<TextureRegion>(1 / 10f, birdAtlas.getRegions());

        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
        randomGenerator = new Random();
        topTubeRectangle = new Rectangle[NUMOFTUBES];
        bottomTubeRectangle = new Rectangle[NUMOFTUBES];

        initGame();
    }

    @Override
    public void dispose() {
        batch.dispose();
        birdAtlas.dispose();
    }

    @Override
    public void render() {

        batch.begin();
        //Sets the background at the middle of the screen
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //If gamestate is oongoing
        if (gameState == GameState.ONGOING) {

            //Checks if the scoring tube has gone pass the middle mark, we add a score and increment the next tube to lookout for
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                if (scoringTube < NUMOFTUBES - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            //Moves the bird's position up when touched, if not it goes down.
            if (Gdx.input.justTouched()) {
                birdVelocity = -20;
            }

            //Check on the tubes
            for (int i = 0; i < NUMOFTUBES; i++) {

                //This is to check when the tube has gone off the edge of the screen
                if (tubeX[i] < -topTube.getWidth()) {

                    //Set the tube to go to the back
                    tubeX[i] += NUMOFTUBES * distanceBetweenTubes;
                    //Calculate a random offset for the tube
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - GAP - Gdx.graphics.getHeight() / 2);
                } else {
                    //Moves the tubes to the left
                    tubeX[i] = tubeX[i] - TUBEVELOCITY;
                }

                //Draw the tubes according to the calculations
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + GAP / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - GAP / 2 - bottomTube.getHeight() + tubeOffset[i]);

                //Set the rectangles to overlay on the tubes
                topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + GAP / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - GAP / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

            birdVelocity = birdVelocity + 2;
            birdYPos -= birdVelocity;
            //If bird falls below the screen, end the game
            if (birdYPos < 0) {
                gameState = GameState.STOP;
            }

        } else if (gameState == GameState.START) {
            if (Gdx.input.justTouched()) {
                gameState = GameState.ONGOING;
            }

        } else if (gameState == GameState.STOP) {
            //Show the gameover graphic
            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
            //Restart the game on touch
            if (Gdx.input.justTouched()) {
                gameState = GameState.ONGOING;
                initGame();
            }
        }
        //Get time to handle the animation
        timePassed += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(timePassed, true), Gdx.graphics.getWidth() / 2 - animation.getKeyFrame(timePassed).getRegionWidth() / 2, birdYPos);
        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdYPos + animation.getKeyFrame(timePassed).getRegionHeight() / 2, animation.getKeyFrame(timePassed).getRegionHeight() / 2);



        for (int i = 0; i < NUMOFTUBES; i++) {


            if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])) {
                gameState = GameState.STOP;
            }
        }

        /*
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < NUMOFTUBES; i++) {

            shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
        }
        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.end();
        */

    }

    public void initGame() {

        for (int i = 0; i < NUMOFTUBES; i++) {
            //Calculate the offset for the tob and bottom tubes
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - GAP - Gdx.graphics.getHeight() / 2);
            //Set the first tubes at the edge of the screen
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + +Gdx.graphics.getWidth() / 2 + i * distanceBetweenTubes;

            //Makes 4 top and bottom rectangles to overlay tubes
            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
        }

        birdYPos = Gdx.graphics.getHeight() / 2 - animation.getKeyFrame(timePassed).getRegionHeight() / 2;
        score = 0;
        scoringTube = 0;
        birdVelocity = 0;
        timePassed = 0;
    }

}
