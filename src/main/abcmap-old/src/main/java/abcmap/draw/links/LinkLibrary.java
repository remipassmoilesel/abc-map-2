package abcmap.draw.links;

import java.util.ArrayList;

import abcmap.events.ProjectEvent;
import abcmap.managers.stub.MainManager;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;

/**
 * Conteneur de liens. Un liens est un objet cliquable sur carte. Ce conteneur
 * centralise tous les liens d'un projet et permet de simplifier les recherches
 * de liens.
 * 
 * @author remipassmoilesel
 *
 */
public class LinkLibrary implements HasNotificationManager {

	private static LinkLibrary linkLibrary;

	public static void init() {
		if (linkLibrary != null)
			throw new IllegalStateException();

		linkLibrary = new LinkLibrary();
	}

	public static LinkRessource getLink(String location, LinkAction action) {

		if (linkLibrary == null)
			throw new IllegalStateException();

		return linkLibrary.searchAndReturn(location, action);
	}

	/** La liste de tous les liens d'un projet */
	private ArrayList<LinkRessource> links;

	/** Rester à l'écoute du gestionnaire de projet */
	private NotificationManager notifm;

	public LinkLibrary() {
		links = new ArrayList<LinkRessource>(20);

		notifm = new NotificationManager(this);
		notifm.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {

				/*
				 * Vider la liste de liens lors des changements de projets
				 */
				if (ProjectEvent.isNewProjectLoadedEvent(arg)) {
					resetLibrary();
				}
			}
		});

		MainManager.getProjectManager().getNotificationManager().addObserver(this);
	}

	public static void resetLibrary() {
		linkLibrary.links.clear();
	}

	private LinkRessource searchAndReturn(String location, LinkAction action) {

		if (location == null || action == null)
			throw new NullPointerException("location: " + location
					+ ", action: " + action);

		// créer une ressource
		LinkRessource link = new LinkRessource(location, action);

		// chercher si la ressource existe
		int index = links.indexOf(link);

		// la ressource n'existe pas, enregistrement
		if (index == -1) {
			links.add(link);
			return link;
		}

		// la ressource existe, retour
		else {
			return links.get(index);
		}

	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

}
