package main.eventhandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import main.Main;
import main.cmdhandler.AdminHandler;
import main.cmdhandler.PartyHandler;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.datahandler.SettingsData.DmOption;
import main.datahandler.SettingsData.FriendOption;
import main.datahandler.SettingsData.JoinMsgOption;
import main.datahandler.SettingsData.PartyOption;
import main.datahandler.WorldData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static main.Main.SCHEDULER;

public class EventListener implements Listener {

    private static final HashMap<Player, Integer> taskId = new HashMap<>();

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        try {
            e.joinMessage(null);
            boolean isOp = false;
            if (e.getPlayer().isOp()) {
                String wName = e.getPlayer().getWorld().getName();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    e.getPlayer().sendMessage(Main.INDEX + "§e§l숨겨진 관리자 모드§c: §f관리자가 아닌 유저에게서 §e완벽하게 숨겨지지만§f, §c관리자 채팅§f만을 사용 가능하고 대부분의 §9소셜 기능§f을 사용할 수 §c없습니다§f.\n" + Main.INDEX + "§f해제하려면 §b/관리자 공개§f를 입력하세요. 다른 유저들에게 방금 들어온 걸로 표시되며 제한이 모두 해제됩니다.");
                    e.getPlayer().showTitle(Title.title(Component.text("§e숨겨진 관리자 모드"), Component.text("§c유저들이 볼 수 없고, 기능이 제한됩니다. 해제하려면 §b/관리자 공개§c를 입력하세요."), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(6), Duration.ofSeconds(1))));
                    if (Objects.equals(WorldData.getworldType(wName), WorldData.WorldType.NOT_SET))
                        e.getPlayer().sendMessage("§c§l주의: §f현재 접속한 월드는 종류가 설정되지 않았습니다. 이는 오류를 발생시킬 수 있으므로 §b/월드 설정 " + wName + " <타입>§f으로 변경하세요.");
                }, 6);
                isOp = true;
                AdminHandler.getAdminChat().put(e.getPlayer(), true);
                AdminHandler.getAdminReveal().put(e.getPlayer(), false);
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) p.sendMessage(Component.text("§7[!] 관리자 §c" + e.getPlayer().getName() + "§7님이 접속했습니다.").append(Main.ADMIN_MSG_SYMBOL));
                    else p.hidePlayer(Main.getPlugin(Main.class), e.getPlayer());
                }
            }

            /* todo: 이거 완성 /*
            /* if (ColorHandler.getPlayerColor().get(e.getPlayer()) == null) {
                e.getPlayer().sendMessage(Main.INDEX + "§a색깔 §f§l흰색§a을 흭득했습니다.");
                e.getPlayer().playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));
                if (isOp) {
                    e.getPlayer().sendMessage(Main.INDEX + "§a색깔 §4§l짙은 빨간색§a을 흭득했습니다.");
                    ColorHandler.getPlayerColor().put(e.getPlayer(), ChatColor.DARK_RED);
                    e.getPlayer().displayName(Component.text(ChatColor.DARK_RED + e.getPlayer().getName()));
                    e.getPlayer().playerListName(Component.text(ChatColor.DARK_RED + e.getPlayer().getName()));
                } else {
                    ColorHandler.getPlayerColor().put(e.getPlayer(), ChatColor.WHITE);
                }
            }*/

            UUID uuid = e.getPlayer().getUniqueId();
            if (SettingsData.getSettings("dm", uuid) == null) SettingsData.setSettings("dm", uuid, DmOption.ALL.name());
            if (SettingsData.getSettings("party", uuid) == null)
                SettingsData.setSettings("party", uuid, PartyOption.ALL.name());
            if (SettingsData.getSettings("friend", uuid) == null)
                SettingsData.setSettings("friend", uuid, FriendOption.ALL.name());
            if (SettingsData.getSettings("joinMsg", uuid) == null)
                SettingsData.setSettings("joinMsg", uuid, JoinMsgOption.ALL.name());

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isOp) break;
                String playerSet = SettingsData.getSettings("joinMsg", p.getUniqueId());
                JoinMsgOption playerOption = JoinMsgOption.valueOf(playerSet);
                if (playerOption == JoinMsgOption.FRIENDS) {
                    if (FriendData.getPlayerFriendList(p.getUniqueId()).contains(e.getPlayer().getUniqueId().toString()))
                        p.sendMessage(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 접속하셨습니다.");
                } else if (playerOption == JoinMsgOption.ALL)
                    p.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속하셨습니다.");
                if (p.isOp() && !AdminHandler.getAdminReveal().get(p)) {
                    e.getPlayer().hidePlayer(Main.getPlugin(Main.class), p);
                }
            }
            setScoreboard(e.getPlayer());
            if (!FriendData.getPlayerFriendList(e.getPlayer().getUniqueId()).isEmpty()) {
                for (String uuid1 : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
                    Player friend = Bukkit.getPlayer(UUID.fromString(uuid1));
                    if (friend != null && friend.isOnline() && JoinMsgOption.valueOf(SettingsData.getSettings("joinMsg", UUID.fromString(uuid1))) == JoinMsgOption.FRIENDS) {
                        friend.sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 접속했습니다."));
                    }
                }
            }
        } catch (Exception exception) {
            Main.printException(exception);
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        try {
            if (taskId.get(e.getPlayer()) != null) SCHEDULER.cancelTask(taskId.get(e.getPlayer()));
            taskId.remove(e.getPlayer());
            e.quitMessage(null);
            boolean isOp = false;
            if (e.getPlayer().isOp()) {
                if (!AdminHandler.getAdminReveal().get(e.getPlayer()))
                    isOp = true;
                AdminHandler.getAdminChat().remove(e.getPlayer());
                AdminHandler.getAdminReveal().remove(e.getPlayer());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) {
                        p.sendMessage(Component.text("§7[!] 관리자 §c" + e.getPlayer().getName() + "§7님이 퇴장했습니다.").append(Main.ADMIN_MSG_SYMBOL));
                    }
                }
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isOp) break;
                String playerOption = SettingsData.getSettings("joinMsg", p.getUniqueId());
                if (JoinMsgOption.valueOf(playerOption) == JoinMsgOption.FRIENDS) {
                    if (FriendData.getPlayerFriendList(p.getUniqueId()).contains(e.getPlayer().getUniqueId().toString()))
                        p.sendMessage(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다.");
                } else if (JoinMsgOption.valueOf(playerOption) == JoinMsgOption.ALL)
                    p.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 퇴장했습니다.");
            }
            if (!FriendData.getPlayerFriendList(e.getPlayer().getUniqueId()).isEmpty() && !isOp) {
                for (String uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
                    Player friend = Bukkit.getPlayer(UUID.fromString(uuid));
                    if (friend != null && friend.isOnline() && JoinMsgOption.valueOf(SettingsData.getSettings("joinMsg", UUID.fromString(uuid))) == JoinMsgOption.FRIENDS) {
                        friend.sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다."));
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
                        player.sendMessage(Main.INDEX + "파티장이 파티에서 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
                }
                for (Player player : playerList)
                    player.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속을 종료해서 파티에서 퇴장됐습니다.");
            }
        } catch (Exception exception) {
            Main.printException(exception);
        }
    }

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent e) {
        try {
            if (e.getPlayer().isOp() && AdminHandler.getAdminChat().get(e.getPlayer())) {
                e.setCancelled(true);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) p.sendMessage(Component.text("§f> §c§l관리자 §4| §r§f" + e.getPlayer().getName() + "§f: ").append(e.message()));
                }
            }
            // 파티 채팅 모드인지 확인
            else if (PartyHandler.getIsPartyChat().getOrDefault(e.getPlayer(), false)) {
                // 파티 채팅
                e.setCancelled(true);
                TextComponent component = (TextComponent) e.message();
                for (Player player : PartyHandler.getParty().get(PartyHandler.getPlayerParty().get(e.getPlayer())))
                    if (PartyHandler.getIsPartyOwner().getOrDefault(player, false))
                        player.sendMessage(PartyHandler.getPlayerParty().get(e.getPlayer()) + " | 파티장 | " + e.getPlayer().getName() + ": " + component.content());
                    else
                        player.sendMessage(PartyHandler.getPlayerParty().get(e.getPlayer()) + " | " + e.getPlayer().getName() + ": " + component.content());
            }
        } catch (Exception exception) {
            Main.printException(exception);
        }
    }

    @EventHandler
    public void onAchivement(@NotNull PlayerAdvancementDoneEvent e) {
        if (e.getPlayer().isOp()) {
            e.message(null);
        }
    }

    public static void setScoreboard(Player p) {
        int tmpTaskId = SCHEDULER.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            final Scoreboard board = manager.getNewScoreboard();
            final Objective objective = board.registerNewObjective("test", Criteria.DUMMY, Component.text("§e레모니 §a월드"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score = objective.getScore(" ");
            score.setScore(10);
            Score nickname = objective.getScore("닉네임: " + p.getName());
            nickname.setScore(9);
            Score score1 = objective.getScore("  ");
            score1.setScore(8);
            WorldData.WorldType worldType = WorldData.getworldType(p.getWorld().getName());
            switch (Objects.requireNonNull(worldType)) {
                case LOBBY -> {
                    objective.getScore("§dRPG§f 돈: §6" + Main.getEconomy().format(Main.getEconomy().getBalance(p))).setScore(7);
                    objective.getScore("§a야생§f 코인: §60").setScore(6);
                    objective.getScore("    ").setScore(5);
                } case SURVIVING -> {
                    objective.getScore("§a야생§f 코인: §60").setScore(7);
                    objective.getScore("§e일일 퀘스트§f: §a0/0").setScore(6);
                    objective.getScore("    ").setScore(5);
                }
            } Score ping;
            if (p.getPing() < 50)
                ping = objective.getScore("핑: " + ChatColor.DARK_GREEN + p.getPing() + "ms");
            else if (p.getPing() < 100)
                ping = objective.getScore("핑: " + ChatColor.GREEN + p.getPing() + "ms");
            else if (p.getPing() < 150)
                ping = objective.getScore("핑: " + ChatColor.YELLOW + p.getPing() + "ms");
            else if (p.getPing() < 200)
                ping = objective.getScore("핑: " + ChatColor.RED + p.getPing() + "ms");
            else ping = objective.getScore("핑: " + ChatColor.DARK_RED + p.getPing() + "ms");
            ping.setScore(0);
            p.setScoreboard(board);
        }, 0, 20L);
        taskId.put(p, tmpTaskId);
    }

}
