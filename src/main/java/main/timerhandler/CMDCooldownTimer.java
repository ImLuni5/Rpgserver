package main.timerhandler;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CMDCooldownTimer implements Runnable {

    private static final Map<Player, Integer> cmdClickStack = new HashMap<>();

    @Override
    public void run() {
        if (!cmdClickStack.isEmpty()) {
            for (Map.Entry<Player, Integer> entry : cmdClickStack.entrySet()) {
                cmdClickStack.put(entry.getKey(), entry.getValue() - 1);
                if (entry.getValue() <= 0) {
                    cmdClickStack.remove(entry.getKey());
                }
            }
        }
    }
    public static Map<Player, Integer> getCMDClickStack() {
        return cmdClickStack;
    }
}
