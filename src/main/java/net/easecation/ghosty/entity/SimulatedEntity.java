package net.easecation.ghosty.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.*;
import net.easecation.ghosty.recording.entity.SkinInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimulatedEntity extends Entity {

    /**
     * 全局的实体名称标签提供器，用于接入应用层插件的多语言翻译
     */
    public interface NameTagProcessor {
        String getNameTag(Player player, String origin);
    }

    static EntityMetadata cloneEntityMetadata(EntityMetadata metadata) {
        Map<Integer, EntityData> map = metadata.getMap();
        EntityMetadata re = new EntityMetadata();
        map.forEach((i, data) -> re.put(data));
        return re;
    }

    static Map<SkinInfo, Skin> registeredSkinMap = new HashMap<>();

    public static void registerSkin(Skin skin) {
        registeredSkinMap.put(SkinInfo.fromSkin(skin), skin);
    }

    public static NameTagProcessor globalNameTagProcessor = null;

    private final int networkId;
    private final String entityIdentifier;
    private final long originEid;

    public Item item = null;
    private UUID uuid = null;
    private SkinInfo skinInfo = null;

    public SimulatedEntity(FullChunk chunk, CompoundTag nbt, int networkId, String entityIdentifier, long originEid) {
        super(chunk, nbt);
        this.networkId = networkId;
        this.entityIdentifier = entityIdentifier;
        this.originEid = originEid;
        //this.needEntityBaseTick = false;
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    public long getOriginEid() {
        return originEid;
    }

    @Override
    protected float getBaseOffset() {
        if (this.networkId == EntityItem.NETWORK_ID) {
            return 0.125f;
        } else if (this.networkId == -1 || this.networkId == /*EntityID.PLAYER*/63) {
            return 1.62f;
        } else if (this.networkId == EntityPrimedTNT.NETWORK_ID) {
            return 0.49f;
        }
        return super.getBaseOffset();
    }

    public UUID getUniqueId() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        return this.uuid;
    }

    @Override
    public void setNameTag(String name) {
        if (globalNameTagProcessor == null) {
            super.setNameTag(name);
            return;
        }
        this.setDataProperty(new StringEntityData(DATA_NAMETAG, name), false);
        for (Player player : this.getViewers().values()) {
            StringEntityData data = new StringEntityData(DATA_NAMETAG, globalNameTagProcessor.getNameTag(player, name));
            this.sendData(new Player[]{player}, new EntityMetadata().put(data));
        }
    }

    @Override
    public void setScoreTag(String score) {
        if (globalNameTagProcessor == null) {
            super.setScoreTag(score);
            return;
        }
        this.setDataProperty(new StringEntityData(DATA_SCORE_TAG, score), false);
        for (Player player : this.getViewers().values()) {
            StringEntityData data = new StringEntityData(DATA_SCORE_TAG, globalNameTagProcessor.getNameTag(player, score));
            this.sendData(new Player[]{player}, new EntityMetadata().put(data));
        }
    }

    public SkinInfo getSkinInfo() {
        return skinInfo;
    }

    public void setSkinInfo(SkinInfo skinInfo) {
        this.skinInfo = skinInfo;
        if (this.getNetworkId() == -1 || this.getNetworkId() == /*EntityID.PLAYER*/ 63) {
            Skin skin = registeredSkinMap.get(skinInfo);
            if (skin != null) {
                for (Player player : this.hasSpawned.values()) {
                    this.sendSkin(player, skin);
                }
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }
        DataPacket pk = this.createAddEntityPacket(player);
        player.dataPacket(pk);
        if (this.getNetworkId() == -1 || this.getNetworkId() == /*EntityID.PLAYER*/ 63) {
            if (skinInfo != null) {
                Skin skin = registeredSkinMap.get(skinInfo);
                if (skin != null) {
                    this.sendSkin(player, skin);
                }
            }
        }
        super.spawnTo(player);
    }

    public void sendSkin(Player player, Skin skin) {
        PlayerListPacket pk1 = new PlayerListPacket();
        pk1.type = PlayerListPacket.TYPE_ADD;
        PlayerListPacket.Entry entry = new PlayerListPacket.Entry(this.uuid, this.getId(), this.getNameTag(), skin);
        pk1.entries = new PlayerListPacket.Entry[]{entry};
        player.dataPacket(pk1);
    }

    protected DataPacket createAddEntityPacket(Player player) {
        if (networkId == EntityItem.NETWORK_ID) {
            AddItemEntityPacket addEntity = new AddItemEntityPacket();
            addEntity.entityUniqueId = this.getId();
            addEntity.entityRuntimeId = this.getId();
            addEntity.x = (float) this.x;
            addEntity.y = (float) this.y + this.getBaseOffset();
            addEntity.z = (float) this.z;
            addEntity.speedX = (float) this.motionX;
            addEntity.speedY = (float) this.motionY;
            addEntity.speedZ = (float) this.motionZ;
            if (globalNameTagProcessor != null && !this.getNameTag().isEmpty() || !this.getScoreTag().isEmpty()) {
                addEntity.metadata = cloneEntityMetadata(this.dataProperties)
                    .putString(Entity.DATA_NAMETAG, globalNameTagProcessor.getNameTag(player, this.getNameTag()))
                    .putString(Entity.DATA_SCORE_TAG, globalNameTagProcessor.getNameTag(player, this.getScoreTag()));
            } else {
                addEntity.metadata = this.dataProperties;
            }
            addEntity.item = this.item;
            return addEntity;
        } else if (networkId == -1 || networkId == /*EntityID.PLAYER*/ 63) {
            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getNameTag();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            //pk.headYaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.item;
            if (globalNameTagProcessor != null && !this.getNameTag().isEmpty() || !this.getScoreTag().isEmpty()) {
                pk.metadata = cloneEntityMetadata(this.dataProperties)
                    .putString(Entity.DATA_NAMETAG, globalNameTagProcessor.getNameTag(player, this.getNameTag()))
                    .putString(Entity.DATA_SCORE_TAG, globalNameTagProcessor.getNameTag(player, this.getScoreTag()));
            } else {
                pk.metadata = this.dataProperties;
            }
            return pk;
        } else if (networkId == 0 && !this.entityIdentifier.isEmpty()) {
            AddEntityPacket addEntityPacket = (AddEntityPacket) super.createAddEntityPacket();
            addEntityPacket.id = this.entityIdentifier;
            if (globalNameTagProcessor != null && !this.getNameTag().isEmpty() || !this.getScoreTag().isEmpty()) {
                addEntityPacket.metadata = cloneEntityMetadata(this.dataProperties)
                    .putString(Entity.DATA_NAMETAG, globalNameTagProcessor.getNameTag(player, this.getNameTag()))
                    .putString(Entity.DATA_SCORE_TAG, globalNameTagProcessor.getNameTag(player, this.getScoreTag()));
            }
            return addEntityPacket;
        } else {
            AddEntityPacket addEntityPacket = (AddEntityPacket) super.createAddEntityPacket();
            if (globalNameTagProcessor != null && !this.getNameTag().isEmpty() || !this.getScoreTag().isEmpty()) {
                addEntityPacket.metadata = cloneEntityMetadata(this.dataProperties)
                    .putString(Entity.DATA_NAMETAG, globalNameTagProcessor.getNameTag(player, this.getNameTag()))
                    .putString(Entity.DATA_SCORE_TAG, globalNameTagProcessor.getNameTag(player, this.getScoreTag()));
            }
            return addEntityPacket;
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
