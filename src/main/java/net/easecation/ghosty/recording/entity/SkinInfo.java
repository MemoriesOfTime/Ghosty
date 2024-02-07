package net.easecation.ghosty.recording.entity;

import cn.nukkit.entity.data.Skin;
import net.easecation.ghosty.MD5Util;

import java.util.Objects;

/**
 * @author glorydark
 */
public class SkinInfo {

    private final String geoName;

    private final String dataHash;

    public SkinInfo(String geoName, String dataHash) {
        this.geoName = geoName;
        this.dataHash = dataHash;
    }

    public static SkinInfo fromSkin(Skin skin) {
        String skinMd5;
            /*if (skin.getSkinMd5() != null) {
                skinMd5 = skin.getSkinMd5();
            } else {
                skinMd5 = MD5Util.md5SkinData(skin.getSkinData().data);
                skin.setSkinMd5(skinMd5);
            }*/
        //使用getSkinResourcePatch代替
        return new SkinInfo(skin.getSkinResourcePatch(), skinMd5 = MD5Util.md5SkinData(skin.getSkinData().data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkinInfo skinInfo = (SkinInfo) o;

        if (!Objects.equals(geoName, skinInfo.geoName)) return false;
        return Objects.equals(dataHash, skinInfo.dataHash);
    }

    @Override
    public int hashCode() {
        int result = geoName != null ? geoName.hashCode() : 0;
        result = 31 * result + (dataHash != null ? dataHash.hashCode() : 0);
        return result;
    }

    public String getDataHash() {
        return dataHash;
    }

    public String getGeoName() {
        return geoName;
    }
}
