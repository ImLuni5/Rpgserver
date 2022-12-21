package main.timerhandler;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InvCooldownTimer implements Runnable {

    private static final Map<Player, Integer> invClickCooldown = new HashMap<>();

    @Override
    public void run() {
        if (!invClickCooldown.isEmpty()) {
            for (Map.Entry<Player, Integer> entry : invClickCooldown.entrySet()) {
                // 인벤토리 클릭 쿨타임 끝
                if (entry.getValue() <= 0) {
                    invClickCooldown.remove(entry.getKey());
                }
                // 인벤토리 클릭 쿨타임 0.05초(1틱)씩 감소
                invClickCooldown.put(entry.getKey(), entry.getValue() - 1);
            }
        }
    }

    public static Map<Player, Integer> getInvClickCooldown() {
        return invClickCooldown;
    }

}
