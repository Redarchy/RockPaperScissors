package com.mygdx.game.multiplayer.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.multiplayer.instances.ClientState;

public class RemoteClient {

    private Connection connection;
    private String name;
    private ClientState clientState;
    private int currentGameID; //the current game ID of this client


    public RemoteClient(Connection connection) {
        this.connection = connection;
        clientState = ClientState.NAMELESS;
        currentGameID = -1;
    }

    public void setClientState(ClientState state) {
        this.clientState = state;
    }

    public void setName(String name) {
        if(clientState == ClientState.NAMELESS) {
            this.name = name;
            clientState = ClientState.IDLE;
        } else {
            Log.error("Tried to set a name for a client that already has one!");
        }
    }

    public void setGameID(int ID) {
        this.currentGameID = ID;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getPlayerName() {
        return name;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public int getGameID() {
        return currentGameID;
    }

}
