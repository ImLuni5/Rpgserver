package main.eventhandler;

import main.Main;
import main.cmdhandler.RecipeHandler;
import main.cmdhandler.SettingsHandler;
import main.datahandler.SettingsData;
import main.recipehandler.Recipe;
import main.timerhandler.InvCooldownTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class IClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        switch (PlainTextComponentSerializer.plainText().serialize(e.getView().title())) {
            case "설정 GUI" -> {
                e.setCancelled(true);
                if (InvCooldownTimer.getInvClickCooldown().containsKey(p)) return;
                else InvCooldownTimer.getInvClickCooldown().put(p, 5);
                if (e.getSlot() == 19) {
                    p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));
                    SettingsData.nextSettings("dm", uuid);
                    SettingsHandler.openSettings(p);
                } else if (e.getSlot() == 21) {
                    p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));
                    SettingsData.nextSettings("party", uuid);
                    SettingsHandler.openSettings(p);
                } else if (e.getSlot() == 23) {
                    p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));
                    SettingsData.nextSettings("friend", uuid);
                    SettingsHandler.openSettings(p);
                } else if (e.getSlot() == 25) {
                    p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));
                    SettingsData.nextSettings("joinMsg", uuid);
                    SettingsHandler.openSettings(p);
                }
            }
            case "레시피 보기" -> e.setCancelled(true);
            case "레시피 만들기" -> {
                Inventory inventory = e.getClickedInventory();
                ItemStack i = e.getCurrentItem();
                if (inventory == null) {
                    e.setCancelled(true);
                    return;
                } if (i != null && i.isSimilar(RecipeHandler.blank)) {
                    e.setCancelled(true);
                    return;
                } if (i != null && i.getType().equals(Material.PAPER) && i.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                    e.setCancelled(true);
                    return;
                } if (i != null && i.isSimilar(RecipeHandler.make)) {
                    e.setCancelled(true);
                    List<ItemStack> tableItems = new ArrayList<>();
                    for (int l : RecipeHandler.recipeTableSlot) {
                        ItemStack tableItem = inventory.getItem(l);
                        if (tableItem != null && !tableItem.getType().equals(Material.AIR))
                            tableItems.add(inventory.getItem(l));
                    } if (tableItems.isEmpty()) {
                        p.sendMessage(Main.INDEX + "§c재료 아이템이 부족합니다!");
                        p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                        return;
                    } ItemStack result = inventory.getItem(RecipeHandler.recipeResultSlot);
                    if (result == null || result.getType().equals(Material.AIR)) {
                        p.sendMessage(Main.INDEX + "§c결과 아이템이 없습니다!");
                        p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                        return;
                    } p.closeInventory();
                    ItemStack desc = inventory.getItem(RecipeHandler.recipeDescSlot);
                    String key = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Objects.requireNonNull(desc).lore()).get(0)).replace("§d키: ", "");
                    String name = PlainTextComponentSerializer.plainText().serialize(desc.displayName()).replace("§e레시피: ", "");
                    List<ItemStack> ingrediants = new ArrayList<>();
                    for (int l : RecipeHandler.recipeTableSlot) {
                        if (inventory.getItem(l) != null) ingrediants.add(inventory.getItem(l));
                        else ingrediants.add(new ItemStack(Material.AIR));
                    }
                    new Recipe(key, name, result, ingrediants);
                    p.sendMessage(Main.INDEX + "§a레시피 제작에 성공했습니다.");
                }
            }
        }
    }
}
