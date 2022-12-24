package main.eventhandler;

import main.Main;
import main.recipehandler.Recipe;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CraftHandler implements Listener {
    public static final List<Integer> itemTables = Arrays.asList(11, 12, 13, 20, 21, 22, 29, 30, 31);
    public static final int resultTable = 24;
    public static final ItemStack blank = Main.item(Material.GRAY_STAINED_GLASS_PANE, " ", null, 1, false);
    private static final Inventory craftingUI; static {
        craftingUI = Bukkit.createInventory(null, 45, Component.text("제작대"));
        for (int i = 0; i < 45; i++) {
            craftingUI.setItem(i, blank);
        } for (int i : itemTables) {
            craftingUI.setItem(i, new ItemStack(Material.AIR));
        } craftingUI.setItem(resultTable, new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block != null && block.getType().equals(Material.CRAFTING_TABLE)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.openInventory(craftingUI);
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        if (e.getView().title().equals(Component.text("제작대"))) {
            ItemStack item = e.getCurrentItem();
            Inventory inventory = e.getClickedInventory();
            if (e.getSlot() == resultTable) {
                if (e.getCursor() == null || e.getCursor().getType().equals(Material.AIR)) {
                    for (int i : itemTables) {
                        if (inventory != null) {
                            inventory.setItem(i, new ItemStack(Material.AIR));
                        }
                    }
                } else {
                    e.setCancelled(true);
                    return;
                }
            }
            if (item != null && item.isSimilar(blank) || (inventory == null)) {
                e.setCancelled(true);
                return;
            } if (item != null && item.getType().equals(Material.AIR) && (e.getSlot() == resultTable)) {
                e.setCancelled(true);
                return;
            } for (String key : Recipe.recipeData.getStringList("Recipes.recipeKeys")) {
                Recipe recipe = Objects.requireNonNull(Recipe.getRecipe(key));
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    if (getOnTable(inventory).equals(recipe.getIngredients())) {
                        if (inventory.contains(blank)) inventory.setItem(resultTable, recipe.getResult());
                    } else inventory.setItem(resultTable, new ItemStack(Material.AIR));
                }, 1);
            }
        }
    }

    private static @NotNull List<ItemStack> getOnTable(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i : itemTables) {
            ItemStack toAdd = inventory.getItem(i);
            if (toAdd == null) items.add(new ItemStack(Material.AIR));
            else items.add(toAdd);
        } return items;
    }
}
