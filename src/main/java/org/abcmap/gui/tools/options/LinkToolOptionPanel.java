package org.abcmap.gui.tools.options;

public class LinkToolOptionPanel extends ToolOptionPanel {
/*
    private FormUpdater formUpdater;
	private JTextField txtLocation;
	private JRadioButton[] rdActions;
	private HtmlCheckbox chkActivateLink;
	private ComponentGroup compGroup;

	public LinkToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		compGroup = new ComponentGroup();

		// checkbox d'activation des liens
		chkActivateLink = new HtmlCheckbox("Activer un lien hypertexte");
		chkActivateLink.addActionListener(new Performer(DrawConstants.MODIFY_LINK));
		add(chkActivateLink, largeWrap);

		// champs texte avec le chemin de la source
		GuiUtils.addLabel("Source du lien: ", this, "wrap");

		txtLocation = new JTextField(30);
		TextFieldDelayedAction.delayedActionFor(txtLocation,
				new Performer(DrawConstants.MODIFY_LINK), false);
		add(txtLocation, gapLeft + "width 150px, " + largeWrap);
		compGroup.add(txtLocation);

		// type d'actions disponibles
		LinkAction[] actions = new LinkAction[] { LinkAction.OPEN_IN_BROWSER,
				LinkAction.OPEN_ON_DESKTOP, LinkAction.OPEN_IN_MAILER, };
		String[] actionLabels = new String[] { "Ouvrir dans un navigateur",
				"Ouvrir dans le système", "Ouvrir dans une messagerie", };

		// boutons radios avec nature de l'action
		GuiUtils.addLabel("Nature de l'action: ", this, "wrap");

		ButtonGroup group = new ButtonGroup();
		rdActions = new JRadioButton[actionLabels.length];

		for (int i = 0; i < actionLabels.length; i++) {
			rdActions[i] = new JRadioButton(actionLabels[i]);
			rdActions[i].setActionCommand(actions[i].toString());
			rdActions[i].addActionListener(new Performer(DrawConstants.MODIFY_LINK));
			add(rdActions[i], gapLeft + "wrap");

			group.add(rdActions[i]);
			compGroup.add(rdActions[i]);
		}

		// objet de mise à jour
		formUpdater = new FormUpdater();

		// ecouter le projet et le gest de dessin
		observer.setDefaultUpdatableObject(formUpdater);
		projectm.getNotificationManager().addObserver(this);

		// première mise à jour
		formUpdater.run();

	}

	/**
	 * Mettre à jour les formulaires
	 * 
	 * @author remipassmoilesel
	 *

	private class FormUpdater extends abcmap.utils.gui.FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer la premiere forme selectionnée et eventuellement son
			// lien
			LayerElement shp = (LayerElement) getFirstSelectedElement();
			LinkRessource link = shp != null ? shp.getLinkRessources() : null;

			// pas d'elements selectionnes, ou pas de lien, RAZ puis arret
			if (link == null) {
				compGroup.setEnabled(false);
				compGroup.setSelectedWithoutFire(false);
				updateComponentWithoutFire(chkActivateLink, false);
				return;
			}

			// activer le formulaire
			compGroup.setEnabled(true);

			// recuperer l'action
			LinkAction action = link.getAction();

			// maj du check d'activation
			updateComponentWithoutFire(chkActivateLink, true);

			// maj du composant texte
			updateComponentWithoutFire(txtLocation, link.getLocation());

			// maj des boutons radios
			for (int i = 0; i < rdActions.length; i++) {
				if (action.toString().equalsIgnoreCase(rdActions[i].getActionCommand())) {
					if (rdActions[i].isSelected() != true) {
						updateComponentWithoutFire(rdActions[i], true);
					}
					break;
				}
			}

		}

	}

	private class Performer extends ShapeUpdater {

		public Performer(DrawConstants mode) {
			setMode(mode);
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			LinkRessource link = null;

			// le lien est activé
			if (chkActivateLink.isSelected()) {

				// creer la ressource
				String location = txtLocation.getText();
				LinkAction action = getSelectedAction();

				if (location == null || action == null)
					return;

				link = LinkLibrary.getLink(location, action);
			}

			// creer l'objet a transferer
			ShapeProperties pp = new ShapeProperties();
			pp.linkRessource = link;

			setProperties(pp);

		}

	}

	private LinkAction getSelectedAction() {

		LinkAction action = null;
		for (JRadioButton btn : rdActions) {
			try {
				action = LinkAction.valueOf(btn.getActionCommand());
				break;
			} catch (Exception e) {
				Log.error(e);
			}
		}

		return action;
	}
	*/

}
