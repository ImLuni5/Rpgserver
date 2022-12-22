package main.datahandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendData {
    public static FileConfiguration friendData;
    private static final File friend = new File("GameData/friendData.yml");

    public static void loadData() {
        friendData = YamlConfiguration.loadConfiguration(friend);
        try {
            if (!friend.exists()) {
                friendData.save(friend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData() {
        try {
            friendData.save(friend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static @NotNull List<String> getPlayerFriendList(UUID player) {
        if (friendData.getList("friend." + player) == null) return new ArrayList<>();
        return friendData.getStringList("friend." + player);
    }

    public static @NotNull List<String> getPlayerIgnoreList(UUID player) {
        if (friendData.getList("ignore." + player) == null) return new ArrayList<>();
        return friendData.getStringList("ignore." + player);
    }

    public static void addFriend(UUID player, UUID friend) {
        List<String> playerFriendList = getPlayerFriendList(player);
        List<String> friendFriendList = getPlayerFriendList(friend);
        playerFriendList.add(friend.toString());
        friendFriendList.add(player.toString());
        friendData.set("friend." + player, playerFriendList);
        friendData.set("friend." + friend, friendFriendList);
        saveData();
    }

    public static void removeFriend(UUID player, UUID friend) {
        List<String> playerFriendList = getPlayerFriendList(player);
        List<String> friendFriendList = getPlayerFriendList(friend);
        if (playerFriendList.contains(friend.toString())) {
            playerFriendList.remove(friend.toString());
            friendFriendList.remove(player.toString());
            friendData.set("friend." + player, playerFriendList);
            friendData.set("friend." + friend, friendFriendList);
            saveData();
        }
    }

    public static boolean addIgnore(UUID player, @NotNull UUID friend) {
        List<String> playerIgnoreList = getPlayerIgnoreList(player);
        if (!playerIgnoreList.contains(friend.toString())) {
            removeFriend(player, friend);
            playerIgnoreList.add(friend.toString());
            friendData.set("ignore." + player, playerIgnoreList);
            saveData();
            return true;
        } else return false;
    }

    public static boolean removeIgnore(UUID player, @NotNull UUID friend) {
        List<String> playerIgnoreList = getPlayerIgnoreList(player);
        if (playerIgnoreList.contains(friend.toString())) {
            playerIgnoreList.remove(friend.toString());
            friendData.set("friend." + player, playerIgnoreList);
            saveData();
            return true;
        } else return false;
    }
}
