package abcmap.importation.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import abcmap.utils.Utils;

public class DataEntryList extends ArrayList<DataEntry> {

	private ArrayList<String> comments;

	public DataEntryList() {
		this.comments = new ArrayList<String>();
	}

	public ArrayList<String> getComments() {
		return comments;
	}

	public void addComment(String comment) {
		comments.add(comment);
	}

	/**
	 * Assemble les commentaires et les retourne en chaine de texte
	 * 
	 * @return
	 */
	public String getOneStringComments() {
		return Utils.join(" ", comments);
	}

	/**
	 * Retourne la liste de tous les entetes des entrées du conteneur, HORS
	 * champs latitude et longitude
	 * 
	 * @return
	 */
	public Set<String> getAllHeaders() {

		HashSet<String> rslt = new HashSet<>();

		for (DataEntry entry : this) {
			rslt.addAll(entry.getFieldNames());
		}

		return rslt;
	}

}
