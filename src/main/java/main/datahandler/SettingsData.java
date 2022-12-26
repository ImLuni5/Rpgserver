package main.datahandler;

import main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class SettingsData {
    public enum DmOption { ALL, FRIENDS, NEVER }
    public enum FriendOption { ALL, PARTY, NEVER }
    public enum PartyOption { ALL, FRIENDS, NEVER }
    public enum JoinMsgOption { ALL, FRIENDS, NEVER }
    public static FileConfiguration settingsData;
    private static final File settings = new File("GameData/settingsData.yml");

    public static void loadData() {
        settingsData = YamlConfiguration.loadConfiguration(settings);
        try {
            if (!settings.exists()) {
                settingsData.save(settings);
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static void saveData() {
        try {
            settingsData.save(settings);
        } catch (Exception e) {
            Main.printException(e);
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
        try {
            switch (Option) {
                case "dm" -> {
                    if (DmOption.valueOf(getSettings("dm", uuid)) == DmOption.FRIENDS) {
                        setSettings("dm", uuid, DmOption.NEVER.name());
                    } else if (DmOption.valueOf(getSettings("dm", uuid)) == DmOption.NEVER) {
                        setSettings("dm", uuid, DmOption.ALL.name());
                    } else {
                        setSettings("dm", uuid, DmOption.FRIENDS.name());
                    }
                }
                case "friend" -> {
                    if (FriendOption.valueOf(getSettings("friend", uuid)) == FriendOption.PARTY) {
                        setSettings("friend", uuid, FriendOption.NEVER.name());
                    } else if (FriendOption.valueOf(getSettings("friend", uuid)) == FriendOption.NEVER) {
                        setSettings("friend", uuid, FriendOption.ALL.name());
                    } else {
                        setSettings("friend", uuid, FriendOption.PARTY.name());
                    }
                }
                case "party" -> {
                    if (PartyOption.valueOf(getSettings("party", uuid)) == PartyOption.FRIENDS) {
                        setSettings("party", uuid, PartyOption.NEVER.name());
                    } else if (PartyOption.valueOf(getSettings("party", uuid)) == PartyOption.NEVER) {
                        setSettings("party", uuid, PartyOption.ALL.name());
                    } else {
                        setSettings("party", uuid, PartyOption.FRIENDS.name());
                    }
                }
                case "joinMsg" -> {
                    if (JoinMsgOption.valueOf(getSettings("joinMsg", uuid)) == JoinMsgOption.FRIENDS) {
                        setSettings("joinMsg", uuid, JoinMsgOption.NEVER.name());
                    } else if (JoinMsgOption.valueOf(getSettings("joinMsg", uuid)) == JoinMsgOption.NEVER) {
                        setSettings("joinMsg", uuid, JoinMsgOption.ALL.name());
                    } else {
                        setSettings("joinMsg", uuid, JoinMsgOption.FRIENDS.name());
                    }
                }
            }
        } catch (Exception exception) {
            Main.printException(exception);
        }
    }
}
