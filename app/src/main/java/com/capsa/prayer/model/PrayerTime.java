package com.capsa.prayer.model;

public class PrayerTime {
	private String prayerName = "";
	private String prayerTime = "";
	private boolean alarmSet = false;

	public String getPrayerName() {
		return prayerName;
	}

	public void setPrayerName(String prayerName) {
		this.prayerName = prayerName;
	}

	public String getPrayerTime() {
		return prayerTime;
	}

	public void setPrayerTime(String prayerTime) {
		this.prayerTime = prayerTime;
	}

	public boolean isAlarmSet() {
		return alarmSet;
	}

	public void setAlarm(boolean alarmSet) {
		this.alarmSet = alarmSet;
	}

}
