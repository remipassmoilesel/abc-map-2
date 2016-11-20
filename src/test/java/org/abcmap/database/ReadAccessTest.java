package org.abcmap.database;

import org.abcmap.core.utils.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Function;

public class ReadAccessTest implements Runnable {

    public static int numericalId = 0;
    private final Connection conn;
    private final Function<String, Void> callWhenDone;

    public ReadAccessTest(Connection conn, Function<String, Void> callWhenDone) {
        this.callWhenDone = callWhenDone;
        this.conn = conn;
    }

    @Override
    public void run() {

        numericalId++;
        String stringId = this.getClass().getSimpleName() + "_" + numericalId;

        try {
            SQLUtils.processTransaction(conn, (conn1 -> {

                System.out.println(stringId + " Reading");

                for (int i = 0; i < 5; i++) {

                    PreparedStatement stat = conn.prepareStatement("SELECT * FROM " + ConcurrentReadWriteDatabaseTest.TEST_TABLE_NAME);
                    stat.executeQuery();

                    Thread.sleep(500);
                }

                return null;
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(stringId + " Done");

        callWhenDone.apply(stringId);
    }
}