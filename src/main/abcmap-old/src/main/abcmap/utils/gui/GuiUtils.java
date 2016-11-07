package abcmap.utils.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.managers.Log;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;
import abcmap.utils.sandbox.DrawingTestFrame;
import abcmap.utils.sandbox.DrawingTestFrame.DrawingProcess;

public class GuiUtils {

	/**
	 * Configure le rendu d'IHM pour utiliser l'aspect système, ou l'aspect
	 * "Métal" si erreur. Configure la police par défaut.
	 * 
	 * @param font
	 */
	public static void configureUIManager(String className) {

		// look and feel du systeme ou metal
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			Log.error(e);
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e1) {
				Log.error(e);
			}
		}

	}

	/**
	 * Analyse toutes les propriétés de l'UIManager et remplace la valeur de
	 * celles qui conernent la police d'affichage.
	 * 
	 * @param font
	 */
	public static void setDefaultUIFont(Font font) {

		// Valeur à insérer
		FontUIResource fontUi = new FontUIResource(font);

		// itérer les propriétés
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {

			// couple clef / valeur
			Object key = keys.nextElement();
			Object value = UIManager.get(key);

			// les valeurs sont compatibles, remplacement
			if (value != null
					&& value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, fontUi);
			}
		}

	}

	/**
	 * Affiche des composants dans une fenetre <br />
	 * Pour debuggage et création de GUI.
	 * 
	 * @param comp
	 */
	public static void showThese(Component[] components) {
		showThese(Arrays.asList(components));
	}

	/**
	 * Affiche des composants dans une fenetre <br />
	 * Pour debuggage et création de GUI.
	 * 
	 * @param comp
	 */
	public static void showThese(List<Component> components) {

		JPanel panel = new JPanel(new MigLayout());
		for (Component comp : components) {
			panel.add(comp, "wrap");
		}

		showThis(panel, -1, -1);
	}

	/**
	 * Affiche un composant dans une fenetre <br />
	 * Pour debuggage et création de GUI.
	 * 
	 * @param comp
	 */
	public static void showThis(final Component comp) {
		showThis(comp, -1, -1);
	}

	/**
	 * Affiche un composant dans une fenetre <br />
	 * Pour debuggage et création de GUI.
	 * 
	 * @param comp
	 */
	public static void showThis(final Component comp, final int width,
			final int height) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				// créer une fenêtre
				JFrame jf = new JFrame();

				// arret en quittant
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// contenu de la fenêtre
				JPanel cp = new JPanel(new FlowLayout());
				cp.add(comp);

				jf.setContentPane(cp);

				// dimensions de la fenêtre
				if (width <= 0 || height <= 0) {
					jf.pack();
				} else {
					jf.setSize(new Dimension(width, height));
				}

				// positonnement
				jf.setLocationRelativeTo(null);

				jf.setVisible(true);
			}
		});

	}

	/**
	 * Afficher une séquence de dessin
	 * 
	 * @param dp
	 */
	public static void showDrawingTestFrame(DrawingProcess dp) {
		DrawingTestFrame.show(dp);
	}

	/**
	 * Dessiner les axes x et y d'un objet graphics
	 * 
	 * @param g
	 */
	public static void drawGraphicsAxesLines(Graphics2D g) {

		// caracteristiques de l'axe x
		Color xColor = Color.blue;
		int xMax = 500;

		// caracteristiques de l'axe y
		Color yColor = Color.red;
		int yMax = 500;

		drawGraphicsAxesLines(g, xMax, xColor, yMax, yColor);
	}

	/**
	 * Dessiner les axes x et y d'un objet graphics
	 * 
	 * @param g
	 */
	public static void drawGraphicsAxesLines(Graphics2D g, int xMax,
			Color xColor, int yMax, Color yColor) {

		GuiUtils.throwIfNotOnEDT();

		int xStep = 10;
		int yStep = 10;
		int thick = 5;

		g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

		// dessiner l'axe x
		g.setColor(xColor);
		g.setPaint(xColor);
		g.drawLine(0, 0, xMax, 0);

		// dessiner les thicks x
		for (int i = xStep; i < xMax; i += xStep) {
			g.fillRect(i, 0, thick / 2, thick);
		}

		// dessiner x
		g.drawString("x", xMax + 20, 30);

		// dessiner l'axe y
		g.setColor(yColor);
		g.setPaint(yColor);
		g.drawLine(0, 0, 0, yMax);

		// dessiner les thicks x
		for (int i = yStep; i < yMax; i += yStep) {
			g.fillRect(0, i, thick, thick / 2);
		}

		// dessienr y
		g.drawString("y", 10, yMax + 20);
	}

	/**
	 * Augmenter la qualité de rendu d'un objet graphics
	 * 
	 * @param g2d
	 */
	public static void applyQualityRenderingHints(Graphics2D g2d) {

		HashMap<Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
		hints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(hints);
		
	}

	/**
	 * Dessiner une chaine de caractères avec lignes directrices de police
	 * 
	 * @param g
	 * @param font
	 * @param str
	 * @param x
	 * @param y
	 */
	public static void drawStringAndLines(Graphics2D g, Font font, String str,
			int x, int y) {

		// objet de mesures de police
		FontMetrics fm = g.getFontMetrics(font);

		// couleurs des lignes
		Color heightColor = Color.green;
		Color ascentColor = Color.cyan;
		Color maxAscentColor = Color.blue;
		Color descentColor = Color.pink;
		Color maxDescentColor = Color.pink;
		Color leadingColor = Color.black;
		Color widthColor = Color.darkGray;

		// largeur de la chaine a dessiner
		int width = fm.stringWidth(str);

		// dessin de la chaine
		g.setFont(font);
		g.drawString(str, x, y);

		// largeur des traits
		g.setStroke(new BasicStroke(1));

		// dessiner le point de référence
		int pointWidth = 6;
		g.setColor(Color.black);
		g.setPaint(Color.black);
		g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth,
				pointWidth);

		// dessiner la hauteur
		g.setColor(heightColor);
		g.drawLine(x, y - fm.getHeight(), x + width, y - fm.getHeight());

		// dessiner la ligne supérieure
		g.setColor(ascentColor);
		g.drawLine(x, y - fm.getAscent(), x + width, y - fm.getAscent());

		// dessiner la ligne supérieure max
		g.setColor(maxAscentColor);
		g.drawLine(x, y - fm.getMaxAscent(), x + width, y - fm.getMaxAscent());

		// dessiner la ligne inférieure
		g.setColor(descentColor);
		g.drawLine(x, y - fm.getDescent(), x + width, y - fm.getDescent());

		// dessiner la ligne inférieure max
		g.setColor(maxDescentColor);
		g.drawLine(x, y - fm.getMaxDescent(), x + width, y - fm.getMaxDescent());

		// dessiner la ligne leading
		g.setColor(leadingColor);
		g.drawLine(x, y - fm.getLeading(), x + width, y - fm.getLeading());

		// dessiner la largeur
		g.setColor(widthColor);
		g.drawLine(x, y - fm.getHeight(), x, y);
		g.drawLine(x + width, y - fm.getHeight(), x + width, y);

	}

	/**
	 * Remplir une zone avec une couleur transparente, pour debuggage
	 * 
	 * @param g
	 * @param area
	 */
	public static void fillArea(Graphics2D g, Area area) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(createTransparencyComposite(0.5f));
		g2.setPaint(Color.blue);
		g.fill(area);
	}

	/**
	 * Créer un objet de transparence à ajouter à un objet graphics
	 * 
	 * @param value
	 * @return
	 */
	public static AlphaComposite createTransparencyComposite(float value) {
		int type = AlphaComposite.SRC_OVER;
		return AlphaComposite.getInstance(type, value);
	}

	/**
	 * Retourne le parent de la classe recherchée ou null si aucun parent ou pas
	 * de parent de la classe recherchée.
	 * 
	 * @param comp
	 * @param searchedClass
	 * @return
	 */
	public static Component searchParentOf(Component comp, Class searchedClass) {

		throwIfNotOnEDT();

		// le composant est null: arret
		if (comp == null) {
			return null;
		}

		// recuperer le parent
		Container parent = comp.getParent();

		// le parent est null: arret
		if (parent == null) {
			return null;
		}

		// le parent correspond à la classe recherchée: arret
		else if (searchedClass.isInstance(parent)) {
			return parent;
		}

		// le parent ne correspond pas à la classe recherchée: recherche
		else {
			return searchParentOf(parent, searchedClass);
		}

	}

	public static List<Component> listAllComponentsFrom(Component comp) {
		return listAllComponentsFrom(comp, null);
	}

	/**
	 * Lister toutes composants d'un conteneur plus le conteneur passé en
	 * paramètre et les ajouter à la liste passée en paramètre. Si l'argument
	 * liste est null, une nouvelle liste sera créé.
	 * 
	 * @param comp
	 * @param list
	 * @return
	 */
	public static List<Component> listAllComponentsFrom(Component comp,
			List<Component> list) {

		throwIfNotOnEDT();

		// liste nulle: création
		if (list == null) {
			list = new ArrayList<Component>();
		}

		// cas de base: le composant n'est pas un conteneur, ajout
		if (comp instanceof Container == false) {
			list.add(comp);
		}

		// sinon ajout puis exploration du contenu
		else {

			list.add(comp);

			Container cont = (Container) comp;
			for (Component c : cont.getComponents()) {
				listAllComponentsFrom(c, list);
			}
		}

		return list;

	}

	/**
	 * Applique la méthode revalidate et repaint sur tous les enfants du
	 * composant passé en paramètre.
	 * 
	 * @param comp
	 */
	public static void refreshAllComponentsFrom(Component comp) {

		List<Component> list = listAllComponentsFrom(comp, null);

		Collections.reverse(list);

		for (Component c : list) {
			c.revalidate();
			c.repaint();
		}
	}

	/**
	 * Utilitaire d'ajout en coordonnées absolues.
	 * 
	 * @param panel
	 * @param comp
	 * @param x
	 * @param y
	 */
	public static void addElementToJpanelWithoutLayout(JPanel panel,
			Component comp, int x, int y) {

		panel.setLayout(null);

		Rectangle b = comp.getBounds();
		b.x = x;
		b.y = y;

		panel.add(comp);
		comp.setBounds(b);
	}

	public static void throwIfNotOnEDT() {
		if (SwingUtilities.isEventDispatchThread() == false) {
			throw new IllegalStateException("Running out of EDT");
		}
	}

	public static void throwIfOnEDT() {
		if (SwingUtilities.isEventDispatchThread() == true) {
			throw new IllegalStateException("Running in EDT");
		}
	}

	/**
	 * Changer la valeur d'un composant de texte si nécéssaire et le repeindre
	 * dans tous les cas.
	 * 
	 * @param comp
	 * @param value
	 */
	public static void changeText(JTextComponent comp, String value) {

		throwIfNotOnEDT();

		// modifier la valeur si necessaire
		if (Utils.safeEquals(comp.getText(), value) == false) {
			comp.setText(value);
		}

		// repeindre
		comp.revalidate();
		comp.repaint();

	}

	/**
	 * Change la valeur d'un composant texte sans envoyer d'évenement. A
	 * utiliser pour modifier la valeur d'un composant texte qui implémente
	 * CaretListener ou DocumentListener.
	 * <p>
	 * <b>Attention, ne concerne pas tous les type de listeners</b>
	 * 
	 * @param comp
	 * @param value
	 */
	public static void changeTextWithoutFire(JTextComponent comp, String value) {

		throwIfNotOnEDT();

		// retirer les listeners
		ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

		// changer la valeur
		comp.setText(value);
		comp.revalidate();
		comp.repaint();

		// remettre les listeners
		addSupportedListenersTo(comp, listeners);

	}

	/**
	 * Change la valeur d'un composant de formulaire sans envoyer d'evenement.
	 * <p>
	 * <b>Attention, ne concerne pas tous les type de listeners</b>
	 * 
	 * @param text
	 * @param value
	 */
	public static void changeWithoutFire(JComboBox comp, Object value) {

		throwIfNotOnEDT();

		// retirer les listeners
		ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

		// changer la valeur
		comp.setSelectedItem(value);
		comp.revalidate();
		comp.repaint();

		// remettre les listeners
		addSupportedListenersTo(comp, listeners);

	}

	/**
	 * Change la valeur d'un composant de formulaire sans envoyer d'evenement.
	 * <p>
	 * <b>Attention, ne concerne pas tous les type de listeners</b>
	 * 
	 * @param text
	 * @param value
	 */
	public static void changeIndexWithoutFire(JComboBox comp, int index) {

		throwIfNotOnEDT();

		// retirer les listeners
		ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

		// changer la valeur
		comp.setSelectedIndex(index);
		comp.revalidate();
		comp.repaint();

		// remettre les listeners
		addSupportedListenersTo(comp, listeners);

	}

	/**
	 * Change la valeur d'un bouton uniquement si nécéssaire et le repeint dans
	 * tous les cas. Utilise la méthode setSelected(), qui n'envoie pas
	 * d'ActionEvent.
	 * 
	 * @param text
	 * @param value
	 */
	public static void setSelected(AbstractButton comp, Boolean value) {

		throwIfNotOnEDT();

		// changer la valeur si nécéssaire
		if (comp.isSelected() != value) {
			comp.setSelected(value);
		}

		// repeindre dans tous les cas
		comp.revalidate();
		comp.repaint();

	}

	/**
	 * Change la valeur d'un composant de formulaire sans envoyer d'evenement.
	 * <p>
	 * <b>Attention, ne concerne pas tous les type de listeners</b>
	 * 
	 * @param text
	 * @param value
	 */
	public static void changeWithoutFire(JSlider comp, int value) {

		// retirer les listeners
		ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

		// changer la valeur
		comp.setValue(value);
		comp.revalidate();
		comp.repaint();

		// remettre les listeners
		addSupportedListenersTo(comp, listeners);

	}

	/**
	 * Change la valeur d'un composant de formulaire sans envoyer d'evenement.
	 * <p>
	 * <b>Attention, ne concerne pas tous les type de listeners</b>
	 * 
	 * @param text
	 * @param value
	 */
	public static void changeWithoutFire(JList comp, Object value,
			boolean shouldAutoScroll) {

		// retirer les listeners
		ArrayList<EventListener> listeners = removeSupportedListenersFrom(comp);

		// changer la valeur
		comp.setSelectedValue(value, shouldAutoScroll);
		comp.revalidate();
		comp.repaint();

		// remettre les listeners
		addSupportedListenersTo(comp, listeners);

	}

	/**
	 * Ajoute une liste de listeners à un composant. Tous les composant ne sont
	 * pas pris en charge. Léve une exception si un listener n'est pas pris en
	 * charge.
	 * <p>
	 * Envoi un IllegalStateException si un listener de la liste au moins n'et
	 * pas réaffecté.
	 * 
	 * @param comp
	 * @param listeners
	 */
	protected static void addSupportedListenersTo(Component comp,
			ArrayList<EventListener> lst) {

		/*
		 * Chaque type de composant est traité séparement étant donné que le
		 * retrait des liseners ne peut pas se faire uniformément selon le type
		 * de composants. Chaque listener ajouté doit ête retiré de la liste et
		 * une exception est levé si un listener n'est pas réaffecté.
		 */

		/*
		 * Composants texte
		 */
		if (comp instanceof JTextComponent) {

			JTextComponent txt = ((JTextComponent) comp);

			// récupérer le document si possible
			AbstractDocument doc = null;
			if (txt.getDocument() instanceof AbstractDocument) {
				doc = (AbstractDocument) ((JTextComponent) comp).getDocument();
			}

			for (EventListener l : lst) {

				if (l instanceof CaretListener) {
					txt.addCaretListener((CaretListener) l);
				}

				else if (l instanceof DocumentListener && doc != null) {
					doc.addDocumentListener((DocumentListener) l);
				}

				else {
					throw new IllegalStateException("Unknown listener: "
							+ l.getClass() + " / " + comp.getClass());
				}
			}

		}

		/*
		 * Listes
		 */
		else if (comp instanceof JList) {

			JList list = ((JList) comp);

			for (EventListener l : lst) {

				if (l instanceof ListSelectionListener) {
					list.addListSelectionListener((ListSelectionListener) l);
				}

				else {
					throw new IllegalStateException("Unknown listener: "
							+ l.getClass() + " / " + comp.getClass());
				}
			}

		}

		/*
		 * Listes
		 */
		else if (comp instanceof JComboBox) {

			JComboBox list = ((JComboBox) comp);

			for (EventListener l : lst) {

				if (l instanceof ActionListener) {
					list.addActionListener((ActionListener) l);
				}

				else {
					throw new IllegalStateException("Unknown listener: "
							+ l.getClass() + " / " + comp.getClass());
				}
			}

		}

		/*
		 * Listes
		 */
		else if (comp instanceof JSlider) {

			JSlider list = ((JSlider) comp);

			for (EventListener l : lst) {

				if (l instanceof ChangeListener) {
					list.addChangeListener((ChangeListener) l);
				}

				else {
					throw new IllegalStateException("Unknown listener: "
							+ l.getClass() + " / " + comp.getClass());
				}
			}

		}

		else {
			throw new IllegalStateException("Unsupported component: "
					+ comp.getClass());
		}

	}

	/**
	 * Enlève tous les objets à l'écoute d'un composant et les retourne.
	 * <p>
	 * <b>Tous les listeners ne sont pas pris en charge.</b>
	 * 
	 * @param comp
	 * @return
	 */
	public static ArrayList<EventListener> removeSupportedListenersFrom(
			JComponent comp) {

		/*
		 * Pas d'utilisation de la méthode getListeners(<? class extends
		 * EventListener?>) étant donné que les retours sont inattendus: les
		 * listeners non ajoutés à l'aide d'un getFooListener ne sont pas
		 * retournés.
		 */

		// liste résultat
		ArrayList<EventListener> lst = new ArrayList<EventListener>();

		/*
		 * Composants texte
		 */
		if (comp instanceof JTextComponent) {

			// lister les carets listeners
			List<CaretListener> clisteners = Arrays
					.asList(((JTextComponent) comp).getCaretListeners());

			// conserver une référence
			lst.addAll(clisteners);

			// les retirer
			for (CaretListener l : clisteners) {
				((JTextComponent) comp).removeCaretListener(l);
			}

			if (((JTextComponent) comp).getDocument() instanceof AbstractDocument) {

				AbstractDocument doc = (AbstractDocument) ((JTextComponent) comp)
						.getDocument();

				// lister les documents listeners
				List<DocumentListener> dlisteners = Arrays.asList(doc
						.getDocumentListeners());

				// conserver une référence
				lst.addAll(dlisteners);

				// les retirer
				for (DocumentListener l : dlisteners) {
					doc.removeDocumentListener(l);
				}

			}
		}

		/*
		 * Listes
		 */
		else if (comp instanceof JList) {

			// lister les listeners
			List<ListSelectionListener> llisteners = Arrays
					.asList(((JList) comp).getListSelectionListeners());

			// garder une référence
			lst.addAll(llisteners);

			// retirer les listeners
			for (ListSelectionListener l : llisteners) {
				((JList) comp).removeListSelectionListener(l);
			}

		}

		/*
		 * Listes
		 */
		else if (comp instanceof JComboBox) {

			// lister les listeners
			List<ActionListener> alisteners = Arrays.asList(((JComboBox) comp)
					.getActionListeners());

			// garder une référence
			lst.addAll(alisteners);

			// retirer les listeners
			for (ActionListener l : alisteners) {
				((JComboBox) comp).removeActionListener(l);
			}

		}

		/*
		 * Sliders
		 */
		else if (comp instanceof JSlider) {

			// lister les listeners
			List<ChangeListener> alisteners = Arrays.asList(((JSlider) comp)
					.getChangeListeners());

			// garder une référence
			lst.addAll(alisteners);

			// retirer les listeners
			for (ChangeListener l : alisteners) {
				((JSlider) comp).removeChangeListener(l);
			}

		}

		else {
			throw new IllegalStateException("Unsupported component: "
					+ comp.getClass());
		}

		return lst;

	}

	/**
	 * Liste et retourne toutes les valeurs d'un combo box
	 * 
	 * @param combo
	 * @return
	 */

	public static <T> List<T> getAllValuesFrom(JComboBox<T> combo) {

		int size = combo.getModel().getSize();

		ArrayList<T> result = new ArrayList<T>();

		for (int i = 0; i < size; i++) {
			result.add(combo.getModel().getElementAt(i));
		}

		return result;

	}

	/**
	 * Ecrire dans un fichiers des informations concernant le l&f passé en
	 * paramètre
	 * 
	 * @param className
	 */
	public static void printUiManagerInfos(String className) {

		try {
			UIManager.setLookAndFeel(className);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		UIDefaults defaults = UIManager.getDefaults();
		Enumeration newKeys = defaults.keys();

		PrintWriter fop = null;
		File file = new File("uimanager_infos.txt");
		try {
			fop = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (newKeys.hasMoreElements()) {
			Object obj = newKeys.nextElement();
			fop.printf("%50s : %s\n", obj, UIManager.get(obj));
		}

		System.out.println("UIManager informations write in :"
				+ file.getAbsolutePath());
	}

	/**
	 * Créé un JLabel et l'ajoute au panneau avec les contraintes passées en
	 * paramètre
	 * 
	 * @param string
	 * @param coordinatesPanel
	 * @param string2
	 * @return
	 */
	public static JLabel addLabel(String text, Container container) {
		return addLabel(text, container, null, null);
	}

	/**
	 * Créé un JLabel et l'ajoute au panneau avec les contraintes passées en
	 * paramètre
	 * 
	 * @param string
	 * @param coordinatesPanel
	 * @param string2
	 * @return
	 */
	public static JLabel addLabel(String text, Container container,
			Object constraints) {
		return addLabel(text, container, constraints, null);
	}

	/**
	 * Créé un JLabel et l'ajoute au panneau avec les contraintes passées en
	 * paramètre. Permet d'harmoniser la création de label et de simplifier le
	 * code.
	 * 
	 * @param text
	 * @param container
	 * @param constraints
	 * @param style
	 * @return
	 */
	public static JLabel addLabel(String text, Container container,
			Object constraints, GuiStyle style) {

		// créer un jlabel
		HtmlLabel lbl = new HtmlLabel(text);

		// appliquer le style si necessaire
		if (style != null) {
			lbl.setStyle(style);
		}

		// ajouter au conteneur
		if (constraints != null) {
			container.add(lbl, constraints);
		}

		else {
			container.add(lbl);
		}

		return lbl;
	}

}
