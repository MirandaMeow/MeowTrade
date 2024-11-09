package fun.miranda.Controller;

import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

import static fun.miranda.MeowTrade.plugin;

public class Rank {
    public static String COOLDOWN = "";
    private static Rank instance;
    private final Scoreboard scoreboard;
    private final HashMap<Player, Integer> playerKill;
    private final HashMap<Player, Integer> playerDeath;
    private Objective objective;

    private Rank() {
        this.playerKill = new HashMap<>();
        this.playerDeath = new HashMap<>();

        ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        assert scoreboardManager != null;
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("MeowTradeKD", Criteria.DUMMY, Strings.Title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.updateScoreboard();
    }

    public static Rank getInstance() {
        if (instance == null) {
            instance = new Rank();
        }
        return instance;
    }

    public static void removeRank() {
        Scoreboard empty = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Rank.ShowScoreBoardToAllPlayer(empty);
        Rank rank = Rank.getInstance();
        rank.clearData();
    }

    public static void ShowScoreBoardToAllPlayer(Scoreboard scoreboard) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public static void showRank() {
        Rank rank = Rank.getInstance();
        Rank.ShowScoreBoardToAllPlayer(rank.getScoreboard());
    }

    public void addPlayerKill(Player player) {
        if (!this.playerKill.containsKey(player)) {
            this.playerKill.put(player, 0);
        }
        if (!this.playerDeath.containsKey(player)) {
            this.playerDeath.put(player, 0);
        }
        this.playerKill.put(player, this.playerKill.get(player) + 1);
    }

    public void addPlayerDeath(Player player) {
        if (!this.playerKill.containsKey(player)) {
            this.playerKill.put(player, 0);
        }
        if (!this.playerDeath.containsKey(player)) {
            this.playerDeath.put(player, 0);
        }
        this.playerDeath.put(player, this.playerDeath.get(player) + 1);
    }

    public void updateScoreboard() {
        this.objective.unregister();
        this.objective = this.scoreboard.registerNewObjective("MeowTradeKD", Criteria.DUMMY, Strings.Title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        HashMap<Player, Integer> sortedMap = Utils.sortHashMap(this.playerKill);

        int length = sortedMap.size() - 1;

        Score cooldown = this.objective.getScore(COOLDOWN);
        cooldown.setScore(length + 2);


        Score desc = this.objective.getScore(Strings.Description);
        desc.setScore(length + 1);

        for (Map.Entry<Player, Integer> entry : sortedMap.entrySet()) {
            Player player = entry.getKey();
            int kills = entry.getValue();
            int deaths = this.playerDeath.get(player);
            int money = new User(player).getMoney();
            Score score = this.objective.getScore(String.format(Strings.KDShow, player.getName(), kills, deaths, money));
            score.setScore(length);
            length--;
        }
    }

    public void clearData() {
        this.playerKill.clear();
        this.playerDeath.clear();
        this.updateScoreboard();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
