package com.etho.motd.utils;

import com.etho.motd.Motd;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static YamlConfiguration DEFAULT;
    public static String DEFAULT_PATH = Motd.getDataFolder() + "/config.yml";

    public static void init() {
        DEFAULT = get(DEFAULT_PATH, "config.yml");
    }

    public static void save() throws IOException {

    }

    public static YamlConfiguration get(String path) {
        return get(path, null);
    }

    public static YamlConfiguration get(String path, String def) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (def != null) {
                    Motd.get().saveResource(def, false);
                } else {
                    file.createNewFile();
                }
                Motd.logger().info("Created " + path);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml;
    }
}