package main.datahandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SettingsData {
    public static FileConfiguration settingsData;
    private static final File settings = new File("GameData/settingsData.yml");

    public static void loadData() {
        settingsData = YamlConfiguration.loadConfiguration(settings);
        try {
            if (!settings.exists()) {
                settingsData.save(settings);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData() {
        try {
            settingsData.save(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer getPlayerSettings(String Option, UUID player) {
        if (settingsData.getInt(Option + "." + player) == 0) return 1;
        return settingsData.getInt(Option + "." + player);
    }

    public static void setSettings(String Option, UUID player, Integer settings) {
        settingsData.set(Option + "." + player, settings);
        saveData();
    }
}
