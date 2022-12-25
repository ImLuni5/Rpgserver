package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.eventhandler.CraftHandler;
import main.eventhandler.EventListener;
import main.eventhandler.IClickHandler;
import main.recipehandler.Recipe;
import main.timerhandler.CMDCooldownTimer;
import main.timerhandler.FriendRequestTimer;
import main.timerhandler.InvCooldownTimer;
import main.timerhandler.PartyInviteTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
        Recipe.loadData();

        // 이벤트 리스너 등록
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new IClickHandler(), this);
        Bukkit.getPluginManager().registerEvents(new CraftHandler(), this);

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

    public static @NotNull ItemStack item(Material type, String name, @Nullable List<String> lore, int count, boolean shiny) {
        ItemStack itemStack = new ItemStack(type);
        if (!itemStack.getType().equals(Material.AIR)) {
            ItemMeta meta = itemStack.getItemMeta();
            if (name != null) meta.displayName(Component.text(name));
            List<Component> loreComponent = new ArrayList<>();
            if (lore != null) {
                for (String s : lore) loreComponent.add(Component.text(s));
                meta.lore(loreComponent);
            }
            if (shiny) meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            itemStack.setItemMeta(meta);
            itemStack.setAmount(count);
        }
        return itemStack;
    }

    /**
     *
     * @param s 다음과 같은 양식으로 입력된 문자열: "재료/,/이름/,/설명[설명1//설명2//설명3//...]/,/아이템갯수/,/인챈트[인챈트1이름/인챈트1레벨//인챈트2이름/인챈트2레벨//...]/,/커스텀아이템 여부"
     * @return 일치하게 제작된 아이템
     */
    public static @NotNull ItemStack stringToItem(@NotNull String s) {
        String[] i = s.split("/,/");
        List<String> lore;
        if (Arrays.stream(i[2].split("//")).toList().get(0).equals("</>")) lore = null;
        else lore = Arrays.stream(i[2].split("//")).toList();
        if (i[1].equals("</>")) i[1] = null;
        ItemStack itemStack = new ItemStack(Material.valueOf(i[0]));
        itemStack.setAmount(Integer.parseInt(i[3]));
        if (!itemStack.getType().equals(Material.AIR)) {
            ItemMeta meta = itemStack.getItemMeta();
            if (i[1] != null) meta.displayName(Component.text(i[1]));
            if (lore != null) {
                List<Component> loreTo = new ArrayList<>();
                for (String lores : lore) {
                    loreTo.add(Component.text(lores));
                }
                meta.lore(loreTo);
            } itemStack.setItemMeta(meta);
            if (!i[4].equals("</>")) {
                String[] ench = i[4].split("//");
                for (String enchant : ench) {
                    String[] e = enchant.split("/");
                    meta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(new NamespacedKey(Main.getPlugin(Main.class), e[0]))), Integer.parseInt(e[1]), true);
                }
            } if (Boolean.parseBoolean(i[5])) {
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            }
        }
        return itemStack;
    }

    /**
     * 아이템을 전용 문자열으로 바꿔주는 메소드
     * @param i 문자열으로 바꿀 아이템
     * @return 바뀐 문자열, 양식: "종류/,/이름/,/설명첫째줄//설명둘째줄//설명셋째줄//../,/수량/,/인챈트[인챈트1이름/인챈트1레벨//인챈트2이름/인챈트2레벨//...]/,/커스텀아이템 여부 (이름, 설명, 인챈트에서 </>는 값이 없음을 의미함)
     */

    public static @NotNull String itemToString(@NotNull ItemStack i) {
        String material = i.getType().toString();
        String name;
        if (PlainTextComponentSerializer.plainText().serialize(i.displayName()).replace("[", "").replace("]", "").replace(" ", "_").toUpperCase().equals(material)) name = "</>";
        else name = PlainTextComponentSerializer.plainText().serialize(i.displayName()).replace("[", "").replace("]", "");
        List<Component> loreComponent = i.lore();
        StringBuilder lore;
        if (loreComponent != null && !loreComponent.isEmpty()) {
            lore = new StringBuilder();
            for (Component c : loreComponent) {
                if (lore.length() < 1) lore.append(PlainTextComponentSerializer.plainText().serialize(c));
                else lore.append("//").append(PlainTextComponentSerializer.plainText().serialize(c));
            }
        } else lore = new StringBuilder("</>");
        boolean custom;
        if (i.getItemMeta() != null) {
            custom = i.getItemFlags().containsAll(Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE)) && i.getItemMeta().isUnbreakable();
        } else custom = false;
        int amount = i.getAmount();
        Map<Enchantment, Integer> enchantments = i.getEnchantments();
        StringBuilder enchants;
        if (!enchantments.isEmpty()) {
            enchants = new StringBuilder();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if (enchants.length() < 1) enchants.append(entry.getKey()).append("/").append(entry.getValue());
                else enchants.append("/").append(entry.getKey()).append("/").append(entry.getValue());
            }
        } else enchants = new StringBuilder("</>");
        return material + "/,/" + name + "/,/" + lore + "/,/" + amount + "/,/" + enchants + "/,/" + custom;
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
