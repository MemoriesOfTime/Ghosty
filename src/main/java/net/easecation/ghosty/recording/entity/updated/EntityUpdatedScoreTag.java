package net.easecation.ghosty.recording.entity.updated;

import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.entity.SimulatedEntity;
import net.easecation.ghosty.recording.entity.EntityRecordNode;
import net.easecation.ghosty.recording.player.updated.PlayerUpdatedArmor2;

import java.util.Objects;

/**
 * Created by Mulan Lin('Snake1999') on 2016/11/19 15:23.
 */
public class EntityUpdatedScoreTag implements EntityUpdated {

    @Override
    public int getUpdateTypeId() {
        return EntityUpdated.TYPE_SCORE_TAG;
    }

    @Override
    public boolean hasStates() {
        return true;
    }

    public static EntityUpdatedScoreTag of(String tn) {
        return new EntityUpdatedScoreTag(tn);
    }

    private String tn;

    @Override
    public void processTo(SimulatedEntity entity) {
        entity.setScoreTag(tn);
    }

    @Override
    public EntityRecordNode applyTo(EntityRecordNode node) {
        node.setScoreTag(tn);
        return node;
    }

    public EntityUpdatedScoreTag(BinaryStream stream) {
        read(stream);
    }

    private EntityUpdatedScoreTag(String tn) {
        this.tn = tn;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EntityUpdatedScoreTag)) return false;
        EntityUpdatedScoreTag o = (EntityUpdatedScoreTag) obj;
        return (Objects.equals(tn, o.tn));
    }

    @Override
    public void write(BinaryStream stream) {
        stream.putString(tn);
    }

    @Override
    public void read(BinaryStream stream) {
        this.tn = stream.getString();
    }

    @Override
    public String toString() {
        return "EntityUpdatedScoreName{" +
            "tn='" + tn + '\'' +
            '}';
    }
}
