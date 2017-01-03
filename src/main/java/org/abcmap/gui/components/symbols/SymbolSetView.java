package org.abcmap.gui.components.symbols;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.draw.DrawManagerException;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Display samples of a symbol set
 *
 * @author remipassmoilesel
 */
class SymbolSetView extends JPanel implements HasListenerHandler<ActionListener> {

    private DrawManager drawm;

    private Font symbolSetfont;
    private String setName;

    private ArrayList<Integer> availablesSymbolCodes;
    private ArrayList<JToggleButton> buttons;

    private Integer selectedCode;

    private Integer symbolDisplaySize;

    private int cols;

    private ListenerHandler<ActionListener> listenerHandler;

    public SymbolSetView() {
        super();

        drawm = Main.getDrawManager();

        this.selectedCode = null;
        this.setName = null;
        this.symbolSetfont = null;
        this.availablesSymbolCodes = null;
        this.symbolDisplaySize = 25;
        this.cols = 3;

        listenerHandler = new ListenerHandler<>();

        reconstructPanel();

    }

    public SymbolSetView(String setname) {
        super();
        this.setName = setname;

        reconstructPanel();
    }

    /**
     * Build panel with symbol samples
     */
    public void reconstructPanel() {

        removeAll();

        // get font associated with set
        boolean exceptionHappend = false;
        if (setName != null) {
            try {
                symbolSetfont = drawm.getSymbolSetFont(setName);
            } catch (DrawManagerException e) {
                exceptionHappend = true;
            }
        }

        // no or no sybols
        if (setName == null || exceptionHappend == true) {
            GuiUtils.addLabel("<i>Pas de symbole à afficher</i>", this);
            return;
        }

        // adjust font for displaying
        Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
        map.put(TextAttribute.SIZE, symbolDisplaySize);
        symbolSetfont = symbolSetfont.deriveFont(map);

        // get availables code
        availablesSymbolCodes = drawm.getAvailableSymbolCodesFor(setName);

        // adapt layout
        this.setLayout(new MigLayout("insets 2, gap 2, wrap " + cols));

        // create a group for all symbols
        ButtonGroup bg = new ButtonGroup();
        buttons = new ArrayList<>();

        SelectionActionDispatcher selectionActionDispatcher = new SelectionActionDispatcher();

        for (int i : availablesSymbolCodes) {


            JToggleButton jt = new JToggleButton();
            jt.setFont(symbolSetfont);
            jt.setText(Character.toString((char) i));

            // select first symbol
            if (availablesSymbolCodes.indexOf(i) == 0) {
                jt.setSelected(true);
            }

            jt.setActionCommand(String.valueOf(i));
            jt.addActionListener(selectionActionDispatcher);

            bg.add(jt);
            buttons.add(jt);

            this.add(jt, "width 60px!, height 40px!");
        }

        this.revalidate();
        this.repaint();

    }

    /**
     * Set the symbol set name
     *
     * @param setName
     */
    public void setSetName(String setName) {
        this.setName = setName;
    }

    /**
     * Listen symbol selection and fire event
     */
    private class SelectionActionDispatcher implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JToggleButton src = ((JToggleButton) e.getSource());

            if (src.isSelected()) {
                selectedCode = Integer.valueOf(src.getActionCommand());
                listenerHandler.fireEvent(e);
            }

        }

    }

    /**
     * Get the selected code
     *
     * @return
     */
    public int getSelectedCode() {
        return selectedCode;
    }

    /**
     * Set the selected code and notify observers
     *
     * @param symbolCode
     */
    public void setSelectedCode(int symbolCode) {
        setSelectedCode(symbolCode, false);
    }

    /**
     * Set the selected code and notify observers if specified
     *
     * @param symbolCode
     */
    public void setSelectedCode(int symbolCode, boolean notify) {

        if (availablesSymbolCodes.indexOf(symbolCode) == -1) {
            throw new IllegalArgumentException("Unknown code: " + symbolCode);
        }

        selectedCode = symbolCode;

        // activate corresponding button
        String code = String.valueOf(symbolCode);
        for (JToggleButton jbtn : buttons) {
            if (code.equals(jbtn.getActionCommand())) {

                GuiUtils.setSelected(jbtn, true);

                // show button in scrollpane
                scrollRectToVisible(jbtn.getBounds());

                break;
            }
        }

        if (notify) {
            listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
        }

    }

    /**
     * Select the first button
     */
    public void selectFirstElement() {
        buttons.get(0).doClick();
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
