package edu.thu.keg.broadcast;

public class EquipmentInfo {
	int resultId = 0;
	String type = null;
	double value = 0;
	long timeseries;

	public EquipmentInfo(int resultid, String type, double value,
			long timeseries) {
		this.setResultId(resultid);
		this.setType(type);
		this.setValue(value);
		this.setTimeseries(timeseries);
	}

	/**
	 * @return the resultId
	 */
	public int getResultId() {
		return resultId;
	}

	/**
	 * @param resultId
	 *            the resultId to set
	 */
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the timeseries
	 */
	public long getTimeseries() {
		return timeseries;
	}

	/**
	 * @param timeseries
	 *            the timeseries to set
	 */
	public void setTimeseries(long timeseries) {
		this.timeseries = timeseries;
	}

}
