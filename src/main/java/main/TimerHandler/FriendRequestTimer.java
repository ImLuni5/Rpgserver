package main.TimerHandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FriendRequestTimer implements Runnable{
    public static HashMap<Player, Integer> playerInviteTime = new HashMap<>();
    public static HashMap<Player, Player> playerInviteOwner = new HashMap<>();

    @Override
    public void run() {
        if (!playerInviteTime.isEmpty()) {
            for (Player player : playerInviteTime.keySet()) {
                // 파티 초대 시간 1초씩 감소
                playerInviteTime.put(player, playerInviteTime.get(player) - 1);
                // 파티 초대 만료
                if (playerInviteTime.get(player) <= 0) {
                    player.sendMessage(Main.index + "친구 추가 요청이 만료되었습니다.");
                    playerInviteTime.remove(player);
                    playerInviteOwner.remove(player);
                }
            }
        }
    }
}
