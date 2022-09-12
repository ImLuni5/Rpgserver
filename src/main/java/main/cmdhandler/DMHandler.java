package main.cmdhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DMHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(Main.INDEX + "/귓속말 <플레이어> <메시지> - 플레이어에게 귓속말을 보냅니다.");
            return;
        }
        Player dm = Bukkit.getPlayer(args[0]);
        // 플레이어가 오프라인일때
        if (!Bukkit.getOnlinePlayers().contains(dm)) {
            commandSender.sendMessage(Main.INDEX + "§c그 플레이어는 온라인이 아닙니다.");
            return;
            // 귓속말에 본인 쳤을때
        } else if (commandSender == dm) {
            commandSender.sendMessage(Main.INDEX + "§c자기 자신에게 귓속말을 보낼 수 없습니다.");
            return;
        }
        // 정상적으로 명령어를 쳤을때
        String msg = args[1];
        commandSender.sendMessage(Main.INDEX + "§aYou §7→ §f" + dm.getName() + "§7: §f" + msg);
        dm.sendMessage(Main.INDEX + commandSender.getName() + " §7→ §aYou§7: §f" + msg);
    }
}
