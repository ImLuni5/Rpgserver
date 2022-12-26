package main.cmdhandler;

import main.Main;
import main.datahandler.SettingsData;
import main.datahandler.SettingsData.DmOption;
import main.datahandler.SettingsData.FriendOption;
import main.datahandler.SettingsData.JoinMsgOption;
import main.datahandler.SettingsData.PartyOption;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SettingsHandler {
    public static void onCommand(CommandSender commandSender) {
        try {
            if (commandSender instanceof Player p) {
                openSettings(p);
            } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
        } catch (Exception e) {
            Main.printException(e);
        }
    }
    public static void openSettings(@NotNull Player p) {
        try {
            Inventory gui = Bukkit.createInventory(null, 36, Component.text("설정 GUI"));
            for (int i = 0; i < 36; i++) {
                gui.setItem(i, Main.item(Material.WHITE_STAINED_GLASS_PANE, " ", null, 1, false));
            }
            gui.setItem(10, Main.item(Material.DIAMOND, "§e귓속말 설정", Arrays.asList("§7귓속말을 누구에게 받을지 설정합니다.", "§7아래 종이를 눌러 설정할 수 있습니다."), 1, false));
            gui.setItem(12, Main.item(Material.LAPIS_LAZULI, "§e파티 초대 설정", Arrays.asList("§7파티 초대를 누구에게 받을지 설정합니다.", "§7아래 종이를 눌러 설정할 수 있습니다."), 1, false));
            gui.setItem(14, Main.item(Material.REDSTONE, "§e친구 요청 설정", Arrays.asList("§7친구 요청을 누구에게 받을지 설정합니다.", "§7아래 종이를 눌러 설정할 수 있습니다."), 1, false));
            gui.setItem(16, Main.item(Material.EMERALD, "§e접속 메시지 설정", Arrays.asList("§7접속 메시지가 보이는 조건을 설정합니다.", "§7아래 종이를 눌러 설정할 수 있습니다."), 1, false));

            ItemStack dmOption;
            String playerDmSet = SettingsData.getSettings("dm", p.getUniqueId());
            DmOption playerDmOption = DmOption.valueOf(playerDmSet);
            if (playerDmOption == DmOption.FRIENDS) {
                dmOption = Main.item(Material.PAPER, "§e친구에게만 귓속말 받기", Arrays.asList("§7오직 당신의 친구만 ", "§7당신에게 귓속말을 보낼 수 있습니다.", "§7당신 또한 친구에게만", "§7귓속말을 보낼 수 있습니다.", " ", "§a  모두에게서 받기", "§b▶ 친구에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §c받지 않기§e로 변경합니다."), 1, true);
            } else if (playerDmOption == DmOption.NEVER) {
                dmOption = Main.item(Material.PAPER, "§c누구에게도 귓속말 받지 않기", Arrays.asList("§7그 누구도 당신에게", "§7귓속말을 보낼 수 없습니다.", "§7당신 또한 그 누구에게도", "§7귓속말을 보낼 수 없습니다.", " ", "§a  모두에게서 받기", "§b  친구에게만 받기", "§c▶ 받지 않기", " ", "§e클릭 시 §a모두에게서 받기§e로 변경합니다."), 1, true);
            } else {
                dmOption = Main.item(Material.PAPER, "§a모두에게서 귓속말 받기", Arrays.asList("§7아무 플레이어나 당신에게", "§7귓속말을 보낼 수 있습니다.", "§7당신 또한 누구에게나", "§7귓속말을 보낼 수 있습니다.", " ", "§a▶ 모두에게서 받기", "§b  친구에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §b친구에게만 받기§e로 변경합니다."), 1, true);
            }
            gui.setItem(19, dmOption);

            ItemStack partyOption;
            String playerPartySet = SettingsData.getSettings("party", p.getUniqueId());
            PartyOption playerPartyOption = PartyOption.valueOf(playerPartySet);
            if (playerPartyOption == PartyOption.FRIENDS) {
                partyOption = Main.item(Material.PAPER, "§e친구에게만 파티 초대 받기", Arrays.asList("§7오직 당신의 친구만 ", "§7당신에게 파티 초대를 보낼 수 있습니다.", " ", "§a  모두에게서 받기", "§b▶ 친구에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §c받지 않기§e로 변경합니다."), 1, true);
            } else if (playerPartyOption == PartyOption.NEVER) {
                partyOption = Main.item(Material.PAPER, "§c누구에게도 파티 초대 받지 않기", Arrays.asList("§7그 누구도 당신에게", "§7파티 초대를 보낼 수 없습니다.", " ", "§a  모두에게서 받기", "§b  친구에게만 받기", "§c▶ 받지 않기", " ", "§e클릭 시 §a모두에게서 받기§e로 변경합니다."), 1, true);
            } else {
                partyOption = Main.item(Material.PAPER, "§a모두에게서 파티 초대 받기", Arrays.asList("§7아무 플레이어나 당신에게", "§7파티 초대를 보낼 수 있습니다.", " ", "§a▶ 모두에게서 받기", "§b  친구에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §b친구에게만 받기§e로 변경합니다."), 1, true);
            }
            gui.setItem(21, partyOption);

            ItemStack friendOption;
            String playerFriendSet = SettingsData.getSettings("friend", p.getUniqueId());
            FriendOption playerFriendOption = FriendOption.valueOf(playerFriendSet);
            if (playerFriendOption == FriendOption.PARTY) {
                friendOption = Main.item(Material.PAPER, "§e파티원에게만 친구 요청 받기", Arrays.asList("§7오직 당신의 파티원만 ", "§7당신에게 친구 요청을 보낼 수 있습니다.", " ", "§a  모두에게서 받기", "§b▶ 파티원에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §c받지 않기§e로 변경합니다."), 1, true);
            } else if (playerFriendOption == FriendOption.NEVER) {
                friendOption = Main.item(Material.PAPER, "§c누구에게도 친구 요청 받지 않기", Arrays.asList("§7그 누구도 당신에게", "§7친구 요청을 보낼 수 없습니다.", " ", "§a  모두에게서 받기", "§b  파티원에게만 받기", "§c▶ 받지 않기", " ", "§e클릭 시 §a모두에게서 받기§e로 변경합니다."), 1, true);
            } else {
                friendOption = Main.item(Material.PAPER, "§a모두에게서 친구 요청 받기", Arrays.asList("§7아무 플레이어나 당신에게", "§7친구 요청을 보낼 수 있습니다.", " ", "§a▶ 모두에게서 받기", "§b  파티원에게만 받기", "§c  받지 않기", " ", "§e클릭 시 §b파티원에게만 받기§e로 변경합니다."), 1, true);
            }
            gui.setItem(23, friendOption);

            ItemStack joinMsgOption;
            String playerJoinSet = SettingsData.getSettings("joinMsg", p.getUniqueId());
            JoinMsgOption playerJoinOption = JoinMsgOption.valueOf(playerJoinSet);
            if (playerJoinOption == JoinMsgOption.FRIENDS) {
                joinMsgOption = Main.item(Material.PAPER, "§e친구의 접속/퇴장 메시지만 보기", Arrays.asList("§7오직 당신의 친구의", "§7접속 메시지만을 볼 수 있습니다.", " ", "§a  모두 보기", "§b▶ 친구만 보기", "§c  보지 않기", " ", "§e클릭 시 §c보지 않기§e로 변경합니다."), 1, true);
            } else if (playerJoinOption == JoinMsgOption.NEVER) {
                joinMsgOption = Main.item(Material.PAPER, "§c접속/퇴장 메시지 보지 않기", Arrays.asList("§7다른 플레이어의 접속 메시지와", "§7퇴장 메시지를 보지 않습니다.", " ", "§a  모두 보기", "§b  친구만 보기", "§c▶ 보지 않기", " ", "§e클릭 시 §a모두에게서 받기§e로 변경합니다."), 1, true);
            } else {
                joinMsgOption = Main.item(Material.PAPER, "§a모두의 접속/퇴장 메시지 보기", Arrays.asList("§7모든 플레이어의 접속 메시지와", "§7퇴장 메시지를 볼 수 있습니다.", " ", "§a▶ 모두 보기", "§b  친구만 보기", "§c  보지 않기", " ", "§e클릭 시 §b친구에게만 받기§e로 변경합니다."), 1, true);
            }
            gui.setItem(25, joinMsgOption);

            p.openInventory(gui);
        } catch (Exception e) {
            Main.printException(e);
        }
    }

}
