package org.abcmap.gui.components.textfields;

import org.abcmap.gui.components.InvalidInputException;

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
