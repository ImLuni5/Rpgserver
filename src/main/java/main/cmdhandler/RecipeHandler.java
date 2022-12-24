package main.cmdhandler;

import main.Main;
import main.eventhandler.CraftHandler;
import main.recipehandler.Recipe;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RecipeHandler {
    public static final int recipeDescSlot = 19;
    public static final int recipeMakeSlot = 49;
    public static final int recipeResultSlot = 25;
    public static final List<Integer> recipeTableSlot = Arrays.asList(12, 13, 14, 21, 22, 23, 30, 31, 32);
    public static final ItemStack blank = Main.item(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ", List.of(), 1, false);
    public static final ItemStack make = Main.item(Material.GOLD_INGOT, "§a레시피 제작하기", Arrays.asList("§c- §e재료 칸에는 적어도 1개 이상의 아이템이 있어야 합니다.", "§c- §e결과 아이템은 공기가 아니여야 합니다.", "", "§e▶ 클릭해서 제작하기"), 1, true);
    private static final String INVAILD_USAGE = Main.INDEX + "/레시피 추가 <키> <이름> - 레시피를 추가합니다.\n" + Main.INDEX + "/레시피 제거 <키> - 레시피를 제거합니다.\n" + Main.INDEX + "/레시피 보기 <키> - 특정 레시피를 직접 봅니다.\n" + Main.INDEX + "/레시피 목록 - 레시피 목록을 봅니다.";
    public static void onCommand(@NotNull CommandSender commandSender, String @NotNull [] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다!");
            return;
        }
        if (args.length == 0) {
            return;
        }
        switch (args[0]) {
            case "추가" -> {
                if (commandSender instanceof Player p) {
                    if (args.length < 3) {
                        p.sendMessage(INVAILD_USAGE);
                        return;
                    }
                    List<String> keys = Recipe.recipeData.getStringList("Recipes.recipeKeys");
                    if (keys.contains(args[1])) {
                        p.sendMessage(Main.INDEX + "§c해당 키의 레시피가 이미 존재합니다!");
                        return;
                    } Inventory gui = Bukkit.createInventory(null, 54, Component.text("레시피 만들기"));
                    for (int i = 0; i < 54; i++) {
                        gui.setItem(i, blank);
                    } for (int i : recipeTableSlot) {
                        gui.setItem(i, new ItemStack(Material.AIR));
                    } gui.setItem(recipeResultSlot, new ItemStack(Material.AIR));
                    gui.setItem(recipeDescSlot, Main.item(Material.PAPER, "§e레시피: " + args[2], List.of("§d키: " + args[1]), 1, true));
                    gui.setItem(recipeMakeSlot, make);
                    p.openInventory(gui);
                } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
            } case "제거" -> {
                if (args.length < 2) {
                    commandSender.sendMessage(INVAILD_USAGE);
                    return;
                }
                Recipe recipe = Recipe.getRecipe(args[1]);
                if (recipe == null) {
                    commandSender.sendMessage(Main.INDEX + "§c해당 키의 레시피가 존재하지 않습니다.");
                    return;
                } commandSender.sendMessage(Main.INDEX + "§a레시피" + recipe.getName() + "§a를 제거했습니다.");
                recipe.remove();
            } case "보기" -> {
                if (commandSender instanceof Player p) {
                    if (args.length < 2) {
                        p.sendMessage(INVAILD_USAGE);
                        return;
                    }
                    Recipe recipe = Recipe.getRecipe(args[1]);
                    if (recipe == null) {
                        p.sendMessage(Main.INDEX + "§c해당 키의 레시피가 존재하지 않습니다.");
                        return;
                    }
                    Inventory gui = Bukkit.createInventory(null, 45, Component.text("레시피 보기"));
                    for (int i = 0; i < 45; i++) {
                        gui.setItem(i, Main.item(Material.BLACK_STAINED_GLASS_PANE, " ", null, 1, false));
                    } int invSlot = 0;
                    for (int i : CraftHandler.itemTables) {
                        gui.setItem(i, recipe.getIngredients().get(invSlot));
                        invSlot++;
                    } gui.setItem(CraftHandler.resultTable, recipe.getResult());
                    p.openInventory(gui);
                } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
            } case "목록" -> {
                StringBuilder message = new StringBuilder();
                for (String s : Recipe.recipeData.getStringList("Recipes.recipeKeys")) {
                    if (message.length() < 1) message.append("§b[").append(s).append("]");
                    else message.append("§b, [").append(s).append("]");
                }
                commandSender.sendMessage(Main.INDEX + "§a레시피§a(" + Recipe.recipeData.getStringList("Recipes.recipeKeys").size() + "): " + message);
            }
        }
    }
}