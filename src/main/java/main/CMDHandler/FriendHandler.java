package main.CMDHandler;

import main.DataHandler.FriendData;
import main.Main;
import main.TimerHandler.FriendRequestTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class FriendHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        if (args.length == 0) {

        } else {
            switch (args[0]) {
                case "추가":
                    if (args.length == 1) player.sendMessage(Main.index + "§c사용법: /친구 추가 <플레이어>");
                    else {
                        Player friend = Bukkit.getPlayer(args[1]);
                        if (!Bukkit.getOnlinePlayers().contains(friend))
                            player.sendMessage(Main.index + "§c그 플레이어는 온라인이 아닙니다.");
                        else if (FriendData.getPlayerFriendList(player.getUniqueId()).contains(friend.getUniqueId()))
                            player.sendMessage(Main.index + "§c이미 해당 플레이어와 친구입니다.");
                        else if (FriendRequestTimer.playerInviteTime.containsKey(friend))
                            player.sendMessage(Main.index + "§c누군가가 해당 플레이어에게 친구 요청을 보냈습니다.");
                        else if (FriendData.getPlayerIgnoreList(player.getUniqueId()).contains(friend.getUniqueId()) || FriendData.getPlayerIgnoreList(friend.getUniqueId()).contains(player.getUniqueId()))
                            player.sendMessage(Main.index + "§c해당 플레이어에게 친구 요청을 보낼 수 없습니다.");
                        else {
                            FriendRequestTimer.playerInviteOwner.put(Bukkit.getPlayer(args[1]), player);
                            FriendRequestTimer.playerInviteTime.put(Bukkit.getPlayer(args[1]), 60);
                            Bukkit.getPlayer(args[1]).sendMessage(Main.index + player.getName() + "님이 친구 추가 요청을 보냈습니다. 60초 이내에 응답해주세요. (/친구 수락/거절)");
                        }
                    }
                    break;
                case "수락":
                    if (FriendRequestTimer.playerInviteTime.containsKey(player)) {
                        FriendData.addFriend(player.getUniqueId(), FriendRequestTimer.playerInviteOwner.get(player).getUniqueId());
                        player.sendMessage(Main.index + "당신은 이제 " + FriendRequestTimer.playerInviteOwner.get(player) + "님과 친구입니다.");
                        FriendRequestTimer.playerInviteOwner.get(player).sendMessage(Main.index + "당신은 이제 " + player + "님과 친구입니다.");
                        FriendRequestTimer.playerInviteTime.remove(player);
                        FriendRequestTimer.playerInviteOwner.remove(player);
                    } else player.sendMessage(Main.index + "§c받은 친구 요청이 존재하지 않습니다.");
                    break;
                case "거절":
                    if (FriendRequestTimer.playerInviteTime.containsKey(player)) {
                        player.sendMessage(Main.index + FriendRequestTimer.playerInviteOwner.get(player) + "님의 친구 추가 요청을 거절했습니다.");
                        FriendRequestTimer.playerInviteOwner.get(player).sendMessage(Main.index + player + "님이 친구 추가 요청을 거절했습니다.");
                        FriendRequestTimer.playerInviteTime.remove(player);
                        FriendRequestTimer.playerInviteOwner.remove(player);
                    } else player.sendMessage(Main.index + "§c받은 친구 요청이 존재하지 않습니다.");
                    break;
                case "삭제":
                    if (args.length == 1) player.sendMessage(Main.index + "§c사용법: /친구 삭제 <플레이어>");
                    else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.index + "§c존재하지 않는 플레이어입니다.");
                    else if (FriendData.addIgnore(player.getUniqueId(), Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId()))
                        player.sendMessage(Main.index + args[1] + "님을 친구 목록에서 삭제했습니다.");
                    else player.sendMessage(Main.index + "§c이미 그 플레이어와는 친구가 아닙니다.");
                    break;
                case "목록":
                    StringBuilder message = new StringBuilder(Main.index + "친구 목록");
                    for (UUID uuid : FriendData.getPlayerFriendList(player.getUniqueId())) {
                        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                            message.append("\n").append(ChatColor.GREEN).append(Bukkit.getPlayer(uuid));
                        else message.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(uuid));
                    }
                    player.sendMessage(message.toString());
                    break;
                case "차단":
                    if (args.length < 3) player.sendMessage(Main.index + "§c사용법: /친구 차단 추가/해제 <플레이어>");
                    else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.index + "§c존재하지 않는 플레이어입니다.");
                    else if (args[2].equals("추가")) {
                        if (FriendData.addIgnore(player.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId()))
                            player.sendMessage(Main.index + args[1] + "님을 차단했습니다.");
                        else player.sendMessage(Main.index + "이미 그 플레이어는 차단됐습니다.");
                    } else if (args[2].equals("해제")) {
                        if (FriendData.removeFriend(player.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId()))
                            player.sendMessage(Main.index + args[1] + "님을 차단 해제 했습니다.");
                        else player.sendMessage(Main.index + "그 플레이어는 차단이 돼 있지 않습니다.");
                    }
                    break;
                case "차단목록":
                    StringBuilder messages = new StringBuilder(Main.index + "차단 목록");
                    for (UUID uuid : FriendData.getPlayerIgnoreList(player.getUniqueId())) {
                        messages.append("\n").append(ChatColor.RED).append(Bukkit.getPlayer(uuid));
                    }
                    player.sendMessage(messages.toString());
                    break;
            }
        }
    }
}
