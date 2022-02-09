package com.etho.motd;

import com.etho.motd.commands.MotdCommand;
import com.etho.motd.listener.PingListener;
import com.etho.motd.utils.Config;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Plugin(
        id = "motd",
        name = "VelocityMotd",
        version = "1.0",
        description = "Custom MOTDs for your velocity servers!",
        authors = {"MattMX", "1etho"}
)
public class Motd {

    private ProxyServer server;
    private Logger logger;
    private File dataFolder;
    static Motd instance;

    @Inject
    public Motd(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        instance = this;
        this.dataFolder = getDataFolder();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.init();
        server.getEventManager().register(this, new PingListener());
        server.getCommandManager().register("vmotd", new MotdCommand());
    }

    public static Motd get() {
        return instance;
    }

    public static File getDataFolder() {
        File dataFolder = instance.dataFolder;
        if (dataFolder == null) {
            String path = "plugins/vmotd/";
            try {
                dataFolder = new File(path);
                dataFolder.mkdir();
                File iconFolder = new File(path + "/icons/");
                iconFolder.mkdir();
                return dataFolder;
            } catch (Exception e) {
                return null;
            }
        } else {
            return dataFolder;
        }
    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        System.out.println(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(instance.dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(instance.dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {

            }
        } catch (IOException ex) {

        }
    }

    public InputStream getResource(@NotNull String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClass().getResource(filename);
            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public static Logger logger() {
        return instance.logger;
    }

    public static ProxyServer server() {
        return instance.server;
    }
}
