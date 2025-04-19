package cn.ureyes.plugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import cn.ureyes.plugin.command.InvSeeTabCompleter;
import cn.ureyes.plugin.command.InvSeeCommand;
import cn.ureyes.plugin.command.EnderSeeCommand;
import cn.ureyes.plugin.listener.InvSeeListener;
import cn.ureyes.plugin.listener.EnderSeeListener;

public final class InvSee extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // 保存默认配置
        this.saveDefaultConfig();

        // 注册命令
        this.getCommand("invsee").setExecutor(new InvSeeCommand(this));
        this.getCommand("invsee").setTabCompleter(new InvSeeTabCompleter());
        this.getCommand("endersee").setExecutor(new EnderSeeCommand(this));

        // 注册监听器
        getServer().getPluginManager().registerEvents(new InvSeeListener(), this);
        getServer().getPluginManager().registerEvents(new EnderSeeListener(), this);

        getLogger().info("InvSee插件已加载");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String getPrefix() {
        String raw = getConfig().getString("prefix", "&eTheUrEyesServer &f&l>>> ");
        return ChatColor.translateAlternateColorCodes('&', raw);
    }
}
