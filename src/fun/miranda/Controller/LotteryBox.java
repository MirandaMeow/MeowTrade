package fun.miranda.Controller;

import fun.miranda.Utils.IO;
import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static fun.miranda.MeowTrade.plugin;

public class LotteryBox {
    private static LotteryBox instance;
    private final File lotteryBoxFile;
    private final YamlConfiguration lotteryBoxConfig;
    private HashMap<String, ItemStack[]> inventoryMap;

    private LotteryBox() {
        this.lotteryBoxFile = new File(plugin.getDataFolder(), "lotteryBox.yml");
        this.lotteryBoxConfig = YamlConfiguration.loadConfiguration(lotteryBoxFile);
        this.load();
    }

    public static LotteryBox getInstance() {
        if (instance == null) {
            instance = new LotteryBox();
        }
        return instance;
    }

    private static Integer getRandomInteger(Integer range) {
        Random random = new Random();
        return random.nextInt(range);
    }

    private void load() {
        List<String> chestsName = Utils.getNodeList(this.lotteryBoxConfig, "");
        if (chestsName.isEmpty()) {
            this.inventoryMap = new HashMap<>();
        } else {
            this.inventoryMap = new HashMap<>();
            for (String chestName : chestsName) {
                this.inventoryMap.put(chestName, (ItemStack[]) IO.deserialize(this.lotteryBoxConfig.getString(chestName)));
            }
        }
    }

    public void newChest(String chestName) {
        this.inventoryMap.put(chestName, plugin.getServer().createInventory(null, 54, chestName).getContents());
        this.save();
    }

    public void setChest(String chestName, ItemStack[] itemStacks) {
        this.inventoryMap.put(chestName, itemStacks);
    }

    public boolean showChest(Player player, String chestName) {
        if (this.hasChest(chestName)) {
            Inventory showInventory = plugin.getServer().createInventory(null, 54, Strings.PrefixChestSet + " " + chestName);
            showInventory.setContents(inventoryMap.get(chestName));
            player.openInventory(showInventory);
            return true;
        }
        return false;
    }

    public boolean delChest(String chestName) {
        if (this.hasChest(chestName)) {
            this.inventoryMap.remove(chestName);
            return true;
        }
        return false;
    }

    public void randomGetItem(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        List<String> useLotteryBox = Config.getInstance().getUseLotteryBoxList();
        if (useLotteryBox.isEmpty()) {
            return;
        }
        for (String lotteryBox : useLotteryBox) {
            if (this.inventoryMap.containsKey(lotteryBox)) {
                ItemStack[] itemStacks = this.inventoryMap.get(lotteryBox);
                for (ItemStack itemStack : itemStacks) {
                    if (itemStack != null) {
                        allItems.add(itemStack);
                    }
                }
            }
        }
        int size = allItems.size();
        if (size == 0) {
            return;
        }
        int roll = getRandomInteger(size);
        ItemStack giveItem = allItems.get(roll);
        if (giveItem == null) {
            return;
        }
        boolean canGiveItem = Utils.canAddItemToInventory(player, giveItem);
        Material.valueOf(giveItem.getType().toString());
        Integer coinBonus = this.getCoinBonus(giveItem);
        if (coinBonus != null) {
            User user = new User(player);
            user.addMoney(coinBonus);
            Rank rank = Rank.getInstance();
            rank.updateScoreboard();
            player.sendMessage(String.format(Strings.GetMoney, coinBonus));
            return;
        }
        if (canGiveItem) {
            player.getInventory().addItem(giveItem);
        }
    }

    private Integer getCoinBonus(ItemStack itemStack) {
        Config CONFIG = Config.getInstance();
        List<String> coinBonusMaterial = CONFIG.getCoinBonusList();
        HashMap<Material, Integer> coinBonusMap = new HashMap<>();
        for (String materialName : coinBonusMaterial) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                coinBonusMap.put(material, CONFIG.getCoinBonus(materialName));
            } catch (IllegalArgumentException ignored) {
            }
        }
        Material material = itemStack.getType();
        return coinBonusMap.getOrDefault(material, null);
    }

    private boolean hasChest(String chestName) {
        return this.inventoryMap.containsKey(chestName);
    }

    public void save() {
        for (Map.Entry<String, ItemStack[]> entry : this.inventoryMap.entrySet()) {
            this.lotteryBoxConfig.set(entry.getKey(), IO.serialize(entry.getValue()));
        }
        try {
            this.lotteryBoxConfig.save(this.lotteryBoxFile);
        } catch (IOException ignored) {
        }
    }

    public List<String> getAllChestNames() {
        return new ArrayList<>(this.inventoryMap.keySet());
    }
}
