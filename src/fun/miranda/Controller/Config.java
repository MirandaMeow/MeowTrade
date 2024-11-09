package fun.miranda.Controller;

import fun.miranda.Utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static fun.miranda.MeowTrade.plugin;

public class Config {
    public static Config instance;
    private final File configFile;
    public FileConfiguration config;
    private Integer killBonus;
    private Integer killBatBonus;
    private Integer supplyCoolDown;
    private Integer defaultCoin;
    private List<String> coinBonus;


    private Config() {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.load();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void load() {
        this.killBonus = this.config.getInt("killBonus", 10);
        this.killBatBonus = this.config.getInt("killBatBonus", 2);
        this.supplyCoolDown = this.config.getInt("supplyCoolDown", 300);
        this.defaultCoin = this.config.getInt("defaultCoin", 49);
        this.coinBonus = Utils.getNodeList((YamlConfiguration) this.config, "coinBonus");
    }

    public void reload() {
        this.reloadConfig();
        this.load();
    }

    public Integer getKillBonus() {
        return this.killBonus;
    }

    public Integer getKillBatBonus() {
        return this.killBatBonus;
    }

    public Integer getSupplyCoolDown() {
        return this.supplyCoolDown;
    }

    public Integer getDefaultCoin() {
        return this.defaultCoin;
    }

    public List<String> getCoinBonusList() {
        if (this.config.get("coinBonus") == null) {
            return new ArrayList<>();
        }
        return this.coinBonus;
    }

    public Integer getCoinBonus(String materialName) {
        return this.config.getInt(String.format("coinBonus.%s", materialName), 0);
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }
}
