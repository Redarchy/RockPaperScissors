package com.mygdx.game.multiplayer.instances;

import com.esotericsoftware.minlog.Log;
import com.mygdx.game.multiplayer.server.GameServer;
import com.mygdx.game.multiplayer.server.Packets;
import com.mygdx.game.multiplayer.server.RemoteClient;

public class GameInstance {

    private GameServer gameServer;
    private int gameID;
    private PlayerInstance player1,player2;

    public static final int SCORE_LIMIT = 3;

    public GameInstance(GameServer server, int ID, RemoteClient c1, RemoteClient c2) {
        gameServer = server;
        gameID = ID;
        player1 = new PlayerInstance(c1);
        player2 = new PlayerInstance(c2);
    }

    public void makeChoice(RemoteClient player, GameChoice choice)
    {
        if(player1.getGameClient() != player && player2.getGameClient() != player) {
            Log.error("Client with name: " + player.getPlayerName() + " tried to make a choice in a game they are not in!");
        }

        //Make the choice and alert the opposing player
        if(player1.getGameClient() == player)
        {
            player1.makeChoice(choice);
            player2.getGameClient().getConnection().sendTCP(new Packets.OpponentChosen());
        }
        else if(player2.getGameClient() == player)
        {
            player2.makeChoice(choice);
            player1.getGameClient().getConnection().sendTCP(new Packets.OpponentChosen());
        }

        if(player1.getChoice() != null && player2.getChoice() != null)
        {
            PlayerInstance winner = determineWinner();
            Boolean gameOver = false;

            if(winner != null)
            {
                winner.incrementScore();
                if(winner.getScore() == SCORE_LIMIT)
                    gameOver = true;
            }

            Packets.RoundResult roundResult = new Packets.RoundResult();
            roundResult.gameOver = gameOver;

            //Create a response for player 1
            roundResult.playerScore = player1.getScore();
            roundResult.opponentScore = player2.getScore();
            roundResult.playerChoice = player1.getChoice();
            roundResult.opponentChoice = player2.getChoice();
            if(winner == player1) {
                roundResult.winner = 1;
            }else if(winner == null) {
                roundResult.winner = 2;
            }
            else {
                roundResult.winner = 3;
            }
            //Send the response to player 1
            player1.getGameClient().getConnection().sendTCP(roundResult);

            //Create a response for player 2
            roundResult.playerScore = player2.getScore();
            roundResult.opponentScore = player1.getScore();
            roundResult.playerChoice = player2.getChoice();
            roundResult.opponentChoice = player1.getChoice();
            if(winner == player2) {
                roundResult.winner = 1;
            } else if(winner == null) {
                roundResult.winner = 2;
            } else {
                roundResult.winner = 3;
            }
            //Send the response to player 2
            player2.getGameClient().getConnection().sendTCP(roundResult);

            //Refresh both players choices
            player1.refreshChoice();
            player2.refreshChoice();

            //Remove this game from the servers list of active games if a player has won
            if(gameOver)
            {
                player1.getGameClient().setClientState(ClientState.IDLE);
                player2.getGameClient().setClientState(ClientState.IDLE);
                gameServer.gameFinished(gameID);
            }
        }
    }

    private PlayerInstance determineWinner() {
        if(player1.getChoice() == null || player2.getChoice() == null) {
            Log.error("Still waiting for both players to make a choice!");
        }

        if(player1.getChoice() == player2.getChoice()){return null;}

        if(player1.getChoice() == GameChoice.ROCK && player2.getChoice() == GameChoice.SCISSORS) {return player1;}

        if(player1.getChoice() == GameChoice.PAPER && player2.getChoice() == GameChoice.ROCK){return player1;}

        if(player1.getChoice() == GameChoice.SCISSORS && player2.getChoice() == GameChoice.PAPER){return player1;}

        return player2;

    }

    public boolean containsClient(RemoteClient client) {
        return (player1.getGameClient() == client) || (player2.getGameClient() == client);
    }

    public RemoteClient getOpponent(RemoteClient client) {
        if(player1.getGameClient() == client) {
            return player2.getGameClient();
        }
        else if(player2.getGameClient() == client) {
            return player1.getGameClient();
        }

        return null;
    }
}
