package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.datahandler.SettingsData.FriendOption;
import main.timerhandler.FriendRequestTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static main.cmdhandler.PartyHandler.getPlayerParty;

public class FriendHandler {
    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        try {
            if (commandSender instanceof Player player) {
                String noArguments = Main.INDEX + "/친구 추가 <플레이어> - 플레이어에게 친구 추가 요청을 보냅니다.\n" + Main.INDEX + "/친구 수락 - 친구 추가 요청을 수락합니다.\n" + Main.INDEX + "/친구 거절 - 친구 추가 요청을 거절합니다.\n" + Main.INDEX + "/친구 삭제 <플레이어> - 친구 목록에서 삭제합니다.\n" + Main.INDEX + "/친구 목록 - 친구 목록을 보여줍니다.\n" + Main.INDEX + "/친구 차단 추가 <플레이어> - 플레이어를 차단 합니다.\n" + Main.INDEX + "/친구 차단 해제 <플레이어> - 플레이어를 차단 해제 합니다.\n" + Main.INDEX + "/친구 차단목록 - 차단 목록을 보여줍니다.";
                String invaildPlayer = Main.INDEX + "§c존재하지 않는 플레이어입니다.";
                // 명령어 외에 아무 구문도 안쳤을때
                if (args.length == 0) {
                    player.sendMessage(noArguments);
                    return;
                }
                if (AdminHandler.isHiddenAdminTrying(player)) return;
                switch (args[0]) {
                    case "추가" -> {
                        if (args.length == 1) player.sendMessage(Main.INDEX + "§c사용법: /친구 추가 <플레이어>");
                        Player friend = Bukkit.getPlayer(args[1]);
                        if (friend == null) player.sendMessage(invaildPlayer);
                        else {
                            String friendSet = SettingsData.getSettings("friend", friend.getUniqueId());
                            FriendOption friendOption = FriendOption.valueOf(friendSet);
                            if (!Bukkit.getOnlinePlayers().contains(friend) || AdminHandler.isHiddenAdmin(friend))
                                player.sendMessage(Main.INDEX + "§c그 플레이어는 온라인이 아닙니다.");
                            else if (friend.equals(player))
                                player.sendMessage(Main.INDEX + "§c자기 자신에게 친구 요청을 보낼 수 없습니다.");
                            else if (FriendData.getPlayerFriendList(player.getUniqueId()).contains(friend.getUniqueId().toString()))
                                player.sendMessage(Main.INDEX + "§c이미 해당 플레이어와 친구입니다.");
                            else if (FriendRequestTimer.getPlayerInviteTime().containsKey(friend))
                                player.sendMessage(Main.INDEX + "§c누군가가 해당 플레이어에게 친구 요청을 보냈습니다.");
                            else if (friendOption == FriendOption.NEVER)
                                player.sendMessage(Main.INDEX + "§c해당 플레이어는 친구 요청을 받지 않도록 설정했습니다.");
                            else if (FriendData.getPlayerIgnoreList(player.getUniqueId()).contains(friend.getUniqueId().toString()) || FriendData.getPlayerIgnoreList(friend.getUniqueId()).contains(player.getUniqueId().toString()))
                                player.sendMessage(Main.INDEX + "§c해당 플레이어에게 친구 요청을 보낼 수 없습니다.");
                            else if (friendOption == FriendOption.PARTY) {
                                List<Player> playerList = PartyHandler.getParty().get(getPlayerParty().get(friend));
                                if (playerList == null)
                                    player.sendMessage(Main.INDEX + "§c해당 플레이어는 친구 요청을 파티원에게만 받도록 설정했습니다.");
                                else if (playerList.contains(player)) {
                                    request(player, friend);
                                } else player.sendMessage(Main.INDEX + "§c해당 플레이어는 친구 요청을 파티원에게만 받도록 설정했습니다.");
                            } else {
                                request(player, friend);
                            }
                        }
                    }
                    case "수락" -> {
                        if (FriendRequestTimer.getPlayerInviteTime().containsKey(player)) {
                            FriendData.addFriend(player.getUniqueId(), FriendRequestTimer.getPlayerInviteOwner().get(player).getUniqueId());
                            player.sendMessage(Main.INDEX + "당신은 이제 " + FriendRequestTimer.getPlayerInviteOwner().get(player).getName() + "님과 친구입니다.");
                            FriendRequestTimer.getPlayerInviteOwner().get(player).sendMessage(Main.INDEX + "당신은 이제 " + player.getName() + "님과 친구입니다.");
                            FriendRequestTimer.getPlayerInviteTime().remove(player);
                            FriendRequestTimer.getPlayerInviteOwner().remove(player);
                        } else player.sendMessage(Main.INDEX + "§c받은 친구 요청이 존재하지 않습니다.");
                    }
                    case "거절" -> {
                        if (FriendRequestTimer.getPlayerInviteTime().containsKey(player)) {
                            player.sendMessage(Main.INDEX + FriendRequestTimer.getPlayerInviteOwner().get(player).getName() + "님의 친구 추가 요청을 거절했습니다.");
                            FriendRequestTimer.getPlayerInviteOwner().get(player).sendMessage(Main.INDEX + player.getName() + "님이 친구 추가 요청을 거절했습니다.");
                            FriendRequestTimer.getPlayerInviteTime().remove(player);
                            FriendRequestTimer.getPlayerInviteOwner().remove(player);
                        } else player.sendMessage(Main.INDEX + "§c받은 친구 요청이 존재하지 않습니다.");
                    }
                    case "삭제" -> {
                        if (args.length == 1) player.sendMessage(Main.INDEX + "§c사용법: /친구 삭제 <플레이어>");
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) player.sendMessage(Main.INDEX + "§c존재하지 않는 플레이어입니다.");
                        else if (FriendData.getPlayerFriendList(player.getUniqueId()).contains(target.getUniqueId().toString())) {
                            FriendData.removeFriend(player.getUniqueId(), target.getUniqueId());
                            player.sendMessage(Main.INDEX + args[1] + "님을 친구 목록에서 삭제했습니다.");
                        } else player.sendMessage(Main.INDEX + "§c이미 그 플레이어와는 친구가 아닙니다.");
                    }
                    case "목록" -> {
                        StringBuilder message = new StringBuilder(Main.INDEX + "친구 목록");
                        for (String uuid : FriendData.getPlayerFriendList(player.getUniqueId())) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuid))))
                                message.append("\n").append(ChatColor.GREEN).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                            else
                                message.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                        }
                        player.sendMessage(message.toString());
                    }
                    case "차단" -> {
                        if (args.length < 3) player.sendMessage(Main.INDEX + "§c사용법: /친구 차단 추가/해제 <플레이어>");
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) player.sendMessage(invaildPlayer);
                        else if (args[2].equals("추가")) {
                            if (FriendData.addIgnore(player.getUniqueId(), target.getUniqueId()))
                                player.sendMessage(Main.INDEX + args[1] + "님을 차단했습니다.");
                            else player.sendMessage(Main.INDEX + "이미 그 플레이어는 차단됐습니다.");
                        } else if (args[2].equals("해제")) {
                            if (FriendData.removeIgnore(player.getUniqueId(), target.getUniqueId()))
                                player.sendMessage(Main.INDEX + args[1] + "님을 차단 해제 했습니다.");
                            else player.sendMessage(Main.INDEX + "그 플레이어는 차단이 돼 있지 않습니다.");
                        }
                    }
                    case "차단목록" -> {
                        StringBuilder messages = new StringBuilder(Main.INDEX + "차단 목록");
                        for (String uuid : FriendData.getPlayerIgnoreList(player.getUniqueId())) {
                            messages.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                        }
                        player.sendMessage(messages.toString());
                    }
                    default -> player.sendMessage(noArguments);
                }
            } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    private static void request(Player player, Player friend) {
        try {
            FriendRequestTimer.getPlayerInviteOwner().put(friend, player);
            FriendRequestTimer.getPlayerInviteTime().put(friend, 60);
            player.sendMessage(Main.INDEX + "해당 플레이어에게 성공적으로 친구 요청을 보냈습니다.");
            friend.sendMessage(Main.INDEX + player.getName() + "님이 친구 추가 요청을 보냈습니다. 60초 이내에 응답해주세요. (/친구 수락/거절)");
        } catch (Exception e) {
            Main.printException(e);
        }
    }

}
