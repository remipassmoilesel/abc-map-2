package org.abcmap.core.project.dao;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Created by remipassmoilesel on 15/11/16.
 */
public abstract class AbstractOrmDAO {

    public static final String TABLE_PREFIX = "abcmap_";

    protected final CustomLogger logger = LogManager.getLogger(AbstractOrmDAO.class);
    protected final JdbcPooledConnectionSource connectionSource;
    protected final Dao dao;
    private final Class<? extends DataModel> dataModel;

    public AbstractOrmDAO(Path dbPath, Class<? extends DataModel> entity) throws DAOException {

        String databaseUrl = "jdbc:sqlite:" + dbPath.toString();
        try {

            // initialize sqlite connection
            this.connectionSource = new JdbcPooledConnectionSource(databaseUrl);
            connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
            connectionSource.setTestBeforeGet(true);
            connectionSource.initialize();

            this.dataModel = entity;

            // create internal dao
            this.dao = DaoManager.createDao(connectionSource, entity);

            // create tables if needed
            createTableIfNotExist();

        } catch (SQLException e) {
            throw new DAOException(e);
        }


    }

    /**
     * Create table scheme if not existing
     *
     * @throws DAOException
     */
    public void createTableIfNotExist() throws DAOException {
        try {
            TableUtils.createTableIfNotExists(connectionSource, dataModel);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Return the connection source for this DAO
     *
     * @return
     */
    protected JdbcPooledConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * Iterate all elements of dao and apply the visitor. If the visitor return false, the iteration stop.
     *
     * @param visitor
     */
    public void visit(DAOVisitor visitor) {

        CloseableIterator it = dao.iterator();
        try {
            while (it.hasNext()) {
                boolean continueIteration = visitor.processObject(it.next());
                if (continueIteration == false) {
                    break;
                }
            }
        } finally {
            try {
                it.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }

    }

    /**
     * Create an entry for this object
     *
     * @param e
     */
    public void create(Object e) throws DAOException {
        try {
            dao.create(e);
        } catch (SQLException e1) {
            throw new DAOException(e1);
        }
    }

    /**
     * Delete an entry for this object
     *
     * @param e
     */
    public void update(Object e) throws DAOException {
        try {
            dao.update(e);
        } catch (SQLException e1) {
            throw new DAOException(e1);
        }
    }

    /**
     * Delete an entry for this object
     *
     * @param e
     */
    public void delete(Object e) throws DAOException {
        try {
            dao.delete(e);
        } catch (SQLException e1) {
            throw new DAOException(e1);
        }
    }

    /**
     * etrieve an id by its id
     *
     * @param e
     */
    public Object readById(Object e) throws DAOException {
        try {
            return dao.queryForId(e);
        } catch (SQLException e1) {
            throw new DAOException(e1);
        }
    }

    /**
     * Return number of row in table
     *
     * @return
     * @throws DAOException
     */
    public long getRowCount() throws DAOException {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Delete all entries from package
     *
     * @throws DAOException
     */
    public void deleteAll() throws DAOException {
        try {
            dao.deleteBuilder().delete();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Close resource if needed
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (connectionSource != null) {
            logger.warning("Database connection was not closed: " + this + " / " + connectionSource);
            connectionSource.close();
        }
    }

}
