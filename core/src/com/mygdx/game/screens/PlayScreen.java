package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Application;
import com.mygdx.game.multiplayer.instances.GameChoice;
import com.mygdx.game.multiplayer.server.GameClient;
import com.mygdx.game.multiplayer.server.Packets;
import com.mygdx.game.utils.AssetHandler;
import com.mygdx.game.utils.Constants;

public class PlayScreen implements Screen {

    private Application app;
    private AssetHandler assetHandler;
    private GameClient gameClient;
    private Stage stage;

    private Texture choices,texUp,texOver,texDown,texLock,tHome,wallpaper;
    private Button[] choiceButtons;
    private Button btn_rock, btn_paper, btn_scissors;
    private Button.ButtonStyle btnStyles[];
    private ClickListener cl1,cl2,cl3;
    private Label p1,p2,score1,score2,warning;
    private Table t1,t2,t3,t4;
    private Skin skin;

    private Packets.RoundResult tempResult;
    public int gameID;
    public int scoreLimit;
    public String playerName;
    public String opponentName;

    public PlayScreen (Application app, AssetHandler assetHandler, GameClient gameClient, Packets.GameSetup setupInfo) {

        this.app = app;
        this.assetHandler = assetHandler;

        this.gameClient = gameClient;
        ((MatchScreen)gameClient.getCurrentScreen()).dispose();
        this.gameClient.setCurrentScreen(this);
        //this.gameClient.updateCurrentScreen();

        this.gameID = setupInfo.gameID;
        this.scoreLimit = setupInfo.scoreLimit;
        this.playerName = setupInfo.playerName;
        this.opponentName = setupInfo.opponentName;

    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT,new OrthographicCamera()),app.batch);
        skin = assetHandler.manager.get(assetHandler.skin);
        wallpaper = assetHandler.manager.get(assetHandler.wallpaper);

        createUIElements();
        setButtonListeners();

        Gdx.input.setInputProcessor(stage);
    }

    private void setButtonListeners() {

        btn_rock.setDisabled(false);
        btn_paper.setDisabled(false);
        btn_scissors.setDisabled(false);

        btn_rock.setStyle(btnStyles[0]);
        btn_paper.setStyle(btnStyles[1]);
        btn_scissors.setStyle(btnStyles[2]);

        btn_rock.addListener(cl1 = new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                warning.setText("You have chosen rock!");
                btn_rock.setDisabled(true);
                makeChoice(GameChoice.ROCK);
                removeButtonListeners();
            }
        });

        btn_paper.addListener(cl2 = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                warning.setText("You have chosen paper!");
                btn_paper.setDisabled(true);
                makeChoice(GameChoice.PAPER);
                removeButtonListeners();
            }
        });

        btn_scissors.addListener(cl3= new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                warning.setText("You have chosen scissors!");
                btn_scissors.setDisabled(true);
                makeChoice(GameChoice.SCISSORS);
                removeButtonListeners();
            }
        });

    }

    private void removeButtonListeners() {
        /*btn_rock.clearListeners();
        btn_paper.clearListeners();
        btn_scissors.clearListeners();*/

        btn_rock.removeListener(cl1);
        btn_paper.removeListener(cl2);
        btn_scissors.removeListener(cl3);

    }

    public void createUIElements() {

        BitmapFont font_bangers = assetHandler.manager.get(assetHandler.fontBangers, BitmapFont.class);

        Texture.TextureFilter min = Texture.TextureFilter.Linear;
        Texture.TextureFilter mag = Texture.TextureFilter.Linear;

        wallpaper = assetHandler.manager.get(assetHandler.wallpaper);
        choices = assetHandler.manager.get(assetHandler.choices);

        wallpaper.setFilter(min,mag);
        choices.setFilter(min,mag);


        TextureRegion[][] trButtons = new TextureRegion[3][4];

        for(int i = 0; i < 3; i++) {

            for(int j = 0; j < 4; j++ ) {
                trButtons[i][j] = new TextureRegion(choices,j*200,i*200,200,200);
            }
        }

        btnStyles = new Button.ButtonStyle[3];

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();

        //for rock choice
        btnStyle.up = new TextureRegionDrawable(trButtons[0][0]);
        btnStyle.over = new TextureRegionDrawable(trButtons[0][1]);
        btnStyle.down = new TextureRegionDrawable(trButtons[0][2]);
        btnStyle.disabled = new TextureRegionDrawable(trButtons[0][3]);
        btnStyles[0] = btnStyle;
        btn_rock = new Button();
        btn_rock.setStyle(btnStyle);

        //for paper choice
        btnStyle = new Button.ButtonStyle();
        btnStyle.up = new TextureRegionDrawable(trButtons[1][0]);
        btnStyle.over = new TextureRegionDrawable(trButtons[1][1]);
        btnStyle.down = new TextureRegionDrawable(trButtons[1][2]);
        btnStyle.disabled = new TextureRegionDrawable(trButtons[1][3]);
        btnStyles[1] = btnStyle;
        btn_paper = new Button();
        btn_paper.setStyle(btnStyle);

        //for scissors choice
        btnStyle = new Button.ButtonStyle();
        btnStyle.up = new TextureRegionDrawable(trButtons[2][0]);
        btnStyle.over = new TextureRegionDrawable(trButtons[2][1]);
        btnStyle.down = new TextureRegionDrawable(trButtons[2][2]);
        btnStyle.disabled = new TextureRegionDrawable(trButtons[2][3]);
        btnStyles[2] = btnStyle;
        btn_scissors = new Button();
        btn_scissors.setStyle(btnStyle);

        //setting up labels
        Label.LabelStyle lblStyle = new Label.LabelStyle();
        lblStyle.font = font_bangers;
        lblStyle.fontColor = Color.DARK_GRAY;

        p1 = new Label(playerName,lblStyle);
        p2 = new Label(opponentName,lblStyle);
        score1 = new Label("0", lblStyle);
        score2 = new Label("0",lblStyle);
        warning = new Label("Let's see your choice, wise person!",lblStyle);

        //setting up tables
        t1 = new Table();
        t1.setFillParent(true);
        t1.top().left();
        t1.padLeft(15f).padTop(15f);
        t1.add(p1);
        t1.row();
        t1.add(score1);

        t2 = new Table();
        t2.setFillParent(true);
        t2.padRight(15f).padTop(15f);
        t2.top().right();
        t2.add(p2);
        t2.row();
        t2.add(score2);

        t3 = new Table();
        t3.setFillParent(true);
        t3.defaults().pad(15f);
        t3.center();
        t3.add(btn_rock);
        t3.add(btn_paper);
        t3.add(btn_scissors);

        t4 = new Table();
        t4.setFillParent(true);
        t4.bottom();
        t4.add(warning);
        t4.padBottom(35f);
        t4.getColor().a = 0.5f; //sets its opacity

        stage.addActor(t1);
        stage.addActor(t2);
        stage.addActor(t3);
        stage.addActor(t4);
    }


    @Override
    public void render(float delta) {

        Gdx.gl20.glClearColor(0,0,0,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.camera.update();
        app.viewport.apply();
        app.batch.setProjectionMatrix(app.camera.combined);

        app.batch.begin();
        app.batch.draw(wallpaper,0,0);
        app.batch.end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when both players have made their choice and the server has sent a responce
     * @param result The result of the current round
     */
    public void startNextRound(Packets.RoundResult result)
    {
        //Draw the players choice
        /*switch (result.playerChoice)
        {
            case ROCK:
                playerChoice = new Sprite(rockTex);
                break;
            case PAPER:
                playerChoice = new Sprite(paperTex);
                break;
            case SCISSORS:
                playerChoice = new Sprite(scissorsTex);
                break;
        }
        playerChoice.setPosition((Gdx.graphics.getWidth() / 4) - 64, (Gdx.graphics.getHeight() / 2) - 32);

        //Draw the opponents choice
        switch (result.opponentChoice)
        {
            case ROCK:
                opponentChoice = new Sprite(rockTex);
                break;
            case PAPER:
                opponentChoice = new Sprite(paperTex);
                break;
            case SCISSORS:
                opponentChoice = new Sprite(scissorsTex);
                break;
        }
        opponentChoice.setPosition(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 4) - 64, (Gdx.graphics.getHeight() / 2) - 32);
        */

        //Start the round finish animation
       /* roundFinishing = true;
        roundFinishingTimer = 0;
        tempResult = result;*/

        tempResult = result;

        if(!tempResult.gameOver) {
            if (tempResult.winner == 1) {
                warning.setText("You won, what a surprise!");
            } else if (tempResult.winner == 2) {
                warning.setText("Drew, not tasty but nasty!");
            } else {
                warning.setText("You lost, loser!");
            }
        } else {
            if (tempResult.winner == 1) {
                warning.setText("You have reached to an end successfully!");
            } else if (tempResult.winner == 3) {
                warning.setText("You are a big idiot with no talent!");
            }
        }

        score1.setText(tempResult.playerScore);
        score2.setText(tempResult.opponentScore);

        if(!tempResult.gameOver) {

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    warning.setText("Prepare for the next round!");
                }
            }, 3f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    warning.setText("Let's see your choice, wise person!");
                    nextRound();
                    setButtonListeners();
                }
            }, 5f);

        } else {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    warning.setText("Now, you are redirected to menu!");
                }
            }, 5f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    app.setScreen(new MenuScreen(app,assetHandler,gameClient));
                    gameClient.client.stop();
                }
            }, 7f);
        }
    }

    public void makeChoice(GameChoice choice)
    {
        gameClient.makeChoice(choice);
    }

    /**
     * Moves the game to the next round after the round finish animation has completed
     */
    public void nextRound()
    {
        //gameActors.endRound(tempResult);
        tempResult = null;
    }

    /**
     * Called when the opponent has chosen but the player hasn't
     */
    public void opponentChosen()
    {
        warning.setText("Opponent made their choice, hurry up!");
        //gameActors.setOpponentStatusText("Chosen", Color.GREEN);
    }



    @Override
    public void resize(int width, int height) {
        app.viewport.update(width, height,true);
        app.batch.setProjectionMatrix(app.camera.combined);
        app.viewport.apply();

        stage.getViewport().update(width , height);
        stage.getViewport().apply();
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
    }

    public Application getGame() {
        return this.app;
    }


    public int getGameID()
    {
        return gameID;
    }

}
