package main.cmdhandler;

import main.Main;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.datahandler.SettingsData.PartyOption;
import main.timerhandler.PartyInviteTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PartyHandler {

    private static final Map<String, List<Player>> party = new HashMap<>();
    private static final Map<String, Player> partyOwner = new HashMap<>();
    private static final Map<Player, Boolean> isPartyOwner = new HashMap<>();
    private static final Map<Player, Boolean> isPartyChat = new HashMap<>();
    private static final Map<Player, String> playerParty = new HashMap<>();

    private static final String NOT_OWNER = "§c파티의 리더가 아닙니다.";
    private static final String NOT_IN_PARTY = "§c당신은 파티에 소속 돼 있지 않습니다.";

    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        try {
            if (commandSender instanceof Player p) {
                // 명령어 외에 아무 구문도 안쳤을때
                String noArguments = Main.INDEX + "/파티 생성 <이름> - 파티를 생성합니다.\n" + Main.INDEX + "/파티 해산 - 파티를 해산합니다.\n" + Main.INDEX + "/파티 초대 <플레이어> - 플레이어를 파티에 초대합니다.\n" + Main.INDEX + "/파티 수락 - 파티 초대를 수락합니다.\n" + Main.INDEX + "/파티 거절 - 파티 초대를 거절합니다.\n" + Main.INDEX + "/파티 파티장위임 <플레이어> - 파티장을 위임합니다.\n" + Main.INDEX + "/파티 목록 - 파티원 목록을 확인합니다.\n" + Main.INDEX + "/파티 강퇴 <플레이어> - 파티원을 강퇴합니다.\n" + Main.INDEX + "/파티 채팅 - 파티 채팅모드를 키거나 끕니다.\n" + Main.INDEX + "/파티 나가기 - 파티에서 나갑니다.\n";
                if (args.length == 0) {
                    p.sendMessage(noArguments);
                    return;
                }
                if (AdminHandler.isHiddenAdminTrying(p)) return;
                switch (args[0]) {
                    case "생성" -> {
                        //파티 이름 안적었을때
                        if (args.length == 1) p.sendMessage(Main.INDEX + "§c사용법: /파티 생성 <이름>");
                        else {
                            //파티에 소속되었는지 확인 & 이미 존재하는 파티 이름인지 확인
                            if (playerParty.containsKey(p))
                                p.sendMessage(Main.INDEX + "§c이미 파티에 소속되었습니다.");
                            else {
                                for (String name : party.keySet()) {
                                    if (args[1].equals(name)) {
                                        p.sendMessage(Main.INDEX + "§c이미 존재하는 파티 이름입니다.");
                                        return;
                                    }
                                }
                            }

                            // 파티 생성
                            List<Player> playerList = new ArrayList<>();
                            playerList.add(p);
                            party.put(args[1], playerList);
                            partyOwner.put(args[1], p);
                            isPartyOwner.put(p, true);
                            playerParty.put(p, args[1]);
                            p.sendMessage(Main.INDEX + args[1] + "파티가 생성되었습니다.");
                            Bukkit.getConsoleSender().sendMessage(Main.INDEX + p.getName() + "님이" + args[1] + "파티를 생성했습니다.");
                        }
                    }
                    case "해산" -> {
                        // 파티 리더인지 확인
                        if (Boolean.TRUE.equals(isPartyOwner.getOrDefault(p, false))) {
                            // 파티 해산
                            String partyName = playerParty.get(p);
                            party.remove(partyName);
                            partyOwner.remove(partyName);
                            isPartyOwner.remove(p);
                            for (Map.Entry<Player, String> entry : playerParty.entrySet()) {
                                if (entry.getValue().equals(partyName)) playerParty.remove(entry.getKey());
                            }
                            p.sendMessage(Main.INDEX + "파티가 해산되었습니다.");
                        } else p.sendMessage(Main.INDEX + NOT_OWNER);
                    }
                    case "초대" -> {
                        if (args.length == 1) p.sendMessage(Main.INDEX + "§c사용법: /파티 초대 <닉네임>");
                        Player inviter = Bukkit.getPlayer(args[1]);
                        if (inviter == null) {
                            p.sendMessage(Main.INDEX + "§c존재하지 않는 플레이어입니다.");
                            return;
                        }
                        String inviterSet = SettingsData.getSettings("party", inviter.getUniqueId());
                        PartyOption inviterOption = PartyOption.valueOf(inviterSet);
                        // 파티 리더인지 확인
                        if (Boolean.TRUE.equals(isPartyOwner.getOrDefault(p, false))) {
                            //플레이어가 온라인인지 확인
                            if (!Bukkit.getOnlinePlayers().contains(inviter) || AdminHandler.isHiddenAdmin(inviter))
                                p.sendMessage(Main.INDEX + "§c해당 플레이어는 온라인이 아닙니다.");
                                // 플레이어가 초대를 받았는지 확인
                            else if (inviter.equals(p))
                                p.sendMessage(Main.INDEX + "§c자기 자신에게 파티 초대를 보낼 수 없습니다.");
                            else if (PartyInviteTimer.getPlayerInviteTime().containsKey(inviter))
                                p.sendMessage(Main.INDEX + "§c해당 플레이어는 이미 누군가가 초대를 보냈습니다.");
                                // 플레이어가 파티에 소속되었는지 확인
                            else if (playerParty.containsKey(inviter))
                                p.sendMessage(Main.INDEX + "§c해당 플레이어는 이미 파티에 소속되어 있습니다.");
                                //플레이어가 파티를 받지 않게 설정했는지 확인
                            else if (inviterOption == PartyOption.NEVER)
                                p.sendMessage(Main.INDEX + "§c해당 플레이어는 파티 초대를 받지 않게 설정했습니다.");
                                //플레이어가 파티를 친구에게서만 받게 설정했는지 확인
                            else if (inviterOption == PartyOption.FRIENDS)
                                if (FriendData.getPlayerFriendList(inviter.getUniqueId()).contains(p.getUniqueId().toString())) {
                                    //초대장 발송
                                    invite(p, inviter);
                                } else p.sendMessage(Main.INDEX + "§c해당 플레이어는 친구에게서만 파티 초대를 받게 설정했습니다.");
                                //초대장 발송
                            else {
                                invite(p, inviter);
                            }
                        } else p.sendMessage(NOT_OWNER);
                    }
                    case "수락" -> {
                        // 파티를 초대받았는지 확인
                        if (PartyInviteTimer.getPlayerInviteTime().containsKey(p)) {
                            // 파티 수락
                            List<Player> playerList = party.get(playerParty.get(PartyInviteTimer.getPlayerInviteOwner().get(p)));
                            playerList.add(p);
                            party.put(playerParty.get(PartyInviteTimer.getPlayerInviteOwner().get(p)), playerList);
                            playerParty.put(p, playerParty.get(PartyInviteTimer.getPlayerInviteOwner().get(p)));
                            PartyInviteTimer.getPlayerInviteOwner().remove(p);
                            PartyInviteTimer.getPlayerInviteTime().remove(p);
                            for (Player player : playerList)
                                player.sendMessage(Main.INDEX + p.getName() + "님이 파티에 참가하였습니다.");
                        } else p.sendMessage(Main.INDEX + "§c초대받은 파티가 없습니다.");
                    }
                    case "거절" -> {
                        // 파티를 초대받았는지 확인
                        if (PartyInviteTimer.getPlayerInviteTime().containsKey(p)) {
                            // 파티 거절
                            p.sendMessage(Main.INDEX + "파티 초대를 거절했습니다.");
                            PartyInviteTimer.getPlayerInviteOwner().get(p).sendMessage(Main.INDEX + p.getName() + "님이 파티 초대를 거절했습니다.");
                            PartyInviteTimer.getPlayerInviteOwner().remove(p);
                            PartyInviteTimer.getPlayerInviteTime().remove(p);
                        } else p.sendMessage(Main.INDEX + "§c초대받은 파티가 없습니다.");
                    }
                    case "파티장위임" -> {
                        //파티 리더인지 확인
                        if (isPartyOwner.getOrDefault(p, false)) {
                            //닉네임을 적었는지 확인
                            if (args.length == 1) p.sendMessage(Main.INDEX + "§c사용법: /파티 파티장위임 <닉네임>");
                            else {
                                List<Player> playerList = party.get(playerParty.get(p));
                                //플레이어가 파티 소속인지 확인
                                if (playerList.contains(Bukkit.getPlayer(args[1]))) {
                                    //파티장 위임
                                    partyOwner.put(playerParty.get(p), Bukkit.getPlayer(args[1]));
                                    isPartyOwner.remove(p);
                                    isPartyOwner.put(Bukkit.getPlayer(args[1]), true);
                                    for (Player player : playerList)
                                        player.sendMessage(Main.INDEX + p.getName() + "님이 " + args[1] + "님에게 파티장을 위임했습니다.");
                                } else p.sendMessage(Main.INDEX + "§c그 플레이어는 당신의 파티 소속이 아닙니다.");
                            }
                        } else p.sendMessage(Main.INDEX + NOT_OWNER);
                    }
                    case "목록" -> {
                        //파티에 소속됐는지 확인
                        if (playerParty.containsKey(p)) {
                            // 목록 보여주기
                            StringBuilder message = new StringBuilder(Main.INDEX + playerParty.get(p) + "파티원 목록: ");
                            for (Player player : party.get(playerParty.get(p)))
                                message.append(player.getName()).append(", ");
                            message.deleteCharAt(message.length() - 1);
                            message.deleteCharAt(message.length() - 1);
                            p.sendMessage(message.toString());
                        } else p.sendMessage(Main.INDEX + NOT_IN_PARTY);
                    }
                    case "강퇴" -> {
                        //파티 리더인지 확인
                        if (isPartyOwner.getOrDefault(p, false)) {
                            //닉네임을 적었는지 확인
                            if (args.length == 1) p.sendMessage(Main.INDEX + "§c사용법: /파티 강퇴 <닉네임>");
                            else {
                                List<Player> playerList = party.get(playerParty.get(p));
                                //플레이어가 파티 소속인지 확인
                                if (playerList.contains(Bukkit.getPlayer(args[1]))) {
                                    // 나 자신이 아닌지 확인
                                    if (Bukkit.getPlayer(args[1]) != p) {
                                        //파티 강퇴
                                        playerList.remove(Bukkit.getPlayer(args[1]));
                                        party.put(playerParty.get(p), playerList);
                                        playerParty.remove(Bukkit.getPlayer(args[1]));
                                        Objects.requireNonNull(Bukkit.getPlayer(args[1])).sendMessage(Main.INDEX + "당신은 파티에서 강퇴 당했습니다.");
                                        for (Player player : playerList)
                                            player.sendMessage(Main.INDEX + args[1] + "님이 파티에서 강퇴 당했습니다.");
                                    } else p.sendMessage(Main.INDEX + "§c자기 자신을 강퇴할 수 없습니다.");
                                } else p.sendMessage(Main.INDEX + "§c그 플레이어는 당신의 파티 소속이 아닙니다.");
                            }
                        } else p.sendMessage(Main.INDEX + NOT_OWNER);
                    }
                    case "채팅" -> {
                        //파티에 소속되었는지 확인
                        if (playerParty.containsKey(p)) {
                            if (Boolean.TRUE.equals(isPartyChat.getOrDefault(p, false))) {
                                isPartyChat.put(p, false);
                                p.sendMessage(Main.INDEX + "파티 채팅 모드가 꺼졌습니다.");
                            } else {
                                isPartyChat.put(p, true);
                                p.sendMessage(Main.INDEX + "파티 채팅 모드가 켜졌습니다.");
                            }
                        } else p.sendMessage(Main.INDEX + NOT_IN_PARTY);
                    }
                    case "나가기" -> {
                        //파티에 소속되었는지 확인
                        if (playerParty.containsKey(p)) {
                            // 파티에서 나가기
                            List<Player> playerList = party.get(playerParty.get(p));
                            playerList.remove(p);
                            if (playerList.isEmpty()) {
                                party.remove(playerParty.get(p));
                                isPartyOwner.remove(p);
                                partyOwner.remove(playerParty.get(p));
                            } else party.put(playerParty.get(p), playerList);
                            playerParty.remove(p);
                            isPartyChat.remove(p);
                            if (Boolean.TRUE.equals(isPartyOwner.getOrDefault(p, false))) {
                                Player randomPlayer = playerList.get(0);
                                isPartyOwner.remove(p);
                                isPartyOwner.put(p, true);
                                partyOwner.put(playerParty.get(randomPlayer), randomPlayer);
                                for (Player player : playerList)
                                    player.sendMessage(Main.INDEX + "파티장이 파티에 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
                            }
                            for (Player player : playerList)
                                player.sendMessage(Main.INDEX + p.getName() + "님이 파티에서 나갔습니다.");
                            p.sendMessage(Main.INDEX + "파티에서 나갔습니다.");
                        } else p.sendMessage(Main.INDEX + NOT_IN_PARTY);
                    }
                    default -> p.sendMessage(noArguments);
                }
            } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    private static void invite(@NotNull Player player, @NotNull Player inviter) {
        try {
            player.sendMessage(Main.INDEX + inviter.getName() + "님에게 초대장을 발송했습니다.");
            inviter.sendMessage(Main.INDEX + player.getName() + "님이 당신에게 파티를 초대했습니다. 파티에 들어오시겠습니까? 60초 이내에 응답해주세요. </파티 수락 or /파티 거절>");
            PartyInviteTimer.getPlayerInviteTime().put(inviter, 60);
            PartyInviteTimer.getPlayerInviteOwner().put(inviter, player);
        } catch (Exception e) {
            Main.printException(e);
        }
    }

    public static Map<String, List<Player>> getParty() {
        return party;
    }

    public static Map<String, Player> getPartyOwner() {
        return partyOwner;
    }

    public static Map<Player, Boolean> getIsPartyOwner() {
        return isPartyOwner;
    }

    public static Map<Player, Boolean> getIsPartyChat() {
        return isPartyChat;
    }

    public static Map<Player, String> getPlayerParty() {
        return playerParty;
    }


    //이제 뭐만들까요
    //해산 기능 만들다

    //c사용법 왜 오타로 인식하지 좀화나네요 아
    //생성 기능 끝
    //헊 코드 최적화하기
    //굿
    //코드에 주석 달아둘게영
    //넹
    //무슨 일본어 닉넴분 머죠 그 개발자분인데
    //니기라기 입니다

    //네ㄴ성 어캐 만들죠
    //무슨 생성이요??
    //파티 생성이요
    //변수 등록하기????
    //우선 파티에 소속됐는지 체크 해야되네요
    //파티를 어캐 등록할지도 고민해야겠네요
    // 그냥 등록하는게 편할것같긴 한데??흠흠
    //이러면 편하겠네요
    //오... 오너 따로 있으니까 편하리
    ///뭔가 되가고 있다...
}
