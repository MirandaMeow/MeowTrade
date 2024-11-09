package fun.miranda.Controller;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;

import static fun.miranda.MeowTrade.plugin;

public class GameControl {
    private static GameControl instance;
    private final SupplyCoolDown coolDown;
    private boolean isGameStart;

    private GameControl() {
        this.isGameStart = false;
        this.coolDown = SupplyCoolDown.getInstance();
    }

    public static GameControl getInstance() {
        if (instance == null) {
            instance = new GameControl();
        }
        return instance;
    }

    public void start() {
        if (this.isGameStart) {
            return;
        }
        this.isGameStart = true;
        this.coolDown.run();

        Rank.showRank();
        User.resetAllPlayersCoins();
        plugin.getServer().getWorlds().get(0).setDifficulty(Difficulty.HARD);
        plugin.getServer().getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, true);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
            User user = new User(player);
            user.setMoney(Config.getInstance().getDefaultCoin());
        }
    }

    public void stop() {
        if (!this.isGameStart) {
            return;
        }
        this.isGameStart = false;
        this.coolDown.stop();

        Rank.removeRank();
        plugin.getServer().getWorlds().get(0).setDifficulty(Difficulty.PEACEFUL);
        plugin.getServer().getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, false);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public boolean isGameStart() {
        return this.isGameStart;
    }
}
