package com.jackyeoh.flappy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;


public class flappy_bird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    private TextureAtlas birdAtlas;
    private Animation<TextureRegion> animation;
    private float timePassed = 0;
    float birdYPos = 0;
    float birdVelocity = 0;
    int gameState = 0;
    Texture topTube;
    Texture bottomTube;
    float gap = 400;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];


    float distanceBetweenTubes;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        birdAtlas = new TextureAtlas(Gdx.files.internal("bird.atlas"));
        animation = new Animation<TextureRegion>(1 / 10f, birdAtlas.getRegions());
        birdYPos = Gdx.graphics.getHeight() / 2 - animation.getKeyFrame(timePassed).getRegionHeight() / 2;
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 1000);
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + i * distanceBetweenTubes;

        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        birdAtlas.dispose();
    }

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (Gdx.input.justTouched()) {
                birdVelocity = -20;
            }

            for (int i = 0; i < numberOfTubes; i++) {

                if(tubeX[i] < - topTube.getWidth()){
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                }
                else
                {
                    tubeX[i] = tubeX[i] - tubeVelocity;

                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
            }

            birdVelocity = birdVelocity + 2;
            birdYPos -= birdVelocity;

        } else {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        }
        timePassed += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(timePassed, true), Gdx.graphics.getWidth() / 2 - animation.getKeyFrame(timePassed).getRegionWidth() / 2, birdYPos);

        batch.end();


    }

}
