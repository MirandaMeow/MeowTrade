package fun.miranda;

import fun.miranda.Commands.MeowTradeCommand;
import fun.miranda.Events.EarnCoin;
import fun.miranda.Events.SetPageOrChest;
import fun.miranda.Events.ShowScoreBoard;
import fun.miranda.Events.TradeClick;
import fun.miranda.Utils.Strings;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MeowTrade extends JavaPlugin {
    public static MeowTrade plugin;
    public Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();

        logger = this.getServer().getLogger();

        this.setupConfigs();

        this.registerCommand("meowtrade", new MeowTradeCommand());

        this.registerEvent(new EarnCoin());
        this.registerEvent(new SetPageOrChest());
        this.registerEvent(new TradeClick());
        this.registerEvent(new ShowScoreBoard());

        logger.info(Strings.Enable);
    }

    public void onDisable() {
        logger.info(Strings.Disable);
    }

    /**
     * 注册命令
     *
     * @param command  命令字符串
     * @param executor 命令
     */
    private void registerCommand(String command, TabExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        assert cmd != null;
        cmd.setExecutor(executor);
    }

    /**
     * 注册监听器
     *
     * @param listener 监听器
     */
    private void registerEvent(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private void setupConfigs() {
        File usersFile = new File(this.getDataFolder(), "players.yml");
        File pagesFile = new File(this.getDataFolder(), "pages.yml");
        File lotteryBoxFile = new File(this.getDataFolder(), "lotteryBox.yml");
        try {
            usersFile.createNewFile();
            pagesFile.createNewFile();
            lotteryBoxFile.createNewFile();
        } catch (IOException ignored) {
        }
    }
}
