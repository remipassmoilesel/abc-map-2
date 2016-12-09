package org.abcmap.gui.components.data;

import org.abcmap.core.managers.ImportManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Select headers of current file to import
 */
public class DataHeadersCombo extends JComboBox<String> implements HasNotificationManager {

    private NotificationManager notifm;
    private ImportManager importm;

    public DataHeadersCombo() {
        super();

        importm = MainManager.getImportManager();

        setEditable(false);

        // listen import manager
        ComboUpdater cbUpdater = new ComboUpdater();
        notifm = new NotificationManager(this);
        notifm.setDefaultUpdatableObject(cbUpdater);
        importm.getNotificationManager().addObserver(this);

        // first update
        cbUpdater.updateFields();

    }

    private class ComboUpdater extends FormUpdater {

        @Override
        protected void updateFields() {
            super.updateFields();

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
    public NotificationManager getNotificationManager() {
        return notifm;
    }

}
