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
    private final HashMap<String, Integer> playerKill;
    private final HashMap<String, Integer> playerDeath;
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

    public void addPlayerKill(String playerName) {
        if (!this.playerKill.containsKey(playerName)) {
            this.playerKill.put(playerName, 0);
        }
        if (!this.playerDeath.containsKey(playerName)) {
            this.playerDeath.put(playerName, 0);
        }
        this.playerKill.put(playerName, this.playerKill.get(playerName) + 1);
    }

    public void addPlayerDeath(String playerName) {
        if (!this.playerKill.containsKey(playerName)) {
            this.playerKill.put(playerName, 0);
        }
        if (!this.playerDeath.containsKey(playerName)) {
            this.playerDeath.put(playerName, 0);
        }
        this.playerDeath.put(playerName, this.playerDeath.get(playerName) + 1);
    }

    public void updateScoreboard() {
        this.objective.unregister();
        this.objective = this.scoreboard.registerNewObjective("MeowTradeKD", Criteria.DUMMY, Strings.Title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        HashMap<String, Integer> sortedMap = Utils.sortHashMap(this.playerKill);

        int length = sortedMap.size() - 1;

        Score cooldown = this.objective.getScore(COOLDOWN);
        cooldown.setScore(length + 2);


        Score desc = this.objective.getScore(Strings.Description);
        desc.setScore(length + 1);

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            String playerName = entry.getKey();
            Player player = plugin.getServer().getPlayer(playerName);
            int kills = entry.getValue();
            int deaths = this.playerDeath.get(playerName);
            assert player != null;
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
