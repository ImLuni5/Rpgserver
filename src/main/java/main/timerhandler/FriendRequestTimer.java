package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestTimer implements Runnable {
    private static final Map<Player, Integer> playerInviteTime = new HashMap<>();
    private static final Map<Player, Player> playerInviteOwner = new HashMap<>();

    @Override
    public void run() {
        try {
            if (!playerInviteTime.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : playerInviteTime.entrySet()) {
                    // 파티 초대 시간 1초씩 감소
                    playerInviteTime.put(entry.getKey(), entry.getValue() - 1);
                    // 파티 초대 만료
                    if (entry.getValue() <= 0) {
                        entry.getKey().sendMessage(Main.INDEX + "친구 추가 요청이 만료되었습니다.");
                        playerInviteTime.remove(entry.getKey());
                        playerInviteOwner.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static Map<Player, Integer> getPlayerInviteTime() {
        return playerInviteTime;
    }

    public static Map<Player, Player> getPlayerInviteOwner() {
        return playerInviteOwner;
    }
}
