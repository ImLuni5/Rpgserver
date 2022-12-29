package main.cmdhandler;

import main.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ColorHandler {
    public static HashMap<Player, ChatColor> getPlayerColor() {
        return playerColor;
    }
    public static final int whiteColorSlot = 10;
    public static final int darkRedColorSlot = 11;
    private static final String clickTo = "§e▶ 클릭해서 이 색깔로 설정하기";
    private static final HashMap<Player, ChatColor> playerColor = new HashMap<>();
    private static final ItemStack blank = Main.item(Material.GRAY_STAINED_GLASS_PANE, " ", null, 1, false);
    public static void onCommand(CommandSender sender) {
        if (sender instanceof Player p) {
            Inventory gui = Bukkit.createInventory(null, 27, Component.text("색깔 선택"));
            for (int i = 0; i < 9; i++) {
                gui.setItem(i, blank);
            } gui.setItem(9, blank);
            gui.setItem(17, blank);
            for (int i = 18; i < 27; i++) {
                gui.setItem(i, blank);
            } gui.setItem(whiteColorSlot, Main.item(Material.WHITE_DYE, "§f§l흰색", Arrays.asList("", "§f입장 시 주어지는 기본 색깔.", "", clickTo), 1, true));
            ItemStack darkRed;
            if (p.isOp()) darkRed = Main.item(Material.RED_DYE, "§4§l짙은 빨간색", Arrays.asList("", "§f관리자 전용으로 주어지는 색깔. ", "", clickTo), 1, true);
            else darkRed = Main.item(Material.GRAY_DYE, "§c???", List.of("§4해금되지 않음"), 1, false);
            gui.setItem(darkRedColorSlot, darkRed);
            for (int i = 12; i < 17; i++)
                gui.setItem(i, Main.item(Material.BARRIER, "§cCOMING SOON", null, 1, false));
            p.openInventory(gui);
        } else sender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
    }
}
