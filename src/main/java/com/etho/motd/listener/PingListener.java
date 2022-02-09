package com.etho.motd.listener;

import com.etho.motd.Motd;
import com.etho.motd.utils.Chat;
import com.etho.motd.utils.Config;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

public class PingListener {

    @Subscribe
    public EventTask onProxyPing(ProxyPingEvent e) {
        return EventTask.async(() -> this.handle(e));
    }

    public void handle(ProxyPingEvent e) {
        ServerPing.Builder pong = e.getPing().asBuilder();
        FileConfiguration c = Config.DEFAULT;
        String key = "general.";
        if (c.getInt(key + "max-players") > 0) {
            pong.maximumPlayers(c.getInt(key + "max-players"));
        }
        if (c.getString(key + "max-players") != null) pong.maximumPlayers(c.getInt(key + "max-players"));
        if (c.getString(key + "online-players") != null) {
            pong.onlinePlayers(c.getInt(key + "online-players"));
        } else {
            pong.onlinePlayers(Motd.server().getAllPlayers().size());
        }
        if (c.getString(key + "version.protocol") != null &&
                c.getString(key + "version.name") != null) {
            pong.version(new ServerPing.Version(c.getInt(key + "version.protocol"),
                    c.getString(key + "version.name")));
        }
        if (c.getStringList(key + "hover") != null) {
            for (String s : c.getStringList(key + "hover")) {
                pong.samplePlayers(new ServerPing.SamplePlayer(
                        Chat.getSerializer().serialize(Chat.color(s)),
                        UUID.randomUUID()
                ));
            }
        }
        Random ran = new Random();
        int min = 1;
        int max = c.getConfigurationSection(key + "motd").getKeys(false).size();
        int rand = ran.nextInt((max - min) + 1) + min;
        key = key + "motd." + rand + ".";
        String icon = c.getString(key + "icon");
        if (icon != null) {
            try {
                pong.favicon(Favicon.create(Paths.get(Motd.getDataFolder() + "/icons/" + icon)));
            } catch (IOException ex) {
                Motd.logger().warn("Unable to parse " + icon + " as a server Favicon.");
            }
        }
        String line1 = c.getString(key + "line-1");
        String line2 = c.getString(key + "line-2");
        boolean line1Center = c.getBoolean(key + "center-1");
        boolean line2Center = c.getBoolean(key + "center-2");
        if (line1Center) {
            line1 = Chat.centerText(line1, 123);
        }
        if (line2Center) {
            line2 = Chat.centerText(line2, 123);
        }
        pong.description(Chat.color(line1 + "\n" + line2));
        e.setPing(pong.build());
    }
}