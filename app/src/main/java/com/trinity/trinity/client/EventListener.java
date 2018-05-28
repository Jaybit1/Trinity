package com.trinity.trinity.client;

import com.jaybit.client.event.events.client.ClientStartupResultEvent;
import com.jaybit.client.event.events.connect.AuthenticationEvent;
import com.jaybit.client.event.events.disconnect.DisconnectEvent;
import com.jaybit.client.event.events.packet.CustomPacketReceiveEvent;
import com.jaybit.client.listener.ClientListener;
import com.jaybit.client.listener.EventHandler;
import com.trinity.trinity.R;
import com.trinity.trinity.packets.PTA_TrinitySpeak;

public class EventListener implements ClientListener{

    @EventHandler
    public void onResult(ClientStartupResultEvent e) {
        if (e.getResult() == ClientStartupResultEvent.ConnectionResult.FAILED) {
            System.out.println("Connection failed.");
            Trinity.log(Trinity.getString(R.string.event_connection_failed));
        }
    }

    @EventHandler
    public void onAuth(AuthenticationEvent e) {
        System.out.println("Connected!");
        Trinity.log(Trinity.getString(R.string.event_connect));
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent e) {
        System.out.println("Disconnected!");
        Trinity.log(Trinity.getString(R.string.event_disconnect));
    }

    @EventHandler
    public void onCustomEvent(CustomPacketReceiveEvent e){
        System.out.println("Received Packet: " + e.getPacket().getClass());
        if (e.getPacket().getClass().isAssignableFrom(PTA_TrinitySpeak.class)) {
            PTA_TrinitySpeak packet = (PTA_TrinitySpeak) e.getPacket();
            Trinity.log(packet.getText());
        }
    }

}
