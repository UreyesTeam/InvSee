package cn.ureyes.plugin.command;

import cn.ureyes.plugin.InvSee;
import cn.ureyes.plugin.listener.EnderSeeListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderSeeCommand implements CommandExecutor {

    private final InvSee plugin;

    public EnderSeeCommand(InvSee plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "只能由玩家执行此命令");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("invsee.endersee.use")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "你没有权限使用此命令");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "用法: /endersee <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "未找到在线玩家: " + args[0]);
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "你不能查看自己的末影箱");
            return true;
        }

        Inventory enderChest = target.getEnderChest();
        Inventory copy = Bukkit.createInventory(null, enderChest.getSize(), ChatColor.DARK_PURPLE + "末影箱查看: " + target.getName());
        copy.setContents(enderChest.getContents());

        player.openInventory(copy);
        EnderSeeListener.viewerToTarget.put(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "你正在查看玩家 " + target.getName() + " 的末影箱");
        return true;
    }
}
