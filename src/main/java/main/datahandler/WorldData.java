package main.datahandler;

import main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WorldData {
    public enum WorldType { LOBBY, SURVIVING, RPG, MINIGAME }
    public static FileConfiguration worldData;
    private static final File world = new File("GameData/worldData.yml");

    public static void loadData() {
        worldData = YamlConfiguration.loadConfiguration(world);
        try {
            if (!world.exists()) {
                worldData.save(world);
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static void saveData() {
        try {
            worldData.save(world);
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static @NotNull WorldType getworldType(String worldName) {
        return WorldType.valueOf(worldData.getString("worlds." + worldName + ".type"));
    }

    public static void setworldType(String worldName, @NotNull WorldType worldType) {
        worldData.set("worlds." + worldName + ".type", worldType.name());
        saveData();
    }
}
