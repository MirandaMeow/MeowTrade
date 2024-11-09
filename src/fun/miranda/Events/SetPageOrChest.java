package fun.miranda.Events;

import fun.miranda.Controller.LotteryBox;
import fun.miranda.Controller.TradePage;
import fun.miranda.Utils.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class SetPageOrChest implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void setPage(InventoryCloseEvent event) {
        String inventoryName = event.getView().getTitle();
        if (!inventoryName.startsWith(Strings.PrefixTradeSet)) {
            return;
        }
        String pageName = inventoryName.replace(Strings.PrefixTradeSet + " ", "");
        TradePage page = new TradePage(pageName);
        page.setContents(event.getInventory().getContents());
        page.save();
        event.getPlayer().sendMessage(String.format(Strings.TradeSetDone, pageName));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void setChest(InventoryCloseEvent event) {
        String inventoryName = event.getView().getTitle();
        if (!inventoryName.startsWith(Strings.PrefixChestSet)) {
            return;
        }
        String chestName = inventoryName.replace(Strings.PrefixChestSet + " ", "");
        LotteryBox box = LotteryBox.getInstance();
        box.setChest(chestName, event.getInventory().getContents());
        box.save();
        event.getPlayer().sendMessage(String.format(Strings.ChestSetDone, chestName));
    }
}
