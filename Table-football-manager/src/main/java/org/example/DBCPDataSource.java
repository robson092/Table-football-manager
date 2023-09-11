package org.example;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPDataSource {
    private static final BasicDataSource ds = new BasicDataSource();
    private DBCPDataSource(){ }

    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/");
        ds.setUsername("postgres");
        ds.setPassword("root");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}

