package main.eventhandler;

import main.Main;
import main.cmdhandler.ColorHandler;
import main.cmdhandler.RecipeHandler;
import main.cmdhandler.SettingsHandler;
import main.datahandler.SettingsData;
import main.recipehandler.Recipe;
import main.timerhandler.InvCooldownTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            UUID uuid = p.getUniqueId();
            String title = PlainTextComponentSerializer.plainText().serialize(p.getOpenInventory().title());
            if (title.contains("레시피 보기")) {
                e.setCancelled(true);
                ItemStack i = e.getCurrentItem();
                if (i != null) {
                    String clicked = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().displayName());
                    if (clicked.contains("닫기")) p.closeInventory();
                    if (clicked.contains("레시피 수정하기")) {
                        String[] args = new String[]{"수정", PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(19)).lore()).get(0)).replace("§d키: ", "").replace("[", "").replace("]", "")};
                        RecipeHandler.onCommand(p, args);
                    }
                }
                if (i != null && PlainTextComponentSerializer.plainText().serialize(i.displayName()).contains("레시피 삭제하기")) {
                    e.setCancelled(true);
                    Inventory inventory = Bukkit.createInventory(null, 45, Component.text("§4레시피 삭제하기"));
                    for (int i2 = 0; i2 < 45; i2++) {
                        inventory.setItem(i2, Main.item(Material.RED_STAINED_GLASS_PANE, " ", null, 1, false));
                    }
                    inventory.setItem(13, e.getInventory().getItem(19));
                    inventory.setItem(30, Main.item(Material.GREEN_DYE, "§a취소", null, 1, false));
                    inventory.setItem(32, Main.item(Material.RED_DYE, "§4확인", Arrays.asList("§c레시피를 완전히 삭제합니다.", "§c§l다시 되돌릴 수 없습니다!!", "", "§e▶ 클릭해서 삭제하기"), 1, false));
                    p.openInventory(inventory);
                }
            } else if (title.contains("레시피 수정하기")) {
                ItemStack i = e.getCurrentItem();
                if (i != null && i.isSimilar(RecipeHandler.blank)) {
                    e.setCancelled(true);
                    return;
                } else if (i != null && i.getType().equals(Material.PAPER) && i.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                    e.setCancelled(true);
                    return;
                } else if (i != null && PlainTextComponentSerializer.plainText().serialize(i.displayName()).contains("변경 사항 저장하기")) {
                    e.setCancelled(true);
                    List<ItemStack> tableItems = new ArrayList<>();
                    for (int l : RecipeHandler.recipeTableSlot) {
                        ItemStack tableItem = e.getInventory().getItem(l);
                        if (tableItem != null && !tableItem.getType().equals(Material.AIR))
                            tableItems.add(e.getInventory().getItem(l));
                    }
                    if (tableItems.isEmpty()) {
                        p.sendMessage(Main.INDEX + "§c재료 아이템이 부족합니다!");
                        p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                        return;
                    }
                    ItemStack result = e.getInventory().getItem(RecipeHandler.recipeResultSlot);
                    if (result == null || result.getType().equals(Material.AIR)) {
                        p.sendMessage(Main.INDEX + "§c결과 아이템이 없습니다!");
                        p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                        return;
                    }
                    String key = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(RecipeHandler.recipeDescSlot)).lore()).get(0)).replace("§d키: ", "").replace("[", "").replace("]", "");
                    String name = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getInventory().getItem(RecipeHandler.recipeDescSlot)).displayName()).replace("§e레시피: ", "").replace("[", "").replace("]", "");
                    List<ItemStack> ingrediants = new ArrayList<>();
                    for (int l : RecipeHandler.recipeTableSlot) {
                        if (e.getInventory().getItem(l) != null) ingrediants.add(e.getInventory().getItem(l));
                        else ingrediants.add(new ItemStack(Material.AIR));
                    }
                    new Recipe(key, name, result, ingrediants);
                    p.closeInventory();
                    p.sendMessage(Main.INDEX + "§a성공적으로 레시피를 수정했습니다.");
                    p.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 100, 1));
                }
            } else if (title.contains("레시피 삭제하기")) {
                ItemStack i = e.getCurrentItem();
                e.setCancelled(true);
                if (i != null && PlainTextComponentSerializer.plainText().serialize(i.displayName()).contains("취소"))
                    p.closeInventory();
                if (i != null && PlainTextComponentSerializer.plainText().serialize(i.displayName()).contains("확인")) {
                    String key = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(13)).lore()).get(0)).replace("§d키: ", "").replace("[", "").replace("]", "");
                    Recipe recipe = Recipe.getRecipe(key);
                    if (recipe == null) p.sendMessage(Main.INDEX + "해당 키의 레시피를 찾을 수 없습니다.");
                    else {
                        recipe.remove();
                        p.closeInventory();
                        p.sendMessage(Main.INDEX + "§a레시피를 삭제했습니다.");
                        p.playSound(Sound.sound(Key.key("minecraft:block.anvil.destroy"), Sound.Source.MASTER, 100, 1));
                    }
                }
            }
            switch (title) {
                case "색깔 선택" -> {
                    e.setCancelled(true);
                    if (e.getSlot() == ColorHandler.whiteColorSlot) {
                        if (ColorHandler.getPlayerColor().get(p).equals(ChatColor.WHITE)) p.sendMessage(Main.INDEX + "§c이미 해당 색상을 사용하고 있습니다.");
                        else {
                            ColorHandler.getPlayerColor().put(p, ChatColor.WHITE);
                            p.displayName(Component.text(ChatColor.WHITE + p.getName()));
                            p.playerListName(Component.text(ChatColor.WHITE + p.getName()));
                        } p.closeInventory();
                    } else if (e.getSlot() == ColorHandler.darkRedColorSlot) {
                        if (!p.isOp()) p.sendMessage(Main.INDEX + "§c아직 이 색을 해금하지 않았습니다.");
                        else if (ColorHandler.getPlayerColor().get(p).equals(ChatColor.DARK_RED)) p.sendMessage(Main.INDEX + "§c이미 해당 색상을 사용하고 있습니다.");
                        else {
                            ColorHandler.getPlayerColor().put(p, ChatColor.DARK_BLUE);
                            p.displayName(Component.text(ChatColor.DARK_RED + p.getName()));
                            p.playerListName(Component.text(ChatColor.DARK_RED + p.getName()));
                        } p.closeInventory();
                    }
                }
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
                case "레시피 만들기" -> {
                    Inventory inventory = p.getOpenInventory().getTopInventory();
                    ItemStack i = e.getCurrentItem();
                    if (i != null && i.isSimilar(RecipeHandler.blank)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (i != null && i.getType().equals(Material.PAPER) && i.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (i != null && i.isSimilar(RecipeHandler.make)) {
                        e.setCancelled(true);
                        List<ItemStack> tableItems = new ArrayList<>();
                        for (int l : RecipeHandler.recipeTableSlot) {
                            ItemStack tableItem = inventory.getItem(l);
                            if (tableItem != null && !tableItem.getType().equals(Material.AIR))
                                tableItems.add(inventory.getItem(l));
                        }
                        if (tableItems.isEmpty()) {
                            p.sendMessage(Main.INDEX + "§c재료 아이템이 부족합니다!");
                            p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                            return;
                        }
                        ItemStack result = inventory.getItem(RecipeHandler.recipeResultSlot);
                        if (result == null || result.getType().equals(Material.AIR)) {
                            p.sendMessage(Main.INDEX + "§c결과 아이템이 없습니다!");
                            p.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.MASTER, 100, 0));
                            return;
                        }
                        p.closeInventory();
                        ItemStack desc = inventory.getItem(RecipeHandler.recipeDescSlot);
                        String key = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Objects.requireNonNull(desc).lore()).get(0)).replace("§d키: ", "").replace("[", "").replace("]", "");
                        String name = PlainTextComponentSerializer.plainText().serialize(desc.displayName()).replace("§e레시피: ", "").replace("[", "").replace("]", "");
                        List<ItemStack> ingrediants = new ArrayList<>();
                        for (int l : RecipeHandler.recipeTableSlot) {
                            if (inventory.getItem(l) != null) ingrediants.add(inventory.getItem(l));
                            else ingrediants.add(new ItemStack(Material.AIR));
                        }
                        new Recipe(key, name, result, ingrediants);
                        p.sendMessage(Main.INDEX + "§a레시피 제작에 성공했습니다.");
                        p.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1000, 1));
                    }
                }
            }
        } catch (Exception exception) {
            Main.printException(exception);
        }
    }
}
