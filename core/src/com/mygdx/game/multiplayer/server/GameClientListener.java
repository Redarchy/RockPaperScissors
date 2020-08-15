package com.mygdx.game.multiplayer.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.screens.*;

public class GameClientListener extends Listener {

    public GameClient gameClient;

    public GameClientListener(GameClient g) {
        super();

        gameClient = g;
    }

    public void connected(Connection connection) {
        Packets.RegisterName registerName = new Packets.RegisterName();
        registerName.name = gameClient.getPlayerName();
        connection.sendTCP(registerName);

        connection.sendTCP(new Packets.PlayerCountRequest());//bozuktan sonra açtım

        gameClient.setServerConnection(connection);
    }

    public void disconnected(Connection connection) {
        gameClient.updateCurrentScreen();
        gameClient.displayErrorMessage("You have disconnected!");
    }

    public void received(Connection connection, Object o) {
        if(gameClient.getCurrentScreen() instanceof MatchScreen) {
            if (o instanceof Packets.RegisterNameResponse) {
                //If the server didn't accept the name registration, then abort the connection
                Packets.RegisterNameResponse response = (Packets.RegisterNameResponse) o;

                switch (response.responseType) {
                    case ALREADY_HAS_NAME:
                        gameClient.abortConnection("You already have a name!");
                        break;
                    case NAME_EXISTS:
                        gameClient.abortConnection("Name taken by another player!");
                        break;
                }
            }
            if (o instanceof Packets.GameSetup) {

                gameClient.startGame((Packets.GameSetup) o);
            }

            if(o instanceof Packets.NumberofPlayers) {
                gameClient.totalPlayerInfo = ((Packets.NumberofPlayers) o).getPlayers();
            }
        }
        else if(gameClient.getCurrentScreen() instanceof PlayScreen) {
            if(o instanceof Packets.RoundResult) {
                ((PlayScreen)gameClient.getCurrentScreen()).startNextRound((Packets.RoundResult)o);
            }
            else if(o instanceof Packets.OpponentChosen) {
                ((PlayScreen)gameClient.getCurrentScreen()).opponentChosen();
            }
            else if(o instanceof Packets.GameEndDisconnect) {
                gameClient.displayErrorMessage("Opponent disconnected!");
            }
        }

    }

}
