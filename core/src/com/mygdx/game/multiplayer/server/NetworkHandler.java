package com.mygdx.game.multiplayer.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.EndPoint;

import com.mygdx.game.multiplayer.instances.GameChoice;
import com.mygdx.game.multiplayer.instances.ClientState;

import java.util.HashMap;
import java.util.Map;

public class NetworkHandler {

    public static final int PORT = 25565;
    public static final String IPADDRESS = "46.1.17.34";

    public static void register(EndPoint endPoint) {

        Kryo kryo = endPoint.getKryo();

        kryo.register(Map.class);
        kryo.register(HashMap.class);
        kryo.register(Packets.NumberofPlayers.class);
        kryo.register(Packets.RegisterName.class);
        kryo.register(Packets.RegisterNameResponse.class);
        kryo.register(Packets.RegisterNameResponse.ResponseType.class);
        kryo.register(Packets.PlayerCountRequest.class);
        kryo.register(Packets.PlayerCount.class);
        kryo.register(Packets.MatchmakeRequest.class);
        kryo.register(Packets.GameSetup.class);
        kryo.register(GameChoice.class);
        kryo.register(ClientState.class);
        kryo.register(Packets.ChoiceMade.class);
        kryo.register(Packets.RoundResult.class);
        kryo.register(Packets.OpponentChosen.class);
        kryo.register(Packets.GameEndDisconnect.class);

    }

}
