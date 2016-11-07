package trys;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abcmap.utils.gui.GuiUtils;

public class AD_SwingTestArea implements Runnable {

	public static void launch() {
		SwingUtilities.invokeLater(new AD_SwingTestArea());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new AD_SwingTestArea());
	}

	@Override
	public void run() {

	}

}
