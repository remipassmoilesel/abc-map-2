package org.abcmap.tests.core.threads;

import org.abcmap.TestUtils;
import org.abcmap.core.threads.ManagedTask;
import org.abcmap.core.threads.ThreadManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Thread testing
 *
 * Not a real test case, just here to be sure that we can access the previous call stack of a thread
 */
public class ThreadTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void test() throws InterruptedException {

        ManagedTask task = ThreadManager.runLater(()->{
            throw new RuntimeException("Fake exception");
        });

        assertTrue(task.getCallStack() != null);
        assertTrue(task.getCallStack().length > 0);

        ManagedTask task2 = ThreadManager.runLater(()->{
            throw new RuntimeException("Fake exception 2");
        }, 500);

        Thread.sleep(600);

        assertTrue(task2.getCallStack() != null);
        assertTrue(task2.getCallStack().length > 0);

    }

}
