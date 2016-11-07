package abcmap.utils.threads;

import abcmap.utils.Utils;

class ThreadAccessTrace {

	public static final int NO_TIMEOUT = -1;

	private String readableId;
	private long millitime;
	private String readableTime;
	private int timeOut;

	private long id;

	public ThreadAccessTrace(Thread t) {
		this(t, NO_TIMEOUT);
	}

	public ThreadAccessTrace(Thread t, int timeOut) {
		this.readableId = Utils.getThreadSimpleID(t);
		this.id = t.getId();
		this.millitime = System.currentTimeMillis();
		this.readableTime = Utils.getDate();
		this.timeOut = NO_TIMEOUT;
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { readableId, timeOut, readableTime,
				millitime, id };
		Object[] keys = new Object[] { "readableId", timeOut, "readableTime",
				"millitime", "id" };

		return Utils.toString(this, keys, values);
	}

	@Override
	public boolean equals(Object obj) {

		Object[] fields1 = new Object[] { this.readableId, this.millitime,
				this.timeOut, this.id };

		Object[] fields2 = null;
		if (obj instanceof ThreadAccessTrace) {
			ThreadAccessTrace obj2 = (ThreadAccessTrace) obj;
			fields2 = new Object[] { obj2.readableId, obj2.millitime,
					obj2.timeOut, obj2.id };
		}

		return Utils.equalsUtil(this, obj, fields1, fields2);

	}

	public String getReadableTime() {
		return readableTime;
	}

	public String getReadableId() {
		return readableId;
	}

	public long getId() {
		return id;
	}

	public long getMillitime() {
		return millitime;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isValidNow() {

		// pas de contrôle de timeout
		if (timeOut == NO_TIMEOUT) {
			return true;
		}

		return millitime + timeOut > System.currentTimeMillis();
	}
}
