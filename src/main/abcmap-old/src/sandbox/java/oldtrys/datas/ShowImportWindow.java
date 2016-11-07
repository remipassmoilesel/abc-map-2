package oldtrys.datas;

import javax.swing.SwingUtilities;

import abcmap.gui.ie.importation.robot.MenuRobotImport;
import abcmap.gui.windows.importation.ImportWindow;

public class ShowImportWindow {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				ImportWindow impWin = new ImportWindow();

				impWin.setDisplayableComponent(new MenuRobotImport());
				impWin.reconstruct();

				impWin.setVisible(true);

			}
		});

	}

}
