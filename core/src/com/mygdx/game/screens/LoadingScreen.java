package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Application;
import com.mygdx.game.multiplayer.server.GameClient;
import com.mygdx.game.multiplayer.server.Packets;
import com.mygdx.game.utils.AssetHandler;
import com.mygdx.game.utils.Constants;

public class LoadingScreen implements Screen {

    private Application app;
    private AssetHandler assetHandler;
    private GameClient gameClient;

    private Label loading;
    private Stage stage;

    private float time;

    public LoadingScreen(Application app, AssetHandler assetHandler, GameClient gameClient){
        this.app = app;
        this.assetHandler = assetHandler;
        this.gameClient = gameClient;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT,new OrthographicCamera()),app.batch );

        Label.LabelStyle style = new Label.LabelStyle();

        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);

        style.font = new BitmapFont();


        loading = new Label("",style);
        loading.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);


        stage.addActor(loading);

        assetHandler.load();
        //assetHandler.manager.finishLoading();
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if(!assetHandler.manager.update()) {

            loading.setText("Loading : " + assetHandler.manager.getProgress() * 100 + "%");

        } else {

            app.setScreen(new MenuScreen(app, assetHandler,gameClient));
            //app.setScreen(new PlayScreen(app,assetHandler,gameClient,new Packets.GameSetup()));

            dispose();
        }


        stage.act(delta);
        stage.draw();

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
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
