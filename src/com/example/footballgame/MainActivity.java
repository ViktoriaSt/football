package com.example.footballgame;

import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

    private Camera mCamera;
    private Scene mMainScene;

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mGoalkeeperTextureRegion;
    private Sprite goalkeeper;

    private TextureRegion mPlayersTextureRegion;
    private TextureRegion mBallTextureRegion;
    private Sprite ball;

    LinkedList<Sprite> sprites = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onLoadComplete() {

    }

    @SuppressWarnings("deprecation")
    @Override
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        int cameraWidth = display.getWidth();
        int cameraHeight = display.getHeight();

        mCamera = new Camera(0, 0, cameraWidth, cameraHeight);

        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera));
    }

    @Override
    public void onLoadResources() {
        mBitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        mGoalkeeperTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Goalkeeper.png", 0, 0);
        mPlayersTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Players.png", 128, 0);
        mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Ball.png", 64, 0);

        mEngine.getTextureManager().loadTexture(mBitmapTextureAtlas);

    }

    @Override
    public Scene onLoadScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        mMainScene = new Scene();

        int color = Color.GREEN;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        mMainScene.setBackground(new ColorBackground(r, g, b));

        final int PlayerX = this.mGoalkeeperTextureRegion.getWidth() / 2;
        final int PlayerY = (int) ((mCamera.getHeight() - mGoalkeeperTextureRegion.getHeight()) / 2);

        goalkeeper = new Sprite(PlayerX, PlayerY, mGoalkeeperTextureRegion);
        goalkeeper.setScale(2);
        ball = new Sprite(PlayerX + this.mGoalkeeperTextureRegion.getWidth(), PlayerY, mBallTextureRegion);

        mMainScene.attachChild(goalkeeper);
        mMainScene.attachChild(ball);

        for (int i = 0; i < 6; i++) {
            sprites.add(getSprite(mPlayersTextureRegion));
            mMainScene.attachChild(getSprite(mPlayersTextureRegion));
        }

        mMainScene.setOnSceneTouchListener(this);

        return mMainScene;
    }

    public Sprite getSprite(TextureRegion textureRegion) {

        int minY = textureRegion.getHeight();
        int maxY = (int) (mCamera.getHeight() - textureRegion.getHeight());
        int rangeY = maxY - minY;

        Random r = new Random();

        int x = r.nextInt((int) mCamera.getWidth() + textureRegion.getWidth());
        int y = r.nextInt(rangeY);

        return new Sprite(x, y, textureRegion);

    }

    private void shootBall(final float pX, final float pY) {

        int offX = (int) (pX - goalkeeper.getX());
        int offY = (int) (pY - goalkeeper.getY());
        if (offX <= 0)
            return;

        int realX = (int) (mCamera.getWidth() + goalkeeper.getWidth() / 2.0f);
        float ratio = (float) offY / (float) offX;
        int realY = (int) ((realX * ratio) + goalkeeper.getY());

        int offRealX = (int) (realX - ball.getX());
        int offRealY = (int) (realY - ball.getY());
        float length = (float) Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
        float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
        float realMoveDuration = length / velocity;
        for (int i = 0; i < sprites.size(); i++) {

        }

        MoveModifier mod = new MoveModifier(realMoveDuration, ball.getX(), realX, ball.getY(), realY);
        ball.registerEntityModifier(mod.deepCopy());

    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
            final float touchX = pSceneTouchEvent.getX();
            final float touchY = pSceneTouchEvent.getY();
            shootBall(touchX, touchY);
            return true;
        }
        return false;
    }

}
