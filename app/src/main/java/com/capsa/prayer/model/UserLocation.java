package com.capsa.prayer.model;

import android.location.Location;

public class UserLocation {
	private String countryName = "";
	private String cityName = "";
	private double latitude;
	private double longitude;
	private double altitude;
	private double qiblaAngle;
	private double timeZone;
	
	private Location gpsLocation;

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getQiblaAngle() {
		return qiblaAngle;
	}

	public void setQiblaAngle(double qiblaAngle) {
		this.qiblaAngle = Math.ceil(qiblaAngle);
	}

	public double getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(double timeZone) {
		this.timeZone = timeZone;
	}

	public Location getGPSLocation() {
		return gpsLocation;
	}

	public void setGPSLocation() {
		gpsLocation = new Location("GPS");
		gpsLocation.setLatitude(this.latitude);
		gpsLocation.setLongitude(this.longitude);
	}

	
}
