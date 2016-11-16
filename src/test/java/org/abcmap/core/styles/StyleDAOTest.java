package org.abcmap.core.styles;

import org.abcmap.TestUtils;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.StyleDAO;
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
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException, DAOException {

        Path tempfolder = TestUtils.PLAYGROUND_DIRECTORY.resolve("stylePersistenceTest");
        Files.createDirectories(tempfolder);

        Path db = tempfolder.resolve("styles.db");

        // clean previous db if necessary
        if (Files.exists(db)) {
            Files.delete(db);
        }

        StyleDAO dao = new StyleDAO(db);

        ArrayList<StyleContainer> stylePatterns = new ArrayList<>();
        stylePatterns.add(new StyleContainer(StyleType.LINE, Color.blue, Color.white, 5));
        stylePatterns.add(new StyleContainer(StyleType.POINT, Color.red, Color.blue, 51));
        stylePatterns.add(new StyleContainer(StyleType.POLYGON, Color.green, Color.white, 15));

        ArrayList<StyleContainer> written = new ArrayList<>();

        PrimitiveIterator.OfInt rand = new Random().ints(0, stylePatterns.size()).iterator();
        for (int i = 0; i < 30; i++) {

            StyleContainer s = new StyleContainer(stylePatterns.get(rand.next()));

            // change id
            s.generateId();

            dao.create(s);

            written.add(s);

        }

        assertTrue("Basic equality test", written.equals(written));

        ArrayList<StyleContainer> read = new ArrayList<>();

        dao.visit((Object o) -> {
            StyleContainer s = (StyleContainer) o;
            read.add(s);
            return true;
        });

        System.out.println(written);
        System.out.println(read);

        assertTrue("Read / write test", written.equals(read));

    }
}