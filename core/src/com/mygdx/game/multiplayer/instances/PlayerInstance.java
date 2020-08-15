package com.mygdx.game.multiplayer.instances;

import com.mygdx.game.multiplayer.server.RemoteClient;
import com.mygdx.game.multiplayer.server.GameServer;

public class PlayerInstance {

    private RemoteClient remoteClient;
    private int score = 0;
    private GameChoice choice;

    public PlayerInstance(RemoteClient client) {
        remoteClient = client;
    }

    public void incrementScore() {
        score++;
    }

    public void makeChoice(GameChoice choice) {
        this.choice = choice;
    }

    public void refreshChoice() {
        choice = null;
    }

    public RemoteClient getGameClient() {
        return remoteClient;
    }

    public int getScore() {
        return score;
    }

    public GameChoice getChoice() {
        return this.choice;
    }

}
