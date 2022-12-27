package main.cmdhandler;

import main.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminHandler {
    private static final HashMap<Player, Boolean> adminChat = new HashMap<>();
    private static final HashMap<Player, Boolean> adminReveal = new HashMap<>();
    private static final String INVAILD_USE = Main.INDEX + "/관리자 채팅 - 관리자 채팅 모드를 키거나 끕니다.\n" + Main.INDEX + "/관리자 공개 - 모습을 드러냅니다. 일반 유저들에겐 방금 서버에 들어온것 처럼 보이게 되며, 완전 투명화가 해제되고 일반 채팅, 파티, 친구 등 플레이어 타겟팅 기능을 사용할 수 있게 됩니다.";
    public static void onCommand(@NotNull CommandSender sender, String[] args) {
        try {
            if (!sender.isOp()) {
                sender.sendMessage(Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다.");
                return;
            }
            if (args.length == 0) {
                sender.sendMessage(INVAILD_USE);
            }
            if (sender instanceof Player p) {
                switch (args[0]) {
                    case "채팅" -> {
                        if (!adminReveal.get(p)) {
                            p.sendMessage(Main.INDEX + "§c현재 모습을 드러내지 않았기 때문에 관리자 채팅을 전환할 수 없습니다. §b/관리자 공개§e를 통해 유저들에게 모습을 드러낸 후 다시 시도하세요.");
                            return;
                        }
                        if (adminChat.get(p)) {
                            adminChat.put(p, false);
                            p.sendMessage(Main.INDEX + "§a이제부터 §b일반 채팅§a 모드입니다.");
                        } else {
                            adminChat.put(p, true);
                            p.sendMessage(Main.INDEX + "§a이제부터 §c관리자 채팅§a 모드입니다.");
                        }
                    }
                    case "공개" -> {
                        if (adminReveal.get(p)) {
                            p.sendMessage(Main.INDEX + "이미 모습을 드러낸 상태입니다.");
                            return;
                        }
                        adminReveal.put(p, true);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage(Component.text("§7[!] 관리자 §c" + p.getName() + "§7님이 관리자 모습을 드러내셨습니다.").append(Main.ADMIN_MSG_SYMBOL));
                            } else {
                                player.showPlayer(Main.getPlugin(Main.class), p);
                                player.sendMessage(Main.INDEX + p.getName() + "님이 접속하셨습니다.");
                            }
                        }
                    }
                }
            } else sender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static boolean isHiddenAdmin(@NotNull Player p) {
        return p.isOp() && !adminReveal.get(p);
    }

    public static HashMap<Player, Boolean> getAdminChat() {
        return adminChat;
    }

    public static HashMap<Player, Boolean> getAdminReveal() {
        return adminReveal;
    }
}
