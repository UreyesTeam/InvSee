package cn.ureyes.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import cn.ureyes.plugin.InvSee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvSeeCommand implements CommandExecutor {

    private final InvSee plugin;
    public static final Map<UUID, UUID> playerToTarget = new HashMap<>();

    public InvSeeCommand(InvSee plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "只能由玩家执行此命令");
            return true;
        }
        Player player = (Player) sender;

        //配置重载
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("invsee.reload")) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "你没有权限重载配置");
                return true;
            }
            plugin.reloadConfig();
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "配置已重载");
            return true;
        }

        //是否有使用权限
        if (!player.hasPermission("invsee.use")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "你没有权限使用此命令");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "用法: /invsee <玩家名>");
            return true;
        }

        //玩家是否在线
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "未找到在线玩家: " + args[0]);
            return true;
        }

        // 禁止查看自己的背包
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "你不能查看自己的背包");
            return true;
        }

        // 创建背包GUI实例
        String title = ChatColor.DARK_GREEN + "背包查看: " + target.getName();
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // 头盔 胸甲 裤子 靴子
        ItemStack[] armor = target.getInventory().getArmorContents();
        gui.setItem(0, armor.length > 3 ? armor[3] : null); // 头盔
        gui.setItem(1, armor.length > 2 ? armor[2] : null); // 胸甲
        gui.setItem(2, armor.length > 1 ? armor[1] : null); // 裤子
        gui.setItem(3, armor.length > 0 ? armor[0] : null); // 靴子
        // 槽4 空

        // 副手
        gui.setItem(5, target.getInventory().getItemInOffHand());

        // 槽6–8 空

        // 不可移动 蓝色玻璃板
        ItemStack bluePane = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = bluePane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            bluePane.setItemMeta(meta);
        }
        for (int slot = 9; slot <= 17; slot++) {
            gui.setItem(slot, bluePane);
        }

        // 主背包
        PlayerInventory inv = target.getInventory();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < 27; i++) {
            gui.setItem(18 + i, contents[9 + i]);
        }

        // 快捷栏
        for (int i = 0; i < 9; i++) {
            gui.setItem(45 + i, contents[i]);
        }

        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "你正在操作玩家 " + target.getName() + " 的背包");

        // 打开箱子操作GUI
        playerToTarget.put(player.getUniqueId(), target.getUniqueId());
        player.openInventory(gui);
        return true;
    }
}
