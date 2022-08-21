package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.FriendData;
import main.eventhandler.EventListener;
import main.timerhandler.PartyInviteTimer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static final String INDEX = "§6[§e§lLemon§6]§f ";
    PluginDescriptionFile pdf = this.getDescription();

    @Override
    public void onEnable() {

        // 데이터 로드
        FriendData.loadData();

        // 이벤트 리스너 등록
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        // 타이머 시작
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PartyInviteTimer(), 0L, 20L);

        // 명령어 등록
        pdf.getCommands().keySet().forEach(s -> {
            Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler());
            Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler());
        });
    }

    @Override
    public void onDisable() {
        Logger.getGlobal().log(Level.FINE, "ㅂ2");
    }


    //ㅎㅇ 여러분
    //mir님이 누구십니ㅏ
    // 베리티입니당 빠르게 접속하기
    // 파티 만드는거 기획합시다
    // 넹
    // ㅎㅇ


    //파티 - yaml으로 저장할 필요 X
    //기능 - 생성, 해산, 초대, 파티장 임명, 파티원 목록 확인, 강퇴, 채팅, 나가기
    // 이정도??
    // 커맨드 새로 만들다?
    // 내
    //심플하게 party?
    //한글 쓰다
    //넹
    // 일단 이정도 기능이면 충분할듯
    //yml에 만들다?
    //놉 변수에 저장하다
    // ㄷㄷ
    //그래서이게뭐에요 엄..
    //우선 생성기능부터 만들죠
    //넹
    //


}
