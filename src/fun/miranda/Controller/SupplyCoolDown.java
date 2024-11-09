package fun.miranda.Controller;

import fun.miranda.Utils.Strings;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static fun.miranda.MeowTrade.plugin;

public class SupplyCoolDown {
    private static SupplyCoolDown instance;
    private final Runnable coolDown;
    private BukkitTask task;
    private Integer coolDownInteger;

    private SupplyCoolDown() {
        this.coolDown = () -> {
            this.coolDownInteger--;
            String[] MMSS = this.parseTime();
            Rank.COOLDOWN = String.format(Strings.CoolDown, MMSS[0], MMSS[1]);
            Rank rank = Rank.getInstance();
            rank.updateScoreboard();
            if (this.coolDownInteger <= 0) {
                LotteryBox box = LotteryBox.getInstance();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.isDead()) {
                        continue;
                    }
                    box.randomGetItem(player);
                }
                this.coolDownInteger = Config.getInstance().getSupplyCoolDown();
                plugin.getServer().broadcastMessage(Strings.Supply);
            }
        };
    }

    public static SupplyCoolDown getInstance() {
        if (instance == null) {
            instance = new SupplyCoolDown();
        }
        return instance;
    }

    public void run() {
        if (this.task != null) {
            if (!this.task.isCancelled()) {
                this.task.cancel();
            }
        }
        this.coolDownInteger = Config.getInstance().getSupplyCoolDown();
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, this.coolDown, 0, 20L);
    }

    public void stop() {
        if (!this.task.isCancelled()) {
            this.task.cancel();
        }
    }

    private String[] parseTime() {
        Integer[] time = new Integer[2];
        time[0] = this.coolDownInteger / 60;
        time[1] = this.coolDownInteger - time[0] * 60;
        String[] out = new String[2];
        if (time[0] < 10) {
            out[0] = "0" + time[0];
        } else {
            out[0] = time[0].toString();
        }
        if (time[1] < 10) {
            out[1] = "0" + time[1];
        } else {
            out[1] = time[1].toString();
        }
        return out;
    }
}
