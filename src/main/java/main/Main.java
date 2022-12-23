package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.eventhandler.EventListener;
import main.eventhandler.IClickHandler;
import main.timerhandler.CMDCooldownTimer;
import main.timerhandler.FriendRequestTimer;
import main.timerhandler.InvCooldownTimer;
import main.timerhandler.PartyInviteTimer;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static final String INDEX = "§6[§e§lLemon§6]§f ";
    public static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private static final Logger log = Bukkit.getLogger();
    private static Economy econ = null;


    final PluginDescriptionFile pdf = this.getDescription();

    @Override
    public void onEnable() {

        // 플러그인 활성화 메시지 전송
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a플러그인이 활성화되었습니다.");

        // 이코노미 불러오기
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 데이터 로드
        FriendData.loadData();
        SettingsData.loadData();

        // 이벤트 리스너 등록
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new IClickHandler(), this);

        // 타이머 시작
        SCHEDULER.scheduleSyncRepeatingTask(this, new PartyInviteTimer(), 0L, 20L);
        SCHEDULER.scheduleSyncRepeatingTask(this, new FriendRequestTimer(), 0L, 20L);
        SCHEDULER.scheduleSyncRepeatingTask(this, new InvCooldownTimer(), 0L, 1L);
        SCHEDULER.scheduleSyncRepeatingTask(this, new CMDCooldownTimer(), 0L, 20L);

        // 명령어 등록
        pdf.getCommands().keySet().forEach(s -> {
            Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler());
            Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler());
        });
    }

    @Override
    public void onDisable() {
        log.info("ㅂ2");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("발트가 없노");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("이코노미가 없노");
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static @NotNull ItemStack item(Material type, String name, @NotNull List<String> lore, boolean shiny) {
        ItemStack itemStack = new ItemStack(type);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text(name));
        List<Component> loreComponent = new ArrayList<>();
        for (String s : lore) loreComponent.add(Component.text(s));
        meta.lore(loreComponent);
        if (shiny) meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);
        return itemStack;
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
