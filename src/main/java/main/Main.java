package main;

import main.cmdhandler.AdminHandler;
import main.cmdhandler.CMDHandler;
import main.datahandler.FriendData;
import main.datahandler.SettingsData;
import main.eventhandler.CraftHandler;
import main.eventhandler.EventListener;
import main.eventhandler.IClickHandler;
import main.recipehandler.Recipe;
import main.timerhandler.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
    public static final Component ADMIN_MSG_SYMBOL = Component.text(" §7[§c§n\uD83D\uDDE1§7]").hoverEvent(HoverEvent.showText(Component.text("§a관리자 전용 메시지로, 일반 유저에겐 보이지 않습니다.")));
    public static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private static final Logger log = Bukkit.getLogger();
    private static Economy econ = null;
    public static final List<String> EXCEPTIONS = new ArrayList<>();

    final PluginDescriptionFile pdf = this.getDescription();

    @Override
    public void onEnable() {
        try {

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
            SCHEDULER.scheduleSyncRepeatingTask(this, new TPATimer(), 0L, 20L);

            // 명령어 등록
            pdf.getCommands().keySet().forEach(s -> {
                Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler());
                Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler());
            });

            // (리로드 시) 기존에 접속해있던 플레이어 처리
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    EventListener.setScoreboard(p);
                    if (p.isOp()) {
                        AdminHandler.getAdminChat().put(p, true);
                        AdminHandler.getAdminReveal().put(p, false);
                        p.setGameMode(GameMode.SPECTATOR);
                        for (Player player : getCommonPlayers()) {
                            player.hidePlayer(this, p);
                            if (p.getGameMode() != GameMode.SPECTATOR) Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> player.sendMessage(Main.INDEX + p.getName() + "님이 퇴장했습니다."), Math.round(Math.random() * 255));
                        }
                    }
                } opMessage(Main.INDEX + "§7서버 리로드로 인해 모든 관리자가 숨김 처리되었습니다.");
            }
        } catch (Exception e) {
            printException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            log.info("ㅂ2");
        } catch (Exception e) {
            printException(e);
        }
    }

    private boolean setupEconomy() {
        try {
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
        } catch (Exception e) {
            printException(e);
            return false;
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static @NotNull ItemStack item(Material type, String name, @Nullable List<String> lore, int count, boolean shiny) {
        try {
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
        } catch (Exception e) {
            printException(e);
            return new ItemStack(Material.AIR);
        }
    }

    /**
     *
     * @param s 다음과 같은 양식으로 입력된 문자열: "재료/,/이름/,/설명[설명1//설명2//설명3//...]/,/아이템갯수/,/인챈트[인챈트1이름/인챈트1레벨//인챈트2이름/인챈트2레벨//...]/,/커스텀아이템 여부"
     * @return 일치하게 제작된 아이템
     */
    public static @NotNull ItemStack stringToItem(@NotNull String s) {
        try {
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
                }
                itemStack.setItemMeta(meta);
                if (!i[4].equals("</>")) {
                    String[] ench = i[4].split("//");
                    for (String enchant : ench) {
                        String[] e = enchant.split("/");
                        meta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(new NamespacedKey(Main.getPlugin(Main.class), e[0]))), Integer.parseInt(e[1]), true);
                    }
                }
                if (Boolean.parseBoolean(i[5])) {
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
                }
            }
            return itemStack;
        } catch (Exception e) {
            printException(e);
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * 아이템을 전용 문자열으로 바꿔주는 메소드
     * @param i 문자열으로 바꿀 아이템
     * @return 바뀐 문자열, 양식: "종류/,/이름/,/설명첫째줄//설명둘째줄//설명셋째줄//../,/수량/,/인챈트[인챈트1이름/인챈트1레벨//인챈트2이름/인챈트2레벨//...]/,/커스텀아이템 여부 (이름, 설명, 인챈트에서 </>는 값이 없음을 의미함)
     */

    public static @NotNull String itemToString(@NotNull ItemStack i) {
        try {
            String material = i.getType().toString();
            String name;
            if (PlainTextComponentSerializer.plainText().serialize(i.displayName()).replace("[", "").replace("]", "").replace(" ", "_").toUpperCase().equals(material))
                name = "</>";
            else
                name = PlainTextComponentSerializer.plainText().serialize(i.displayName()).replace("[", "").replace("]", "");
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
        } catch (Exception e) {
            printException(e);
            return "";
        }
    }

    /**
     * 오류(예외)를 출력하는 메소드
     * @param e 발생한 예외
     */
    public static void printException(Exception e) {
        try {
            log.severe(Main.INDEX + "서버에 오류가 발생했습니다! 아래를 확인해주세요.");
            e.printStackTrace();
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            String errorName = e.getClass().getName();
            String errorMessage;
            if (e.getMessage() == null) {
                errorMessage = "§c알 수 없는 오류 §7(오류 메시지 없음)";
            } else {
                errorMessage = e.getMessage();
            }
            opMessage(String.format("%s§6%s.%s§c에서 오류가 발생했습니다.", INDEX, className, methodName));
            EXCEPTIONS.add(String.format("§4%s: §c%s\n%s§c> §6%s.%s\n%s§4§c> 발생한 시각: §4%tT", errorName, errorMessage, INDEX, className, methodName, INDEX, new Date()));
            opMessage(String.format("%s§4%s: §c%s", INDEX, errorName, errorMessage));
        } catch (Exception e2) {
            opMessage(Main.INDEX + "§4오류 출력 도중 또 다른 오류가 발생했습니다. 콘솔을 확인해주세요.");
            e2.printStackTrace();
        }
    }

    /**
     * 서버 내의 관리자(OP)에게 메시지를 보냄
     * @param message 보낼 메시지
     */
    public static void opMessage(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) p.sendMessage(Component.text(message).append(ADMIN_MSG_SYMBOL));
        }
    }

    /**
     * 관리자가 아닌 플레이어 또는 모습을 드러낸 관리자의 리스트를 얻음 (비어있을 수 있음)
     * @return OP가 없는 모든 플레이어와 모습을 드러낸 관리자의 리스트
     */
    public static @NotNull List<Player> getCommonPlayers() {
        List<Player> l = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isOp()) l.add(p);
            else if (AdminHandler.getAdminReveal().get(p)) l.add(p);
        }
        return l;
    }

    /**
     * 아주 평범한 1초에 1씩 value가 줄어드는 타이머 메소드
     * @param map 맵
     */
    public static void commonMinusTimer(Map<Player, Integer> map) {
        try {
            if (!map.isEmpty()) {
                for (Map.Entry<Player, Integer> entry : map.entrySet()) {
                    map.put(entry.getKey(), entry.getValue() - 1);
                    if (entry.getValue() <= 0) {
                        map.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
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
