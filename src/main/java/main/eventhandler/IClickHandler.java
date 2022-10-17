package main.eventhandler;

import main.datahandler.SettingsData;
import main.timerhandler.InvCooldownTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class IClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().title().equals(Component.text("설정 GUI"))) {
            e.setCancelled(true);
            if (InvCooldownTimer.getInvClickCooldown().containsKey(p)) return;
            else InvCooldownTimer.getInvClickCooldown().put(p, 5);
            if (e.getSlot() == 19) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                ItemStack dmOptionSet = new ItemStack(Material.PAPER);
                ItemMeta dmOptionSetMeta = dmOptionSet.getItemMeta();
                if (SettingsData.getPlayerSettings("dmOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 3);
                    dmOptionSetMeta.displayName(Component.text("§c누구에게도 귓속말 받지 않기"));
                    dmOptionSetMeta.lore(Arrays.asList(Component.text("§7그 누구도 당신에게"), Component.text("§7귓속말을 보낼 수 없습니다."), Component.text("§7당신 또한 그 누구에게도"), Component.text("§7귓속말을 보낼 수 없습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b  친구에게만 받기"), Component.text("§c▶ 받지 않기"), Component.text(" "), Component.text("§e클릭 시 §a모두에게서 받기§e로 변경합니다.")));
                } else if (SettingsData.getPlayerSettings("dmOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 1);
                    dmOptionSetMeta.displayName(Component.text("§a모두에게서 귓속말 받기"));
                    dmOptionSetMeta.lore(Arrays.asList(Component.text("§7아무 플레이어나 당신에게"), Component.text("§7귓속말을 보낼 수 있습니다."), Component.text("§7당신 또한 누구에게나"), Component.text("§7귓속말을 보낼 수 있습니다."), Component.text(" "), Component.text("§a▶ 모두에게서 받기"), Component.text("§b  친구에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §b친구에게만 받기§e로 변경합니다.")));
                } else {
                    SettingsData.setSettings("dmOption", p.getUniqueId(), 2);
                    dmOptionSetMeta.displayName(Component.text("§e친구에게만 귓속말 받기"));
                    dmOptionSetMeta.lore(Arrays.asList(Component.text("§7오직 당신의 친구만 "), Component.text("§7당신에게 귓속말을 보낼 수 있습니다."), Component.text("§7당신 또한 친구에게만"), Component.text("§7귓속말을 보낼 수 있습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b▶ 친구에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §c받지 않기§e로 변경합니다.")));
                }
                dmOptionSet.setItemMeta(dmOptionSetMeta);
                p.getOpenInventory().setItem(23, dmOptionSet);
            } else if (e.getSlot() == 21) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                ItemStack partyOptionSet = new ItemStack(Material.PAPER);
                ItemMeta partyOptionSetMeta = partyOptionSet.getItemMeta();
                if (SettingsData.getPlayerSettings("partyOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 3);
                    partyOptionSetMeta.displayName(Component.text("§c누구에게도 파티 초대 받지 않기"));
                    partyOptionSetMeta.lore(Arrays.asList(Component.text("§7그 누구도 당신에게"), Component.text("§7파티 초대를 보낼 수 없습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b  친구에게만 받기"), Component.text("§c▶ 받지 않기"), Component.text(" "), Component.text("§e클릭 시 §a모두에게서 받기§e로 변경합니다.")));
                } else if (SettingsData.getPlayerSettings("partyOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 1);
                    partyOptionSetMeta.displayName(Component.text("§a모두에게서 파티 초대 받기"));
                    partyOptionSetMeta.lore(Arrays.asList(Component.text("§7아무 플레이어나 당신에게"), Component.text("§7파티 초대를 보낼 수 있습니다."), Component.text(" "), Component.text("§a▶ 모두에게서 받기"), Component.text("§b  친구에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §b친구에게만 받기§e로 변경합니다.")));
                } else {
                    SettingsData.setSettings("partyOption", p.getUniqueId(), 2);
                    partyOptionSetMeta.displayName(Component.text("§e친구에게만 파티 초대 받기"));
                    partyOptionSetMeta.lore(Arrays.asList(Component.text("§7오직 당신의 친구만 "), Component.text("§7당신에게 파티 초대를 보낼 수 있습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b▶ 친구에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §c받지 않기§e로 변경합니다.")));
                }
                partyOptionSet.setItemMeta(partyOptionSetMeta);
                p.getOpenInventory().setItem(21, partyOptionSet);
            } else if (e.getSlot() == 23) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                ItemStack friendOptionSet = new ItemStack(Material.PAPER);
                ItemMeta friendOptionSetMeta = friendOptionSet.getItemMeta();
                if (SettingsData.getPlayerSettings("friendOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 3);
                    friendOptionSetMeta.displayName(Component.text("§c누구에게도 친구 요청 받지 않기"));
                    friendOptionSetMeta.lore(Arrays.asList(Component.text("§7그 누구도 당신에게"), Component.text("§7친구 요청을 보낼 수 없습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b  파티원에게만 받기"), Component.text("§c▶ 받지 않기"), Component.text(" "), Component.text("§e클릭 시 §a모두에게서 받기§e로 변경합니다.")));
                } else if (SettingsData.getPlayerSettings("friendOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 1);
                    friendOptionSetMeta.displayName(Component.text("§a모두에게서 친구 요청 받기"));
                    friendOptionSetMeta.lore(Arrays.asList(Component.text("§7아무 플레이어나 당신에게"), Component.text("§7친구 요청을 보낼 수 있습니다."), Component.text(" "), Component.text("§a▶ 모두에게서 받기"), Component.text("§b  파티원에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §b파티원에게만 받기§e로 변경합니다.")));
                } else {
                    SettingsData.setSettings("friendOption", p.getUniqueId(), 2);
                    friendOptionSetMeta.displayName(Component.text("§e파티원에게만 친구 요청 받기"));
                    friendOptionSetMeta.lore(Arrays.asList(Component.text("§7오직 당신의 파티원만 "), Component.text("§7당신에게 친구 요청을 보낼 수 있습니다."), Component.text(" "), Component.text("§a  모두에게서 받기"), Component.text("§b▶ 파티원에게만 받기"), Component.text("§c  받지 않기"), Component.text(" "), Component.text("§e클릭 시 §c받지 않기§e로 변경합니다.")));
                }
                friendOptionSet.setItemMeta(friendOptionSetMeta);
                p.getOpenInventory().setItem(23, friendOptionSet);
            } else if (e.getSlot() == 25) {
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                ItemStack joinMessageOptionSet = new ItemStack(Material.PAPER);
                ItemMeta joinMessageOptionSetMeta = joinMessageOptionSet.getItemMeta();
                if (SettingsData.getPlayerSettings("joinMessageOption", p.getUniqueId()) == 2) {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 3);
                    joinMessageOptionSetMeta.displayName(Component.text("§c접속/퇴장 메시지 보지 않기"));
                    joinMessageOptionSetMeta.lore(Arrays.asList(Component.text("§7다른 플레이어의 접속 메시지와"), Component.text("§7퇴장 메시지를 보지 않습니다."), Component.text(" "), Component.text("§a  모두 보기"), Component.text("§b  친구만 보기"), Component.text("§c▶ 보지 않기"), Component.text(" "), Component.text("§e클릭 시 §a모두에게서 받기§e로 변경합니다.")));
                } else if (SettingsData.getPlayerSettings("joinMessageOption", p.getUniqueId()) == 3) {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 1);
                    joinMessageOptionSetMeta.displayName(Component.text("§a모두의 접속/퇴장 메시지 보기"));
                    joinMessageOptionSetMeta.lore(Arrays.asList(Component.text("§7모든 플레이어의 접속 메시지와"), Component.text("§7퇴장 메시지를 볼 수 있습니다."), Component.text(" "), Component.text("§a▶ 모두 보기"), Component.text("§b  친구만 보기"), Component.text("§c  보지 않기"), Component.text(" "), Component.text("§e클릭 시 §b친구에게만 받기§e로 변경합니다.")));
                } else {
                    SettingsData.setSettings("joinMessageOption", p.getUniqueId(), 2);
                    joinMessageOptionSetMeta.displayName(Component.text("§e친구의 접속/퇴장 메시지만 보기"));
                    joinMessageOptionSetMeta.lore(Arrays.asList(Component.text("§7오직 당신의 친구의"), Component.text("§7접속 메시지만을 볼 수 있습니다."), Component.text(" "), Component.text("§a  모두 보기"), Component.text("§b▶ 친구만 보기"), Component.text("§c  보지 않기"), Component.text(" "), Component.text("§e클릭 시 §c보지 않기§e로 변경합니다.")));
                }
                joinMessageOptionSet.setItemMeta(joinMessageOptionSetMeta);
                p.getOpenInventory().setItem(25, joinMessageOptionSet);
            }
        }
    }
}
