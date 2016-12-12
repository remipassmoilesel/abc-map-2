package org.abcmap.gui.dialogs.loading;

import org.abcmap.gui.HtmlLabel;

import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {

	private static LoadingDialogManager ldm = new LoadingDialogManager();

	LoadingDialog() {

		setUndecorated(true);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(325, 140));
		setLocationRelativeTo(null);

		((JComponent) this.getContentPane()).setBorder(BorderFactory
				.createLineBorder(Color.DARK_GRAY, 3));

		HtmlLabel lblWait = new HtmlLabel("Veuillez patienter...");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		JPanel mainPanel = new JPanel();
		mainPanel.add(lblWait, "wrap");
		mainPanel.add(progressBar, "wrap");

		setContentPane(mainPanel);

	}

	public static LoadingDialogManager getManager() {
		return ldm;
	}

	@Deprecated
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

}
