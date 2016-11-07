package abcmap.gui.comps.textfields;

import abcmap.exceptions.InvalidInputException;

/**
 * Champ de saisie de nombres formattés.
 * 
 * @author remipassmoilesel
 *
 */
public interface NumberTextField {

	public Double getDoubleValue() throws InvalidInputException;

	public Float getFloatValue() throws InvalidInputException;

	public Integer getIntegerValue() throws InvalidInputException;

}
