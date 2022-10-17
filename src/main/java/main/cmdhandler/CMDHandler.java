package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.timerhandler.CMDCooldownTimer;
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
        if (CMDCooldownTimer.getCMDClickStack().containsKey((Player) commandSender)) {
            for (Map.Entry<Player, Integer> e : CMDCooldownTimer.getCMDClickStack().entrySet()) {
               if (e.getKey().equals(commandSender) && e.getValue() >= 4) {
                   if (e.getValue() < 5) e.setValue(e.getValue()+1);
                   commandSender.sendMessage(Main.INDEX + "§c커맨드 사용이 너무 빠릅니다. 잠시 후에 다시 시도해주세요.");
                   return true;
               } else {
                   e.setValue(e.getValue()+1);
                   break;
               }
            }
        } else CMDCooldownTimer.getCMDClickStack().put((Player) commandSender, 1);
        switch (s) {
            case "파티" -> PartyHandler.onCommand(commandSender, strings);
            case "친구" -> FriendHandler.onCommand(commandSender, strings);
            case "귓속말", "귓말", "tell", "귓", "w", "msg" -> DMHandler.onCommand(commandSender, strings);
            case "돈" -> MoneyHandler.onCommand(commandSender, strings);
            case "설정", "settings" -> SettingsHandler.onCommand(commandSender);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        Player player = (Player) commandSender;
        if (strings.length == 1) {
            switch (s) {
                case "파티" -> {
                    if (strings[0].contains("생")) return List.of("생성");
                    else if (strings[0].contains("해")) return List.of("해산");
                    else if (strings[0].contains("초")) return List.of("초대");
                    else if (strings[0].contains("수")) return List.of("수락");
                    else if (strings[0].contains("거")) return List.of("거절");
                    else if (strings[0].contains("파")) return List.of("파티장위임");
                    else if (strings[0].contains("목")) return List.of("목록");
                    else if (strings[0].contains("강")) return List.of("강퇴");
                    else if (strings[0].contains("채")) return List.of("채팅");
                    else if (strings[0].contains("나")) return List.of("나가기");
                    return Arrays.asList("생성", "해산", "초대", "수락", "거절", "파티장위임", "목록", "강퇴", "채팅", "나가기");
                }
                case "친구" -> {
                    if (strings[0].contains("추")) return List.of("추가");
                    else if (strings[0].contains("수")) return List.of("수락");
                    else if (strings[0].contains("거")) return List.of("거절");
                    else if (strings[0].contains("삭")) return List.of("삭제");
                    else if (strings[0].contains("목")) return List.of("목록");
                    else if (strings[0].contains("차")) {
                        if (strings[0].contains("목")) return List.of("차단목록");
                        return Arrays.asList("차단", "차단목록");
                    }
                    return Arrays.asList("추가", "수락", "거절", "삭제", "목록", "차단", "차단목록");
                }
                case "귓속말", "귓말", "귓", "tell", "msg", "w" -> {
                    if (strings[0].contains("설")) {
                        return List.of("설정");
                    } else if (strings[0].isEmpty()) {
                        List<String> dmList = new ArrayList<>();
                        dmList.add("설정");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            dmList.add(p.getName());
                        }
                        return dmList;
                    }
                    return null;
                }
                case "돈" -> {
                    if (player.isOp()) {
                        if (strings[0].contains("보")) return List.of("보내기");
                        else if (strings[0].contains("주")) return List.of("주기");
                        else if (strings[0].contains("뺏")) return List.of("뺏기");
                        return Arrays.asList("보내기", "주기", "뺏기");
                    }
                    return List.of("보내기");
                }
            }
        } else if (strings.length == 2) {
            if (s.equals("친구")) {
                if (strings[0].equals("차단")) {
                    if (strings[1].contains("추")) return List.of("추가");
                    else if (strings[1].contains("해")) return List.of("해제");
                    return Arrays.asList("추가", "해제");
                }
                else if (strings[0].equals("삭제")) {
                    List<String> friendList = new ArrayList<>();
                    for (String uuid : FriendData.getPlayerFriendList(player.getUniqueId()))
                        friendList.add(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))).getName());
                    if (List.of(friendList) == null) return null;
                    return friendList;
                }
            } else if (s.equals("귓속말") || s.equals("귓말") || s.equals("귓") || s.equals("tell") || s.equals("msg") || s.equals("w")) {
                if (strings[0].equals("설정")) {
                    if (strings[1].contains("모")) {
                        return List.of("모두에게");
                    } else if (strings[1].contains("친")) {
                        return List.of("친구에게");
                    } else if (strings[1].contains("받")) {
                        return List.of("받지않음");
                    }
                    return Arrays.asList("모두에게", "친구에게", "받지않음");
                }
            }
        }
        return null;
    }
}

