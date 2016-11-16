package org.abcmap.core.project.dao;

import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.core.project.ProjectMetadataPeer;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This object allow to read and write metadata from project
 */
public class ProjectMetadataDAO extends AbstractOrmDAO {

    public ProjectMetadataDAO(Path dbPath) throws DAOException {
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

    public void writeMetadata(ProjectMetadata ctr) throws DAOException {

        deleteAll();

        HashMap<PMConstants, String> values = ctr.getAllValues();
        Iterator<PMConstants> it = values.keySet().iterator();

        while (it.hasNext()) {

            PMConstants name = it.next();
            String value = values.get(name);

            try {
                dao.create(new ProjectMetadataPeer(name, value));
            } catch (SQLException e) {
                throw new DAOException(e);
            }

        }

    }


}
