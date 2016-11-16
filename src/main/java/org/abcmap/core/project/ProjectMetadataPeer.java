package org.abcmap.core.project;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.project.dao.DataModel;

@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "metadata")
public class ProjectMetadataPeer implements DataModel {

    private static final String NAME_FIELD_NAME = "name";
    private static final String VALUE_FIELD_NAME = "value";

    @DatabaseField(id = true, columnName = NAME_FIELD_NAME)
    private PMConstants name;

    @DatabaseField(columnName = VALUE_FIELD_NAME)
    private String value;

    public ProjectMetadataPeer() {

    }

    public ProjectMetadataPeer(PMConstants name, String value) {
        this.name = name;
        this.value = value;
    }

    public PMConstants getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
