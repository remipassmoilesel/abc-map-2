package abcmap.gui.comps.textfields;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import abcmap.exceptions.InvalidInputException;
import abcmap.utils.gui.GuiUtils;

public class DecimalTextField extends JTextField implements NumberTextField {

	private String previousValue;

	private static String NUMBER_PATTERN = "^-?(\\d+(\\.\\d*)?)?$";
	private static String DIGIT_PATTERN = "^-?[\\d|\\.]*$";

	public DecimalTextField() {
		super();

		GuiUtils.throwIfNotOnEDT();

		this.previousValue = new String();
	}

	public DecimalTextField(int i) {
		super(i);
	}

	protected Document createDefaultModel() {
		return new DoubleFormatDocument();
	}

	private class DoubleFormatDocument extends PlainDocument {

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

			if (str == null)
				return;

			// verifier la valeur d'insertion
			if (str.matches(DIGIT_PATTERN)) {
				super.insertString(offs, new String(str), a);
			}

			// verifier le nombre complet
			String txt = DecimalTextField.this.getText();
			if (txt.matches(NUMBER_PATTERN) == true) {
				previousValue = txt;
			} else {
				DecimalTextField.this.setText(previousValue);
			}

		}
	}

	@Override
	public Double getDoubleValue() throws InvalidInputException {
		try {
			return Double.parseDouble(this.getText());
		} catch (Exception e) {
			throw new InvalidInputException(e);
		}
	}

	@Override
	public Float getFloatValue() throws InvalidInputException {
		try {
			return Float.parseFloat(this.getText());
		} catch (Exception e) {
			throw new InvalidInputException(e);
		}
	}

	@Override
	public Integer getIntegerValue() throws InvalidInputException {
		try {
			return Math.round(Float.parseFloat(this.getText()));
		} catch (Exception e) {
			throw new InvalidInputException(e);
		}
	}

}
