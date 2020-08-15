package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Application;
import com.mygdx.game.multiplayer.server.GameClient;
import com.mygdx.game.utils.AssetHandler;
import com.mygdx.game.utils.Constants;


public class MenuScreen implements Screen {

    private Application app;
    private AssetHandler assetHandler;

    private Stage stage;
    private Table table, btn_table, lbl_table;

    private TextButton btn_start, btn_exit, btn_settings;
    private Label lbl_game;
    private TextButton.TextButtonStyle btn_style;
    private Label.LabelStyle lbl_style;

    /*private FreeTypeFontGenerator fontgen;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontparam;
    private BitmapFont font;*/

    private Texture texUp,texDown,texOver,texLock,menuwallpaper;
    private String myIP = "localhost";//"46.1.147.34";
    private GameClient gameClient;

    public MenuScreen(Application app, AssetHandler assetHandler, GameClient gameClient) {
        this.app = app;
        this.assetHandler = assetHandler;
        this.gameClient = gameClient;

        gameClient.setCurrentScreen(this);
        gameClient.updateCurrentScreen();
    }

    @Override
    public void show() {


        //createFont();
        Texture.TextureFilter min = Texture.TextureFilter.Linear;
        Texture.TextureFilter mag = Texture.TextureFilter.Linear;

        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT,new OrthographicCamera()),app.batch);

        btn_table = new Table();
        lbl_table = new Table();
        table = new Table();

        //Button Textures
        texUp = assetHandler.manager.get(assetHandler.btnUp);
        texUp.setFilter(min,mag);

        texDown = assetHandler.manager.get(assetHandler.btnDown);
        texDown.setFilter(min,mag);

        texOver = assetHandler.manager.get(assetHandler.btnOver);
        texOver.setFilter(min,mag);

        texLock = assetHandler.manager.get(assetHandler.btnLock);
        texLock.setFilter(min,mag);

        menuwallpaper = assetHandler.manager.get(assetHandler.menuwallpaper);
        menuwallpaper.setFilter(min,mag);


        BitmapFont font_bangers = assetHandler.manager.get(assetHandler.fontBangers, BitmapFont.class);
        btn_style = new TextButton.TextButtonStyle();
        //btn_style.font = font;
        btn_style.font = font_bangers;
        btn_style.up = new TextureRegionDrawable(texUp);
        btn_style.down = new TextureRegionDrawable(texDown);
        btn_style.over = new TextureRegionDrawable(texOver);
        btn_style.disabled = new TextureRegionDrawable(texLock);

        lbl_style = new Label.LabelStyle();
        lbl_style.font = new BitmapFont();
        lbl_style.font.getData().setScale(3f);

        lbl_game = new Label("Rock, Paper and Scissors", lbl_style);

        btn_start = new TextButton("Start Game", btn_style);
        //btn_start.setDisabled(true);
        btn_settings = new TextButton("Settings", btn_style);
        btn_exit = new TextButton("Exit Game", btn_style);

        //lbl_table.setFillParent(true);
        lbl_table.defaults().pad(20f);
        lbl_table.top();
        lbl_table.add(lbl_game).expandX().height(2*Constants.VIEWPORT_HEIGHT/10);

        btn_table.defaults().pad(10f);
        btn_table.add(btn_start).width(0.46875f * Constants.VIEWPORT_WIDTH).height(0.18833f * Constants.VIEWPORT_HEIGHT);
        btn_table.row();
        btn_table.add(btn_settings).width(0.46875f * Constants.VIEWPORT_WIDTH).height(0.18833f * Constants.VIEWPORT_HEIGHT);
        btn_table.row();
        btn_table.add(btn_exit).width(0.46875f * Constants.VIEWPORT_WIDTH).height(0.18833f * Constants.VIEWPORT_HEIGHT);
        btn_table.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);

        table.setFillParent(true);
        //table.add(lbl_table);
        //table.row();
        table.add(btn_table);
        table.padTop(220f);

        //stage.setDebugAll(true);
        //stage.addActor(btn_table);
        //stage.addActor(lbl_table);
        stage.addActor(table);

        //stage.getRoot().getColor().a = 0f;
        //stage.getRoot().addAction(Actions.fadeIn(0.5f));

        //serverHandler();
        buttonHandler();
        Gdx.input.setInputProcessor(stage);

    }




    @Override
    public void render(float delta) {

        //inputHandler();


        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.camera.update();
        app.viewport.apply();
        app.batch.setProjectionMatrix(app.camera.combined);


        app.batch.begin();
        //app.batch.setColor(0.85f, 0.85f, 0.85f, 1);
        app.batch.draw(menuwallpaper,0,0);
        app.batch.end();

        //stage.getCamera().update();
        //stage.getViewport().apply();
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

        app.viewport.update(width, height,true);
        app.batch.setProjectionMatrix(app.camera.combined);
        app.viewport.apply();

        stage.getViewport().update(width , height);
        stage.getViewport().apply();
    }

    public void buttonHandler() {
        btn_start.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                app.setScreen(new MatchScreen(app,assetHandler,gameClient));
                dispose();

                return true;
            }
        });

        btn_exit.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                dispose();
                Gdx.app.exit();

                return true;
            }
        });
    }

    /*private void createFont() {
        fontgen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Bangers-Regular.ttf"));

        fontparam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontparam.size = 85;
        fontparam.color = Color.WHITE;

        font = fontgen.generateFont(fontparam);
    }*/


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
        //menuwallpaper.dispose();
        //assetHandler.dispose();
        /*texLock.dispose();
        texOver.dispose();
        texDown.dispose();
        texUp.dispose();
        */
        //font.dispose();
    }
}
