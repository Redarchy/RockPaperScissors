package com.mygdx.game.multiplayer.server;

import com.badlogic.gdx.Screen;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.multiplayer.instances.ClientState;
import com.mygdx.game.multiplayer.instances.GameChoice;
import com.mygdx.game.screens.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used as a bridge between Kryonet and Libgdx
 * Allows the server to influence what appears onscreen
 * Allows the client to send data to the server when required
 */
public class GameClient {
    public Client client;
    private Screen currentScreen;
    private String name;
    private Connection serverConnection;
    private Packets.PlayerCount playerCountInfo;
    private GameClientListener listener;
    public int totalPlayerInfo = 0;

    /**
     * Creates a new GameClient and registers it with Kryonet
     * Also attaches a ClientListener to it
     */
    public GameClient() {
        //Create the client and start running it in another thread
        client = new Client();

        //Register classes with Kryonet
        NetworkHandler.register(client);

        //Add the event listener to the client thread
        listener = new GameClientListener(this);
        client.addListener(listener);
    }

    /**
     * Attempts to connect to a server with the given name and IP address
     * @param inputIP The user input IP
     * @param inputName The user input name
     */
    public void attemptConnection(String inputIP, String inputName)
    {

        try {
            client.start();
            name = inputName;
            client.connect(5000, inputIP, NetworkHandler.PORT);
        }
        catch (IOException ex) {
            updateCurrentScreen();
            displayErrorMessage("Could not find server...");
        }

        /*new Thread("Connect")
        {
            public void run()
            {
                //Attempt to connect to the server
                try
                {
                    client.start();
                    name = inputName;
                    client.connect(5000, inputIP, NetworkHandler.PORT);
                }
                catch (IOException ex)
                {
                    updateCurrentScreen();
                    displayErrorMessage("Could not find server...");
                }
            }
        }.start();*/
    }

    /**
     * Used to disconnect from the server manually
     * @param reason The reason for disconnecting
     */
    public void abortConnection(String reason) {
        if(client.isConnected()) {
            client.stop();
        }

        displayErrorMessage(reason);
        updateCurrentScreen();
    }

    public void requestPlayerCount() {
        if(serverConnection != null) {
            Packets.PlayerCountRequest req = new Packets.PlayerCountRequest();

            //serverConnection.sendTCP(req); //sorun burda
            client.sendTCP(req);
        } else {
            Log.error("Tried to contact server when there was no server connection!");
        }
    }

    public void requestMatchmake() {
        if(serverConnection != null) {
            serverConnection.sendTCP(new Packets.MatchmakeRequest());
        } else {
            Log.error("Tried to contact server when there was no server connection!");
        }
    }

    /**
     * Used to move to the game screen when the server has found a match for a player
     * @param setupInfo Information about the match from the server
     */
    public void startGame(Packets.GameSetup setupInfo)
    {
        ((MatchScreen)currentScreen).getGame().setGameInfo(this,setupInfo);
        //((MatchScreen)currentScreen).getGame().setGameInfo(setupInfo);
    }

    /**
     * Used to make a choice in the current game
     * @param choice The choice to make
     */
    public void makeChoice(GameChoice choice)
    {
        if(!(currentScreen instanceof PlayScreen))
            return;

        //Prepare the packet to send to the server
        Packets.ChoiceMade choiceMade = new Packets.ChoiceMade();
        choiceMade.gameID = ((PlayScreen)currentScreen).getGameID();
        choiceMade.choice = choice;

        //Send the packet to the server
        serverConnection.sendTCP(choiceMade);
    }

    /**
     * Gets the current player count information
     * @return The current player count information
     */
    public Packets.PlayerCount getPlayerCountInfo() { return playerCountInfo; }

    /**
     * Gets the name of this client
     * @return The name of this client
     */
    public String getPlayerName(){ return name; }

    /**
     * Gets the current screen
     * @return The current screen
     */
    public Screen getCurrentScreen()
    {
        return currentScreen;
    }

    /**
     * Sets the current screen to the passed in screen reference
     * @param screen The screen to set the current screen to
     */
    public void setCurrentScreen(Screen screen)//(NetScreen screen)
    {
        currentScreen = screen;
    }

    /**
     * Sets the server connection to the passed in reference
     * @param con The server set the connection of
     */
    public void setServerConnection(Connection con)
    {
        serverConnection = con;
        updateCurrentScreen();
    }

    /**
     * Sets the current player count info
     * Called when the server has updated the client about the current player count
     * @param info The current player count info
     */
    public void setPlayerCountInfo(Packets.PlayerCount info)
    {
        playerCountInfo = info;
        updateCurrentScreen();
    }

    /**
     * Updates the current game screen with information about the server connection and any errors
     */
    public void updateCurrentScreen()
    {
        Boolean connected = client.isConnected();

        //currentScreen.updateConnectionInfo(connected);
    }

    /**
     * Displays an error message to the current screen
     * @param message The error message to display
     */
    public void displayErrorMessage(String message)
    {
        //currentScreen.displayErrorMessage(message);
    }

}