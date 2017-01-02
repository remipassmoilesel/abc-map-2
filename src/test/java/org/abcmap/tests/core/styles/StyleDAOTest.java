package org.abcmap.tests.core.styles;

import org.abcmap.TestUtils;
import org.abcmap.core.dao.StyleDAO;
import org.abcmap.core.styles.StyleContainer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class StyleDAOTest {


    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void tests() throws IOException {

        Path tempfolder = TestUtils.PLAYGROUND_DIRECTORY.resolve("stylePersistenceTest");
        Files.createDirectories(tempfolder);

        Path db = tempfolder.resolve("styles.db");

        // clean previous db if necessary
        if (Files.exists(db)) {
            Files.delete(db);
        }

        StyleDAO dao = new StyleDAO(db);

        ArrayList<StyleContainer> stylePatterns = new ArrayList<>();
        stylePatterns.add(new StyleContainer(Color.blue, Color.white, 5));
        stylePatterns.add(new StyleContainer(Color.red, Color.blue, 51));
        stylePatterns.add(new StyleContainer(Color.green, Color.white, 15));

        ArrayList<StyleContainer> written = new ArrayList<>();

        PrimitiveIterator.OfInt rand = new Random().ints(0, stylePatterns.size()).iterator();
        for (int i = 0; i < 30; i++) {

            StyleContainer s = new StyleContainer(stylePatterns.get(rand.next()));

            // change numericalId
            s.generateId();

            dao.create(s);

            written.add(s);

        }

        assertTrue("Basic equality test", written.equals(written));

        ArrayList<StyleContainer> read = dao.readStyles();

        // here we cannot compare lists directly because order change after reading
        for (StyleContainer styleContainer : read) {
            assertTrue("Read / write test: " + styleContainer, written.contains(styleContainer));
        }


    }
}
