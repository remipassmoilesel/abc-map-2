package org.abcmap.gui.tests;

import org.abcmap.gui.utils.GuiUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class GuiUtilsTests {

    /**
     * Check if running on EDT or not
     */
    @Test(expected = IllegalStateException.class)
    public void throwIfNotOnEdtTest() {
        GuiUtils.throwIfNotOnEDT();
    }

    /**
     * Check component parent search
     *
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Test
    public void searchParentTest() throws InvocationTargetException,
            InterruptedException {

        SwingUtilities.invokeAndWait(() -> {

            JPanel panel = new JPanel();
            JPanel panel2 = new JPanel();
            JPanel panel3 = new JPanel();

            panel.add(panel2);
            panel2.add(panel3);

            Assert.assertEquals(panel2,
                    GuiUtils.searchParentOf(panel3, JPanel.class));

            Assert.assertEquals(null,
                    GuiUtils.searchParentOf(new JPanel(), JPanel.class));
        });

    }

    /**
     * Check component children search
     *
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Test
    public void searchChildsTest() throws InvocationTargetException,
            InterruptedException {

        SwingUtilities.invokeAndWait(() -> {


            JPanel panel = new JPanel();
            JPanel panel2 = new JPanel();
            JPanel panel3 = new JPanel();


            panel.add(panel2);
            panel2.add(panel3);

            // expected result
            List<JPanel> rslt = Arrays.asList(panel, panel2, panel3);

            List<Component> list = GuiUtils.listAllComponentsFrom(panel);

            Assert.assertTrue(rslt.equals(list));
        });

    }
}
