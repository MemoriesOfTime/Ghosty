package net.easecation.ghosty.recording.entity.updated;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.GhostyPlugin;
import net.easecation.ghosty.entity.SimulatedEntity;
import net.easecation.ghosty.recording.entity.EntityRecordNode;
import net.easecation.ghosty.recording.level.updated.LevelUpdatedPopup;

/**
 * Created by Mulan Lin('Snake1999') on 2016/11/19 17:02.
 * All rights reserved
 */
public class EntityUpdatedItem implements EntityUpdated {

    private Item item;

    public static EntityUpdatedItem of(Item item) {
        return new EntityUpdatedItem(item);
    }

    @Override
    public int getUpdateTypeId() {
        return EntityUpdated.TYPE_ITEM;
    }

    @Override
    public boolean hasStates() {
        return true;
    }

    @Override
    public void processTo(SimulatedEntity entity) {
        entity.item = item;
    }

    @Override
    public EntityRecordNode applyTo(EntityRecordNode node) {
        node.setItem(item);
        return node;
    }

    public EntityUpdatedItem(BinaryStream stream) {
        read(stream);
    }

    private EntityUpdatedItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EntityUpdatedItem)) return false;
        EntityUpdatedItem o = (EntityUpdatedItem) obj;
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
        return "EntityUpdatedItem{" +
            "item=" + item +
            '}';
    }
}
