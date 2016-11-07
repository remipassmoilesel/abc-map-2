package abcmap.gui.ie.ressources;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public abstract class AbstractGoToWebsite extends InteractionElement {

	public enum Mode {
		GO_TO_HELP_PAGE, GO_TO_SUPPORT_PAGE, 
		
		GO_TO_FAQ_PAGE, GO_TO_BUG_REPORT_PAGE, 
		
		GO_TO_ASK_FORM_PAGE, GO_TO_WEB_SITE, GO_TO_OPENSTREETMAP,
	};

	/** Le mode de la commande */
	private Mode mode;

	/** L'URL a atteindre */
	private String url;

	public AbstractGoToWebsite(Mode mode) {

		this.mode = mode;

		if (Mode.GO_TO_HELP_PAGE.equals(mode)) {
			this.label = "Aide et tutoriels en ligne";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel et trouver "
					+ "de l'aide en ligne.";

			this.url = ConfigurationConstants.HELP_PAGE_URL;
		}

		else if (Mode.GO_TO_SUPPORT_PAGE.equals(mode)) {
			this.label = "Soutenir le projet";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel "
					+ "pour soutenir le projet.";

			this.url = ConfigurationConstants.WEBSITE_FAQ_URL;
		}

		else if (Mode.GO_TO_FAQ_PAGE.equals(mode)) {
			this.label = "Questions fréquentes";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel et trouver "
					+ "de l'aide en ligne.";

			this.url = ConfigurationConstants.WEBSITE_FAQ_URL;
		}

		else if (Mode.GO_TO_BUG_REPORT_PAGE.equals(mode)) {
			this.label = "Signaler un problème";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel pour "
					+ "signaler un problème.";

			this.url = ConfigurationConstants.WEBSITE_FAQ_URL;
		}

		else if (Mode.GO_TO_ASK_FORM_PAGE.equals(mode)) {
			this.label = "Demander une fonctionnalité";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel pour "
					+ "demander une fonctionnalité supplémentaire.";

			this.url = ConfigurationConstants.WEBSITE_FAQ_URL;
		}

		else if (Mode.GO_TO_WEB_SITE.equals(mode)) {
			this.label = "Se rendre sur le site d'Abc-Map";
			this.help = "Cliquez ici pour vous rendre sur le site internet du logiciel.";

			this.url = ConfigurationConstants.WEBSITE_URL;
		}

		else if (Mode.GO_TO_OPENSTREETMAP.equals(mode)) {
			this.label = "Se rendre sur le site OpenStreetMap";
			this.help = "Cliquez ici pour vous rendre sur le site http://OpenStreetMap.org";

			this.url = "http://openstreetmap.org";
		}

		this.displaySimplyInSearch = true;

	}

	@Override
	public void run() {

		// pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		boolean exceptionHapened = false;

		// ouverture du navigateur
		try {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (URISyntaxException e) {
				Log.error(e);
				exceptionHapened = true;
			}
		}

		// erreur lors de l'affichage
		catch (IOException e) {
			Log.error(e);
			exceptionHapened = true;
		}

		if (exceptionHapened) {
			guim.showInformationTextFieldDialog(
					guim.getMainWindow(),
					"Impossible de lancer votre navigateur. Vous pouvez néanmoins "
							+ "trouver la ressource demandée à l'emplacement: ",
					url);
		}

	}

}
