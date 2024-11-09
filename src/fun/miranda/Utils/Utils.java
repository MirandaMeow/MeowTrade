package fun.miranda.Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static fun.miranda.MeowTrade.plugin;

public class Utils {
    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<String> getAllOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    public static List<String> listFilter(List<String> list, String select) {
        List<String> selected = new ArrayList<>();
        for (String s : list) {
            if (s.startsWith(select)) {
                selected.add(s);
            }
        }
        if (!selected.isEmpty()) {
            return selected;
        }
        return list;
    }

    public static void SetItemSold(ItemStack itemStack, int price) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        List<String> lore;
        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
            assert lore != null;
            lore.add(String.format(Strings.ShowPrice, price));
            itemMeta.setLore(lore);
        } else {
            lore = new ArrayList<>();
            lore.add(String.format(Strings.ShowPrice, price));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public static void SetItemDirectTo(ItemStack itemStack, String nextPageName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(String.format(Strings.MoveToPage, nextPageName));
        if (itemMeta.hasLore()) {
            itemMeta.setLore(new ArrayList<>());
        }
        itemStack.setItemMeta(itemMeta);
    }

    public static void ActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public static HashMap<String, Integer> sortHashMap(HashMap<String, Integer> map) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


    public static List<String> getNodeList(YamlConfiguration config, String node) {
        Set<String> nodesSet = config.getConfigurationSection(node).getKeys(false);
        return new ArrayList<>(nodesSet);
    }

    public static boolean canAddItemToInventory(Player player, ItemStack itemToAdd) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                return true;
            }
            if (item.isSimilar(itemToAdd) && ((item.getAmount() + itemToAdd.getAmount()) <= item.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }
}
