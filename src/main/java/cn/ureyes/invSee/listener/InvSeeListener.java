package cn.ureyes.invSee.listener;

import cn.ureyes.invSee.command.InvSeeCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class InvSeeListener implements Listener {

    // 不允许操作的区域
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        HumanEntity player = e.getWhoClicked();
        UUID playerId = player.getUniqueId();
        if (!InvSeeCommand.playerToTarget.containsKey(playerId)) return;

        int slot = e.getRawSlot();

        // 禁止点击玻璃板区以及4，8-8槽位
        if ((slot >= 9 && slot <= 17) || slot == 4 || (slot >= 6 && slot <= 8)) {
            e.setCancelled(true);
            return;
        }

        // 检查盔甲槽是否放入了非盔甲类物品
        if (slot >= 0 && slot <= 3) {
            ItemStack cursor = e.getCursor();
            if (cursor != null && cursor.getType() != null) {
                String name = cursor.getType().name();
                boolean valid = switch (slot) {
                    case 0 -> name.endsWith("_HELMET");
                    case 1 -> name.endsWith("_CHESTPLATE");
                    case 2 -> name.endsWith("_LEGGINGS");
                    case 3 -> name.endsWith("_BOOTS");
                    default -> false;
                };
                if (!valid) {
                    e.setCancelled(true);
                }
            }
        }
    }


    // 禁止拖入拖出
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        HumanEntity player = e.getWhoClicked();
        if (!InvSeeCommand.playerToTarget.containsKey(player.getUniqueId())) return;

        int topSize = e.getView().getTopInventory().getSize();

        for (int slot : e.getRawSlots()) {
            // 禁止拖入玩家自己背包区域
            if (slot >= topSize) {
                e.setCancelled(true);
                return;
            }

            // 禁止拖入玻璃板区以及槽4、6-8
            if ((slot >= 9 && slot <= 17) || slot == 4 || (slot >= 6 && slot <= 8)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    // 关闭界面保存背包改动到目标玩家背包
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        HumanEntity player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (!InvSeeCommand.playerToTarget.containsKey(playerId)) return;

        UUID targetId = InvSeeCommand.playerToTarget.remove(playerId);
        Player target = Bukkit.getPlayer(targetId);
        if (target == null) return;

        Inventory topInv = e.getView().getTopInventory();
        PlayerInventory targetInv = target.getInventory();

        // 护甲
        ItemStack[] newArmor = new ItemStack[4];
        newArmor[0] = topInv.getItem(3); // 靴子
        newArmor[1] = topInv.getItem(2); // 护腿
        newArmor[2] = topInv.getItem(1); // 胸甲
        newArmor[3] = topInv.getItem(0); // 头盔
        targetInv.setArmorContents(newArmor);

        // 副手
        targetInv.setItemInOffHand(topInv.getItem(5));

        // 主背包和快捷栏
        ItemStack[] newStorage = new ItemStack[36];
        for (int i = 0; i < 9; i++) {
            newStorage[i] = topInv.getItem(45 + i);
        }
        for (int i = 0; i < 27; i++) {
            newStorage[9 + i] = topInv.getItem(18 + i);
        }
        targetInv.setStorageContents(newStorage);

        // 保存目标玩家背包
        target.updateInventory();
    }
}
