package main.cmdhandler;

import main.Main;
import main.timerhandler.TPATimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TPAHandler {
    private static final String INVAILD_USE = Main.INDEX + "/tpa <플레이어> - 플레이어에게 텔레포트 요청을 합니다.\n" + Main.INDEX + "/tpaccept - 현재 대기중인 텔레포트 요청을 수락합니다.\n" + Main.INDEX + "/tpdeny - 현재 대기중엔 텔레포트 요청을 거절합니다.";
    @Contract(pure = true)
    public static void onCommand(CommandSender sender, String command, String @NotNull [] args) {
        if (sender instanceof Player p) {
            switch (command) {
                case "tpa" -> {
                    if (args.length == 0) {
                        p.sendMessage(INVAILD_USE);
                        return;
                    } Player reciver = Bukkit.getPlayer(args[0]);
                    if (reciver == null) {
                        p.sendMessage(Main.INDEX + "§c해당 플레이어를 찾을 수 없습니다.");
                        return;
                    } if (reciver.equals(p)) {
                        p.sendMessage(Main.INDEX + "§c자기 자신에게 텔레포트 요청을 보낼 수 없습니다.");
                        return;
                    } if (TPATimer.getTpaCooldown().containsKey(p)) {
                        int cd = TPATimer.getTpaCooldown().get(p);
                        int min;
                        int cdReduce;
                        String zero;
                        min = cd >= 60 ? 1 : 0;
                        cdReduce = cd >= 60 ? 60 : 0;
                        zero = cd >= 10 ? "" : "0";
                        p.sendMessage(Main.INDEX + "§c텔레포트 요청 사용이 너무 빠릅니다. §e" + min + "§c분 §e" + zero + (cd - cdReduce) + "§c초 후에 다시 시도하세요.");
                        return;
                    } if (TPATimer.getTpaRequest().containsKey(reciver)) {
                        p.sendMessage(Main.INDEX + "§c해당 플레이어에게 이미 텔레포트 요청이 있습니다.");
                        return;
                    }
                    TPATimer.getTpaRequest().put(reciver, p);
                    TPATimer.getTpaTimer().put(reciver, 60);
                    TPATimer.getTpaCooldown().put(p, 90);
                    p.sendMessage(Main.INDEX + "§e" + reciver.getName() + "§a님에게 텔레포트 요청을 보냈습니다. §e60§a초의 수락 시간이 주어집니다.");
                    reciver.sendMessage(Main.INDEX + "§e" + p.getName() + "§a님에게 텔레포트 요청이 왔습니다. §e60§a초 안에 §2/tpaccept§a로 §b수락§a하고, §c/tpdeny§a로 §c거절§a하세요.");
                } case "tpaccept" -> {
                    Player requester = TPATimer.getTpaRequest().get(p);
                    if (requester == null) {
                        p.sendMessage(Main.INDEX + "§c텔레포트 요청이 없습니다.");
                        return;
                    } requester.teleport(p);
                    requester.sendMessage(Main.INDEX + "§e" + p.getName() + "§a님이 텔레포트 요청을 수락했습니다. §e1§a분 §e30§a초 후에 다시 텔레포트 요청을 사용할 수 있습니다.");
                    p.sendMessage(Main.INDEX + "§e" + requester.getName() + "§a님의 텔레포트 요청을 수락했습니다.");
                    TPATimer.getTpaRequest().remove(p);
                    TPATimer.getTpaTimer().remove(p);
                } case "tpdeny" -> {
                    Player requester = TPATimer.getTpaRequest().get(p);
                    if (requester == null) {
                        p.sendMessage(Main.INDEX + "§c텔레포트 요청이 없습니다.");
                        return;
                    } requester.sendMessage(Main.INDEX + "§e" + p.getName() + "§c님이 텔레포트 요청을 거절했습니다. §e1§c분 §e30§c초 후에 다시 텔레포트 요청을 사용할 수 있습니다.");
                    p.sendMessage(Main.INDEX + "§e" + requester.getName() + "§c님의 텔레포트 요청을 거절했습니다.");
                    TPATimer.getTpaRequest().remove(p);
                    TPATimer.getTpaTimer().remove(p);
                }
            }
        } else sender.sendMessage(Main.INDEX + "§c이 명령어는 플레이어만 사용할 수 있습니다.");
    }
}
