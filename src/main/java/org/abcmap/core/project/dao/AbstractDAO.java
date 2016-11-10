package org.abcmap.core.project.dao;

import java.sql.Connection;

public abstract class AbstractDAO {

    protected Connection connection;

    public AbstractDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // Do not close connection here
//        if(connection != null){
//            connection.close();
//        }
    }

}
