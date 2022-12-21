package main.eventhandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import main.Main;
import main.cmdhandler.PartyHandler;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static main.Main.SCHEDULER;

public class EventListener implements Listener {

    private static final HashMap<Player, Integer> taskId = new HashMap<>();

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        e.joinMessage(null);

        UUID uuid = e.getPlayer().getUniqueId();
        if (SettingsData.getSettings("dm", uuid) == null) SettingsData.setSettings("dm", uuid, SettingsData.dmOption.ALL.name());
        if (SettingsData.getSettings("party", uuid) == null) SettingsData.setSettings("party", uuid, SettingsData.partyOption.ALL.name());
        if (SettingsData.getSettings("friend", uuid) == null) SettingsData.setSettings("friend", uuid, SettingsData.friendOption.ALL.name());
        if (SettingsData.getSettings("joinMsg", uuid) == null) SettingsData.setSettings("joinMsg", uuid, SettingsData.joinMsgOption.ALL.name());

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerSet = SettingsData.getSettings("joinMsg", p.getUniqueId());
            SettingsData.joinMsgOption playerOption = SettingsData.joinMsgOption.valueOf(playerSet);
            if (playerOption == SettingsData.joinMsgOption.FRIENDS) {
                if (FriendData.getPlayerFriendList(p.getUniqueId()).contains(e.getPlayer().getUniqueId().toString())) p.sendMessage(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 접속하셨습니다.");
            } else if (playerOption == SettingsData.joinMsgOption.ALL) p.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속하셨습니다.");
        }
        int tmpTaskId = SCHEDULER.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            final Scoreboard board = manager.getNewScoreboard();
            final Objective objective = board.registerNewObjective("test", Criteria.DUMMY, Component.text("내 정보"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score = objective.getScore(" ");
            score.setScore(10);
            Score nickname = objective.getScore("닉네임: " + e.getPlayer().getName());
            nickname.setScore(9);
            Score score1 = objective.getScore("  ");
            score1.setScore(8);
            Score ping;
            if (e.getPlayer().getPing() < 50)
                ping = objective.getScore("핑: " + ChatColor.DARK_GREEN + e.getPlayer().getPing() + "ms");
            else if (e.getPlayer().getPing() < 100)
                ping = objective.getScore("핑: " + ChatColor.GREEN + e.getPlayer().getPing() + "ms");
            else if (e.getPlayer().getPing() < 150)
                ping = objective.getScore("핑: " + ChatColor.YELLOW + e.getPlayer().getPing() + "ms");
            else if (e.getPlayer().getPing() < 200)
                ping = objective.getScore("핑: " + ChatColor.RED + e.getPlayer().getPing() + "ms");
            else ping = objective.getScore("핑: " + ChatColor.DARK_RED + e.getPlayer().getPing() + "ms");
            ping.setScore(0);
            Score money = objective.getScore("돈: " + ChatColor.GREEN + Main.getEconomy().format(Main.getEconomy().getBalance(e.getPlayer())));
            money.setScore(7);
            Score score5 = objective.getScore("");
            score5.setScore(6);
            e.getPlayer().setScoreboard(board);
        }, 0, 20L);
        taskId.put(e.getPlayer(), tmpTaskId);
        if (!FriendData.getPlayerFriendList(e.getPlayer().getUniqueId()).isEmpty()) {
            for (String uuid1 : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
                if (SettingsData.joinMsgOption.valueOf(SettingsData.getSettings("joinMsg", UUID.fromString(uuid1))) == SettingsData.joinMsgOption.FRIENDS) {
                    Bukkit.getPlayer(UUID.fromString(uuid1)).sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 접속했습니다."));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        if (taskId.get(e.getPlayer()) != null) SCHEDULER.cancelTask(taskId.get(e.getPlayer()));
        taskId.remove(e.getPlayer());
        e.quitMessage(null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerOption = SettingsData.getSettings("joinMsg", p.getUniqueId());
            if (SettingsData.joinMsgOption.valueOf(playerOption) == SettingsData.joinMsgOption.FRIENDS) {
                if (FriendData.getPlayerFriendList(p.getUniqueId()).contains(e.getPlayer().getUniqueId().toString())) p.sendMessage(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다.");
            } else if (SettingsData.joinMsgOption.valueOf(playerOption) == SettingsData.joinMsgOption.ALL) p.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 퇴장했습니다.");
        }
        if (!FriendData.getPlayerFriendList(e.getPlayer().getUniqueId()).isEmpty()) {
            for (String uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
                if (SettingsData.joinMsgOption.valueOf(SettingsData.getSettings("joinMsg", UUID.fromString(uuid))) == SettingsData.joinMsgOption.FRIENDS) {
                    Bukkit.getPlayer(UUID.fromString(uuid)).sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다."));
                }
            }
        }
        //파티에 소속되었는지 확인
        if (PartyHandler.getPlayerParty().containsKey(e.getPlayer())) {
            // 파티에서 나가기
            List<Player> playerList = PartyHandler.getParty().get(PartyHandler.getPlayerParty().get(e.getPlayer()));
            playerList.remove(e.getPlayer());
            if (playerList.isEmpty()) {
                PartyHandler.getParty().remove(PartyHandler.getPlayerParty().get(e.getPlayer()));
                PartyHandler.getIsPartyOwner().remove(e.getPlayer());
                PartyHandler.getPartyOwner().remove(PartyHandler.getPlayerParty().get(e.getPlayer()));
            } else PartyHandler.getParty().put(PartyHandler.getPlayerParty().get(e.getPlayer()), playerList);
            PartyHandler.getPlayerParty().remove(e.getPlayer());
            if (Boolean.TRUE.equals(PartyHandler.getIsPartyOwner().getOrDefault(e.getPlayer(), false))) {
                Player randomPlayer = playerList.get(0);
                PartyHandler.getIsPartyOwner().remove(e.getPlayer());
                PartyHandler.getIsPartyOwner().put(e.getPlayer(), true);
                PartyHandler.getPartyOwner().put(PartyHandler.getPlayerParty().get(randomPlayer), randomPlayer);
                for (Player player : playerList)
                    player.sendMessage(Main.INDEX + "파티장이 파티에 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
            }
            for (Player player : playerList)
                player.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속을 종료해서 파티에서 퇴장됐습니다.");
        }
    }

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent e) {
        // 파티 채팅 모드인지 확인
        if (Boolean.TRUE.equals(PartyHandler.getIsPartyChat().getOrDefault(e.getPlayer(), false))) {
            // 파티 채팅
            e.setCancelled(true);
            TextComponent component = (TextComponent) e.message();
            for (Player player : PartyHandler.getParty().get(PartyHandler.getPlayerParty().get(e.getPlayer())))
                if (Boolean.TRUE.equals(PartyHandler.getIsPartyOwner().getOrDefault(player, false)))
                    player.sendMessage(PartyHandler.getPlayerParty().get(e.getPlayer()) + " | 파티장 | " + e.getPlayer().getName() + ": " + component.content());
                else
                    player.sendMessage(PartyHandler.getPlayerParty().get(e.getPlayer()) + " | " + e.getPlayer().getName() + ": " + component.content());
        }
    }


}
