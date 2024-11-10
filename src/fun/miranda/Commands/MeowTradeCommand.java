package fun.miranda.Commands;

import fun.miranda.Controller.*;
import fun.miranda.Utils.Strings;
import fun.miranda.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fun.miranda.MeowTrade.plugin;

public class MeowTradeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command commandLine, String s, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Strings.NeedOp);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Strings.MeowTradeUsage);
            return true;
        }
        String command = args[0];
        switch (command) {
            case "reload" -> this.actionReload(sender);
            case "newpage" -> this.actionNewPage(sender, args);
            case "delpage" -> this.actionDelPage(sender, args);
            case "rename" -> this.actionRename(sender, args);
            case "setpage" -> this.actionSetPage(sender, args);
            case "setitem" -> this.actionSetItem(sender, args);
            case "trade" -> this.actionTrade(sender, args);
            case "switch" -> this.actionSwitch(sender, args);
            case "resetallplayer" -> this.actionResetAllPlayer(sender, args);
            case "money" -> this.actionSetMoney(sender, args);
            case "lottery" -> this.actionLottery(sender, args);
            default -> sender.sendMessage(Strings.MeowTradeUsage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command commandLine, String s, String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Utils.listFilter(new ArrayList<>(List.of("reload", "newpage", "delpage", "rename", "setpage", "setitem", "switch", "money", "resetallplayer", "trade", "lottery")), args[0]);
            }
        }
        if (args.length == 2) {
            if (sender.isOp()) {
                List<String> valid = new ArrayList<>(List.of("delpage", "rename", "setpage", "setitem"));
                if (valid.contains(args[0])) {
                    return Utils.listFilter(TradePage.getPagesList(), args[1]);
                }
                switch (args[0]) {
                    case "switch" -> {
                        return Utils.listFilter(new ArrayList<>(List.of("on", "off")), args[1]);
                    }
                    case "setmoney", "money", "trade" -> {
                        return Utils.listFilter(Utils.getAllOnlinePlayers(), args[1]);
                    }
                    case "lottery" -> {
                        return Utils.listFilter(new ArrayList<>(List.of("new", "del", "set")), args[1]);
                    }
                }
            }
        }
        if (args.length == 3) {
            if (sender.isOp()) {
                switch (args[0]) {
                    case "money" -> {
                        return Utils.listFilter(new ArrayList<>(List.of("add", "sub", "set")), args[2]);
                    }
                    case "lottery" -> {
                        LotteryBox box = LotteryBox.getInstance();
                        return Utils.listFilter(box.getAllChestNames(), args[2]);
                    }
                    case "trade" -> {
                        return Utils.listFilter(TradePage.getPagesList(), args[2]);
                    }
                }
            }
        }
        return List.of();
    }

    private void actionReload(CommandSender sender) {
        Config config = Config.getInstance();
        config.reload();
        sender.sendMessage(Strings.Reload);
    }

    private void actionNewPage(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Strings.NeedPageName);
            return;
        }
        String pageName = args[1];
        if (Utils.isNumber(pageName)) {
            sender.sendMessage(Strings.PageMustBeString);
        } else {
            if (TradePage.getPagesList().contains(pageName)) {
                TradePage page = new TradePage(pageName);
                page.deletePage();
            }
            TradePage page = new TradePage(pageName);
            page.save();
            sender.sendMessage(String.format(Strings.CreateNewPage, pageName));
        }
    }

    private void actionDelPage(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Strings.NeedPageName);
        }
        String pageName = args[1];
        if (!TradePage.getPagesList().contains(pageName)) {
            sender.sendMessage(String.format(Strings.PageNotFound, pageName));
        }
        TradePage page = new TradePage(pageName);
        page.deletePage();
        sender.sendMessage(String.format(Strings.DeletePage, pageName));
    }

    private void actionRename(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Strings.NeedPageName);
            return;
        }
        String pageName = args[1];
        String newPageName = args[2];
        if (!TradePage.getPagesList().contains(pageName)) {
            sender.sendMessage(String.format(Strings.PageNotFound, pageName));
            return;
        }
        TradePage page = new TradePage(pageName);
        boolean success = page.rename(newPageName);
        if (success) {
            sender.sendMessage(String.format(Strings.RenamePageSuccess, pageName, newPageName));
        } else {
            sender.sendMessage(String.format(Strings.RenamePageFail, newPageName));
        }
    }

    private void actionSetPage(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Strings.MustBePlayer);
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(Strings.NeedPageName);
            return;
        }
        String pageName = args[1];
        if (!TradePage.getPagesList().contains(pageName)) {
            sender.sendMessage(String.format(Strings.PageNotFound, pageName));
            return;
        }
        TradePage page = new TradePage(args[1]);
        page.setPage(player);
    }

    private void actionSetItem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Strings.MustBePlayer);
            return;
        }
        if (args.length != 4) {
            sender.sendMessage(Strings.WrongSetItem);
            return;
        }
        String pageName = args[1];
        String slotString = args[2];
        String priceOrPage = args[3];
        if (!TradePage.getPagesList().contains(pageName)) {
            sender.sendMessage(String.format(Strings.PageNotFound, pageName));
            return;
        }
        if (!Utils.isNumber(slotString)) {
            sender.sendMessage(Strings.WrongSlot);
            return;
        }
        int slot = Integer.parseInt(slotString);
        if (slot < 1 || slot > 54) {
            sender.sendMessage(Strings.WrongSlot);
            return;
        }
        TradePage page = new TradePage(pageName);
        boolean result = page.setPriceOrPage(slot, priceOrPage);
        if (!result) {
            sender.sendMessage(String.format(Strings.EmptySlot, pageName, slot));
            return;
        }
        if (Utils.isNumber(priceOrPage)) {
            player.sendMessage(String.format(Strings.SetPrice, pageName, slot, priceOrPage));
        } else {
            if (!TradePage.getPagesList().contains(priceOrPage)) {
                sender.sendMessage(String.format(Strings.PageNotFound, priceOrPage));
                return;
            }
            player.sendMessage(String.format(Strings.SetPage, pageName, slot, priceOrPage));
        }
    }

    private void actionTrade(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Strings.TradeUsage);
            return;
        }
        String playerName = args[1];
        List<Entity> target = plugin.getServer().selectEntities(sender, playerName);
        String pageName = args[2];
        if (target.isEmpty()) {
            sender.sendMessage(Strings.PlayerNotFound);
            return;
        }
        if (!TradePage.getPagesList().contains(pageName)) {
            sender.sendMessage(String.format(Strings.PageNotFound, pageName));
            return;
        }
        TradePage page = new TradePage(pageName);
        for (Entity targetEntity : target) {
            if (targetEntity instanceof Player targetPlayer) {
                page.showTrade(targetPlayer);
            }
        }
    }

    private void actionSwitch(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Strings.NeedOnOrOff);
            return;
        }
        String switch_ = args[1];
        GameControl control = GameControl.getInstance();
        switch (switch_) {
            case "on":
                sender.sendMessage(Strings.SwitchToOn);
                control.start();
                return;
            case "off":
                sender.sendMessage(Strings.SwitchToOff);
                control.stop();
                return;
            default:
                sender.sendMessage(Strings.NeedOnOrOff);
        }
    }

    private void actionResetAllPlayer(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Strings.UnnecessaryArgs);
            return;
        }
        User.resetAllPlayersCoins();
        plugin.getServer().broadcastMessage(Strings.PlayersCoinReset);
        if (GameControl.getInstance().isGameStart()) {
            Rank rank = Rank.getInstance();
            rank.updateScoreboard();
        }
    }

    private void actionSetMoney(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Strings.UserSetUsage);
            return;
        }
        String targetName = args[1];
        List<Player> target = new ArrayList<>();
        if (targetName.equals("all")) {
            target.addAll(plugin.getServer().getOnlinePlayers());
        } else if (plugin.getServer().getPlayer(targetName) != null) {
            target.add(plugin.getServer().getPlayer(targetName));
        } else {
            sender.sendMessage(Strings.PlayerNotFound);
            return;
        }
        String setCommand = args[2];
        List<String> validCommand = new ArrayList<>(List.of("add", "sub", "set"));
        if (!validCommand.contains(setCommand)) {
            sender.sendMessage(Strings.UserSetUsage);
            return;
        }
        String moneyString = args[3];
        int money;
        try {
            money = Integer.parseInt(moneyString);
        } catch (Exception e) {
            sender.sendMessage(Strings.ValueError);
            return;
        }
        int count = 0;
        for (Player player : target) {
            count++;
            User user = new User(player);
            String showName = target.size() == 1 ? target.get(0).getName() : Strings.ALL;
            switch (setCommand) {
                case "add":
                    user.addMoney(money);
                    if (count == 1) {
                        sender.sendMessage(String.format(Strings.Add, showName, money, user.getMoney()));
                    }
                    break;
                case "sub":
                    boolean result = user.subMoney(money);
                    if (result) {
                        sender.sendMessage(String.format(Strings.SubSuccess, player.getName(), money, user.getMoney()));
                    } else {
                        sender.sendMessage(String.format(Strings.SubFail, player.getName(), user.getMoney()));
                    }
                    break;
                case "set":
                    user.setMoney(money);
                    if (count == 1) {
                        sender.sendMessage(String.format(Strings.Set, showName, money));
                    }
                    break;
            }
        }
        if (GameControl.getInstance().isGameStart()) {
            Rank rank = Rank.getInstance();
            rank.updateScoreboard();
        }
    }

    private void actionLottery(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Strings.LotteryUsage);
            return;
        }
        String command = args[1];
        String chestName = args[2];
        LotteryBox box = LotteryBox.getInstance();
        switch (command) {
            case "new":
                if (Utils.isNumber(chestName)) {
                    sender.sendMessage(Strings.ChestMustBeString);
                    return;
                }
                box.newChest(chestName);
                sender.sendMessage(String.format(Strings.CreateNewChest, chestName));
                return;
            case "del":
                boolean successDel = box.delChest(chestName);
                if (successDel) {
                    sender.sendMessage(String.format(Strings.DeleteChest, chestName));
                } else {
                    sender.sendMessage(String.format(Strings.ChestNameNotFound, chestName));
                }
                return;
            case "set":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Strings.MustBePlayer);
                    return;
                }
                boolean successShow = box.showChest(player, chestName);
                if (!successShow) {
                    sender.sendMessage(String.format(Strings.ChestNameNotFound, chestName));
                }
                return;
            default:
                sender.sendMessage(Strings.LotteryUsage);
        }
    }
}
