package main.datahandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    public static List<UUID> getPlayerFriendList(UUID player) {
        return (List<UUID>) friendData.getList("friend." + player);
    }

    public static List<UUID> getPlayerIgnoreList(UUID player) {
        return (List<UUID>) friendData.getList("ignore." + player);
    }

    public static void addFriend(UUID player, UUID friend) {
        List<UUID> playerFriendList = getPlayerFriendList(player);
        List<UUID> friendFriendList = getPlayerFriendList(friend);
        playerFriendList.add(friend);
        friendFriendList.add(player);
        friendData.set("friend." + player, playerFriendList);
        friendData.set("friend." + friend, friendFriendList);
        saveData();
    }

    public static boolean removeFriend(UUID player, UUID friend) {
        List<UUID> playerFriendList = getPlayerFriendList(player);
        List<UUID> friendFriendList = getPlayerFriendList(friend);
        if (playerFriendList.contains(friend)) {
            playerFriendList.remove(friend);
            friendFriendList.remove(player);
            friendData.set("friend." + player, playerFriendList);
            friendData.set("friend." + friend, friendFriendList);
            saveData();
            return true;
        } else return false;
    }

    public static boolean addIgnore(UUID player, UUID friend) {
        List<UUID> playerIgnoreList = getPlayerIgnoreList(player);
        if (!playerIgnoreList.contains(friend)) {
            removeFriend(player, friend);
            playerIgnoreList.add(friend);
            friendData.set("ignore." + player, playerIgnoreList);
            saveData();
            return true;
        } else return false;
    }

    public static boolean removeIgnore(UUID player, UUID friend) {
        List<UUID> playerIgnoreList = getPlayerIgnoreList(player);
        if (playerIgnoreList.contains(friend)) {
            playerIgnoreList.remove(friend);
            friendData.set("friend." + player, playerIgnoreList);
            saveData();
            return true;
        } else return false;
    }
}
