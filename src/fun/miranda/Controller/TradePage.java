package fun.miranda.Controller;

import fun.miranda.Utils.IO;
import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fun.miranda.MeowTrade.plugin;

public class TradePage {
    private final File pagesFile;
    private final YamlConfiguration pagesConfig;
    private String pageName;
    private ItemStack[] itemStacks;
    private HashMap<Integer, String> pricesOrPage;


    public TradePage(String pageName) {
        this.pageName = pageName;
        this.pagesFile = new File(plugin.getDataFolder(), "pages.yml");
        this.pagesConfig = YamlConfiguration.loadConfiguration(this.pagesFile);

        this.load();
    }

    public static List<String> getPagesList() {
        File file = new File(plugin.getDataFolder(), "pages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return Utils.getNodeList(config, "");
    }

    public void setPage(Player player) {
        Inventory pageInventory = plugin.getServer().createInventory(null, 54, Strings.PrefixTradeSet + " " + pageName);
        if (itemStacks != null) {
            pageInventory.setContents(itemStacks);
        }
        player.openInventory(pageInventory);
    }

    public void save() {
        this.pagesConfig.set(this.pageName + ".page", IO.serialize(this.itemStacks));
        this.pagesConfig.set(this.pageName + ".price", IO.serialize(this.pricesOrPage));
        try {
            this.pagesConfig.save(this.pagesFile);
        } catch (IOException ignored) {
        }
    }

    public boolean rename(String newPageName) {
        if (TradePage.getPagesList().contains(newPageName)) {
            return false;
        }
        Object object = this.pagesConfig.get(this.pageName);
        this.pagesConfig.set(newPageName, object);
        this.pagesConfig.set(this.pageName, null);
        this.pageName = newPageName;
        this.save();
        return true;
    }

    private void load() {
        String pageString = this.pagesConfig.getString(pageName + ".page", "");
        if (!pageString.isEmpty()) {
            this.itemStacks = (ItemStack[]) IO.deserialize(pageString);
        } else {
            this.itemStacks = new ItemStack[54];
        }
        String pciceString = this.pagesConfig.getString(pageName + ".price", "");
        if (!pciceString.isEmpty()) {
            this.pricesOrPage = (HashMap<Integer, String>) IO.deserialize(pciceString);
        } else {
            this.pricesOrPage = new HashMap<>();
            for (int i = 0; i < 54; i++) {
                this.pricesOrPage.put(i, "");
            }
        }
    }

    public void showTrade(Player player) {
        ItemStack[] showTradeItemStacks = (ItemStack[]) this.deepCopy(this.itemStacks);
        for (Map.Entry<Integer, String> entry : this.pricesOrPage.entrySet()) {
            int slot = entry.getKey();
            String priceOrPage = entry.getValue();
            if (entry.getValue().isEmpty()) {
                continue;
            }
            if (Utils.isNumber(priceOrPage)) {
                int price = Integer.parseInt(priceOrPage);
                if (price == 0) {
                    continue;
                }
                ItemStack itemStack = showTradeItemStacks[slot];
                if (itemStack == null) {
                    continue;
                }
                Utils.SetItemSold(itemStack, price);
            } else {
                ItemStack itemStack = showTradeItemStacks[slot];
                if (itemStack == null) {
                    continue;
                }
                Utils.SetItemDirectTo(itemStack, priceOrPage);
            }
        }
        Inventory showTradeInventory = plugin.getServer().createInventory(null, 54, Strings.TradePrefix + " " + this.pageName);
        showTradeInventory.setContents(showTradeItemStacks);
        player.openInventory(showTradeInventory);
    }

    public boolean setPriceOrPage(Integer slot, String value) {
        if (this.itemStacks[slot - 1] == null) {
            return false;
        }
        this.pricesOrPage.put(slot - 1, value);
        this.save();
        return true;
    }

    public void setContents(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    private Object deepCopy(Object object) {
        return IO.deserialize(IO.serialize(object));
    }

    public ItemStack getItem(int slot) {
        return this.itemStacks[slot];
    }

    public String getPriceOrPage(int slot) {
        return this.pricesOrPage.get(slot);
    }

    public void deletePage() {
        this.pagesConfig.set(this.pageName, null);
        try {
            this.pagesConfig.save(this.pagesFile);
        } catch (IOException ignored) {
        }
    }
}
