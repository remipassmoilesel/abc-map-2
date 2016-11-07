package abcmap.project.properties;

import java.awt.Color;
import java.util.Arrays;

public class DrawPropertiesContainer extends PropertiesContainer {

	public Color fgColor;
	public Color bgColor;
	public String linestyle;
	public int thickness;
	public String texture;

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DrawPropertiesContainer == false)
			return false;

		DrawPropertiesContainer pp = (DrawPropertiesContainer) obj;
		Object[] pps1 = new Object[] { texture, fgColor, bgColor, linestyle, thickness, };
		Object[] pps2 = new Object[] { pp.texture, pp.bgColor, pp.bgColor, pp.linestyle,
				pp.thickness, };

		return Arrays.deepEquals(pps1, pps2);

	}

}
