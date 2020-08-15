package com.mygdx.game.multiplayer.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.multiplayer.instances.ClientState;
import com.mygdx.game.multiplayer.instances.GameInstance;

import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    public Server server;
    public UpdateThread updateThread;
    private int gamesCreated; //all the games created so far.

    private Map<Connection, RemoteClient> remoteClients;
    private Map<ClientState, Integer> playerCount;
    private Map<Integer, GameInstance> activeGames;

    public GameServer() throws IOException {
        server = new Server();
        Log.debug("Server started...");

        NetworkHandler.register(server);
        server.addListener(new GameServerListener(this));

        try {
            server.bind(NetworkHandler.PORT);
        } catch (BindException e) {
            throw new BindException("Port " + NetworkHandler.PORT + " is already being used on this machine, perhaps you already have a server running.");
        }

        server.start();

        //Initialise remoteClients, state count and other variables
        remoteClients = new HashMap<>();
        playerCount = new HashMap<>();
        activeGames = new HashMap<>();
        refreshPlayerCount();
        gamesCreated = 0;

        //Start update thread
        updateThread = new UpdateThread();
        updateThread.gameServer = this;
        new Thread(updateThread).start();
    }

    public void addClient(Connection connection) {
        if(!remoteClients.containsKey(connection)) {
            remoteClients.put(connection, new RemoteClient(connection));
        }
        else {
            Log.info("Client with connection ID: " + connection.getID() + " is already connected!");
        }
    }

    public void removeClient(Connection connection) {
        if(remoteClients.containsKey(connection)) {
            RemoteClient clientToRemove = remoteClients.get(connection);
            if(clientToRemove.getClientState() == ClientState.INGAME) {
                //Check if the client being removed is in a game
                if(activeGames.containsKey(clientToRemove.getGameID()) && activeGames.get(clientToRemove.getGameID()).containsClient(clientToRemove)) {
                    //If the opponent is still connected, then send them to the main menu
                    RemoteClient opponent = activeGames.get(clientToRemove.getGameID()).getOpponent(clientToRemove);
                    if(opponent.getConnection().isConnected()) {
                        opponent.getConnection().sendTCP(new Packets.GameEndDisconnect());
                        opponent.setClientState(ClientState.IDLE);
                        opponent.setGameID(-1);
                    }

                    //Remove the game instance from the list of active games
                    gameFinished(clientToRemove.getGameID());
                }
            }

            //Finally remove the client from the list of active connections
            remoteClients.remove(connection);
        }
        else {
            Log.error("Connection with ID: " + connection.getID() + " is not in client list!");
        }
    }

    public void registerName(Connection connection, String name) {
        if(remoteClients.containsKey(connection)) {
            //Set the initial value of the response to be accepted
            Packets.RegisterNameResponse.ResponseType responseType = Packets.RegisterNameResponse.ResponseType.ACCEPTED;

            //Check if the client already has a name
            if(remoteClients.get(connection).getClientState() != ClientState.NAMELESS) {
                responseType = Packets.RegisterNameResponse.ResponseType.ALREADY_HAS_NAME;
            }
            else {
                //Check if the requested name exists in the list of remote clients
                for (RemoteClient client : remoteClients.values()) {
                    if (name.equals(client.getPlayerName())) {
                        responseType = Packets.RegisterNameResponse.ResponseType.NAME_EXISTS;
                    }
                }
            }

            //If the response is still accepted, then set the clients name
            if(responseType == Packets.RegisterNameResponse.ResponseType.ACCEPTED) {
                remoteClients.get(connection).setName(name);
                Log.info("Client " + connection.getID() + " has registered name: " + name);
            }

            //Create a response for the client
            Packets.RegisterNameResponse response = new Packets.RegisterNameResponse();
            response.requestedName = name;
            response.responseType = responseType;

            //Now send the response to the client
            connection.sendTCP(response);
        }
        else {
            Log.error("A remote client with the provided connection does not exist! ID: " + connection.getID());
        }
    }

    public void queueClientMatchmaking(Connection connection) {
        if(remoteClients.containsKey(connection)) {
            RemoteClient client = remoteClients.get(connection);

            //Only queue the client for matchmaking if they are idle
            if(client.getClientState() == ClientState.IDLE) {
                client.setClientState(ClientState.QUEUED);
                Log.info(remoteClients.get(connection).getPlayerName() + " has queued for matchmaking!");
            }
            else {
                Log.error("Tried to set state to QUEUED for client " + client.getPlayerName() + " with state: " + client.getClientState());
            }
        }
        else {
            Log.error("A remote client with the provided connection does not exist! ID: " + connection.getID());
        }
    }


    public void attemptMatchmake() {
        //Return if there aren't enough players on the server
        if(remoteClients.size() < 2)
            return;

        ArrayList<RemoteClient> players = new ArrayList<>();

        //Try and pair queued players with eachother
        for(RemoteClient client : remoteClients.values()) {
            if(client.getClientState() == ClientState.QUEUED) {
                players.add(client);
                if(players.size() == 2) {
                    //Create a GameSetup packet to send to each client
                    Packets.GameSetup gameSetup = new Packets.GameSetup();
                    gameSetup.gameID = gamesCreated;
                    gameSetup.scoreLimit = GameInstance.SCORE_LIMIT;

                    //Send the packet to the first player
                    gameSetup.playerName = players.get(0).getPlayerName();
                    gameSetup.opponentName = players.get(1).getPlayerName();
                    players.get(0).getConnection().sendTCP(gameSetup);

                    //Send the packet to the second player
                    gameSetup.playerName = players.get(1).getPlayerName();
                    gameSetup.opponentName = players.get(0).getPlayerName();
                    players.get(1).getConnection().sendTCP(gameSetup);

                    //Change the state for each player to be ingame
                    players.get(0).setClientState(ClientState.INGAME);
                    players.get(1).setClientState(ClientState.INGAME);

                    //Change the gameID for each player to the ID of the new game
                    players.get(0).setGameID(gamesCreated);
                    players.get(1).setGameID(gamesCreated);

                    //Create a game instance and add it to the list of all active game instances
                    GameInstance newGame = new GameInstance(this, gamesCreated, players.get(0), players.get(1));
                    activeGames.put(gamesCreated, newGame);

                    //Output matchup to log
                    Log.info("Game " + gamesCreated + ": " + players.get(0).getPlayerName() + " vs " + players.get(1).getPlayerName());
                    gamesCreated++;

                    //Clear the players list and carry on running, so multiple games can be created each function call
                    players = new ArrayList<>();
                }
            }
        }

    }

    public void makeChoiceInGame(Connection playerConnection, Packets.ChoiceMade choiceMade) {
        if(!remoteClients.containsKey(playerConnection)) {
            Log.error("Could not find the client to make the choice for");
            return;
        }

        if(!activeGames.containsKey(choiceMade.gameID)) {
            Log.error("Could not find the game to make the choice in");
            return;
        }

        RemoteClient clientMakingChoice = remoteClients.get(playerConnection);
        GameInstance gameBeingPlayed = activeGames.get(choiceMade.gameID);

        gameBeingPlayed.makeChoice(clientMakingChoice, choiceMade.choice);
    }

    public void refreshPlayerCount() {
        Map<ClientState, Integer> newPlayerCount = new HashMap<>();

        //Set the value for each state to be zero
        for(ClientState state : ClientState.values()) {
            newPlayerCount.put(state, 0);
        }

        //Now get the amount of players in each state
        for (RemoteClient r : remoteClients.values()) {
            newPlayerCount.put(r.getClientState(), newPlayerCount.get(r.getClientState()) + 1);
        }

        //Now assign the new count map to the old count map
        playerCount = newPlayerCount;
    }

    public void broadcastPlayerCount() {
        /*Packets.PlayerCount playerCount = new Packets.PlayerCount();
        playerCount.playerCount = getPlayerCount();
        for(Connection connection : remoteClients.keySet()) {
            connection.sendTCP(playerCount);
        }
         */

        Packets.NumberofPlayers pCount = new Packets.NumberofPlayers();
        pCount.setPlayers(getTotalPlayerCount());

        for(Connection connection : remoteClients.keySet()) {
            connection.sendTCP(pCount);
        }

    }

    public Map<ClientState, Integer> getPlayerCount() {
        return playerCount;
    }

    public int getPlayerCount(ClientState state) {
        return playerCount.get(state);
    }

    public void gameFinished(int id) {
        if(!activeGames.containsKey(id)) {
            Log.error("Tried to close a game that doesn't exist! ID: " + id);
            return;
        }

        Log.info("Closed game with ID" + id);
        activeGames.remove(id);
    }

    public int getTotalPlayerCount(){
        int sum = 0;
        for(int i : playerCount.values()) {
            sum = sum + i;
        }
        return sum;
    }


}
