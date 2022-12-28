package main.cmdhandler;

import main.Main;
import main.datahandler.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WorldHandler {
    private static final String INVAILD_USE = Main.INDEX + "/월드 목록 - 월드 목록을 봅니다.\n" + Main.INDEX + "/월드 설정 <월드 이름> <타입> - 월드 타입(종류)를 설정합니다. 가능한 설정: (§e로비§f, §c야생§f, §dRPG§f, §b미니게임§f)";
    public static void onCommand(CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(INVAILD_USE);
            return;
        } switch (args[0]) {
            case "목록" -> {
                sender.sendMessage(Main.INDEX + "§2----------------[ §a전체 월드 목록 §2]----------------");
                if (Bukkit.getWorlds().isEmpty()) sender.sendMessage(Main.INDEX + "§7월드가 없습니다.");
                else {
                    int l = 0;
                    for (World w : Bukkit.getWorlds()) {
                        l++;
                        sender.sendMessage(String.format("%s§a%d §2| §a%s§2, §a타입: §e%s", Main.INDEX, l, w.getName(), WorldData.getworldType(w.getName())));
                    }
                }
                sender.sendMessage(Main.INDEX + "§2--------------------------------------------");
            } case "설정" -> {
                /* todo: 월드 타입 설정 기능 */
            }
        }
    }
}
