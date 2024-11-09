package fun.miranda.Events;

import fun.miranda.Controller.GameControl;
import fun.miranda.Controller.Rank;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ShowScoreBoard implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void showScoreBoard(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!GameControl.getInstance().isGameStart()) {
            return;
        }
        Rank rank = Rank.getInstance();
        player.setScoreboard(rank.getScoreboard());
        player.setGameMode(GameMode.ADVENTURE);
    }
}
