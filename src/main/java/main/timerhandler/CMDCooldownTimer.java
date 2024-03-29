package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CMDCooldownTimer implements Runnable {

    private static final Map<Player, Integer> cmdClickStack = new HashMap<>();

    @Override
    public void run() {
        try {
            Main.commonMinusTimer(cmdClickStack);
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    public static Map<Player, Integer> getCMDClickStack() {
        return cmdClickStack;
    }
}
