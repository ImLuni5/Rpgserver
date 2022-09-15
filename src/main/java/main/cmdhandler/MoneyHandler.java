package main.cmdhandler;

import main.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyHandler {

    private static final Economy econ = Main.getEconomy();
    private static final String INVALID_PLAYER = "§c존재하지 않는 플레이어입니다.";

    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {

        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage(Main.INDEX + "돈: " + econ.format(econ.getBalance(player)));
        } else {
            switch (args[0]) {
                case "보내기" -> {
                    if (args.length < 3) player.sendMessage(Main.INDEX + "§c사용법: /돈 보내기 <플레이어> <돈>");
                    else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.INDEX + INVALID_PLAYER);
                    else {
                        if (econ.getBalance(player) >= Double.parseDouble(args[2])) {
                            EconomyResponse withdrawResponse = econ.withdrawPlayer(player, Double.parseDouble(args[2]));
                            if (withdrawResponse.transactionSuccess()) {
                                EconomyResponse depositResponse = econ.depositPlayer(Bukkit.getPlayer(args[1]), Double.parseDouble(args[2]));
                                if (depositResponse.transactionSuccess()) {
                                    player.sendMessage(Main.INDEX + "성공적으로 " + args[1] + "님에게 " + args[2] + "원을 보냈습니다.");
                                    Bukkit.getPlayer(args[1]).sendMessage(Main.INDEX + player.getName() + "님이 " + args[2] + "원을 보냈습니다.");
                                } else {
                                    player.sendMessage(Main.INDEX + "§c돈을 보내는 것을 실패했습니다.");
                                    player.sendMessage(Main.INDEX + ChatColor.RED + depositResponse.errorMessage);
                                    econ.depositPlayer(player, Double.parseDouble(args[2]));
                                }
                            } else {
                                player.sendMessage(Main.INDEX + "§c돈을 보내는 것을 실패했습니다.");
                                player.sendMessage(Main.INDEX + ChatColor.RED + withdrawResponse.errorMessage);
                            }
                        } else player.sendMessage(Main.INDEX + "§c당신의 돈이 부족합니다.");
                    }
                }
                case "주기" -> {
                    if (player.isOp()) {
                        if (args.length < 3) player.sendMessage(Main.INDEX + "§c사용법: /돈 주기 <플레이어> <돈>");
                        else if (Bukkit.getPlayer(args[1]) == null) player.sendMessage(Main.INDEX + INVALID_PLAYER);
                        else {
                            EconomyResponse depositResponse = econ.depositPlayer(Bukkit.getPlayer(args[1]), Double.parseDouble(args[2]));
                            if (depositResponse.transactionSuccess()) {
                                player.sendMessage(Main.INDEX + "성공적으로 " + args[1] + "님에게 " + args[2] + "원을 줬습니다.");
                                Bukkit.getPlayer(args[1]).sendMessage(Main.INDEX + player.getName() + "님이 " + args[2] + "원을 줬습니다.");
                            } else {
                                player.sendMessage(Main.INDEX + "§c돈을 주는 것을 실패했습니다.");
                                player.sendMessage(Main.INDEX + ChatColor.RED + depositResponse.errorMessage);
                                econ.depositPlayer(player, Double.parseDouble(args[2]));
                            }
                        }
                    } else player.sendMessage(Main.INDEX + "§c당신은 서버의 관리자가 아닙니다.");
                }
                case "뺏기" -> {
                    if (player.isOp()) {
                        if (args.length < 3) player.sendMessage(Main.INDEX + "§c사용법: /돈 뺏기 <플레이어> <돈>");
                        else if (Bukkit.getPlayer(args[1]) == null)
                            player.sendMessage(Main.INDEX + INVALID_PLAYER);
                        else {
                            EconomyResponse withdrawResponse = econ.withdrawPlayer(player, Double.parseDouble(args[2]));
                            if (withdrawResponse.transactionSuccess()) {
                                player.sendMessage(Main.INDEX + "성공적으로 " + args[1] + "님에게 " + args[2] + "원을 뺏었습니다.");
                                Bukkit.getPlayer(args[1]).sendMessage(Main.INDEX + player.getName() + "님이 " + args[2] + "원을 뺏었습니다.");
                            } else {
                                player.sendMessage(Main.INDEX + "§c돈을 뺏는 것을 실패했습니다.");
                                player.sendMessage(Main.INDEX + ChatColor.RED + withdrawResponse.errorMessage);
                            }
                        }
                    } else player.sendMessage(Main.INDEX + "§c당신은 서버의 관리자가 아닙니다.");
                }
                default -> player.sendMessage(Main.INDEX + "돈: " + econ.format(econ.getBalance(player)));
            }
        }
    }
}
