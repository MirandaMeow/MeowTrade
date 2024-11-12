package fun.miranda.Events;

import fun.miranda.Controller.Config;
import fun.miranda.Controller.GameControl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathClearBag implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void clearBag(PlayerDeathEvent event) {
        if (!GameControl.getInstance().isGameStart()) {
            return;
        }
        if (!Config.getInstance().getClearBagWhenDeath()) {
            return;
        }
        Player player = event.getEntity();
        player.getInventory().clear();
    }
}
