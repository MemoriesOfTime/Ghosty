package net.easecation.ghosty.recording.player.updated;

import cn.nukkit.level.Location;
import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.entity.PlaybackNPC;
import net.easecation.ghosty.recording.player.PlayerRecordNode;

/**
 * Created by Mulan Lin('Snake1999') on 2016/11/19 15:26.
 */
public class PlayerUpdatedRotation implements PlayerUpdated {

    private double yaw;
    private double pitch;

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    @Override
    public int getUpdateTypeId() {
        return TYPE_ROTATION;
    }

    @Override
    public boolean hasStates() {
        return true;
    }

    @Override
    public void processTo(PlaybackNPC ghost) {
        Location location = ghost.getLocation();
        location.yaw = yaw;
        location.pitch = pitch;
        ghost.teleport(location);
    }

    @Override
    public PlayerRecordNode applyTo(PlayerRecordNode node) {
        node.setYaw(yaw);
        node.setPitch(pitch);
        return node;
    }

    public static PlayerUpdatedRotation of(double yaw, double pitch) {
        return new PlayerUpdatedRotation(yaw, pitch);
    }

    public PlayerUpdatedRotation(BinaryStream stream) {
        this.read(stream);
    }

    private PlayerUpdatedRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlayerUpdatedRotation o)) return false;
        return (yaw == o.yaw) && (pitch == o.pitch);
    }

    @Override
    public void write(BinaryStream stream) {
        stream.putFloat((float) this.yaw);
        stream.putFloat((float) this.pitch);
    }

    @Override
    public void read(BinaryStream stream) {
        this.yaw = stream.getFloat();
        this.pitch = stream.getFloat();
    }

    @Override
    public String toString() {
        return "PlayerUpdatedRotation{" +
            "yaw=" + yaw +
            ", pitch=" + pitch +
            '}';
    }
}
