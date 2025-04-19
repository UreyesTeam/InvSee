package cn.ureyes.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderSeeListener implements Listener {

    public static final Map<UUID, UUID> viewerToTarget = new HashMap<>();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        HumanEntity viewer = e.getPlayer();
        UUID viewerId = viewer.getUniqueId();

        if (!viewerToTarget.containsKey(viewerId)) return;

        UUID targetId = viewerToTarget.remove(viewerId);
        Player target = Bukkit.getPlayer(targetId);
        if (target == null) return;

        Inventory view = e.getView().getTopInventory();
        target.getEnderChest().setContents(view.getContents());
        target.updateInventory();
    }
}
