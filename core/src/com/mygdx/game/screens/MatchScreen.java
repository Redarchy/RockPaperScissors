package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Application;
import com.mygdx.game.multiplayer.server.GameClient;
import com.mygdx.game.multiplayer.server.NetworkHandler;
import com.mygdx.game.utils.AssetHandler;
import com.mygdx.game.utils.Constants;

public class MatchScreen implements Screen {

    private Application app;
    private AssetHandler assetHandler;

    private Stage stage;
    private Table table,table2;
    private TextButton btn_join, btn_exit, btn_findserver;
    private TextButton.TextButtonStyle txtbtnstyle;
    private Label label, label1, label2;
    private TextField txt_name;
    private Skin skin;
    private BitmapFont font_bangers;
    private Texture wallpaper,texUp,texOver,texDown,texLock,tHome;
    private TextureRegion texHome[];

    private GameClient gameClient;

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT,new OrthographicCamera()),app.batch);
        //skin = new Skin(Gdx.files.internal("skins/glassy/skin/glassy-ui.json"));
        skin = assetHandler.manager.get(assetHandler.skin);
        font_bangers  = assetHandler.manager.get(assetHandler.fontBangers,BitmapFont.class);

        createUIElements();

        table = new Table();
        table.setPosition(Constants.VIEWPORT_WIDTH/2,Constants.VIEWPORT_HEIGHT/2);
        table.add(label).padBottom(150f);
        table.row();
        table.center().add(txt_name).width(txt_name.getWidth()).height(txt_name.getHeight()).padBottom(120f);
        table.row();
        table.center().add(btn_join).width(btn_join.getWidth()).height(btn_join.getHeight()).padBottom(10f);

        table2 = new Table();
        table2.setPosition(Constants.VIEWPORT_WIDTH/2,Constants.VIEWPORT_HEIGHT/2);
        table2.add(label1).padBottom(150f);
        table2.row();
        //table2.add(label2);
        table2.row();
        table2.add(btn_findserver);


        btn_exit.setPosition(stage.getWidth()-btn_exit.getWidth() - 20f,20f);
        stage.addActor(table);
        stage.addActor(btn_exit);
        stage.addActor(label2);

        Gdx.input.setInputProcessor(stage);
        buttonHandler();

    }

    private void buttonHandler() {
        btn_exit.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                app.setScreen(new MenuScreen(app,assetHandler,gameClient));
                gameClient.abortConnection("Return to menu");
            }
        });

        btn_join.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                createConnectedScreen(txt_name.getText());
                table.remove();
                txt_name.clear();
                txt_name.remove();

            }
        });
    }

    private void createConnectedScreen(String name) {
        String ipAddress;

        if(app.ADMIN) {
            ipAddress = "localhost";
        } else {
            ipAddress = NetworkHandler.IPADDRESS;
        }

        gameClient.attemptConnection(ipAddress, name);

        if(gameClient.client.isConnected()) {

            gameClient.requestPlayerCount();

            label1.setText("Welcome " + gameClient.getPlayerName() + "!");

            label2.setText("Getting information...");
            label2.setText("Online Players : " + gameClient.totalPlayerInfo);

            stage.addActor(table2);
        }

        btn_findserver.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameClient.requestMatchmake();
            }
        });


    }

    public void createUIElements () {
        Texture.TextureFilter min = Texture.TextureFilter.Linear;
        Texture.TextureFilter mag = Texture.TextureFilter.Linear;

        wallpaper = assetHandler.manager.get(assetHandler.wallpaper);
        texUp = assetHandler.manager.get(assetHandler.btnUp);
        texOver = assetHandler.manager.get(assetHandler.btnOver);
        texDown = assetHandler.manager.get(assetHandler.btnDown);
        texLock = assetHandler.manager.get(assetHandler.btnLock);
        tHome = assetHandler.manager.get(assetHandler.btnHome);

        texHome = new TextureRegion[4];
        for(int i = 0; i < 4; i++)
        {
            texHome[i] = new TextureRegion(tHome,i*100,0,100,100);
            texHome[i].getTexture().setFilter(min,mag);
        }

        txtbtnstyle = new TextButton.TextButtonStyle();

        txtbtnstyle.up = new TextureRegionDrawable(texUp);
        txtbtnstyle.over = new TextureRegionDrawable(texOver);
        txtbtnstyle.down = new TextureRegionDrawable(texDown);
        txtbtnstyle.disabled = new TextureRegionDrawable(texLock);
        txtbtnstyle.font = font_bangers;

        TextButton.TextButtonStyle homebtnstyle = new TextButton.TextButtonStyle();
        homebtnstyle.up = new TextureRegionDrawable(texHome[0]);
        homebtnstyle.over = new TextureRegionDrawable(texHome[1]);
        homebtnstyle.down = new TextureRegionDrawable(texHome[2]);
        homebtnstyle.disabled = new TextureRegionDrawable(texHome[3]);
        homebtnstyle.font = font_bangers;

        Label.LabelStyle lblstyle = new Label.LabelStyle();
        lblstyle.font = font_bangers;
        //lblstyle.font.setColor(Color.RED);

        label = new Label("Please enter your nickname",lblstyle);
        label.getStyle().fontColor = Color.DARK_GRAY;
        label.setSize(2*Constants.VIEWPORT_WIDTH/3,Constants.VIEWPORT_HEIGHT/4);

        label1 = new Label("",lblstyle);
        label1.getStyle().fontColor = Color.DARK_GRAY;
        label1.setSize(2*Constants.VIEWPORT_WIDTH/3,Constants.VIEWPORT_HEIGHT/4);

        label2 = new Label("",lblstyle);
        label2.setPosition(400,420);
        label2.getStyle().fontColor = Color.DARK_GRAY;


        txt_name = new TextField("Name here",skin);
        txt_name.setSize(450f, 2*Constants.VIEWPORT_HEIGHT/15);
        txt_name.setAlignment(Align.center);

        btn_join = new TextButton("Join Server", txtbtnstyle);
        btn_join.setSize(450f,140f);

        btn_exit = new TextButton("",homebtnstyle);
        btn_exit.setSize(100f,100f);

        btn_findserver = new TextButton("FIND A GAME",txtbtnstyle);
        btn_findserver.setSize(450f,140f);

    }

    @Override
    public void render(float delta) {

        Gdx.gl20.glClearColor(0,0,0,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.camera.update();
        app.viewport.apply();
        app.batch.setProjectionMatrix(app.camera.combined);


        //updates the information of number of online players simultaneously
        if(gameClient.client.isConnected()) {

                //System.out.println("online player : " + gameClient.getPlayerCountInfo().totalPlayerCount());
                label2.setText("Online Players : " + gameClient.totalPlayerInfo);
        }

        app.batch.begin();
        app.batch.draw(wallpaper,0,0);
        app.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        app.viewport.update(width, height,true);
        app.batch.setProjectionMatrix(app.camera.combined);
        app.viewport.apply();

        stage.getViewport().update(width , height);
        stage.getViewport().apply();

    }

    public Application getGame() {
        return app;
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
        System.out.println("Match Screen temizlendi.");
        stage.dispose();
        label1.clear();
        label1.remove();
        label.clear();
        label.remove();
        label2.clear();
        label2.remove();
        btn_findserver.clear();
        btn_findserver.remove();
        btn_join.remove();
        btn_exit.remove();
        //skin.dispose();
    }

    public MatchScreen(Application app, AssetHandler assetHandler, GameClient gameClient) {
       this.app = app;
       this.assetHandler = assetHandler;
       this.gameClient = gameClient;

       gameClient.setCurrentScreen(this);
       //gameClient.updateCurrentScreen();
    }
}
