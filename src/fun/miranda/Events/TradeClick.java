package fun.miranda.Events;

import fun.miranda.Controller.TradePage;
import fun.miranda.Controller.User;
import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TradeClick implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void playerClickChest(InventoryClickEvent event) {
        String inventoryName = event.getView().getTitle();
        if (!inventoryName.startsWith(Strings.TradePrefix)) {
            return;
        }
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
        String pageName = inventoryName.replace(Strings.TradePrefix + " ", "");
        TradePage page = new TradePage(pageName);
        int slot = event.getSlot();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        User user = new User(player);
        ItemStack tradeItem = page.getItem(slot);
        String priceOrPage = page.getPriceOrPage(slot);
        if (Utils.isNumber(priceOrPage)) {
            int price = Integer.parseInt(priceOrPage);
            boolean success = user.subMoney(price);
            if (success) {
                boolean canAdd = Utils.canAddItemToInventory(player, tradeItem);
                if (!canAdd) {
                    player.sendMessage(Strings.InventoryFull);
                } else {
                    player.getInventory().addItem(tradeItem);
                    player.sendMessage(String.format(Strings.BuySuccess, price, user.getMoney()));
                }
            } else {
                player.sendMessage(String.format(Strings.BuyFail, price, user.getMoney()));
            }
        } else {
            if (priceOrPage.isEmpty()) {
                return;
            }
            if (!TradePage.getPagesList().contains(pageName)) {
                return;
            }
            TradePage nextPagePage = new TradePage(priceOrPage);
            nextPagePage.showTrade(player);
        }
    }
}
