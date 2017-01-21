package org.abcmap.gui.components.data;

import org.abcmap.core.managers.ImportManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Select headers of current file to import
 */
public class DataHeadersCombo extends JComboBox<String> implements HasEventNotificationManager {

    private EventNotificationManager notifm;
    private ImportManager importm;

    public DataHeadersCombo() {
        super();

        importm = Main.getImportManager();

        setEditable(false);

        // listen import manager
        ComboUpdater cbUpdater = new ComboUpdater();
        notifm = new EventNotificationManager(this);
        notifm.addEventListener(cbUpdater);
        importm.getNotificationManager().addObserver(this);

        // first update
        cbUpdater.updateFormFields();

    }

    private class ComboUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();

            // get values of combo
            List<String> currents = GuiUtils.getAllValuesFrom(DataHeadersCombo.this);

            // get values from manager
            ArrayList<String> importHeaders = importm.getDataImportCurrentHeaders();
            if (importHeaders.size() < 1) {
                importHeaders.add("Aucun en-tête");
            }

            // checks values
            if (Utils.safeEquals(importHeaders, currents) == false) {

                // create new modele
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel(
                        importHeaders.toArray(new String[importHeaders.size()]));

                // change model
                DataHeadersCombo.this.setModel(model);

            }

        }

    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}
