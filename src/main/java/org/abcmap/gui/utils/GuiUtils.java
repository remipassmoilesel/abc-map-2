package org.abcmap.gui.utils;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.HtmlLabel;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class GuiUtils {

    private static final CustomLogger logger = LogManager.getLogger(GuiUtils.class);
    private static Object qualityRenderingHints;

    /**
     * Configure default look and feel
     *
     * @param className
     */
    public static void configureUIManager(String className) {

        try {
            UIManager.setLookAndFeel(className);
        } catch (Exception e) {
            logger.error(e);
            try {
                UIManager.setLookAndFeel(UIManager
                        .getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException | UnsupportedLookAndFeelException e1) {
                logger.error(e);
            }
        }

    }

    /**
     * Set all UI font vars to specified value
     *
     * @param font
     */
    public static void setDefaultUIFont(Font font) {

        // create value
        FontUIResource fontUi = new FontUIResource(font);

        // iterate all properties
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {

            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            // replace compatible values
            if (value != null
                    && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, fontUi);
            }
        }

    }

    /**
     * Show components in window
     *
     * @param components
     */
    public static void showThese(Component[] components) {
        showThese(Arrays.asList(components));
    }

    /**
     * Display components in a window, for debug purpose
     *
     * @param components
     */
    public static void showThese(List<Component> components) {

        JPanel panel = new JPanel(new MigLayout());
        for (Component comp : components) {
            panel.add(comp, "wrap");
        }

        showThis(panel, -1, -1);
    }

    /**
     * Display components in a window, for debug purpose
     *
     * @param comp
     */
    public static void showThis(final Component comp) {
        showThis(comp, -1, -1);
    }

    /**
     * Display components in a window, for debug purpose
     *
     * @param comp
     */
    public static void showThis(final Component comp, final int width,
                                final int height) {

        SwingUtilities.invokeLater(() -> {

            JFrame jf = new JFrame();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel cp = new JPanel(new FlowLayout());
            cp.add(comp);

            jf.setContentPane(cp);

            if (width <= 0 || height <= 0) {
                jf.pack();
            } else {
                jf.setSize(new Dimension(width, height));
            }

            jf.setLocationRelativeTo(null);
            jf.setVisible(true);
        });

    }

    /**
     * Show a drawing sequence, for debug purposes
     *
     * @param dp
     */
    public static void showDrawingTestFrame(DrawingTestFrame.DrawingProcedure dp) {
        DrawingTestFrame.show(dp);
    }

    /**
     * Draw graphical axes of a Java Graphic object
     *
     * @param g
     */
    public static void drawGraphicsAxesLines(Graphics2D g) {

        Color xColor = Color.blue;
        int xMax = 500;

        Color yColor = Color.red;
        int yMax = 500;

        drawGraphicsAxesLines(g, xMax, xColor, yMax, yColor);
    }

    /**
     * Show x and y axes associated with a Java Graphics object.
     * <p>
     * For debug purposes
     *
     * @param g
     */
    public static void drawGraphicsAxesLines(Graphics2D g, int xMax,
                                             Color xColor, int yMax, Color yColor) {

        GuiUtils.throwIfNotOnEDT();

        int xStep = 10;
        int yStep = 10;
        int thick = 5;

        g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        // dessiner l'axe x
        g.setColor(xColor);
        g.setPaint(xColor);
        g.drawLine(0, 0, xMax, 0);

        // dessiner les thicks x
        for (int i = xStep; i < xMax; i += xStep) {
            g.fillRect(i, 0, thick / 2, thick);
        }

        // dessiner x
        g.drawString("x", xMax + 20, 30);

        // dessiner l'axe y
        g.setColor(yColor);
        g.setPaint(yColor);
        g.drawLine(0, 0, 0, yMax);

        // dessiner les thicks x
        for (int i = yStep; i < yMax; i += yStep) {
            g.fillRect(0, i, thick, thick / 2);
        }

        // dessienr y
        g.drawString("y", 10, yMax + 20);
    }

    /**
     * Improve Java Graphics rendering quality
     *
     * @param g2d
     */
    public static void applyQualityRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHints(getQualityRenderingHints());
    }

    /**
     * Get java rendering hints corresponding to high quality rendering
     *
     * @return
     */
    public static HashMap<RenderingHints.Key, Object> getQualityRenderingHints() {
        HashMap<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return hints;
    }

    /**
     * Draw a string with guide lines
     *
     * @param g
     * @param font
     * @param str
     * @param x
     * @param y
     */
    public static void drawStringAndLines(Graphics2D g, Font font, String str,
                                          int x, int y) {

        FontMetrics fm = g.getFontMetrics(font);

        // guide line colors
        Color heightColor = Color.green;
        Color ascentColor = Color.cyan;
        Color maxAscentColor = Color.blue;
        Color descentColor = Color.pink;
        Color maxDescentColor = Color.pink;
        Color leadingColor = Color.black;
        Color widthColor = Color.darkGray;

        int width = fm.stringWidth(str);

        g.setFont(font);
        g.drawString(str, x, y);

        g.setStroke(new BasicStroke(1));

        int pointWidth = 6;
        g.setColor(Color.black);
        g.setPaint(Color.black);
        g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth,
                pointWidth);

        // draw height
        g.setColor(heightColor);
        g.drawLine(x, y - fm.getHeight(), x + width, y - fm.getHeight());

        // draw top line
        g.setColor(ascentColor);
        g.drawLine(x, y - fm.getAscent(), x + width, y - fm.getAscent());

        // draw top max line
        g.setColor(maxAscentColor);
        g.drawLine(x, y - fm.getMaxAscent(), x + width, y - fm.getMaxAscent());

        // draw bottom line
        g.setColor(descentColor);
        g.drawLine(x, y - fm.getDescent(), x + width, y - fm.getDescent());

        // draw bottom max line
        g.setColor(maxDescentColor);
        g.drawLine(x, y - fm.getMaxDescent(), x + width, y - fm.getMaxDescent());

        // draw leading line
        g.setColor(leadingColor);
        g.drawLine(x, y - fm.getLeading(), x + width, y - fm.getLeading());

        // draw width lines
        g.setColor(widthColor);
        g.drawLine(x, y - fm.getHeight(), x, y);
        g.drawLine(x + width, y - fm.getHeight(), x + width, y);

    }

    /**
     * Fill a area with a semi opaque color
     *
     * @param g
     * @param area
     */
    public static void fillArea(Graphics2D g, Area area) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(createTransparencyComposite(0.5f));
        g2.setPaint(Color.blue);
        g.fill(area);
    }

    /**
     * Create graphic transparency
     *
     * @param value
     * @return
     */
    public static AlphaComposite createTransparencyComposite(float value) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, value);
    }

    /**
     * Return the getNext parent of specified class, or null
     *
     * @param comp
     * @param searchedClass
     * @return
     */
    public static Component searchParentOf(Component comp, Class searchedClass) {

        throwIfNotOnEDT();

        if (comp == null) {
            return null;
        }

        Container parent = comp.getParent();

        if (parent == null) {
            return null;
        } else if (searchedClass.isInstance(parent)) {
            return parent;
        } else {
            return searchParentOf(parent, searchedClass);
        }

    }

    /**
     * Get all child of given class
     *
     * @param comp
     * @return
     */
    public static List<Component> listAllComponentsFrom(Component comp) {
        return listAllComponentsFrom(comp, null);
    }

    /**
     * Get all child of given class
     *
     * @param comp
     * @param list
     * @return
     */
    public static List<Component> listAllComponentsFrom(Component comp,
                                                        List<Component> list) {

        throwIfNotOnEDT();

        if (list == null) {
            list = new ArrayList<Component>();
        }

        if (comp instanceof Container == false) {
            list.add(comp);
        } else {

            list.add(comp);

            Container cont = (Container) comp;
            for (Component c : cont.getComponents()) {
                listAllComponentsFrom(c, list);
            }
        }

        return list;

    }

    /**
     * Apply revalidate and repaint on all child of component
     *
     * @param comp
     */
    public static void refreshAllComponentsFrom(Component comp) {

        List<Component> list = listAllComponentsFrom(comp, null);

        Collections.reverse(list);

        for (Component c : list) {
            c.revalidate();
            c.repaint();
        }
    }

    /**
     * Add element with absolute coordinates
     *
     * @param panel
     * @param comp
     * @param x
     * @param y
     */
    public static void addElementToJpanelWithoutLayout(JPanel panel,
                                                       Component comp, int x, int y) {

        panel.setLayout(null);

        Rectangle b = comp.getBounds();
        b.x = x;
        b.y = y;

        panel.add(comp);
        comp.setBounds(b);
    }

    public static void throwIfNotOnEDT() {
        if (SwingUtilities.isEventDispatchThread() == false) {
            throw new IllegalStateException("Running out of EDT");
        }
    }

    public static void throwIfOnEDT() {
        if (SwingUtilities.isEventDispatchThread() == true) {
            throw new IllegalStateException("Running in EDT");
        }
    }

    /**
     * Change text component value and repaint component
     *
     * @param comp
     * @param value
     */
    public static void changeText(JTextComponent comp, String value) {

        throwIfNotOnEDT();

        // modifier la valeur si necessaire
        if (Utils.safeEquals(comp.getText(), value) == false) {
            comp.setText(value);
        }

        // repeindre
        comp.revalidate();
        comp.repaint();

    }

    /**
     * Change a component value without fire event. Beware: not all listeners are concerned.
     *
     * @param comp
     * @param value
     */
    public static void changeTextWithoutFire(JTextComponent comp, String value) {

        throwIfNotOnEDT();

        // remove listeners
        ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

        // change value
        comp.setText(value);
        comp.revalidate();
        comp.repaint();

        // add listeners again
        addSupportedListenersTo(comp, listeners);

    }

    /**
     * Change a component value without fire event. Beware: not all listeners are concerned.
     *
     * @param comp
     * @param value
     */
    public static void changeWithoutFire(JComboBox comp, Object value) {

        throwIfNotOnEDT();

        // remove listeners
        ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

        // change value
        comp.setSelectedItem(value);
        comp.revalidate();
        comp.repaint();

        // add listeners again
        addSupportedListenersTo(comp, listeners);

    }

    /**
     * Change a component value without fire event. Beware: not all listeners are concerned.
     *
     * @param comp
     * @param index
     */
    public static void changeIndexWithoutFire(JComboBox comp, int index) {

        throwIfNotOnEDT();

        // remove listeners
        ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

        // change value
        comp.setSelectedIndex(index);
        comp.revalidate();
        comp.repaint();

        // add listeners again
        addSupportedListenersTo(comp, listeners);

    }

    /**
     * Change a component value without fire event.
     *
     * @param comp
     * @param value
     */
    public static void setSelected(AbstractButton comp, Boolean value) {

        throwIfNotOnEDT();

        // change value if necessary
        if (comp.isSelected() != value) {
            comp.setSelected(value);
        }

        // repaint
        comp.revalidate();
        comp.repaint();

    }

    /**
     * Change a component value without fire event. Beware: not all listeners are concerned.
     *
     * @param comp
     * @param value
     */
    public static void changeWithoutFire(JSlider comp, int value) {

        // remove listeners
        ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

        // change value
        comp.setValue(value);
        comp.revalidate();
        comp.repaint();

        // add listeners again
        addSupportedListenersTo(comp, listeners);

    }

    /**
     * Change a component value without fire event. Beware: not all listeners are concerned.
     *
     * @param comp
     * @param value
     */
    public static void changeWithoutFire(JList comp, Object value,
                                         boolean shouldAutoScroll) {

        // remvoe listeners
        ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

        // change value
        comp.setSelectedValue(value, shouldAutoScroll);
        comp.revalidate();
        comp.repaint();

        // add listeners again
        addSupportedListenersTo(comp, listeners);

    }

    /**
     * Add a listener list to a component. Throw an exception if a component is not compatible with listener.
     *
     * @param comp
     * @param lst
     */
    public static void addSupportedListenersTo(Component comp,
                                                  ArrayList<EventListener> lst) {

		/*
         * Every component is processed separately because every listeners cannot be reached with getListeners()
		 */

		/*
         * Composants texte
		 */
        if (comp instanceof JTextComponent) {

            JTextComponent txt = ((JTextComponent) comp);

            // récupérer le document si possible
            AbstractDocument doc = null;
            if (txt.getDocument() instanceof AbstractDocument) {
                doc = (AbstractDocument) ((JTextComponent) comp).getDocument();
            }

            for (EventListener l : lst) {

                if (l instanceof CaretListener) {
                    txt.addCaretListener((CaretListener) l);
                } else if (l instanceof DocumentListener && doc != null) {
                    doc.addDocumentListener((DocumentListener) l);
                } else {
                    throw new IllegalStateException("Unknown listener: "
                            + l.getClass() + " / " + comp.getClass());
                }
            }

        } else if (comp instanceof JList) {

            JList list = ((JList) comp);

            for (EventListener l : lst) {

                if (l instanceof ListSelectionListener) {
                    list.addListSelectionListener((ListSelectionListener) l);
                } else {
                    throw new IllegalStateException("Unknown listener: "
                            + l.getClass() + " / " + comp.getClass());
                }
            }

        } else if (comp instanceof JComboBox) {

            JComboBox list = ((JComboBox) comp);

            for (EventListener l : lst) {

                if (l instanceof ActionListener) {
                    list.addActionListener((ActionListener) l);
                } else {
                    throw new IllegalStateException("Unknown listener: "
                            + l.getClass() + " / " + comp.getClass());
                }
            }

        } else if (comp instanceof JSlider) {

            JSlider list = ((JSlider) comp);

            for (EventListener l : lst) {

                if (l instanceof ChangeListener) {
                    list.addChangeListener((ChangeListener) l);
                } else {
                    throw new IllegalStateException("Unknown listener: "
                            + l.getClass() + " / " + comp.getClass());
                }
            }

        } else {
            throw new IllegalStateException("Unsupported component: "
                    + comp.getClass());
        }

    }

    /**
     * Remove all supported listeners from a component
     *
     * @param comp
     * @return
     */
    public static ArrayList<EventListener> removeSupportedListenersFrom(
            JComponent comp) {

        /*
         * Every component is processed separately because every listeners cannot be reached with getListeners()
		 */


        ArrayList<EventListener> lst = new ArrayList<EventListener>();


        if (comp instanceof JTextComponent) {

            // lister les carets listeners
            List<CaretListener> clisteners = Arrays
                    .asList(((JTextComponent) comp).getCaretListeners());

            // conserver une référence
            lst.addAll(clisteners);

            // les retirer
            for (CaretListener l : clisteners) {
                ((JTextComponent) comp).removeCaretListener(l);
            }

            if (((JTextComponent) comp).getDocument() instanceof AbstractDocument) {

                AbstractDocument doc = (AbstractDocument) ((JTextComponent) comp)
                        .getDocument();

                // lister les documents listeners
                List<DocumentListener> dlisteners = Arrays.asList(doc
                        .getDocumentListeners());

                // conserver une référence
                lst.addAll(dlisteners);

                // les retirer
                for (DocumentListener l : dlisteners) {
                    doc.removeDocumentListener(l);
                }

            }
        } else if (comp instanceof JList) {

            List<ListSelectionListener> llisteners = Arrays
                    .asList(((JList) comp).getListSelectionListeners());

            lst.addAll(llisteners);

            for (ListSelectionListener l : llisteners) {
                ((JList) comp).removeListSelectionListener(l);
            }

        } else if (comp instanceof JComboBox) {

            List<ActionListener> alisteners = Arrays.asList(((JComboBox) comp)
                    .getActionListeners());

            lst.addAll(alisteners);

            for (ActionListener l : alisteners) {
                ((JComboBox) comp).removeActionListener(l);
            }

        } else if (comp instanceof JSlider) {

            List<ChangeListener> alisteners = Arrays.asList(((JSlider) comp)
                    .getChangeListeners());

            lst.addAll(alisteners);

            for (ChangeListener l : alisteners) {
                ((JSlider) comp).removeChangeListener(l);
            }

        } else {
            throw new IllegalStateException("Unsupported component: "
                    + comp.getClass());
        }

        return lst;

    }

    /**
     * Get all values from a combo list
     *
     * @param combo
     * @return
     */

    public static <T> List<T> getAllValuesFrom(JComboBox<T> combo) {

        int size = combo.getModel().getSize();

        ArrayList<T> result = new ArrayList<T>();

        for (int i = 0; i < size; i++) {
            result.add(combo.getModel().getElementAt(i));
        }

        return result;

    }

    /**
     * Print informations about a look and feel
     *
     * @param className
     */
    public static void printUiManagerInfos(String className) {

        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        UIDefaults defaults = UIManager.getDefaults();
        Enumeration newKeys = defaults.keys();

        PrintWriter fop = null;
        File file = new File("uimanager_infos.txt");
        try {
            fop = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (newKeys.hasMoreElements()) {
            Object obj = newKeys.nextElement();
            fop.printf("%50s : %s\n", obj, UIManager.get(obj));
        }

        System.out.println("UIManager informations write in :" + file.getAbsolutePath());
    }

    /**
     * Create a JLabel and add it to a component
     *
     * @param text
     * @param container
     * @return
     */
    public static JLabel addLabel(String text, Container container) {
        return addLabel(text, container, null, null);
    }

    /**
     * Create a JLabel and add it to a component
     *
     * @param text
     * @param container
     * @param constraints
     * @return
     */
    public static JLabel addLabel(String text, Container container,
                                  Object constraints) {
        return addLabel(text, container, constraints, null);
    }

    /**
     * Create a JLabel and add it to a component
     *
     * @param text
     * @param container
     * @param constraints
     * @param style
     * @return
     */
    public static JLabel addLabel(String text, Container container,
                                  Object constraints, GuiStyle style) {

        // create a jlabel
        HtmlLabel lbl = new HtmlLabel(text);

        // apply style
        if (style != null) {
            lbl.setStyle(style);
        }

        // add it to container with specified constraints
        if (constraints != null) {
            container.add(lbl, constraints);
        }

        // or add it without constraints
        else {
            container.add(lbl);
        }

        // and return it
        return lbl;
    }

    /**
     * Show image in window
     *
     * @param img
     */
    public static void showImage(BufferedImage img) {
        showImage("", img);
    }

    /**
     * Show image in windwos, with specified title
     *
     * @param title
     * @param img
     */
    public static void showImage(String title, BufferedImage img) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame();
            frame.setTitle(title + " " + img.toString());

            // create label for image, with border
            JLabel lbl = new JLabel(new ImageIcon(img));
            lbl.setBorder(BorderFactory.createLineBorder(Color.blue));

            JPanel content = new JPanel();
            content.add(lbl);

            frame.setContentPane(content);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }


}
