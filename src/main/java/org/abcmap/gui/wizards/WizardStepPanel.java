package org.abcmap.gui.wizards;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.managers.stub.MainManager;
import net.miginfocom.swing.MigLayout;
import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlButton;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.GuiManager;
import abcmap.managers.ProjectManager;
import abcmap.utils.Refreshable;

/**
 * Panneau d'étape d'assistant. Un assistant est composé de plusieurs étapes.
 * Comporte un bouton précédent, suivant, et un bouton retour au menu des
 * assistants.
 * 
 * @author remipassmoilesel
 *
 */
public class WizardStepPanel extends JPanel implements Refreshable {

	/**
	 * Marque indiquant que la chaine correspond à un group d'interaction.
	 * Utilisée dans les liste d'objets passées en paramètres dans addElements()
	 */
	protected static final String IE_GROUP_MARK = "#";

	/** Le nom de l'étape */
	private String stepName;

	/** Le wizard parent */
	private Wizard parent;

	/** Le bouton suivant */
	private JButton btnPrevious;

	/** Le bouton précédent */
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

	@Override
	public void reconstruct() {

		removeAll();

		// titre de l'assitant
		HtmlLabel title = new HtmlLabel(parent.getTitle());
		title.setStyle(GuiStyle.DOCK_MENU_TITLE_1);
		addItem(title);

		// titre de l'étape
		HtmlLabel lblStepName = new HtmlLabel(stepName);
		lblStepName.setStyle(GuiStyle.WIZARD_STEP_NAME);
		addItem(lblStepName);

		String[] toolTips = new String[] { "Etape suivante",
				"Revenir à la liste des assistants", "Ouvrir dans une fenêtre",
				"Etape précédente", };

		// bouton suivant / précédent / home
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

		// activer ou desactiver le bouton de nouvelle fenêtre
		btnOpenWindow.setEnabled(parent.isNewWindowButtonEnabled());

		// affichage des boutons
		JPanel pan = new JPanel(new MigLayout("insets 5, gap 5"));
		String btnDim = "width 30!, height 30!";

		JButton[] btns = new JButton[] { btnPrevious, btnHome, btnOpenWindow,
				btnNext };
		for (int i = 0; i < btns.length; i++) {
			JButton btn = btns[i];
			btn.setToolTipText(toolTips[i]);
			pan.add(btn, btnDim);
		}
		add(pan, "align center, wrap");

		// ajout des objets de l'étape
		for (Object o : elements) {

			// l'element est un composant graphique, ajout tel quel
			if (o instanceof Component) {
				addItem((Component) o);
			}

			// l'element est une chaine spéciale de la forme
			// #GroupClass#Description
			// créer un bouton d'affichage de groupe d'interaction
			else if (o instanceof String
					&& o.toString().substring(0, 1).equals(IE_GROUP_MARK)) {

				// séparer le nom de la clase et le texte du bouton
				String[] parts = o.toString().split(IE_GROUP_MARK);
				if (parts.length < 3) {
					throw new IllegalStateException("Invalid format: '"
							+ o.toString()
							+ "'. Expected: #GroupClass#Description");
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

			// l'element est un groupe d'interaction
			else if (o instanceof InteractionElement) {

				InteractionElement ie = (InteractionElement) o;
				HtmlButton btn = new HtmlButton(ie.getLabel());
				if (ie.getMenuIcon() != null) {
					btn.setIcon(ie.getMenuIcon());
				}

				btn.addActionListener(ie);

				addItem(btn);

			}

			// l'element est de type texte
			else {
				addItem(new HtmlLabel(o.toString()));
			}

		}

	}

	private void addItem(Component c) {
		super.add(c, "width " + (Dock.MENU_ITEM_WIDTH - 5) + "!, wrap 15px");
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

	@Override
	public void refresh() {
		this.validate();
		this.repaint();
	}

	/**
	 * Ajouter des éléments au panneau d'étape, dans l'ordre dans lequel ils
	 * sont donnés.
	 * <p>
	 * Si l'élément est de type String, un panneau avec JLabel sera créé.
	 * <p>
	 * Si l'élément est de type Component, alors il sera ajouté tel quel.
	 * 
	 * @param elements
	 */
	public void addElements(List elements) {
		this.elements = elements;
	}

	public List<Object> getElements() {
		return elements;
	}

}
