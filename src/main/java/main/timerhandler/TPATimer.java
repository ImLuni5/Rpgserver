package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TPATimer implements Runnable {
    private static final HashMap<Player, Player> tpaRequest = new HashMap<>();
    private static final HashMap<Player, Integer> tpaTimer = new HashMap<>();
    private static final HashMap<Player, Integer> tpaCooldown = new HashMap<>();

    public static HashMap<Player, Player> getTpaRequest() {
        return tpaRequest;
    }

    public static HashMap<Player, Integer> getTpaTimer() {
        return tpaTimer;
    }

    public static HashMap<Player, Integer> getTpaCooldown() {
        return tpaCooldown;
    }

    @Override
    public void run() {
        try {
            if (!tpaRequest.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : tpaTimer.entrySet()) {
                    tpaTimer.put(entry.getKey(), entry.getValue() - 1);
                    if (entry.getValue() <= 0) {
                        Player reciver = entry.getKey();
                        Player requster = tpaRequest.get(entry.getKey());
                        reciver.sendMessage(Main.INDEX + "§e" + requster.getName() + "§c님에게 온 텔레포트 요청이 만료되었습니다.");
                        requster.sendMessage(Main.INDEX + "§e" + reciver.getName() + "§c님에게 보낸 텔레포트 요청이 만료되었습니다.");
                        tpaTimer.remove(entry.getKey());
                        tpaRequest.remove(entry.getKey());
                    }
                }
            } if (!tpaCooldown.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : tpaCooldown.entrySet()) {
                    tpaCooldown.put(entry.getKey(), entry.getValue() - 1);
                    if (entry.getValue() <= 0) {
                        entry.getKey().sendMessage(Main.INDEX + "§7텔레포트 요청 쿨타임이 끝났습니다.");
                        tpaCooldown.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
