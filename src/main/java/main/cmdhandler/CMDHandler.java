package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.recipehandler.Recipe;
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
            case "compass", "나침반" -> CompassHandler.onCommand(commandSender, strings);
            case "recipe", "레시피" -> RecipeHandler.onCommand(commandSender, strings);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        Player player = (Player) commandSender;
        if (strings.length == 1) {
            switch (s) {
                case "레시피", "recipe" -> {
                    return Arrays.asList("리로드", "목록", "보기", "수정", "제거", "추가");
                }
                case "나침반", "compass" -> {
                    return List.of("track");
                }
                case "파티" -> {
                    return Arrays.asList("생성", "해산", "초대", "수락", "거절", "파티장위임", "목록", "강퇴", "채팅", "나가기");
                }
                case "친구" -> {
                    return Arrays.asList("추가", "수락", "거절", "삭제", "목록", "차단", "차단목록");
                }
                case "귓속말", "귓말", "귓", "tell", "msg", "w" -> {
                    if (strings[0].isEmpty()) {
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
                        return Arrays.asList("보내기", "주기", "뺏기");
                    }
                    return List.of("보내기");
                }
            }
        } else if (strings.length == 2) {
            switch (s) {
                case "레시피", "recipe" -> {
                    switch (strings[0]) {
                        case "추가", "목록" -> {
                            return List.of();
                        } case "제거", "보기", "수정" -> {
                            return Recipe.recipeData.getStringList("Recipes.recipeKeys");
                        }
                    }
                }
                case "나침반", "compass" -> {
                    List<String> list = new ArrayList<>(List.of("clear"));
                    for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
                    return list;
                }
                case "친구" -> {
                    if (strings[0].equals("차단")) {
                        return Arrays.asList("추가", "해제");
                    } else if (strings[0].equals("삭제")) {
                        List<String> friendList = new ArrayList<>();
                        for (String uuid : FriendData.getPlayerFriendList(player.getUniqueId()))
                            friendList.add(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))).getName());
                        if (friendList.isEmpty()) return List.of();
                        return friendList;
                    }
                }
                case "귓속말", "귓말", "귓", "tell", "msg", "w" -> {
                    if (strings[0].equals("설정")) {
                        return Arrays.asList("모두에게", "친구에게", "받지않음");
                    }
                }
            }
        } else if (strings.length == 3) {
            if ((s.equals("레시피") || s.equals("recipe")) && strings[0].equals("추가")) return List.of();
        }
        return null;
    }
}

