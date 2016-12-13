package org.abcmap.tests.database;

import org.abcmap.core.utils.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Function;


public class WriteAccessTest implements Runnable {

    public static int numericalId = 0;
    private final Connection conn;
    private final Function<String, Void> callWhenDone;

    public WriteAccessTest(Connection conn, Function<String, Void> callWhenDone) {
        this.callWhenDone = callWhenDone;
        this.conn = conn;
    }

    @Override
    public void run() {

        numericalId++;
        String stringId = this.getClass().getSimpleName() + numericalId;

        System.out.println(stringId + " Launching");


        try {
            SQLUtils.processTransaction(conn, (conn1 -> {

                System.out.println(stringId + " Writing");

                PreparedStatement createStat = conn.prepareStatement("CREATE TABLE \"" + stringId.toLowerCase() + "\" (columnA TEXT NOT NULL, columnB TEXT NOT NULL);");
                createStat.execute();

                for (int i = 0; i < 5; i++) {


                    PreparedStatement stat = conn.prepareStatement("INSERT INTO " + stringId.toLowerCase() + " (columnA, columnB) VALUES(?,?);");
                    stat.setString(1, "fakeValue_" + System.nanoTime());
                    stat.setString(2, "fakeValue_" + System.nanoTime());

                    stat.execute();

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
