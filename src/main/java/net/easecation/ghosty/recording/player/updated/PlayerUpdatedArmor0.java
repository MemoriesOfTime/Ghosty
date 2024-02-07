package net.easecation.ghosty.recording.player.updated;

import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.GhostyPlugin;
import net.easecation.ghosty.entity.PlaybackNPC;
import net.easecation.ghosty.recording.player.PlayerRecordNode;

/**
 * Created by Mulan Lin('Snake1999') on 2016/11/19 17:02.
 * All rights reserved
 */
public class PlayerUpdatedArmor0 implements PlayerUpdated {

    private Item item;

    public static PlayerUpdatedArmor0 of(Item item) {
        return new PlayerUpdatedArmor0(item);
    }

    @Override
    public int getUpdateTypeId() {
        return PlayerUpdated.TYPE_ARMOR_0;
    }

    @Override
    public boolean hasStates() {
        return true;
    }

    @Override
    public void processTo(PlaybackNPC ghost) {
        if (ghost != null && ghost.getInventory() != null) {
            PlayerInventory inv = ghost.getInventory();
            inv.setArmorItem(0, item == null ? Item.get(Item.AIR) : item);
            inv.sendArmorContents(ghost.getViewers().values());
        }
    }

    @Override
    public PlayerRecordNode applyTo(PlayerRecordNode node) {
        node.setArmor0(item);
        return node;
    }

    public PlayerUpdatedArmor0(BinaryStream stream) {
        read(stream);
    }

    private PlayerUpdatedArmor0(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlayerUpdatedArmor0)) return false;
        PlayerUpdatedArmor0 o = (PlayerUpdatedArmor0) obj;
        return (item.equals(o.item));
    }

    @Override
    public void write(BinaryStream stream) {
        stream.putSlot(GhostyPlugin.DATA_SAVE_PROTOCOL, this.item);
    }

    @Override
    public void read(BinaryStream stream) {
        this.item = stream.getSlot(GhostyPlugin.DATA_SAVE_PROTOCOL);
    }

    @Override
    public String toString() {
        return "PlayerUpdatedArmor0{" +
            "item=" + item +
            '}';
    }
}
