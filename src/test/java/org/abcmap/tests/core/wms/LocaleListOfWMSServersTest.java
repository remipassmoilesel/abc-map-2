package org.abcmap.tests.core.wms;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.wms.WmsServerCredentials;
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

        //
        // Test local list
        //

        //System.out.println(mapm().getListOfPredefinedWmsServers());

        ArrayList<WmsServerCredentials> localeList = mapm().getLocaleListOfPredefinedWmsServers();
        assertTrue("Locale list of WMS servers test 1", localeList != null);
        assertTrue("Locale list of WMS servers test 2", localeList.size() > 1);

        for (WmsServerCredentials server : localeList) {
            assertTrue("Locale list of WMS servers test: " + server, server.getName() != null);
            assertTrue("Locale list of WMS servers test: " + server, server.getUrl() != null);
        }

        // test search
        assertTrue("Search in locale list of WMS servers test: ",
                mapm().getPredefinedWmsServer(localeList.get(0).getName(), null) != null);
        assertTrue("Search in locale list of WMS servers test: ",
                mapm().getPredefinedWmsServer(null, localeList.get(0).getUrl()) != null);

        //
        // Test distant list
        //

        ArrayList<WmsServerCredentials> distantList = mapm().getDistantListOfPredefinedWmsServers();
        assertTrue("Distant list of WMS servers test: " + distantList.size(), distantList.size() > 0);

    }

}
