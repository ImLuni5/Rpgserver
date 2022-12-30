package main.datahandler;

import main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class PlayerData {

    public static FileConfiguration settingsData;
    private static final File player = new File("GameData/playerData.yml");

    public static void loadData() {
        settingsData = YamlConfiguration.loadConfiguration(player);
        try {
            if (!player.exists()) {
                settingsData.save(player);
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static void saveData() {
        try {
            settingsData.save(player);
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    /**
     * 현재 얻을 수 있는 가능한 설정: surviveCoin, dailyQuest, nameColor
     * @param data 얻을 데이터 이름
     * @param player 플레이어 UUID
     */
    public static @Nullable String getPlayerData(String data, UUID player) {
        return settingsData.getString(player + "." + data);
    }

    /**
     * 현재 바꿀 수 있는 가능한 설정: surviveCoin, dailyQuest, nameColor
     * @param data 설정할 데이터 이름
     * @param player 플레이어 UUID
     * @param settings 바꿀 설정
     */
    public static void setData(String data, UUID player, String settings) {
        settingsData.set(player + "." + data, settings);
        saveData();
    }

}
