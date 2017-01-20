package org.abcmap.core.dao;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.SQLUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Created by remipassmoilesel on 15/11/16.
 */
public abstract class AbstractOrmDAO {

    protected final CustomLogger logger = LogManager.getLogger(AbstractOrmDAO.class);
    protected final JdbcPooledConnectionSource connectionSource;
    protected final Dao dao;
    private final Class<? extends DataModel> dataModel;

    public AbstractOrmDAO(Path dbPath, Class<? extends DataModel> entity) throws IOException {

        try {

            // initialize sqlite connection
            this.connectionSource = SQLUtils.getH2OrmliteConnectionPool(dbPath);

            this.dataModel = entity;

            // create internal dao
            this.dao = DaoManager.createDao(connectionSource, entity);

            // create tables if needed
            createTableIfNotExist();

        } catch (SQLException e) {
            throw new IOException(e);
        }


    }

    /**
     * Create table scheme if not existing
     *
     * @throws IOException
     */
    public void createTableIfNotExist() throws IOException {
        try {
            TableUtils.createTableIfNotExists(connectionSource, dataModel);
        } catch (SQLException e) {
            throw new IOException(e);
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
    public void create(Object e) throws IOException {

        throwIfIllegalType(e);

        try {
            dao.create(e);
        } catch (SQLException e1) {
            throw new IOException(e1);
        }
    }

    /**
     * Update an entry for this object
     *
     * @param e
     */
    public int update(Object e) throws IOException {

        throwIfIllegalType(e);

        try {
            return dao.update(e);
        } catch (SQLException e1) {
            throw new IOException(e1);
        }
    }

    /**
     * Delete an entry for this object
     *
     * @param e
     */
    public int delete(Object e) throws IOException {

        throwIfIllegalType(e);

        try {
            return dao.delete(e);
        } catch (SQLException e1) {
            throw new IOException(e1);
        }
    }

    /**
     * Retrieve an entry by id
     * <p>
     * Return null if nothing is found
     *
     * @param e
     */
    public Object readById(Object e) throws IOException {
        try {
            return dao.queryForId(e);
        } catch (SQLException e1) {
            throw new IOException(e1);
        }
    }

    /**
     * Return number of row in table
     *
     * @return
     * @throws IOException
     */
    public long getRowCount() throws IOException {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Delete all entries from package
     *
     * @throws IOException
     */
    public void deleteAll() throws IOException {
        try {
            dao.deleteBuilder().delete();
        } catch (SQLException e) {
            throw new IOException(e);
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
            logger.debug("Database connection was not closed: " + this + " / " + connectionSource);
            close();
        }

    }

    public void close() throws IOException {

        if (connectionSource != null) {
            connectionSource.close();
        }

        if (dao != null) {
            dao.closeLastIterator();
        }

    }

    protected void throwIfIllegalType(Object o) {
        if (dataModel.isInstance(o) == false) {
            throw new IllegalArgumentException("Illegal class " + o.getClass().getName() + ", need " + dataModel.getName());
        }
    }

}
