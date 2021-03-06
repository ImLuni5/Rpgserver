package main.CMDHandler;

import main.Main;
import main.TimerHandler.PartyInviteTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyHandler {

    public static HashMap<String, List<Player>> party = new HashMap<>();
    public static HashMap<String, Player> partyOwner = new HashMap<>();
    public static HashMap<Player, Boolean> isPartyOwner = new HashMap<>();
    public static HashMap<Player, Boolean> isPartyChat = new HashMap<>();
    public static HashMap<Player, String> playerParty = new HashMap<>();

    public static void onCommand(CommandSender commandSender, String[] args) {

        // /파티 명령어 실행 시
        if (args.length == 0)
            commandSender.sendMessage(Main.index + "/파티 생성 <이름> - 파티를 생성합니다.\n" + Main.index + "/파티 해산 - 파티를 해산합니다.\n" + Main.index + "/파티 초대 <플레이어> - 플레이어를 파티에 초대합니다.\n" + Main.index + "/파티 수락 - 파티 초대를 수락합니다.\n" + Main.index + "/파티 거절 - 파티 초대를 거절합니다.\n" + Main.index + "/파티 파티장위임 <플레이어> - 파티장을 위임합니다.\n" + Main.index + "/파티 목록 - 파티원 목록을 확인합니다.\n" + Main.index + "/파티 강퇴 <플레이어> - 파티원을 강퇴합니다.\n" + Main.index + "/파티 채팅 - 파티 채팅모드를 키거나 끕니다.\n" + Main.index + "/파티 나가기 - 파티에서 나갑니다.\n");
        else {
            switch (args[0]) {
                case "생성":
                    //파티 이름 안적었을때
                    if (args.length == 1) commandSender.sendMessage(Main.index + "§c사용법: /파티 생성 <이름>");
                    else {
                        //파티에 소속되었는지 확인 & 이미 존재하는 파티 이름인지 확인
                        if (playerParty.containsKey((Player) commandSender))
                            commandSender.sendMessage(Main.index + "§c이미 파티에 소속되었습니다.");
                        else {
                            for (String name : party.keySet()) {
                                if (args[1].equals(name)) {
                                    commandSender.sendMessage(Main.index + "§c이미 존재하는 파티 이름입니다.");
                                    return;
                                }
                            }
                        }

                        // 파티 생성
                        List<Player> playerList = new ArrayList<>();
                        playerList.add((Player) commandSender);
                        party.put(args[1], playerList);
                        partyOwner.put(args[1], (Player) commandSender);
                        isPartyOwner.put((Player) commandSender, true);
                        playerParty.put((Player) commandSender, args[1]);
                        commandSender.sendMessage(Main.index + args[1] + "파티가 생성되었습니다.");
                    }
                    break;
                case "해산":
                    // 파티 리더인지 확인
                    if (isPartyOwner.getOrDefault((Player) commandSender, false)) {
                        // 파티 해산
                        String partyName = playerParty.get((Player) commandSender);
                        party.remove(partyName);
                        partyOwner.remove(partyName);
                        isPartyOwner.remove((Player) commandSender);
                        for (Player player : playerParty.keySet()) {
                            if (playerParty.get(player).equals(partyName)) playerParty.remove(player);

                        }
                        commandSender.sendMessage(Main.index + "파티가 해산되었습니다.");
                    } else commandSender.sendMessage(Main.index + "§c파티의 리더가 아닙니다.");
                    break;
                case "초대":
                    // 파티 리더인지 확인
                    if (isPartyOwner.getOrDefault((Player) commandSender, false)) {
                        //플레이어 이름을 적었는지 확인
                        if (args.length == 1) commandSender.sendMessage(Main.index + "§c사용법: /파티 초대 <닉네임>");
                            //플레이어가 온라인인지 확인
                        else if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1])))
                            commandSender.sendMessage(Main.index + "§c해당 플레이어는 온라인이 아닙니다.");
                            // 플레이어가 초대를 받았는지 확인
                        else if (PartyInviteTimer.playerInviteTime.containsKey(Bukkit.getPlayer(args[1])))
                            commandSender.sendMessage(Main.index + "§c해당 플레이어는 이미 누군가가 초대를 보냈습니다.");
                            // 플레이어가 파티에 소속되었는지 확인
                        else if (playerParty.containsKey(Bukkit.getPlayer(args[1])))
                            commandSender.sendMessage(Main.index + "§c해당 플레이어는 이미 파티에 소속되어 있습니다.");
                            //초대장 발송
                        else {
                            Player inviter = Bukkit.getPlayer(args[1]);
                            commandSender.sendMessage(Main.index + args[1] + "님에게 초대장을 발송했습니다.");
                            inviter.sendMessage(Main.index + commandSender.getName() + "님이 당신에게 파티를 초대했습니다. 파티에 들어오시겠습니까? 60초 이내에 응답해주세요. </파티 수락 or /파티 거절>");
                            PartyInviteTimer.playerInviteTime.put(inviter, 60);
                            PartyInviteTimer.playerInviteOwner.put(inviter, (Player) commandSender);
                        }
                    } else commandSender.sendMessage("§c파티의 리더가 아닙니다.");
                    break;
                case "수락":
                    // 파티를 초대받았는지 확인
                    if (PartyInviteTimer.playerInviteTime.containsKey((Player) commandSender)) {
                        // 파티 수락
                        List<Player> playerList = party.get(playerParty.get(PartyInviteTimer.playerInviteOwner.get((Player) commandSender)));
                        playerList.add((Player) commandSender);
                        party.put(playerParty.get(PartyInviteTimer.playerInviteOwner.get((Player) commandSender)), playerList);
                        playerParty.put((Player) commandSender, playerParty.get(PartyInviteTimer.playerInviteOwner.get((Player) commandSender)));
                        PartyInviteTimer.playerInviteOwner.remove((Player) commandSender);
                        PartyInviteTimer.playerInviteTime.remove((Player) commandSender);
                        for (Player player : playerList)
                            player.sendMessage(Main.index + commandSender.getName() + "님이 파티에 참가하였습니다.");
                    } else commandSender.sendMessage(Main.index + "§c초대받은 파티가 없습니다.");
                    break;
                case "거절":
                    // 파티를 초대받았는지 확인
                    if (PartyInviteTimer.playerInviteTime.containsKey((Player) commandSender)) {
                        // 파티 거절
                        commandSender.sendMessage(Main.index + "파티 초대를 거절했습니다.");
                        PartyInviteTimer.playerInviteOwner.get((Player) commandSender).sendMessage(Main.index + commandSender.getName() + "님이 파티 초대를 거절했습니다.");
                        PartyInviteTimer.playerInviteOwner.remove((Player) commandSender);
                        PartyInviteTimer.playerInviteTime.remove((Player) commandSender);
                    } else commandSender.sendMessage(Main.index + "§c초대받은 파티가 없습니다.");
                    break;
                case "파티장위임":
                    //파티 리더인지 확인
                    if (isPartyOwner.getOrDefault((Player) commandSender, false)) {
                        //닉네임을 적었는지 확인
                        if (args.length == 1) commandSender.sendMessage(Main.index + "§c사용법: /파티 파티장위임 <닉네임>");
                        else {
                            System.out.println(party.get(playerParty.get((Player) commandSender)));
                            List<Player> playerList = party.get(playerParty.get((Player) commandSender));
                            //플레이어가 파티 소속인지 확인
                            if (playerList.contains(Bukkit.getPlayer(args[1]))) {
                                //파티장 위임
                                partyOwner.put(playerParty.get((Player) commandSender), Bukkit.getPlayer(args[1]));
                                isPartyOwner.remove((Player) commandSender);
                                isPartyOwner.put(Bukkit.getPlayer(args[1]), true);
                                for (Player player : playerList)
                                    player.sendMessage(Main.index + commandSender.getName() + "님이 " + args[1] + "님에게 파티장을 위임했습니다.");
                            } else commandSender.sendMessage(Main.index + "§c그 플레이어는 당신의 파티 소속이 아닙니다.");
                        }
                    } else commandSender.sendMessage(Main.index + "§c파티의 리더가 아닙니다.");
                    break;
                case "목록":
                    //파티에 소속됐는지 확인
                    if (playerParty.containsKey((Player) commandSender)) {
                        // 목록 보여주기
                        StringBuilder message = new StringBuilder(Main.index + playerParty.get((Player) commandSender) + "파티원 목록: ");
                        for (Player player : party.get(playerParty.get((Player) commandSender)))
                            message.append(player.getName()).append(", ");
                        message.deleteCharAt(message.length() - 1);
                        message.deleteCharAt(message.length() - 1);
                        commandSender.sendMessage(message.toString());
                    } else commandSender.sendMessage(Main.index + "§c당신은 파티에 소속 돼 있지 않습니다.");
                    break;
                case "강퇴":
                    //파티 리더인지 확인
                    if (isPartyOwner.getOrDefault((Player) commandSender, false)) {
                        //닉네임을 적었는지 확인
                        if (args.length == 1) commandSender.sendMessage(Main.index + "§c사용법: /파티 강퇴 <닉네임>");
                        else {
                            List<Player> playerList = party.get(playerParty.get((Player) commandSender));
                            //플레이어가 파티 소속인지 확인
                            if (playerList.contains(Bukkit.getPlayer(args[1]))) {
                                // 나 자신이 아닌지 확인
                                if (Bukkit.getPlayer(args[1]) != commandSender) {
                                    //파티 강퇴
                                    playerList.remove(Bukkit.getPlayer(args[1]));
                                    party.put(playerParty.get((Player) commandSender), playerList);
                                    playerParty.remove(Bukkit.getPlayer(args[1]));
                                    Bukkit.getPlayer(args[1]).sendMessage(Main.index + "당신은 파티에서 강퇴 당했습니다.");
                                    for (Player player : playerList)
                                        player.sendMessage(Main.index + args[1] + "님이 파티에서 강퇴 당했습니다.");
                                } else commandSender.sendMessage(Main.index + "§c자기 자신을 강퇴할 수 없습니다.");
                            } else commandSender.sendMessage(Main.index + "§c그 플레이어는 당신의 파티 소속이 아닙니다.");
                        }
                    } else commandSender.sendMessage(Main.index + "§c파티의 리더가 아닙니다.");
                    break;
                case "채팅":
                    //파티에 소속되었는지 확인
                    if (playerParty.containsKey((Player) commandSender)) {
                        if (isPartyChat.getOrDefault((Player) commandSender, false)) {
                            isPartyChat.put((Player) commandSender, false);
                            commandSender.sendMessage(Main.index + "파티 채팅 모드가 꺼졌습니다.");
                        } else {
                            isPartyChat.put((Player) commandSender, true);
                            commandSender.sendMessage(Main.index + "파티 채팅 모드가 켜졌습니다.");
                        }
                    } else commandSender.sendMessage(Main.index + "§c당신은 파티에 소속 돼 있지 않습니다.");
                    break;
                case "나가기":
                    //파티에 소속되었는지 확인
                    if (playerParty.containsKey((Player) commandSender)) {
                        // 파티에서 나가기
                        List<Player> playerList = party.get(playerParty.get((Player) commandSender));
                        playerList.remove((Player) commandSender);
                        if (playerList.isEmpty()) {
                            party.remove(playerParty.get((Player) commandSender));
                            isPartyOwner.remove((Player) commandSender);
                            partyOwner.remove(playerParty.get((Player) commandSender));
                        } else party.put(playerParty.get((Player) commandSender), playerList);
                        playerParty.remove((Player) commandSender);
                        isPartyChat.remove((Player) commandSender);
                        if (isPartyOwner.getOrDefault((Player) commandSender, false)) {
                            Player randomPlayer = playerList.get(0);
                            isPartyOwner.remove((Player) commandSender);
                            isPartyOwner.put((Player) commandSender, true);
                            partyOwner.put(playerParty.get(randomPlayer), randomPlayer);
                            for (Player player : playerList)
                                player.sendMessage(Main.index + "파티장이 파티에 나가서 " + randomPlayer.getName() + "님이 새로운 파티장이 됐습니다.");
                        }
                        for (Player player : playerList)
                            player.sendMessage(Main.index + commandSender.getName() + "님이 파티에서 나갔습니다.");
                        commandSender.sendMessage(Main.index + "파티에서 나갔습니다.");
                    } else commandSender.sendMessage(Main.index + "§c당신은 파티에 소속 돼 있지 않습니다.");
                    break;

            }
        }
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
