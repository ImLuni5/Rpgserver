package main.cmdhandler;

import main.Main;
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
import java.util.Objects;

public class RecipeHandler {
    public static final int recipeDescSlot = 19;
    public static final int recipeMakeSlot = 49;
    public static final int recipeResultSlot = 25;
    public static final List<Integer> recipeTableSlot = Arrays.asList(12, 13, 14, 21, 22, 23, 30, 31, 32);
    public static final ItemStack blank = Main.item(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ", null, 1, false);
    public static final ItemStack make = Main.item(Material.GOLD_INGOT, "§a레시피 제작하기", Arrays.asList("§c- §e재료 칸에는 적어도 1개 이상의 아이템이 있어야 합니다.", "§c- §e결과 아이템은 공기가 아니여야 합니다.", "", "§e▶ 클릭해서 제작하기"), 1, true);
    private static final String INVAILD_USAGE = Main.INDEX + "/레시피 추가 <키> <이름> - 레시피를 추가합니다.\n" + Main.INDEX + "/레시피 제거 <키> - 레시피를 제거합니다.\n" + Main.INDEX + "/레시피 보기 <키> - 특정 레시피를 직접 봅니다.\n" + Main.INDEX + "/레시피 수정 <키> - 해당 레시피를 수정합니다.\n" + Main.INDEX + "/레시피 목록 - 레시피 목록을 봅니다.\n" + Main.INDEX + "/레시피 리로드 - 레시피 파일을 다시 읽습니다.";
    public static void onCommand(@NotNull CommandSender commandSender, String @NotNull [] args) {
        try {
            if (!commandSender.isOp()) {
                commandSender.sendMessage(Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다!");
                return;
            }
            if (args.length == 0) {
                return;
            }
            switch (args[0]) {
                case "수정" -> {
                    if (commandSender instanceof Player p) {
                        if (wrong(commandSender, args)) return;
                        Recipe recipe = Recipe.getRecipe(args[1]);
                        Inventory gui = Bukkit.createInventory(null, 54, Component.text("레시피 수정하기: " + Objects.requireNonNull(recipe).getName()));
                        for (int i = 0; i < 54; i++) {
                            gui.setItem(i, blank);
                        }
                        int rawSlot = 0;
                        List<ItemStack> ingredients = recipe.getIngredients();
                        for (int i : recipeTableSlot) {
                            gui.setItem(i, ingredients.get(rawSlot));
                            rawSlot++;
                        }
                        gui.setItem(recipeResultSlot, recipe.getResult());
                        gui.setItem(recipeDescSlot, Main.item(Material.PAPER, "§e레시피: " + recipe.getName(), List.of("§d키: " + args[1]), 1, true));
                        gui.setItem(recipeMakeSlot, Main.item(Material.EMERALD, "§a변경 사항 저장하기", Arrays.asList("§e레시피의 변경 사항을 저장합니다.", "", "§b▶ 클릭해서 저장하기"), 1, true));
                        p.openInventory(gui);
                    } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 실행할 수 있습니다.");
                }
                case "리로드" -> {
                    Recipe.loadData();
                    commandSender.sendMessage(Main.INDEX + "레시피 목록을 다시 불러왔습니다.");
                }
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
                        }
                        Inventory gui = Bukkit.createInventory(null, 54, Component.text("레시피 만들기"));
                        for (int i = 0; i < 54; i++) {
                            gui.setItem(i, blank);
                        }
                        for (int i : recipeTableSlot) {
                            gui.setItem(i, new ItemStack(Material.AIR));
                        }
                        gui.setItem(recipeResultSlot, new ItemStack(Material.AIR));
                        gui.setItem(recipeDescSlot, Main.item(Material.PAPER, "§e레시피: " + args[2], List.of("§d키: " + args[1]), 1, true));
                        gui.setItem(recipeMakeSlot, make);
                        p.openInventory(gui);
                    } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
                }
                case "제거" -> {
                    if (wrong(commandSender, args)) return;
                    Recipe recipe = Recipe.getRecipe(args[1]);
                    commandSender.sendMessage(Main.INDEX + "§e레시피 §6\"" + Objects.requireNonNull(recipe).getName() + "§6\"§e을(를) 제거했습니다.");
                    recipe.remove();
                }
                case "보기" -> {
                    if (commandSender instanceof Player p) {
                        if (wrong(p, args)) return;
                        Recipe recipe = Recipe.getRecipe(args[1]);
                        Inventory gui = Bukkit.createInventory(null, 54, Component.text("레시피 보기: " + Objects.requireNonNull(recipe).getName() + " §1(" + args[1] + ")"));
                        for (int i = 0; i < 54; i++) {
                            gui.setItem(i, Main.item(Material.BLACK_STAINED_GLASS_PANE, " ", null, 1, false));
                        }
                        int invSlot = 0;
                        for (int i : recipeTableSlot) {
                            gui.setItem(i, recipe.getIngredients().get(invSlot));
                            invSlot++;
                        }
                        gui.setItem(recipeResultSlot, recipe.getResult());
                        gui.setItem(19, Main.item(Material.PAPER, "§e레시피: " + recipe.getName(), List.of("§d키: " + args[1]), 1, true));
                        gui.setItem(50, Main.item(Material.FEATHER, "§e레시피 수정하기", Arrays.asList("§a레시피의 결과 아이템, 재료를 수정합니다.", "", "§b▶ 클릭해서 수정 메뉴 열기"), 1, true));
                        gui.setItem(48, Main.item(Material.RED_DYE, "§c닫기", null, 1, false));
                        gui.setItem(53, Main.item(Material.REDSTONE, "§4레시피 삭제하기", Arrays.asList("§c레시피를 삭제합니다.", "§c§l이 작업은 되돌릴 수 없습니다!", "", "§e▶ 클릭해서 삭제하기"), 1, false));
                        p.openInventory(gui);
                    } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
                }
                case "목록" -> {
                    StringBuilder message = new StringBuilder();
                    for (String s : Recipe.recipeData.getStringList("Recipes.recipeKeys")) {
                        if (message.length() < 1) message.append("§b[").append(s).append("]");
                        else message.append("§b, [").append(s).append("]");
                    }
                    commandSender.sendMessage(Main.INDEX + "§a레시피§a(" + Recipe.recipeData.getStringList("Recipes.recipeKeys").size() + "): " + message);
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    private static boolean wrong(CommandSender commandSender, String @NotNull [] args) {
        if (args.length < 2) {
            commandSender.sendMessage(INVAILD_USAGE);
            return true;
        }
        if (Recipe.getRecipe(args[1]) == null) {
            commandSender.sendMessage(Main.INDEX + "§c해당 키의 레시피가 존재하지 않습니다.");
            return true;
        } return false;
    }
}
