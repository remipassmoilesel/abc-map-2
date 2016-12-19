package org.abcmap.gui.wizards;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Step of assistant. Has next and preivous button.
 */
public class WizardStepPanel extends JPanel {

    /**
     * Tag used to do link to interaction groups
     */
    protected static final String IE_GROUP_LINK_MARK = "#";

    private String stepName;
    private Wizard parent;
    private JButton btnPrevious;
    private JButton btnNext;

    private boolean btnNextEnabled;
    private boolean btnPreviousEnabled;

    protected GuiManager guim;
    protected ProjectManager projectm;

    private List elements;

    public WizardStepPanel(Wizard wizard) {
        super(new MigLayout("fillx, insets 5 10 5 5"));

        guim = MainManager.getGuiManager();
        projectm = MainManager.getProjectManager();

        parent = wizard;
        stepName = "";

        btnNextEnabled = true;
        btnPreviousEnabled = true;

    }

    public void reconstruct() {

        removeAll();

        // wizard title
        HtmlLabel title = new HtmlLabel(parent.getTitle());
        title.setStyle(GuiStyle.DOCK_MENU_TITLE_1);
        addItem(title);

        // step title
        HtmlLabel lblStepName = new HtmlLabel(stepName);
        lblStepName.setStyle(GuiStyle.WIZARD_STEP_NAME);
        addItem(lblStepName);

        String[] toolTips = new String[]{"Etape suivante",
                "Revenir à la liste des assistants", "Ouvrir dans une fenêtre",
                "Etape précédente",};

        btnPrevious = new JButton(GuiIcons.WIZARD_PREVIOUS);
        btnPrevious.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.previousStep();
            }
        });
        btnPrevious.setEnabled(btnPreviousEnabled);

        btnNext = new JButton(GuiIcons.WIZARD_NEXT);
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.nextStep();
            }
        });
        btnNext.setEnabled(btnNextEnabled);

        JButton btnHome = new JButton(GuiIcons.WIZARD_HOME);
        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showWizardHome();
            }
        });

        JButton btnOpenWindow = new JButton(GuiIcons.WIZARD_NEW_WINDOW);
        btnOpenWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showInDetachedWindow();
            }
        });

        // new window button
        btnOpenWindow.setEnabled(parent.isNewWindowButtonEnabled());

        // separated panel for buttons
        JPanel pan = new JPanel(new MigLayout("insets 5, gap 5"));
        String btnDim = "width 30!, height 30!";

        JButton[] btns = new JButton[]{btnPrevious, btnHome, btnOpenWindow,
                btnNext};
        for (int i = 0; i < btns.length; i++) {
            JButton btn = btns[i];
            btn.setToolTipText(toolTips[i]);
            pan.add(btn, btnDim);
        }
        add(pan, "align center, wrap");

        // add step objects
        for (Object o : elements) {

            // element is a graphical Swing component, add it as it is
            if (o instanceof Component) {
                addItem((Component) o);
            }

            // element is a formatted string like this: #GroupClass#Description
            // This represent a link to a group, create button and add it
            else if (o instanceof String && o.toString().substring(0, 1).equals(IE_GROUP_LINK_MARK)) {

                // séparer le nom de la clase et le texte du bouton
                String[] parts = o.toString().split(IE_GROUP_LINK_MARK);
                if (parts.length < 3) {
                    throw new IllegalStateException("Invalid format: '" + o.toString() + "'. Expected: #GroupClass#Description");
                }

                final String className = parts[1];
                String buttonText = parts[2];

                HtmlButton bt = new HtmlButton(buttonText);
                bt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        guim.showGroupInDock(className);
                    }
                });

                addItem(bt);
            }

            // interaction element
            else if (o instanceof InteractionElement) {

                InteractionElement ie = (InteractionElement) o;
                HtmlButton btn = new HtmlButton(ie.getLabel());
                if (ie.getMenuIcon() != null) {
                    btn.setIcon(ie.getMenuIcon());
                }

                btn.addActionListener(ie);

                addItem(btn);

            }

            // simple text
            else {
                addItem(new HtmlLabel(o.toString()));
            }

        }

    }

    private void addItem(Component c) {
        super.add(c, "width 90%!, wrap 15px");
    }

    public void setNextButtonEnabled(boolean val) {
        btnNextEnabled = val;
    }

    public void setPreviousButtonEnabled(boolean val) {
        btnPreviousEnabled = val;
    }

    public String getName() {
        return stepName;
    }

    public void setName(String name) {
        this.stepName = name;
    }

    public void refresh() {
        this.validate();
        this.repaint();
    }

    public void addElements(List elements) {
        this.elements = elements;
    }

    public List<Object> getElements() {
        return elements;
    }

}
