package abcmap.gui.dock;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.dock.comps.DockMenuWidget;
import abcmap.gui.dock.comps.Dock.DockOrientation;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;

/**
 * Constructeur de dock latéral. Prend en paramètre une liste de composants et
 * une orientation et construit un focke.
 * 
 * @author remipassmoilesel
 *
 */
public class DockBuilder {

	private List<Object> widgets;
	private DockOrientation orientation;

	public void setWidgets(List<Object> ieg) {
		this.widgets = ieg;
	}

	public void setOrientation(DockOrientation orientation) {
		this.orientation = orientation;
	}
	
	public Dock make() {

		// creer le dock
		Dock dock = new Dock(orientation);

		// iterer pour creer les boutons
		for (Object o : widgets) {

			// l'element proposé est un menu
			if (o instanceof InteractionElementGroup) {

				InteractionElementGroup ieg = (InteractionElementGroup) o;

				// creer le bouton
				DockMenuWidget btt = new DockMenuWidget();
				btt.setInteractionElementGroup(ieg);
				btt.setWindowMode(ieg.getWindowMode());

				// ajout du bouton au dock
				dock.addWidget(btt);

				// rafraichir le composant
				btt.revalidate();
				btt.repaint();
			}

			// l'element proposé est un autre composant
			else if (o instanceof JComponent) {
				dock.addWidget((JComponent) o);
			}

			else {
				throw new IllegalArgumentException("Unknown type: "
						+ o.getClass().getName());
			}

		}

		return dock;
	}
}
