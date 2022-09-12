package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.timerhandler.FriendRequestTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class FriendHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        String noArguments = Main.INDEX + "/친구 추가 <플레이어> - 플레이어에게 친구 추가 요청을 보냅니다.\n" + Main.INDEX + "/친구 수락 - 친구 추가 요청을 수락합니다.\n" + Main.INDEX + "/친구 거절 - 친구 추가 요청을 거절합니다.\n" + Main.INDEX + "/친구 삭제 <플레이어> - 친구 목록에서 삭제합니다.\n" + Main.INDEX + "/친구 목록 - 친구 목록을 보여줍니다.\n" + Main.INDEX + "/친구 차단 추가 <플레이어> - 플레이어를 차단 합니다.\n" + Main.INDEX + "/친구 차단 해제 <플레이어> - 플레이어를 차단 해제 합니다.\n" + Main.INDEX + "/친구 차단목록 - 차단 목록을 보여줍니다.";
        // 명령어 외에 아무 구문도 안쳤을때
        if (args.length == 0) {
            player.sendMessage(noArguments);
            return;
        }
        switch (args[0]) {
            case "추가":
                if (args.length == 1) player.sendMessage(Main.INDEX + "§c사용법: /친구 추가 <플레이어>");
                else {
                    Player friend = Bukkit.getPlayer(args[1]);
                    if (!Bukkit.getOnlinePlayers().contains(friend))
                        player.sendMessage(Main.INDEX + "§c그 플레이어는 온라인이 아닙니다.");
                    else if (FriendData.getPlayerFriendList(player.getUniqueId()).contains(friend.getUniqueId().toString()))
                        player.sendMessage(Main.INDEX + "§c이미 해당 플레이어와 친구입니다.");
                    else if (FriendRequestTimer.getPlayerInviteTime().containsKey(friend))
                        player.sendMessage(Main.INDEX + "§c누군가가 해당 플레이어에게 친구 요청을 보냈습니다.");
                    else if (FriendData.getPlayerIgnoreList(player.getUniqueId()).contains(friend.getUniqueId().toString()) || FriendData.getPlayerIgnoreList(friend.getUniqueId()).contains(player.getUniqueId()))
                        player.sendMessage(Main.INDEX + "§c해당 플레이어에게 친구 요청을 보낼 수 없습니다.");
                    else {
                        FriendRequestTimer.getPlayerInviteOwner().put(Bukkit.getPlayer(args[1]), player);
                        FriendRequestTimer.getPlayerInviteTime().put(Bukkit.getPlayer(args[1]), 60);
                        Bukkit.getPlayer(args[1]).sendMessage(Main.INDEX + player.getName() + "님이 친구 추가 요청을 보냈습니다. 60초 이내에 응답해주세요. (/친구 수락/거절)");
                    }
                }
                break;
            case "수락":
                if (FriendRequestTimer.getPlayerInviteTime().containsKey(player)) {
                    FriendData.addFriend(player.getUniqueId(), FriendRequestTimer.getPlayerInviteOwner().get(player).getUniqueId());
                    player.sendMessage(Main.INDEX + "당신은 이제 " + FriendRequestTimer.getPlayerInviteOwner().get(player).getName() + "님과 친구입니다.");
                    FriendRequestTimer.getPlayerInviteOwner().get(player).sendMessage(Main.INDEX + "당신은 이제 " + player.getName() + "님과 친구입니다.");
                    FriendRequestTimer.getPlayerInviteTime().remove(player);
                    FriendRequestTimer.getPlayerInviteOwner().remove(player);
                } else player.sendMessage(Main.INDEX + "§c받은 친구 요청이 존재하지 않습니다.");
                break;
            case "거절":
                if (FriendRequestTimer.getPlayerInviteTime().containsKey(player)) {
                    player.sendMessage(Main.INDEX + FriendRequestTimer.getPlayerInviteOwner().get(player) + "님의 친구 추가 요청을 거절했습니다.");
                    FriendRequestTimer.getPlayerInviteOwner().get(player).sendMessage(Main.INDEX + player + "님이 친구 추가 요청을 거절했습니다.");
                    FriendRequestTimer.getPlayerInviteTime().remove(player);
                    FriendRequestTimer.getPlayerInviteOwner().remove(player);
                } else player.sendMessage(Main.INDEX + "§c받은 친구 요청이 존재하지 않습니다.");
                break;
            case "삭제":
                if (args.length == 1) player.sendMessage(Main.INDEX + "§c사용법: /친구 삭제 <플레이어>");
                else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.INDEX + "§c존재하지 않는 플레이어입니다.");
                else if (FriendData.addIgnore(player.getUniqueId(), Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId()))
                    player.sendMessage(Main.INDEX + args[1] + "님을 친구 목록에서 삭제했습니다.");
                else player.sendMessage(Main.INDEX + "§c이미 그 플레이어와는 친구가 아닙니다.");
                break;
            case "목록":
                StringBuilder message = new StringBuilder(Main.INDEX + "친구 목록");
                for (String uuid : FriendData.getPlayerFriendList(player.getUniqueId())) {
                    if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuid))))
                        message.append("\n").append(ChatColor.GREEN).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                    else message.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                }
                player.sendMessage(message.toString());
                break;
            case "차단":
                if (args.length < 3) player.sendMessage(Main.INDEX + "§c사용법: /친구 차단 추가/해제 <플레이어>");
                else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.INDEX + "§c존재하지 않는 플레이어입니다.");
                else if (args[2].equals("추가")) {
                    if (FriendData.addIgnore(player.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId()))
                        player.sendMessage(Main.INDEX + args[1] + "님을 차단했습니다.");
                    else player.sendMessage(Main.INDEX + "이미 그 플레이어는 차단됐습니다.");
                } else if (args[2].equals("해제")) {
                    if (FriendData.removeIgnore(player.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId()))
                        player.sendMessage(Main.INDEX + args[1] + "님을 차단 해제 했습니다.");
                    else player.sendMessage(Main.INDEX + "그 플레이어는 차단이 돼 있지 않습니다.");
                }
                break;
            case "차단목록":
                StringBuilder messages = new StringBuilder(Main.INDEX + "차단 목록");
                for (String uuid : FriendData.getPlayerIgnoreList(player.getUniqueId())) {
                    messages.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(UUID.fromString(uuid)));
                }
                player.sendMessage(messages.toString());
                break;
            default:
                player.sendMessage(noArguments);
        }
    }
}
