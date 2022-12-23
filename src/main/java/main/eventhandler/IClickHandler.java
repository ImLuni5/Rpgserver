package main.eventhandler;

import main.cmdhandler.SettingsHandler;
import main.datahandler.SettingsData;
import main.timerhandler.InvCooldownTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        if (e.getView().title().equals(Component.text("설정 GUI"))) {
            e.setCancelled(true);
            if (InvCooldownTimer.getInvClickCooldown().containsKey(p)) return;
            else InvCooldownTimer.getInvClickCooldown().put(p, 5);
            if (e.getSlot() == 19) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 100, 1);
                SettingsData.nextSettings("dm", uuid);
                SettingsHandler.openSettings(p);
            } else if (e.getSlot() == 21) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER,100, 1);
                SettingsData.nextSettings("party", uuid);
                SettingsHandler.openSettings(p);
            } else if (e.getSlot() == 23) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER,100, 1);
                SettingsData.nextSettings("friend", uuid);
                SettingsHandler.openSettings(p);
            } else if (e.getSlot() == 25) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER,100, 1);
                SettingsData.nextSettings("joinMsg", uuid);
                SettingsHandler.openSettings(p);
            }
        }
    }
}
