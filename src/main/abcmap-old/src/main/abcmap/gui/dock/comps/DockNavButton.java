package abcmap.gui.dock.comps;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import abcmap.gui.GuiIcons;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

/**
 * Bouton haut de menu dock avec fonctions déterminées: suivant, précédent, fermer ... 
 * @author remipassmoilesel
 *
 */
public class DockNavButton extends JLabel {

	public static final String EXPAND_HELP = "EXPAND_HELP";
	public static final String NEXT = "NEXT";
	public static final String PREVIOUS = "PREVIOUS";
	public static final String CLOSE = "CLOSE";
	private String type;
	private GuiManager guim;
	private boolean expandHelp;

	private Dock dockParent;
	private Container widgetSpace;

	public DockNavButton(String type) {
		super();

		this.guim = MainManager.getGuiManager();

		this.type = type;

		// etendre l'aide ou non
		this.expandHelp = false;

		// choix de l'icone
		ImageIcon ic = null;
		if (EXPAND_HELP.equals(type))
			ic = GuiIcons.DI_NAVB_EXPAND_INFOS;
		else if (NEXT.equals(type))
			ic = GuiIcons.DI_NAVB_NEXT;
		else if (PREVIOUS.equals(type))
			ic = GuiIcons.DI_NAVB_PREVIOUS;
		else if (CLOSE.equals(type))
			ic = GuiIcons.DI_NAVB_CLOSE;
		this.setIcon(ic);

		// ecouter les clics
		addMouseListener(new ClickListener());

		// curseur main
		setCursor(guim.getClickableCursor());

	}

	public void checkValidity() {

		if (dockParent == null) {
			dockParent = (Dock) GuiUtils.searchParentOf(this, Dock.class);
			if (dockParent == null)
				return;
		}

		int i = dockParent.getWidgetSpacePanel().getIndex();
		int h = dockParent.getWidgetSpacePanel().getHistorySize();

		if (PREVIOUS.equals(type)) {
			this.setEnabled(i > 0);
		}

		else if (NEXT.equals(type)) {
			this.setEnabled(i < h - 1);
		}

	}

	private class ClickListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {

			// recuperer les parents
			if (dockParent == null) {
				dockParent = (Dock) GuiUtils.searchParentOf(DockNavButton.this, Dock.class);
				if (dockParent == null)
					return;
			}

			if (widgetSpace == null) {
				widgetSpace = (Container) GuiUtils.searchParentOf(DockNavButton.this,
						DockWidgetSpacePanel.class);
				if (widgetSpace == null)
					return;
			}

			// effectuer l'action
			if (NEXT.equals(type)) {
				dockParent.displayNextWidgetspaceAvailable();
			}

			else if (PREVIOUS.equals(type)) {
				dockParent.displayPreviousWidgetspaceAvailable();
			}

			else if (CLOSE.equals(type)) {
				dockParent.hideWidgetspace();
			}

			else if (EXPAND_HELP.equals(type)) {
				expandHelp = !expandHelp;
				Dock.expandHelpRecursively(widgetSpace, expandHelp);
			}

			checkValidity();

		}

	}

}
