package net.easecation.ghosty.recording.level.updated;

import cn.nukkit.Player;
import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.recording.level.LevelRecordNode;
import net.easecation.ghosty.recording.player.updated.PlayerUpdatedArmor2;

public class LevelUpdatedMessage implements LevelUpdated {

    private String message;

    private LevelUpdatedMessage(String message) {
        this.message = message;
    }

    public LevelUpdatedMessage(BinaryStream stream) {
        this.read(stream);
    }

    public static LevelUpdatedMessage of(String message) {
        return new LevelUpdatedMessage(message);
    }

    @Override
    public int getUpdateTypeId() {
        return LevelUpdated.TYPE_MESSAGE;
    }

    @Override
    public void processTo(LevelRecordNode node) {
        node.offerLevelGlobalCallback((level) -> {
            for (Player player : level.getPlayers().values()) {
                player.sendMessage(this.message);
            }
        });
    }

    public void backwardTo(LevelRecordNode node) {
        // 不需要做任何事
    }

    @Override
    public void write(BinaryStream stream) {
        stream.putString(message);
    }

    @Override
    public void read(BinaryStream stream) {
        this.message = stream.getString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LevelUpdatedMessage)) return false;
        LevelUpdatedMessage o = (LevelUpdatedMessage) obj;
        return this.message.equals(o.message);
    }

    @Override
    public String toString() {
        return "LevelUpdatedMessage{" +
            "message='" + message + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
