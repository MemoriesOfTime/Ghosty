package net.easecation.ghosty.recording.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.easecation.ghosty.GhostyPlugin;
import net.easecation.ghosty.recording.level.updated.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.easecation.ghosty.GhostyPlugin.DEBUG_DUMP;

/**
 * 这里是暂存着每一帧的所有更新的地方，用于回放和录制
 * 所有变更都会在这里暂存，因为事件来源包含各处，例如事件监听器、每tick比较、在应用层插件手动插入
 * 然后在RecordEngine每tick进行一次写入Updates并清空这里的暂存
 */
public final class LevelRecordNode {

    /**
     * 仅用于回放
     * 在播放时，记录所有的方块变更的变更前方块。用于回放时的回退。
     */
    private final Int2ObjectLinkedOpenHashMap<Map<BlockVector3, Block>> blockChangeLog = new Int2ObjectLinkedOpenHashMap<>();
    /**
     * 用于录制和回放
     * 暂存所有应用于世界的方块变更，然后在每tick应用于世界
     */
    private final Map<BlockVector3, Block> blockChanges = new HashMap<>();
    /**
     * 用于录制和回放
     * 暂存所有应用于世界的数据包，然后在每tick应用于世界
     */
    private final Long2ObjectMap<List<DataPacket>> levelChunkPackets = new Long2ObjectOpenHashMap<>();
    /**
     * 只用于回放
     * 在回放时的level回调，用于一些特殊的updates（例如title等）
     */
    private final List<Consumer<Level>> levelGlobalCallback = new ArrayList<>();
    /**
     * 只用于录制
     * 来自外部的直接添加的Updates
     */
    private final List<LevelUpdated> extraRecordUpdates = new ArrayList<>();

    public void clear() {
        blockChanges.clear();
        levelChunkPackets.clear();
        levelGlobalCallback.clear();
        extraRecordUpdates.clear();
    }

    public List<LevelUpdated> toUpdated() {
        List<LevelUpdated> list = new ArrayList<>();
        for (Map.Entry<BlockVector3, Block> entry : blockChanges.entrySet()) {
            list.add(LevelUpdatedBlockChange.of(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Long, List<DataPacket>> entry : levelChunkPackets.long2ObjectEntrySet()) {
            for (DataPacket packet : entry.getValue()) {
                // BlockEventPacket
                if (packet instanceof BlockEventPacket pk) {
                    list.add(LevelUpdatedBlockEvent.of(pk));
                }
                // LevelEventPacket
                else if (packet instanceof LevelEventPacket pk) {
                    list.add(LevelUpdatedLevelEvent.of(pk));
                }
                // LevelSoundEventPacket
                else if (packet instanceof LevelSoundEventPacket pk) {
                    list.add(LevelUpdatedLevelSoundEvent.of(pk));
                } else if (packet instanceof PlaySoundPacket pk) {
                    list.add(LevelUpdatedPlaySound.of(pk));
                }
            }
        }
        list.addAll(extraRecordUpdates);
        return list;
    }

    public void applyToLevel(int tick, Level level) {
        for (Map.Entry<BlockVector3, Block> entry : blockChanges.entrySet()) {
            Block originBlock = level.getBlock(entry.getKey().asVector3());
            this.blockChangeLog.putIfAbsent(tick, new HashMap<>());
            this.blockChangeLog.get(tick).put(entry.getKey(), originBlock);
            level.setBlock(entry.getKey().asVector3(), entry.getValue(), true, false);
        }
        for (Map.Entry<Long, List<DataPacket>> entry : levelChunkPackets.long2ObjectEntrySet()) {
            int chunkX = Level.getHashX(entry.getKey());
            int chunkZ = Level.getHashZ(entry.getKey());
            for (DataPacket pk : entry.getValue()) {
                level.addChunkPacket(chunkX, chunkZ, pk);
            }
        }
        for (Consumer<Level> consumer : levelGlobalCallback) {
            consumer.accept(level);
        }
    }

    public void handleBlockChange(BlockVector3 pos, Block block) {
        blockChanges.put(pos, block);
        if (DEBUG_DUMP) {
            GhostyPlugin.getInstance().getLogger().debug("Block change: " + pos + " -> " + block);
        }
    }

    public void fallbackBlockChangeTo(int tick, Level level) {
        // 从后往前寻找blockChangeLog
        if (blockChangeLog.isEmpty()) {
            return;
        }
        int last = blockChangeLog.lastIntKey();
        while (last > tick) {
            Map<BlockVector3, Block> map = blockChangeLog.remove(last);
            for (Map.Entry<BlockVector3, Block> entry : map.entrySet()) {
                level.setBlock(entry.getKey().asVector3(), entry.getValue(), true, false);
            }
            last = blockChangeLog.isEmpty() ? -1 : blockChangeLog.lastIntKey();
        }
    }

    private static final Long2ObjectFunction<List<DataPacket>> CHUNK_PACKET_MAPPING_FUNCTION = k -> new ArrayList<>();

    public void handleLevelChunkPacket(long chunkIndex, DataPacket pk) {
        levelChunkPackets.computeIfAbsent(chunkIndex, CHUNK_PACKET_MAPPING_FUNCTION).add(pk);
    }

    public void offerLevelGlobalCallback(Consumer<Level> callback) {
        levelGlobalCallback.add(callback);
    }

    public void offerExtraRecordUpdate(LevelUpdated update) {
        extraRecordUpdates.add(update);
    }

}
