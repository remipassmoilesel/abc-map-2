package org.abcmap.core.dao;

import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.core.project.ProjectMetadataPeer;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This object allow to read and write metadata from project
 */
public class ProjectMetadataDAO extends AbstractOrmDAO {

    public ProjectMetadataDAO(Path dbPath) throws IOException {
        super(dbPath, ProjectMetadataPeer.class);
    }

    public ProjectMetadata readMetadata() {

        ProjectMetadata ctr = new ProjectMetadata();

        visit((Object o) -> {
            ProjectMetadataPeer p = (ProjectMetadataPeer) o;
            ctr.updateValue(p.getName(), p.getValue());
            return true;
        });

        return ctr;
    }

    public void writeMetadata(ProjectMetadata ctr) throws IOException {

        deleteAll();

        HashMap<String, String> values = ctr.getAllValues();
        Iterator<String> it = values.keySet().iterator();

        while (it.hasNext()) {

            String name = it.next();
            String value = values.get(name);

            try {
                dao.create(new ProjectMetadataPeer(name, value));
            } catch (SQLException e) {
                throw new IOException(e);
            }

        }

    }


}
