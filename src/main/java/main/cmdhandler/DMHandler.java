package main.cmdhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DMHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Main.INDEX + "/귓속말 <플레이어> <메시지> - 플레이어에게 귓속말을 보냅니다.");
            return;
        }
        Player DM = Bukkit.getPlayer(args[0]);
        // 플레이어 안쳤을때
        if (DM == null) {
            commandSender.sendMessage(Main.INDEX + "§c플레이어를 찾을 수 없습니다.");
            return;
        // 메시지 안쳤을때
        } else if (args[1] == null) {
            commandSender.sendMessage(Main.INDEX + "§c메시지를 입력해주세요.");
            return;
        // 플레이어가 오프라인일때
        } else if (!Bukkit.getOnlinePlayers().contains(DM)) {
            commandSender.sendMessage(Main.INDEX + "§c그 플레이어는 온라인이 아닙니다.");
            return;
        // 귓속말에 본인 쳤을때
        } else if (commandSender == DM) {
            commandSender.sendMessage(Main.INDEX + "§c자기 자신에게 귓속말을 보낼 수 없습니다.");
            return;
        }
        // 정상적으로 명령어를 쳤을때
        String msg = args[1];
        commandSender.sendMessage(Main.INDEX + "§aYou §7→ §f" + DM.getName() + "§7: §f" + msg);
        DM.sendMessage(Main.INDEX + commandSender.getName() + " §7→ §aYou§7: §f" + msg);
    }
}
