package org.abcmap.core.project;

/**
 * Layers metadata are stored in a separate table
 */
public class LayerIndexEntry {

    private LayerType type;
    private String layerId;
    private String name;
    private boolean visible;
    private int zindex;

    public LayerIndexEntry(String layerId, String name, boolean visible, int zindex, LayerType type) {
        if (layerId == null) {
            generateNewIndex();
        } else {
            this.layerId = layerId;
        }
        this.name = name;
        this.visible = visible;
        this.zindex = zindex;
        this.type = type;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

    public String getLayerId() {
        return layerId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getZindex() {
        return zindex;
    }

    public void setZindex(int zindex) {
        this.zindex = zindex;
    }

    public void generateNewIndex() {
        this.setLayerId(generateId());
    }

    public static final String generateId() {
        return "abcmap_layerid_" + System.nanoTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayerIndexEntry that = (LayerIndexEntry) o;

        if (visible != that.visible) return false;
        if (zindex != that.zindex) return false;
        if (type != that.type) return false;
        if (layerId != null ? !layerId.equals(that.layerId) : that.layerId != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (layerId != null ? layerId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (visible ? 1 : 0);
        result = 31 * result + zindex;
        return result;
    }
}
