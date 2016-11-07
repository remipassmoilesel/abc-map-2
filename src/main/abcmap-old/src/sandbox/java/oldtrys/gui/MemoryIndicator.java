package oldtrys.gui;

import javax.swing.SwingUtilities;

import abcmap.gui.comps.importation.ImageMemoryChargePanel;
import abcmap.utils.gui.GuiUtils;

public class MemoryIndicator implements Runnable {

	public static void launch() {
		SwingUtilities.invokeLater(new MemoryIndicator());
	}

	@Override
	public void run() {

		ImageMemoryChargePanel imcp = new ImageMemoryChargePanel();
		imcp.setMemoryIndicatorFor(10000, 1000000000);
		imcp.reconstruct();
		
		GuiUtils.showThis(imcp);

	}

}
