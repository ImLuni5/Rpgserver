package main.EventHandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import main.CMDHandler.PartyHandler;
import main.DataHandler.FriendData;
import main.Main;
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
        e.joinMessage(Component.text(Main.index + e.getPlayer().getName() + "님이 접속하셨습니다."));
        for (UUID uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendActionBar(Component.text(Main.index + "친구 " + e.getPlayer().getName() + "님이 접속했습니다."));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.quitMessage(Component.text(Main.index + e.getPlayer().getName() + "님이 퇴장하셨습니다."));
        for (UUID uuid : FriendData.getPlayerFriendList(e.getPlayer().getUniqueId())) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendActionBar(Component.text(Main.index + "친구 " + e.getPlayer().getName() + "님이 퇴장했습니다."));
        }
        //파티에 소속되었는지 확인
        if (PartyHandler.playerParty.containsKey(e.getPlayer())) {
            // 파티에서 나가기
            List<Player> playerList = PartyHandler.party.get(PartyHandler.playerParty.get(e.getPlayer()));
            playerList.remove(e.getPlayer());
            if (playerList.isEmpty()) {
                PartyHandler.party.remove(PartyHandler.playerParty.get(e.getPlayer()));
                PartyHandler.isPartyOwner.remove(e.getPlayer());
                PartyHandler.partyOwner.remove(PartyHandler.playerParty.get(e.getPlayer()));
            } else PartyHandler.party.put(PartyHandler.playerParty.get(e.getPlayer()), playerList);
            PartyHandler.playerParty.remove(e.getPlayer());
            if (PartyHandler.isPartyOwner.getOrDefault(e.getPlayer(), false)) {
                Player randomPlayer = playerList.get(0);
                PartyHandler.isPartyOwner.remove(e.getPlayer());
                PartyHandler.isPartyOwner.put(e.getPlayer(), true);
                PartyHandler.partyOwner.put(PartyHandler.playerParty.get(randomPlayer), randomPlayer);
                for (Player player : playerList) player.sendMessage(Main.index + "파티장이 파티에 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
            }
            for (Player player : playerList) player.sendMessage(Main.index + e.getPlayer().getName() + "님이 접속을 종료해서 파티에서 퇴장됐습니다.");
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        // 파티 채팅 모드인지 확인
        if (PartyHandler.isPartyChat.getOrDefault(e.getPlayer(), false)) {
            // 파티 채팅
            e.setCancelled(true);
            TextComponent component = (TextComponent) e.message();
            for (Player player : PartyHandler.party.get(PartyHandler.playerParty.get(e.getPlayer()))) player.sendMessage(PartyHandler.playerParty.get(e.getPlayer()) + " | " + e.getPlayer().getName() + ": " + component.content());
        }
    }


}
