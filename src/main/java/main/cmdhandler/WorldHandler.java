package main.cmdhandler;

import main.Main;
import main.datahandler.WorldData;
import main.datahandler.WorldData.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public class WorldHandler {
    private static final String INVAILD_USE = Main.INDEX + "/월드 목록 - 월드 목록을 봅니다.\n" + Main.INDEX + "/월드 설정 <월드 이름> <타입> - 월드 타입(종류)를 설정합니다. 가능한 설정: (§e로비§f, §c야생§f, §dRPG§f, §b미니게임§f)";
    public static void onCommand(CommandSender sender, String @NotNull [] args) {
        try {
            if (args.length == 0) {
                sender.sendMessage(INVAILD_USE);
                return;
            }
            switch (args[0]) {
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
                }
                case "설정" -> {
                    if (args.length < 3) {
                        sender.sendMessage(INVAILD_USE);
                        return;
                    }
                    World w = Bukkit.getWorld(args[1]);
                    String type = args[2];
                    if (w == null) {
                        sender.sendMessage(Main.INDEX + "§c해당 이름의 월드를 찾을 수 없습니다.");
                    } else if (!type.equals(WorldType.NOT_SET.name()) && !type.equals(WorldType.LOBBY.name()) && !type.equals(WorldType.RPG.name()) && !type.equals(WorldType.MINIGAME.name())&& !type.equals(WorldType.SURVIVING.name())) {
                        sender.sendMessage(Main.INDEX + "§c알 수 없는 월드 타입입니다.");
                    } else {
                        if (type.equals("NOT_SET")) sender.sendMessage(Main.INDEX + "§7주의: 월드 타입을 초기화하고 있습니다. 이는 서버에 심각한 오류를 발생시킬 수 있습니다.");
                        sender.sendMessage(MessageFormat.format("{0}§a월드 §e{1}§a의 타입을 §7{2}§a에서 §d{3}§a으로 변경했습니다.", Main.INDEX, args[1], WorldData.worldData.get("world." + args[1] + ".type"), type));
                        WorldData.worldData.set("world." + args[1] + ".type", type);
                        WorldData.saveData();
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
