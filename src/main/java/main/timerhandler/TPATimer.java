package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TPATimer implements Runnable {
    private static final HashMap<Player, Player> tpaRequest = new HashMap<>();
    private static final HashMap<Player, Integer> tpaTimer = new HashMap<>();

    public static HashMap<Player, Player> getTpaRequest() {
        return tpaRequest;
    }

    public static HashMap<Player, Integer> getTpaTimer() {
        return tpaTimer;
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
                        reciver.sendMessage(Main.INDEX + "§e" + requster.getName() + "§c님에게 온 tpa 요청이 만료되었습니다.");
                        requster.sendMessage(Main.INDEX + "§e" + reciver.getName() + "§c님에게 보낸 tpa 요청이 만료되었습니다.");
                        tpaTimer.remove(entry.getKey());
                        tpaRequest.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
