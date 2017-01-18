package org.abcmap.tests.core.wms;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.wms.PredefinedWmsServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Try to load locale list of WMS servers
 */
public class LocaleListOfWMSServersTest extends ManagerTreeAccessUtil {

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void tests() throws Exception {

        //System.out.println(mapm().getListOfPredefinedWmsServers());

        ArrayList<PredefinedWmsServer> list = mapm().getListOfPredefinedWmsServers();
        assertTrue("List of WMS servers test 1", list != null);
        assertTrue("List of WMS servers test 2", list.size() > 1);

        for (PredefinedWmsServer server : list) {
            assertTrue("List of WMS servers test: " + server, server.getName() != null);
            assertTrue("List of WMS servers test: " + server, server.getUrl() != null);
        }

        // test search
        assertTrue("Search in list of WMS servers test: ", mapm().getPredefinedWMSServer(list.get(0).getName(), null) != null);
        assertTrue("Search in list of WMS servers test: ", mapm().getPredefinedWMSServer(null, list.get(0).getUrl()) != null);

    }

}
