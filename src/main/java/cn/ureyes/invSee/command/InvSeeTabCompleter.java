package cn.ureyes.invSee.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InvSeeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // 添加reload指令
        if (args.length == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(onlinePlayer.getName());
                }
            }
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }

        return completions;
    }
}