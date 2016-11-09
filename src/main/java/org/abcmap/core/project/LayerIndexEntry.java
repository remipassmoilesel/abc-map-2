package org.abcmap.core.project;

/**
 * Layers metadata are stored in a separate table
 */
public class LayerIndexEntry {
    private String id;
    private String name;
    private boolean visible;
    private int zindex;

    public LayerIndexEntry(String id, String name, boolean visible, int zindex) {
        if (id == null) {
            generateNewIndex();
        } else {
            this.id = id;
        }
        this.name = name;
        this.visible = visible;
        this.zindex = zindex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.setId(generateId());
    }

    public static final String generateId() {
        int hash = String.valueOf(System.nanoTime()).hashCode();
        return "layer_" + hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayerIndexEntry that = (LayerIndexEntry) o;

        if (visible != that.visible) return false;
        if (zindex != that.zindex) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (visible ? 1 : 0);
        result = 31 * result + zindex;
        return result;
    }
}
