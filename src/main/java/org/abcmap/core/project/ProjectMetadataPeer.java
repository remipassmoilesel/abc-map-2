package org.abcmap.core.project;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;

@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "METADATA")
public class ProjectMetadataPeer implements DataModel {

    private static final String NAME_FIELD_NAME = "NAME";
    private static final String VALUE_FIELD_NAME = "VALUE";

    @DatabaseField(id = true, columnName = NAME_FIELD_NAME)
    private String name;

    @DatabaseField(columnName = VALUE_FIELD_NAME)
    private String value;

    public ProjectMetadataPeer() {

    }

    public ProjectMetadataPeer(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Return name of metadata peer
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return value of metadata peer
     *
     * @return
     */
    public String getValue() {
        return value;
    }
}
