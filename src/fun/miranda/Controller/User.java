package fun.miranda.Controller;

import fun.miranda.Utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static fun.miranda.MeowTrade.plugin;

public class User {
    private final String name;
    private final ConfigurationSection config;
    private final File userFile;
    private final YamlConfiguration userConfig;
    private Integer money;

    public User(Player player) {
        this.userFile = new File(plugin.getDataFolder(), "players.yml");
        this.userConfig = YamlConfiguration.loadConfiguration(this.userFile);

        this.name = player.getName();
        this.config = this.getConfig();
        this.money = this.config.getInt("money", 0);
    }

    public static void resetAllPlayersCoins() {
        File userFile = new File(plugin.getDataFolder(), "players.yml");
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
        List<String> players = Utils.getNodeList(userConfig, "");
        for (String player : players) {
            userConfig.set(player, null);
        }
        try {
            userConfig.save(userFile);
        } catch (IOException ignored) {
        }
    }

    public void addMoney(Integer money) {
        this.money += money;
        this.config.set("money", this.money);
        this.saveUserConfig();
    }

    private boolean enough(Integer money) {
        return this.money >= money;
    }

    public boolean subMoney(Integer money) {
        if (this.enough(money)) {
            this.money -= money;
            this.config.set("money", this.money);
            this.saveUserConfig();
            return true;
        }
        return false;
    }

    public Integer getMoney() {
        return this.money;
    }

    public void setMoney(Integer money) {
        this.money = money;
        this.config.set("money", this.money);
        this.saveUserConfig();
    }

    private ConfigurationSection getConfig() {
        if (this.userConfig.getConfigurationSection(this.name) == null) {
            this.userConfig.createSection(this.name);
            this.saveUserConfig();
        }
        return this.userConfig.getConfigurationSection(this.name);
    }

    private void saveUserConfig() {
        try {
            this.userConfig.save(this.userFile);
        } catch (IOException ignored) {
        }
    }
}
