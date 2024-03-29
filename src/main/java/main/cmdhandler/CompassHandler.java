package main.cmdhandler;

import main.Main;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static main.Main.SCHEDULER;

public class CompassHandler {
    private static final HashMap<Player, Integer> coordTaskId = new HashMap<>();
    private static final HashMap<Player, Player> glowTarget = new HashMap<>();
    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        try {
            final String noArg = Main.INDEX + "/compass track <엔티티> - 엔티티를 추적합니다.\n" + Main.INDEX + "/compass track <x> <y> <z> - 해당 좌표의 블럭을 추적합니다.";
            if (commandSender instanceof Player p) {
                if (args.length < 2) {
                    p.sendMessage(noArg);
                    return;
                } else if (!p.isOp()) {
                    p.sendMessage(Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다!");
                    return;
                }
                if (args[0].equals("track")) {
                    if (args[1].equals("clear")) {
                        Player target = glowTarget.get(p);
                        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
                        SynchedEntityData data = ((CraftPlayer) target).getHandle().getEntityData();
                        data.set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0);
                        connection.send(new ClientboundSetEntityDataPacket(target.getEntityId(), data, true));
                        p.sendActionBar(Component.text());
                        p.sendMessage(String.format("%s§e%s§a를 더 이상 추적하지 않습니다.", Main.INDEX, target.getName()));
                        SCHEDULER.cancelTask(coordTaskId.get(p));
                        coordTaskId.remove(p);
                        glowTarget.remove(p);
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null && !AdminHandler.isHiddenAdmin(target)) {
                        p.sendMessage(String.format("%s§e%s§a를 추적합니다.", Main.INDEX, target.getName()));
                        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
                        SynchedEntityData data = ((CraftPlayer) target).getHandle().getEntityData();
                        data.set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0x40);
                        connection.send(new ClientboundSetEntityDataPacket(target.getEntityId(), data, true));
                        glowTarget.put(p, target);
                        int i = SCHEDULER.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                            Location l = glowTarget.get(p).getLocation();
                            p.sendActionBar(Component.text(String.format("§e%s §a추적 중 §f| §2x: §f%d, §2y: §f%d, §2z: §f%d", target.getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ())));
                            p.setCompassTarget(l);
                        }, 0, 1L);
                        coordTaskId.put(p, i);
                    } else p.sendMessage(Main.INDEX + "해당 플레이어를 찾을 수 없습니다.");
                }
            } else commandSender.sendMessage(Main.INDEX + "이 명령어는 플레이어만 사용할 수 있습니다.");
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
