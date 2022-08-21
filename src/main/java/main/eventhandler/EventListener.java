package main.eventhandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import main.Main;
import main.cmdhandler.PartyHandler;
import main.datahandler.FriendData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.joinMessage(Component.text(Main.INDEX + e.getPlayer().getName() + "님이 접속하셨습니다."));
        for (UUID uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 접속했습니다."));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.quitMessage(Component.text(Main.INDEX + e.getPlayer().getName() + "님이 퇴장하셨습니다."));
        for (UUID uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendActionBar(Component.text(Main.INDEX + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다."));
        }
        //파티에 소속되었는지 확인
        if (PartyHandler.getPlayerParty().containsKey(e.getPlayer())) {
            // 파티에서 나가기
            List<Player> playerList = PartyHandler.getParty().get(PartyHandler.getPlayerParty().get(e.getPlayer()));
            playerList.remove(e.getPlayer());
            if (playerList.isEmpty()) {
                PartyHandler.getParty().remove(PartyHandler.getPlayerParty().get(e.getPlayer()));
                PartyHandler.getIsPartyOwner().remove(e.getPlayer());
                PartyHandler.getPartyOwner().remove(PartyHandler.getPlayerParty().get(e.getPlayer()));
            } else PartyHandler.getParty().put(PartyHandler.getPlayerParty().get(e.getPlayer()), playerList);
            PartyHandler.getPlayerParty().remove(e.getPlayer());
            if (PartyHandler.getIsPartyOwner().getOrDefault(e.getPlayer(), false)) {
                Player randomPlayer = playerList.get(0);
                PartyHandler.getIsPartyOwner().remove(e.getPlayer());
                PartyHandler.getIsPartyOwner().put(e.getPlayer(), true);
                PartyHandler.getPartyOwner().put(PartyHandler.getPlayerParty().get(randomPlayer), randomPlayer);
                for (Player player : playerList)
                    player.sendMessage(Main.INDEX + "파티장이 파티에 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
            }
            for (Player player : playerList)
                player.sendMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속을 종료해서 파티에서 퇴장됐습니다.");
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        // 파티 채팅 모드인지 확인
        if (PartyHandler.getIsPartyChat().getOrDefault(e.getPlayer(), false)) {
            // 파티 채팅
            e.setCancelled(true);
            TextComponent component = (TextComponent) e.message();
            for (Player player : PartyHandler.getParty().get(PartyHandler.getPlayerParty().get(e.getPlayer())))
                player.sendMessage(PartyHandler.getPlayerParty().get(e.getPlayer()) + " | " + e.getPlayer().getName() + ": " + component.content());
        }
    }


}
