package fun.miranda.Events;

import fun.miranda.Controller.Config;
import fun.miranda.Controller.GameControl;
import fun.miranda.Controller.Rank;
import fun.miranda.Controller.User;
import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static fun.miranda.MeowTrade.plugin;

public class EarnCoin implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void kill(PlayerDeathEvent event) {
        Player victimPlayer = event.getEntity();
        Player damagerPlayer = victimPlayer.getKiller();
        if (damagerPlayer == null) {
            return;
        }
        if (!GameControl.getInstance().isGameStart()) {
            return;
        }
        event.setDeathMessage(null);
        Config config = Config.getInstance();
        User user = new User(damagerPlayer);
        user.addMoney(config.getKillBonus());
        Rank rank = Rank.getInstance();
        Rank.showRank();
        rank.addPlayerKill(damagerPlayer);
        rank.addPlayerDeath(victimPlayer);
        rank.updateScoreboard();
        plugin.getServer().broadcastMessage(String.format(Strings.Kill, damagerPlayer.getName(), victimPlayer.getName(), config.getKillBonus()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void killBat(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player damager = event.getEntity().getKiller();
        if (!victim.getType().equals(EntityType.BAT)) {
            return;
        }
        if (damager == null) {
            return;
        }
        if (!GameControl.getInstance().isGameStart()) {
            return;
        }
        Config config = Config.getInstance();
        User user = new User(damager);
        user.addMoney(config.getKillBatBonus());
        Utils.ActionBarMessage(damager, String.format(Strings.KillBat, config.getKillBatBonus(), user.getMoney()));
        Rank rank = Rank.getInstance();
        Rank.showRank();
        rank.updateScoreboard();
    }
}
