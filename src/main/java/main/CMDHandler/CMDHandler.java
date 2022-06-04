package main.CMDHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CMDHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (s.equals("파티")) PartyHandler.onCommand(commandSender, strings);
        else if (s.equals("친구")) FriendHandler.onCommand(commandSender, strings);

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 1) {
            if (s.equals("파티")) return Arrays.asList("생성", "해산", "초대", "수락", "거절", "파티장위임", "목록", "강퇴", "채팅", "나가기");
        }

        return null;
    }
}

