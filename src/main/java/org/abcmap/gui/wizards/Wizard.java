package org.abcmap.gui.wizards;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.iegroup.docks.GroupWizard;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.HasDisplayableSpace;
import org.abcmap.gui.windows.DetachedWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Software wizard
 */
public class Wizard {

    /**
     * Steps of wizard
     */
    protected ArrayList<WizardStepPanel> steps;

    /**
     * Current displayed step
     */
    protected int currentStep;

    /**
     * Wizard title
     */
    protected String title;

    /**
     * Wizard description
     */
    protected String description;

    /**
     * Where is display wizard
     */
    private HasDisplayableSpace parent;

    protected GuiManager guim;

    /**
     * If set to true, a button allow user to use wizard in another window
     */
    protected boolean newWindowButtonEnabled;

    public Wizard() {
        guim = Main.getGuiManager();

        this.steps = new ArrayList<>();
        this.currentStep = 0;
        this.title = "no name";
        this.description = "no description";

        this.newWindowButtonEnabled = true;
    }

    public void reconstructStepPanels() {
        for (WizardStepPanel wsp : steps) {
            wsp.reconstruct();
        }
    }

    public boolean isNewWindowButtonEnabled() {
        return newWindowButtonEnabled;
    }


    public void setNewWindowButtonEnabled(boolean newWindowButtonEnabled) {
        this.newWindowButtonEnabled = newWindowButtonEnabled;
    }

    /**
     * Create and register a new step in wizard
     *
     * @return
     */
    protected WizardStepPanel addNewStep() {
        WizardStepPanel wsp = new WizardStepPanel(this);
        steps.add(wsp);
        return wsp;
    }

    /**
     * Create and register a new step in wizard
     *
     * @return
     */

    protected WizardStepPanel addNewStep(String name, List elements) {

        WizardStepPanel wsp = new WizardStepPanel(this);
        steps.add(wsp);

        wsp.setName(name);
        wsp.addElements(elements);

        wsp.reconstruct();

        return wsp;
    }

    /**
     * Create and register new steps from a list
     *
     * @return
     */
    protected void addNewSteps(ArrayList<String> titles, ArrayList<List> elements) {

        int ts = titles.size();
        int es = elements.size();

        if ((ts + es) / 2 != ts) {
            throw new IllegalStateException("Invalids arguments, lists must have the same size: " + ts + " " + es);
        }

        for (int i = 0; i < titles.size(); i++) {
            String t = titles.get(i);
            addNewStep(t, elements.get(i));
        }

    }

    /**
     * Clone this wizard and display it in a new window
     */
    public void showInDetachedWindow() {

        GuiUtils.throwIfNotOnEDT();

        Wizard w2;
        try {
            w2 = this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        DetachedWindow wizwin = guim.getWizardDetachedWindow();
        w2.setNewWindowButtonEnabled(false);
        w2.setDisplayableSpace(wizwin);

        w2.showStep(currentStep);

        // uneeded beacause called by showStep
        // wizwin.reconstruct();

        wizwin.moveToDefaultPosition();
        wizwin.setVisible(true);

    }

    public void setDisplayableSpace(HasDisplayableSpace parent) {
        this.parent = parent;
    }

    /**
     * Display a wizard step by index
     *
     * @param i
     */
    public void showStep(int i) {

        currentStep = i;

        if (steps.size() < 1) {
            throw new IllegalStateException("No step to show !");
        }

        if (parent == null) {
            throw new NullPointerException("No parent where show wizard !");
        }

        if (currentStep > steps.size() - 1) {
            currentStep = steps.size() - 1;
        }

        if (currentStep < 0) {
            currentStep = 0;
        }

        WizardStepPanel wsp = steps.get(currentStep);

        boolean prevEnabled = currentStep > 0;
        boolean suivEnabled = currentStep < steps.size() - 1;

        wsp.setPreviousButtonEnabled(prevEnabled);
        wsp.setNextButtonEnabled(suivEnabled);

        wsp.reconstruct();

        parent.displayComponent(wsp);

    }

    public void nextStep() {
        currentStep++;
        showStep(currentStep);
    }

    public void previousStep() {
        currentStep--;
        showStep(currentStep);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void showWizardHome() {
        guim.showGroupInDock(GroupWizard.class);
    }

    /**
     * Create a string representing a link to a group of interaction element.
     * <p>
     * From this string a button will be created, button which can show a group in interface
     *
     * @param class1
     * @param desc
     * @return
     */
    protected String createShowGroupString(Class<? extends GroupOfInteractionElements> class1, String desc) {
        return WizardStepPanel.IE_GROUP_LINK_MARK + class1.getSimpleName() + WizardStepPanel.IE_GROUP_LINK_MARK + desc;
    }

}
