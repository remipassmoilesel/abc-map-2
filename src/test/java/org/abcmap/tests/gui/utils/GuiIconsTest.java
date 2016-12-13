package org.abcmap.tests.gui.utils;

import org.abcmap.gui.GuiIcons;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class GuiIconsTest {

    @Test
    public void test() throws IllegalAccessException {

        Field[] fields = GuiIcons.class.getDeclaredFields();

        // test if all icons are accessibles
        for (Field field : fields) {

            if (Modifier.isPublic(field.getModifiers())) {
                Object value = field.get(null);
                assertTrue("Gui icon test: " + field, value != null);
                assertTrue("Gui icon test: " + field, value != new ImageIcon());
            }

        }


    }
}
