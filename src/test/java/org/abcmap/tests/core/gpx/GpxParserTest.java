package org.abcmap.tests.core.gpx;

import com.google.common.io.Resources;
import org.abcmap.core.importation.gpx.GpxParser;
import org.abcmap.core.importation.gpx.GpxParsingException;
import org.abcmap.core.importation.gpx.GpxPointsList;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxParserTest {

    @Test
    public void parseTest() throws GpxParsingException, IOException {

        // files to parse
        String sourceV10 = Resources.toString(GpxParserTest.class.getResource("/gpx/track-1.0.gpx"), Charset.forName("utf-8"));
        String sourceV11 = Resources.toString(GpxParserTest.class.getResource("/gpx/track-1.1.gpx"), Charset.forName("utf-8"));

        // parse GPX v 1.0
        GpxParser parserv10 = new GpxParser();
        parserv10.setGpxSource(sourceV10);
        parserv10.parse();

        ArrayList<GpxPointsList> list1 = parserv10.getPointsLists();
        Assert.assertTrue("GPX v1.0 test", list1.size() == 1);
        Assert.assertTrue("GPX v1.0 test 2", list1.get(0).getPoints().size() > 0);

        // parse GPX v1.1
        GpxParser parserv11 = new GpxParser();
        parserv11.setGpxSource(sourceV11);
        parserv11.parse();

        ArrayList<GpxPointsList> list2 = parserv11.getPointsLists();
        Assert.assertTrue("GPX v1.1 test", list2.size() == 3);
        Assert.assertTrue("GPX v1.1 test 2", list2.get(0).getPoints().size() > 0);

        /*
        System.out.println("list1");
        System.out.println(list1);
        System.out.println("list2");
        System.out.println(list2);
        */
    }

}
