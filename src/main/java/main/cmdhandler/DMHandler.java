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
            // 아무것도 입력하지 않았을 때
            if (args.length < 2) {
                commandSender.sendMessage(noArguments);
                return;
            }
            // 설정을 입력했을 때
            if (args[0].equals("설정")) {
                if (commandSender instanceof Player p) {
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
                } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
            }
            Player dm = Bukkit.getPlayer(args[0]);
            // 플레이어가 오프라인이거나 존재하지 않을때
            if (!Bukkit.getOnlinePlayers().contains(dm) || dm == null) {
                commandSender.sendMessage(Main.INDEX + "§c해당 플레이어는 온라인이 아닙니다.");
                return;
            } else if (AdminHandler.isHiddenAdmin(dm)) {
                if (!commandSender.isOp()) {
                    commandSender.sendMessage(Main.INDEX + "§c해당 플레이어는 온라인이 아닙니다.");
                    return;
                }
            }
            String dmSet = SettingsData.getSettings("dm", dm.getUniqueId());
            DmOption dmOption = DmOption.valueOf(dmSet);
            // 귓속말에 본인 쳤을때
            if (commandSender == dm) {
                commandSender.sendMessage(Main.INDEX + "§c자기 자신에게 귓속말을 보낼 수 없습니다.");
                return;
                // 귓속말 받는 사람이 친구에게만 받도록 설정했을때
            } else if (dmOption == DmOption.FRIENDS) {
                if (commandSender instanceof Player p && !FriendData.getPlayerFriendList(dm.getUniqueId()).contains(p.getUniqueId().toString())) {
                    commandSender.sendMessage(Main.INDEX + "§c해당 플레이어는 친구에게만 귓속말을 받도록 설정했습니다.");
                    return;
                }
                // 귓속말 받는 사람이 모든 귓속말 차단했을때
            } else if (commandSender instanceof Player && dmOption == DmOption.NEVER) {
                commandSender.sendMessage(Main.INDEX + "§c해당 플레이어는 귓속말을 받지 않도록 설정했습니다.");
                return;
                // 귓속말 받는 사람 또는 보내는 사람이 서로를 차단했을때
            } else if (commandSender instanceof Player p && FriendData.getPlayerIgnoreList(dm.getUniqueId()).contains(p.getUniqueId().toString()) || commandSender instanceof Player p2 && FriendData.getPlayerIgnoreList(p2.getUniqueId()).contains(dm.getUniqueId().toString())) {
                commandSender.sendMessage(Main.INDEX + "§c해당 플레이어에게 귓속말을 보낼 수 없습니다.");
                return;
                // 비공개 관리자가 일반 유저한테 보내려 할때
            } else if (commandSender instanceof Player p && AdminHandler.isHiddenAdmin(p) && !dm.isOp()) {
                commandSender.sendMessage(Main.INDEX + "§c현재 모습을 드러내지 않았기 때문에 일반 유저에겐 귓속말을 보낼 수 없습니다. §b/관리자 공개§c를 통해 유저들에게 모습을 드러낸 후 다시 시도하세요.");
                return;
            } StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                message.append(args[i]).append(" ");
            // 정상적으로 명령어를 쳤을때
            commandSender.sendMessage(Main.INDEX + "§aYou §7→ §f" + dm.getName() + "§7: §f" + message);
            String senderName;
            if (commandSender instanceof Player p) senderName = p.getName();
            else senderName = "§d§lCONSOLE";
            dm.sendMessage(Main.INDEX + senderName + " §7→ §aYou§7: §f" + message);
            Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a" + senderName + " §7→  §e" + dm.getName() + "§7: §f" + message);
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
