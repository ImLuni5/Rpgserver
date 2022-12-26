package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.datahandler.SettingsData.DmOption;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DMHandler {

    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        try {
            final String noArguments = Main.INDEX + "/귓속말 <플레이어> <메시지> - 플레이어에게 귓속말을 보냅니다.\n" + Main.INDEX + "/귓속말 설정 모두에게 - 모두에게 귓속말을 받도록 설정합니다.\n" + Main.INDEX + "/귓속말 설정 친구에게 - 친구에게만 귓속말을 받도록 설정합니다.\n" + Main.INDEX + "/귓속말 설정 받지않음 - 귓속말을 받지 않습니다.";
            Player p = (Player) commandSender;
            // 아무것도 입력하지 않았을 때
            if (args.length < 2) {
                p.sendMessage(noArguments);
                return;
            }
            // 설정을 입력했을 때
            if (args[0].equals("설정")) {
                String playerSet = SettingsData.getSettings("dm", p.getUniqueId());
                DmOption playerOption = DmOption.valueOf(playerSet);
                switch (args[1]) {
                    case "모두에게" -> {
                        if (playerOption == DmOption.ALL) {
                            p.sendMessage(Main.INDEX + "§c이미 모든 플레이어에게서 귓속말을 받습니다.");
                        } else {
                            SettingsData.setSettings("dm", p.getUniqueId(), DmOption.ALL.name());
                            p.sendMessage(Main.INDEX + "§a이제 모든 플레이어에게서 귓속말을 받습니다.");
                        }
                        return;
                    }
                    case "친구에게" -> {
                        if (playerOption == DmOption.FRIENDS) {
                            p.sendMessage(Main.INDEX + "§c이미 친구에게서만 귓속말을 받습니다.");
                        } else {
                            SettingsData.setSettings("dm", p.getUniqueId(), DmOption.FRIENDS.name());
                            p.sendMessage(Main.INDEX + "§a이제 친구에게서만 귓속말을 받습니다.");
                        }
                        return;
                    }
                    case "받지않음" -> {
                        if (playerOption == DmOption.NEVER) {
                            p.sendMessage(Main.INDEX + "§c이미 모든 귓속말을 받지 않습니다.");
                        } else {
                            SettingsData.setSettings("dm", p.getUniqueId(), DmOption.NEVER.name());
                            p.sendMessage(Main.INDEX + "§a이제 모든 귓속말을 받지 않습니다.");
                        }
                        return;
                    }
                    default -> {
                        p.sendMessage(noArguments);
                        return;
                    }
                }
            }
            Player dm = Bukkit.getPlayer(args[0]);
            // 플레이어가 오프라인이거나 존재하지 않을때
            if (!Bukkit.getOnlinePlayers().contains(dm) || dm == null) {
                p.sendMessage(Main.INDEX + "§c해당 플레이어는 온라인이 아닙니다.");
                return;
            }
            String dmSet = SettingsData.getSettings("dm", dm.getUniqueId());
            DmOption dmOption = DmOption.valueOf(dmSet);
            // 귓속말에 본인 쳤을때
            if (p == dm) {
                p.sendMessage(Main.INDEX + "§c자기 자신에게 귓속말을 보낼 수 없습니다.");
                return;
                // 귓속말 받는 사람이 친구에게만 받도록 설정했을때
            } else if (dmOption == DmOption.FRIENDS) {
                if (!FriendData.getPlayerFriendList(dm.getUniqueId()).contains(p.getUniqueId().toString())) {
                    p.sendMessage(Main.INDEX + "§c해당 플레이어는 친구에게만 귓속말을 받도록 설정했습니다.");
                    return;
                }
                // 귓속말 받는 사람이 모든 귓속말 차단했을때
            } else if (dmOption == DmOption.NEVER) {
                p.sendMessage(Main.INDEX + "§c해당 플레이어는 귓속말을 받지 않도록 설정했습니다.");
                return;
                // 귓속말 받는 사람 또는 보내는 사람이 서로를 차단했을때
            } else if (FriendData.getPlayerIgnoreList(dm.getUniqueId()).contains(p.getUniqueId().toString()) || FriendData.getPlayerIgnoreList(p.getUniqueId()).contains(dm.getUniqueId().toString())) {
                p.sendMessage(Main.INDEX + "§c해당 플레이어에게 귓속말을 보낼 수 없습니다.");
                return;
            }
            // 정상적으로 명령어를 쳤을때
            p.sendMessage(Main.INDEX + "§aYou §7→ §f" + dm.getName() + "§7: §f" + args[1]);
            dm.sendMessage(Main.INDEX + p.getName() + " §7→ §aYou§7: §f" + args[1]);
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
