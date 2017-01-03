package org.abcmap.gui.components.symbols;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Symbol selection on list
 */
public class SymbolSelector extends JPanel implements HasListenerHandler<ActionListener> {

    private DrawManager drawm;
    private SymbolSetView view;
    private ArrayList<String> availablesSets;
    private JComboBox<String> comboSetName;
    private JScrollPane scrollpane;

    private String selectedSetName;
    private Integer selectedSymbolCode;

    private ListenerHandler<ActionListener> listenerHandler;

    public SymbolSelector() {

        super(new MigLayout("insets 0"));

        this.drawm = Main.getDrawManager();
        this.listenerHandler = new ListenerHandler<>();

        // selection of symbol sets
        this.availablesSets = drawm.getAvailableSymbolSets();
        this.comboSetName = new JComboBox<>(availablesSets.toArray(new String[availablesSets.size()]));
        comboSetName.addActionListener(new ComboListener());

        add(comboSetName, "wrap");

        // view of symbol samples
        this.view = new SymbolSetView();
        view.getListenerHandler().add(new ViewListener());
        view.setSetName(availablesSets.get(0));
        view.reconstructPanel();

        // view is in scroll pane
        scrollpane = new JScrollPane(view);
        scrollpane.setAutoscrolls(true);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.getVerticalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        add(scrollpane, "width 210px!, height 200px!, wrap");

        // activate first set
        comboSetName.setSelectedIndex(0);
        view.selectFirstElement();
    }

    /**
     * Display selected set in scrollpane
     */
    private class ComboListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            selectedSetName = (String) comboSetName.getSelectedItem();

            view.setSetName(selectedSetName);
            view.reconstructPanel();
            view.selectFirstElement();

            scrollpane.revalidate();
            scrollpane.repaint();

        }

    }

    /**
     * Listen symbol selection and transmit events to component listeners
     */
    private class ViewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedSymbolCode = view.getSelectedCode();
            listenerHandler.fireEvent(e);
        }

    }

    /**
     * Get selected set name
     *
     * @return
     */
    public String getSelectedSetName() {
        return selectedSetName;
    }

    /**
     * Get selected symbol code
     *
     * @return
     */
    public Integer getSelectedSymbolCode() {
        return selectedSymbolCode;
    }

    /**
     * Update values of panel and notify observers
     *
     * @param symbolSetName
     * @param symbolCode
     */
    public void updateValues(String symbolSetName, int symbolCode) {
        updateValues(symbolSetName, symbolCode, true);
    }

    /**
     * Update values of panel and notify observers if specified
     *
     * @param symbolSetName
     * @param symbolCode
     * @param notify
     */
    public void updateValues(String symbolSetName, int symbolCode, boolean notify) {

        // nom du set
        GuiUtils.changeWithoutFire(comboSetName, symbolSetName);
        selectedSetName = symbolSetName;

        // code
        view.setSelectedCode(symbolCode, notify);
        selectedSymbolCode = symbolCode;

    }


    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
