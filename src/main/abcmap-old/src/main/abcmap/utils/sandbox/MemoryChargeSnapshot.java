package abcmap.utils.sandbox;

import abcmap.utils.Utils;

public class MemoryChargeSnapshot {

	private String date;
	private long nano;
	private long totalMemory;
	private long freeMemory;
	private long maxMemory;

	/**
	 * Enregistre l'état de la memoire et retourne un objet conteneur
	 * d'informations
	 * 
	 * @return
	 */
	public static MemoryChargeSnapshot snap() {
		Runtime runtime = Runtime.getRuntime();
		return new MemoryChargeSnapshot(runtime.totalMemory(), runtime.freeMemory(),
				runtime.maxMemory());
	}

	public MemoryChargeSnapshot(long totalMemory, long freeMemory, long maxMemory) {

		// enregistrer la date de création
		this.nano = System.nanoTime();
		this.date = Utils.getDate("yyyy-MM-dd HH:mm:ss S");

		this.totalMemory = totalMemory;
		this.freeMemory = freeMemory;
		this.maxMemory = maxMemory;

	}

	public String getReadableString() {

		double mb = 1024 * 1024;

		String usedMemoryStr = Utils.round((totalMemory - freeMemory) / mb, 3) + " mb";
		String freeMemoryStr = Utils.round((freeMemory / mb), 3) + " mb";
		String totalMemoryStr = Utils.round((totalMemory / mb), 3) + " mb";
		String maxMemoryStr = Utils.round((maxMemory / mb), 3) + " mb";

		Object[] keys = new Object[] { "Used memory", "Free memory", "Total memory", "Max memory",
				"Date" };

		Object[] values = new Object[] { usedMemoryStr, freeMemoryStr, totalMemoryStr, maxMemoryStr,
				date };

		return Utils.toString(this, keys, values);
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { date, totalMemory, freeMemory, maxMemory, nano };

		Object[] keys = new Object[] { "date", "totalMemory", "freeMemory", "maxMemory", "nano" };

		return Utils.toString(this, keys, values);

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getNano() {
		return nano;
	}

	public void setNano(long nano) {
		this.nano = nano;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

}
