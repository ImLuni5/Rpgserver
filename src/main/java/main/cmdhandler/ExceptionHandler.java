package main.cmdhandler;

import main.Main;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ExceptionHandler {
    private static final String INVAILD_USAGE = Main.INDEX + "/오류 생성 <메시지> - 해당 메시지의 RuntimeException을 던집니다.\n" + Main.INDEX + "/오류 목록 - 최근 오류 목록을 봅니다.\n" + Main.INDEX + "/오류 초기화 - 최근 오류 목록을 초기화합니다.";
    public static void onCommand(CommandSender sender, String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다.");
            return;
        } else if (args.length < 1) {
            sender.sendMessage(INVAILD_USAGE);
            return;
        } switch (args[0]) {
            case "생성" -> {
                if (args.length < 2) throw new RuntimeException();
                else throw new RuntimeException(args[1]);
            }
            case "목록" -> {
                sender.sendMessage(Main.INDEX + "§c----------------[ §6최근 오류 목록 §c]----------------");
                int l = 0;
                if (Main.EXCEPTIONS.isEmpty()) sender.sendMessage(Main.INDEX + "§7서버 시작 후 기록된 오류가 없습니다.");
                else {
                    for (String s : Main.EXCEPTIONS) {
                        l++;
                        sender.sendMessage(String.format("%s§e%d §6| %s", Main.INDEX, l, s));
                    }
                }
                sender.sendMessage(Main.INDEX + "§c--------------------------------------------");
            }
            case "초기화" -> {
                Main.EXCEPTIONS.clear();
                sender.sendMessage(Main.INDEX + "§a모든 오류 기록을 제거했습니다.");
            }
            default -> sender.sendMessage(INVAILD_USAGE);
        }
    }
}
