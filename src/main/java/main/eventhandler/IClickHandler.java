package main.eventhandler;

import main.cmdhandler.SettingsHandler;
import main.datahandler.SettingsData;
import main.timerhandler.InvCooldownTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class IClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().title().equals(Component.text("설정 GUI"))) {
            e.setCancelled(true);
            if (InvCooldownTimer.getInvClickCooldown().containsKey(p)) return;
            else InvCooldownTimer.getInvClickCooldown().put(p, 5);
            if (e.getSlot() == 19) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                SettingsHandler.onCommand(p);
                if (SettingsData.getPlayerSettings("dmOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 3);
                } else if (SettingsData.getPlayerSettings("dmOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 1);
                } else {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 2);
                }
            } else if (e.getSlot() == 21) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                SettingsHandler.onCommand(p);
                if (SettingsData.getPlayerSettings("partyOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 3);
                } else if (SettingsData.getPlayerSettings("partyOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 1);
                } else {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 2);
                }
            } else if (e.getSlot() == 23) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                SettingsHandler.onCommand(p);
                if (SettingsData.getPlayerSettings("friendOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 3);
                } else if (SettingsData.getPlayerSettings("friendOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 1);
                } else {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 2);
                }
            } else if (e.getSlot() == 25) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                SettingsHandler.onCommand(p);
                if (SettingsData.getPlayerSettings("joinMessageOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 3);
                } else if (SettingsData.getPlayerSettings("joinMessageOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 1);
                } else {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 2);
                }
            }
        }
    }
}
