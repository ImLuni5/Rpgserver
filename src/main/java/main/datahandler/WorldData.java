package main.datahandler;

import main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class WorldData {
    public enum WorldType { LOBBY, SURVIVING, RPG, MINIGAME, NOT_SET }
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

    public static @Nullable WorldType getworldType(String worldName) {
        String data = worldData.getString("world." + worldName + ".type");
        if (data == null) return null;
        return WorldType.valueOf(data);
    }

    public static void setworldType(String worldName, @NotNull WorldType worldType) {
        worldData.set("world." + worldName + ".type", worldType.name());
        saveData();
    }
}
