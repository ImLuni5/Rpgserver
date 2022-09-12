package main.cmdhandler;

import main.datahandler.FriendData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CMDHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        switch (s) {
            case "파티" -> PartyHandler.onCommand(commandSender, strings);
            case "친구" -> FriendHandler.onCommand(commandSender, strings);
            case "귓속말", "귓말", "귓", "tell", "msg", "w" -> DMHandler.onCommand(commandSender, strings);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        Player player = (Player) commandSender;

        if (strings.length == 1) {
            if (s.equals("파티")) return Arrays.asList("생성", "해산", "초대", "수락", "거절", "파티장위임", "목록", "강퇴", "채팅", "나가기");
            else if (s.equals("친구")) return Arrays.asList("추가", "수락", "거절", "삭제", "목록", "차단", "차단목록");
        } else if (strings.length == 2) {
            if (s.equals("친구")) {
                if (strings[0].equals("차단")) return Arrays.asList("추가", "해제");
                else if (strings[0].equals("삭제")) {
                    List<String> friendList = new ArrayList<>();
                    for (UUID uuid : FriendData.getPlayerFriendList(player.getUniqueId()))
                        friendList.add(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
                    return friendList;
                }
            } else if (s.equals("귓속말") || s.equals("귓말") || s.equals("귓") || s.equals("tell") || s.equals("msg") || s.equals("w")) return List.of();
        }
        return null;
    }
}
