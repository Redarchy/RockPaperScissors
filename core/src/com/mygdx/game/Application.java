package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.multiplayer.server.GameServer;
import com.mygdx.game.multiplayer.server.GameClient;
import com.mygdx.game.multiplayer.server.Packets;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.MatchScreen;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.utils.AssetHandler;
import com.mygdx.game.utils.Constants;

import java.io.IOException;


public class Application extends Game {


	public OrthographicCamera camera;
	public Viewport viewport;
	public SpriteBatch batch;
	public AssetHandler assetManager;

	public static final boolean ADMIN = true; //set false when getting output for clients, set true for admin output.

	public GameServer gameServer;
	public GameClient gameClient;

	private boolean exitToMenuFlag;
	private boolean startGameFlag;
	private Packets.GameSetup startGameInfo;


	@Override
	public void create() {

		gameClient = new GameClient();

		if(ADMIN) {
			try {
				gameServer = new GameServer();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		assetManager = new AssetHandler();

		batch = new SpriteBatch();
		//aspectratio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.setToOrtho(false);
		viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT,camera);
		camera.update();
		viewport.apply();


		this.setScreen(new LoadingScreen(this,assetManager,gameClient));
		//this.setScreen(new PlayScreen(this,assetManager,gameClient,new Packets.GameSetup()));

	}

	@Override
	public void render() {

		/**
		*Screen transitions due to the state of change in game
		*must be handled inside the game's thread.
		* Otherwise, there will be an error of synchronization of OpenGL thread.
		 */
		super.render();

		if(startGameFlag) {
			setGameScreen(startGameInfo);
			startGameFlag = false;
		}

		if(exitToMenuFlag) {
			gameClient.client.stop();
			setScreen(new MenuScreen(this,assetManager,gameClient));
			exitToMenuFlag = false;
		}
	}

	public void setGameScreen(Packets.GameSetup startGameInfo) {
		((MatchScreen) gameClient.getCurrentScreen()).dispose();
		setScreen(new PlayScreen(this,assetManager,gameClient, startGameInfo));


	}

	public void setGameInfo(GameClient client, Packets.GameSetup setupInfo) {
		startGameFlag = true;
		startGameInfo = setupInfo;
	}

	public void exitToMenu() {
		exitToMenuFlag = true;
	}
}
