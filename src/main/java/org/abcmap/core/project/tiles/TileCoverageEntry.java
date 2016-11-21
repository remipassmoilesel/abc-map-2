package org.abcmap.core.project.tiles;

import java.util.Objects;

/**
 * Store information about a tile coverage
 */
public class TileCoverageEntry {

    private String coverageName;
    private String spatialTableName;
    private String dataTableName;

    public TileCoverageEntry(String coverageName) {
        this.coverageName = coverageName;
        this.spatialTableName = TileStorage.generateSpatialTableName(coverageName);
        this.dataTableName = TileStorage.generateDataTableName(coverageName);
    }

    public String getCoverageName() {
        return coverageName;
    }

    public void setCoverageName(String coverageName) {
        this.coverageName = coverageName;
    }

    public String getSpatialTableName() {
        return spatialTableName;
    }

    public void setSpatialTableName(String spatialTableName) {
        this.spatialTableName = spatialTableName;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileCoverageEntry that = (TileCoverageEntry) o;
        return Objects.equals(coverageName, that.coverageName) &&
                Objects.equals(spatialTableName, that.spatialTableName) &&
                Objects.equals(dataTableName, that.dataTableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverageName, spatialTableName, dataTableName);
    }


}
