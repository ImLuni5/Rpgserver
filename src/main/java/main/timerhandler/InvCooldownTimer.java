package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InvCooldownTimer implements Runnable {

    private static final Map<Player, Integer> invClickCooldown = new HashMap<>();

    @Override
    public void run() {
        try {
            Main.commonMinusTimer(invClickCooldown);
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static Map<Player, Integer> getInvClickCooldown() {
        return invClickCooldown;
    }

}
