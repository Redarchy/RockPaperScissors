package com.mygdx.game.multiplayer.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class GameServerListener extends Listener {

    public GameServer gameServer;

    public GameServerListener(GameServer server) {
        //super();
        this.gameServer = server;
    }

    @Override
    public void received(Connection connection, Object o) {

        if(o instanceof Packets.RegisterName) {
            System.out.println("[SERVERLISTENER] : a register name request had arrived.");
            gameServer.registerName(connection, ((Packets.RegisterName)o).name);
        }
        else if(o instanceof Packets.PlayerCountRequest) {
            System.out.println("[SERVERLISTENER] : a player count request had arrived.");

            //bozuktan sonra burayı açtım
            gameServer.refreshPlayerCount();
            System.out.println("[SERVERLISTENER] : Player Count data updated.");

            /*
            Packets.PlayerCount pCount = new Packets.PlayerCount();
            pCount.playerCount = gameServer.getPlayerCount();
            */
            Packets.NumberofPlayers pCount = new Packets.NumberofPlayers();
            pCount.setPlayers(gameServer.getTotalPlayerCount());
            System.out.println("[SERVERLISTENER] : Player Count data sending.");
            connection.sendTCP(pCount);
            System.out.println("[SERVERLISTENER] : Player Count data has sent.");

        }
        else if(o instanceof Packets.MatchmakeRequest) {
            gameServer.queueClientMatchmaking(connection);
        }
        else if(o instanceof Packets.ChoiceMade) {
            gameServer.makeChoiceInGame(connection, (Packets.ChoiceMade)o);
        }
    }

    @Override
    public void connected(Connection connection) {
        gameServer.addClient(connection);
        Log.info("Client connected with ID: " + connection.getID());
    }

    @Override
    public void disconnected(Connection connection) {
        gameServer.removeClient(connection);
        Log.info("Removed client with ID: " + connection.getID());
    }
}
