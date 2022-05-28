package main.EventHandler;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.joinMessage(Component.text("§6[§e§lLemon§6]§f " + e.getPlayer().getName() + "님이 접속하셨습니다."));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.quitMessage(Component.text("§6[§e§lLemon§6]§f " + e.getPlayer().getName() + "님이 퇴장하셨습니다."));
    }


}
