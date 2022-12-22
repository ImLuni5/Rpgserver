package main.datahandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SettingsData {
    public enum dmOption { ALL, FRIENDS, NEVER }
    public enum friendOption { ALL, PARTY, NEVER }
    public enum partyOption { ALL, FRIENDS, NEVER }
    public enum joinMsgOption { ALL, FRIENDS, NEVER }
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

    public static @Nullable String getSettings(String Option, UUID player) {
        return settingsData.getString(Option + "." + player);
    }

    public static void setSettings(String Option, UUID player, String settings) {
        settingsData.set(Option + "." + player, settings);
        saveData();
    }

    public static void nextSettings(@NotNull String Option, UUID uuid) {
        switch (Option) {
            case "dm" -> {
                if (dmOption.valueOf(getSettings("dm", uuid)) == dmOption.FRIENDS) {
                    setSettings("dm", uuid, dmOption.NEVER.name());
                } else if (dmOption.valueOf(getSettings("dm", uuid)) == dmOption.NEVER) {
                    setSettings("dm", uuid, dmOption.ALL.name());
                } else {
                    setSettings("dm", uuid, dmOption.FRIENDS.name());
                }
            }
            case "friend" -> {
                if (friendOption.valueOf(getSettings("friend", uuid)) == friendOption.PARTY) {
                    setSettings("friend", uuid, friendOption.NEVER.name());
                } else if (friendOption.valueOf(getSettings("friend", uuid)) == friendOption.NEVER) {
                    setSettings("friend", uuid, friendOption.ALL.name());
                } else {
                    setSettings("friend", uuid, friendOption.PARTY.name());
                }
            }
            case "party" -> {
                if (partyOption.valueOf(getSettings("party", uuid)) == partyOption.FRIENDS) {
                    setSettings("party", uuid, partyOption.NEVER.name());
                } else if (partyOption.valueOf(getSettings("party", uuid)) == partyOption.NEVER) {
                    setSettings("party", uuid, partyOption.ALL.name());
                } else {
                    setSettings("party", uuid, partyOption.FRIENDS.name());
                }
            }
            case "joinMsg" -> {
                if (joinMsgOption.valueOf(getSettings("joinMsg", uuid)) == joinMsgOption.FRIENDS) {
                    setSettings("joinMsg", uuid, joinMsgOption.NEVER.name());
                } else if (joinMsgOption.valueOf(getSettings("joinMsg", uuid)) == joinMsgOption.NEVER) {
                    setSettings("joinMsg", uuid, joinMsgOption.ALL.name());
                } else {
                    setSettings("joinMsg", uuid, joinMsgOption.FRIENDS.name());
                }
            }
        }
    }
}
