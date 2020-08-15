package com.mygdx.game.multiplayer.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import static java.lang.Thread.sleep;

public class UpdateThread implements Runnable {

    public GameServer gameServer;

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                gameServer.refreshPlayerCount();
                gameServer.broadcastPlayerCount(); //bu amk çocuğu bozuk(tu)
                gameServer.attemptMatchmake();
                sleep(3000);
            }
            catch (Exception e)
            {
                System.out.println(e.getStackTrace());
            }
        }
    }



}
