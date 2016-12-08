package org.abcmap.gui.components.progressbar;

import org.abcmap.core.utils.Utils;

public class ProgressbarTask {

	private String label;
	private int currentValue;
	private int maxValue;
	private int minValue;
	private long lastTimeUpdated;
	private boolean indeterminate;

	public ProgressbarTask() {
		this.label = new String();
		this.indeterminate = false;
		this.minValue = 0;
		this.maxValue = 100;
		this.currentValue = 0;

		this.lastTimeUpdated = 0l;
	}

	public ProgressbarTask(String label, boolean indeterminate, int min, int max, int current) {
		this();

		this.label = label;
		this.indeterminate = indeterminate;
		this.minValue = min;
		this.maxValue = max;
		this.currentValue = current;
	}

	@Override
	public boolean equals(Object obj) {

		Object[] fields1 = new Object[] { this.label, this.indeterminate, this.minValue,
				this.maxValue, this.currentValue, this.lastTimeUpdated };

		Object[] fields2 = null;
		if (obj instanceof ProgressbarTask) {
			ProgressbarTask pbt = (ProgressbarTask) obj;
			fields2 = new Object[] { pbt.label, pbt.indeterminate, pbt.minValue, pbt.maxValue,
					pbt.currentValue, pbt.lastTimeUpdated };
		}

		return Utils.equalsUtil(this, obj, fields1, fields2);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int current) {
		this.currentValue = current;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int max) {
		this.maxValue = max;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int min) {
		this.minValue = min;
	}

	public long getLastTimeUpdated() {
		return lastTimeUpdated;
	}

	public long getEllapsedTimeSinceUpdated() {
		return System.currentTimeMillis() - lastTimeUpdated;
	}

	public void touch() {
		this.lastTimeUpdated = System.currentTimeMillis();
	}

	public void setIndeterminate(boolean b) {
		indeterminate = b;
	}

	public boolean isIndeterminate() {
		return indeterminate;
	}

	public void finish() {
		this.setCurrentValue(this.getMaxValue());
	}

	@Override
	public String toString() {

		Object[] keys = new Object[] { "min", "current", "max", "indeterminate", "last update", };
		Object[] values = new Object[] { minValue, currentValue, maxValue, indeterminate,
				lastTimeUpdated, };

		return Utils.toString(this, keys, values);

	}
}
