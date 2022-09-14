package main.datahandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SettingsData {
    public static FileConfiguration settingsData;
    private static final File settings = new File("GameData/settingsData.yml");

    // 제 뇌로 파일 코드를 이해하지 못해서 일단 루니님꺼 배꼈습니다

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

    public static Integer getPlayerSettings(UUID player) {
        if (settingsData.getInt("dmOption." + player) == 0) return 1;
        return settingsData.getInt("dmOption." + player);
    }

    public static void setSettings(UUID player, Integer settings) {
        settingsData.set("dmOption." + player, settings);
        saveData();
    }
}
