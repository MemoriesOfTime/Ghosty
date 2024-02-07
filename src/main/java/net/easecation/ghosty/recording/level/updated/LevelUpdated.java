package net.easecation.ghosty.recording.level.updated;

import cn.nukkit.utils.BinaryStream;
import net.easecation.ghosty.recording.level.LevelRecordNode;

public interface LevelUpdated {

    int TYPE_BLOCK_CHANGE = 0;
    int TYPE_BLOCK_EVENT = 1;
    int TYPE_LEVEL_EVENT = 2;
    int TYPE_LEVEL_SOUND_EVENT = 3;
    int TYPE_PLAY_SOUND = 4;
    int TYPE_TITLE = 5;
    int TYPE_MESSAGE = 6;
    int TYPE_ACTION_BAR = 7;
    int TYPE_POPUP = 8;
    int TYPE_BOSS_EVENT = 9;
    int TYPE_SCOREBOARD_DISPLAY = 10;
    int TYPE_CUSTOM_EVENT = 11;
    int TYPE_TIME = 12;
    int TYPE_WEATHER = 13;

    /**
     * @return the type id of this Updated.
     */
    int getUpdateTypeId();

    /**
     * Process to the playback level.
     * @param node LevelRecordNode
     */
    void processTo(LevelRecordNode node);

    void backwardTo(LevelRecordNode node);

    /**
     * Write to the stream.
     * @param stream BinaryStream
     */
    void write(BinaryStream stream);

    /**
     * Read from the stream.
     * @param stream BinaryStream
     */
    void read(BinaryStream stream);

    /**
     * Create a LevelUpdated from the stream.
     * @param stream BinaryStream
     * @return LevelUpdated
     */
    static LevelUpdated fromBinaryStream(BinaryStream stream) {
        int type = stream.getByte();
        switch (type) {
            case TYPE_BLOCK_CHANGE:
                 return new LevelUpdatedBlockChange(stream);
            case TYPE_BLOCK_EVENT:
                 return new LevelUpdatedBlockEvent(stream);
            case TYPE_LEVEL_EVENT:
                 return new LevelUpdatedLevelEvent(stream);
            case TYPE_LEVEL_SOUND_EVENT:
                 return new LevelUpdatedLevelSoundEvent(stream);
            case TYPE_PLAY_SOUND:
                 return new LevelUpdatedPlaySound(stream);
            case TYPE_TITLE:
                 return new LevelUpdatedTitle(stream);
            case TYPE_MESSAGE:
                 return new LevelUpdatedMessage(stream);
            case TYPE_ACTION_BAR:
                 return new LevelUpdatedActionBar(stream);
            case TYPE_POPUP:
                 return new LevelUpdatedPopup(stream);
            // case TYPE_BOSS_EVENT: return new LevelUpdatedBossEvent(stream);
            // case TYPE_SCOREBOARD_DISPLAY: return new LevelUpdatedScoreboardDisplay(stream);
            case TYPE_CUSTOM_EVENT:
                 return new LevelUpdatedCustom(stream);
            case TYPE_TIME:
                 return new LevelUpdatedTime(stream);
            case TYPE_WEATHER:
                 return new LevelUpdatedWeather(stream);
            default:
                 throw new IllegalArgumentException("Unknown LevelUpdated type id: " + type);
        }
    }

}
