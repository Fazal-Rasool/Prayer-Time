package com.capsa.prayer.managers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.revicers.Alarm;
import com.capsa.prayer.utills.AppConstants;

public class AppManager {
	Context mContext;

	int screenWidth;
	int screenHeight;

	public AppManager(Context context) {
		mContext = context;
		
		// DisplayMetrics metrics = new DisplayMetrics();
		// ((Fragment)
		// context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// screenWidth = metrics.widthPixels;
		// screenHeight = metrics.heightPixels;

	}

	// //set alarm ///////
	@SuppressLint("SimpleDateFormat")
	public void setAlarm(String prayerTime, int id) {

		Calendar calNow = Calendar.getInstance(Locale.US);
		Calendar calSet = (Calendar) calNow.clone();

		try {
			Date date = new SimpleDateFormat("hh:mm a", Locale.US)
					.parse(prayerTime);
			prayerTime = new SimpleDateFormat("HH:mm").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		prayerTime = prayerTime.replace("am", "").trim();
		prayerTime = prayerTime.replace("pm", "").trim();

		String[] timeArray = prayerTime.split(":");

		calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
		calSet.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
		calSet.set(Calendar.SECOND, 0);
		calSet.set(Calendar.MILLISECOND, 0);

		if (calSet.compareTo(calNow) <= 0) {
			// Today Set time passed, count to tomorrow
			calSet.add(Calendar.DATE, 1);
		}

		Intent intentAlarm = new Intent(mContext, Alarm.class);
		intentAlarm.putExtra("id", id);
		AlarmManager alarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		// alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
		// PendingIntent.getBroadcast(mContext, id, intentAlarm,
		// PendingIntent.FLAG_UPDATE_CURRENT));

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet
				.getTimeInMillis(), 86400000, PendingIntent.getBroadcast(
				mContext, id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

		Log.d("alarm set for after ", " min with id=" + id);
	}

	// // cancel alarm///
	public void cancelAlarm(int id) {
		Intent intent = new Intent(mContext, Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(mContext, id, intent,
				0);

		AlarmManager alarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);

		Log.d("", "alarm cancel min with id=" + id);

	}

	/************** Reading File *******************/
	private String readTextFile(String pathtoFile, boolean isUTF) {
		String str = "";
		AssetManager assetManager = mContext.getAssets();
		InputStream input;
		byte[] buffer;
		int size;
		try {
			input = assetManager.open(AppConstants.DATA_MAIN_FOLDER
					+ pathtoFile);
			size = input.available();
			buffer = new byte[size];
			input.read(buffer);
			input.close();
			if (isUTF) {
				str = new String(buffer, "UTF-8");
			} else {
				str = new String(buffer, "windows-1252");
			}
		} catch (Exception e) {
			e.toString();
		}
		return str;
	}

	public ArrayList<UserLocation> getCitiesList(String pathTofile) {
		ArrayList<UserLocation> citiesList = new ArrayList<UserLocation>();

		AssetManager assestManager = mContext.getAssets();
		InputStream input;
		BufferedReader bufferReader;

		try {
			pathTofile = AppConstants.DATA_MAIN_FOLDER + pathTofile;
			input = assestManager.open(pathTofile.trim());
			bufferReader = new BufferedReader(new InputStreamReader(input,
					"UTF-8"));

			String line;
			while ((line = bufferReader.readLine()) != null) {
				String[] data = line.split("#");

				if (data.length == 6) {
					UserLocation city = new UserLocation();
					city.setCountryName(data[0]);
					city.setCityName(data[1]);
					city.setLatitude(Double.parseDouble(data[2]));
					city.setLongitude(Double.parseDouble(data[3]));
					city.setAltitude(Double.parseDouble(data[4]));
					city.setTimeZone(Double.parseDouble(data[5]));
					city.setGPSLocation();

					citiesList.add(city);
				}
			}
			bufferReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return citiesList;
	}

	public String[] getCountriesList() {
		String str = readTextFile("CountriesHeadings.txt", true);
		String[] lines = str.split("\n");
		return lines;
	}

	/*************************************** End ***************************************/
	/************************************* Country *************************************/
	public void setSelectedCountry(String country) {
		SharedPreferences.Editor pref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		pref.putString("country", country.trim());
		pref.commit();

		Log.e("", "" + getSelectedCountry());
	}

	public String getSelectedCountry() {
		SharedPreferences pref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString("country", "").trim();
	}

	public void setCountryDialogRun(boolean isRun) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putBoolean("country_dialog_run", isRun);
		mpref.commit();
	}

	public boolean isCountryDialogRun() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getBoolean("country_dialog_run", false);
	}

	/************************************* END *************************************************/
	/************************************* Country *************************************/
	public void setSelectedLocation(UserLocation userLocation) {
		SharedPreferences.Editor pref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();

		pref.putString("country", userLocation.getCountryName());
		pref.putString("city", userLocation.getCityName().trim());
		pref.putString("latitude", "" + userLocation.getLatitude());
		pref.putString("longitude", "" + userLocation.getLongitude());
		pref.putString("altitude", "" + userLocation.getAltitude());
		pref.putString("timeZone", "" + userLocation.getTimeZone());
		pref.putString("angle", "" + userLocation.getQiblaAngle());

		pref.commit();
	}

	public UserLocation getSelectedLocation() {
		SharedPreferences pref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);

		UserLocation userLocation = new UserLocation();
		userLocation.setCountryName(pref.getString("country", ""));
		userLocation.setCityName(pref.getString("city", ""));
		userLocation.setLatitude(Double.parseDouble(pref.getString("latitude",
				"0")));
		userLocation.setLongitude(Double.parseDouble(pref.getString(
				"longitude", "0")));
		userLocation.setAltitude(Double.parseDouble(pref.getString("altitude",
				"0")));
		userLocation.setQiblaAngle(Double.parseDouble(pref.getString("angle",
				"0")));
		userLocation.setTimeZone(Double.parseDouble(pref.getString("timeZone",
				"0")));
		userLocation.setGPSLocation();

		return userLocation;
	}

	public void setCityDialogRun(boolean isRun) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putBoolean("city_dialog_run", isRun);
		mpref.commit();
	}

	public boolean isCityDialogRun() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getBoolean("city_dialog_run", false);
	}

	/************************************* Bottom Menu Animation *************************************************/
	// To animate view slide out from bottom to top
	public void slideToTopBottom(final View view) {
		TranslateAnimation animate = null;
		if (view.getVisibility() == View.VISIBLE) {
			animate = new TranslateAnimation(0, 0, 0, screenHeight
					+ view.getHeight());
			animate.setDuration(500);
			// animate.setFillAfter(true);
			view.startAnimation(animate);
			view.setVisibility(View.GONE);
			// menu_btn.setVisibility(View.VISIBLE);

		} else {
			animate = new TranslateAnimation(0, 0, screenHeight
					+ view.getHeight(), screenHeight - view.getHeight());
			animate.setDuration(500);
			// animate.setFillAfter(true);
			view.startAnimation(animate);
			view.setVisibility(View.VISIBLE);
			// menu_btn.setVisibility(View.GONE);

		}
	}

	public void hidebottomMenu(View view) {
		if (view.getVisibility() == View.VISIBLE) {
			TranslateAnimation animate = new TranslateAnimation(0, 0, 0,
					screenHeight + view.getHeight());
			animate.setDuration(500);
			view.startAnimation(animate);
			view.setVisibility(View.GONE);
		}
	}

	/************************************* END *************************************************/
	/************************************* Juristic Settings *************************************************/
	public void setJuristic(int juristic) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putInt("Juristic", juristic);
		mpref.commit();
	}

	public int getJuristic() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getInt("Juristic", 0);
	}

	/************************************* Juristic Settings *************************************************/
	public void setCalcMethod(int calcMethod) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putInt("CalcMethod", calcMethod);
		mpref.commit();
	}

	public int getCalcMethod() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getInt("CalcMethod", 4);
	}

	/************************************* Juristic Settings *************************************************/
	public void setLanguage(int languagePosition) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putInt("LANGUAGE_INDEX", languagePosition);

		mpref.commit();
	}

	public int getSelectedLanguage() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getInt("LANGUAGE_INDEX", 0);
	}

	/************************************* Store Font settings *************************************************/
	public void StoreFontSize(int fontSize) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				"font_pref", Context.MODE_PRIVATE).edit();
		mpref.putInt("font", fontSize);
		mpref.commit();

	}

	public int getFontSize() {
		SharedPreferences mpref = mContext.getSharedPreferences("font_pref",
				Context.MODE_PRIVATE);
		return mpref.getInt("font", 16);
	}

	/************************************* Alarm settings *************************************************/
	public void saveAlarmSetting(String prayerName, boolean isSet) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putBoolean(prayerName, isSet);
		mpref.commit();
	}

	public boolean isAlarmSet(String prayerName) {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getBoolean(prayerName, false);
	}

	/************************************* Alarm settings *************************************************/
	public void setGPSSettings(boolean isSet) {
		SharedPreferences.Editor mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE).edit();
		mpref.putBoolean("GPS", isSet);
		mpref.commit();
	}

	public boolean isGPSSelected() {
		SharedPreferences mpref = mContext.getSharedPreferences(
				AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
		return mpref.getBoolean("GPS", false);
	}

	/************************************* show or hide drop down list ************************************************/
	public void showHidedroplist(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
			v.startAnimation(closedropdown());
		} else {
			v.setVisibility(View.VISIBLE);
			v.startAnimation(opendropdown());
		}
	}

	public void HideMenu(View menu) {
		if (menu.getVisibility() == View.VISIBLE) {
			menu.setVisibility(View.GONE);
		}
	}

	/************************************************************* Animations ************************************************/
	public Animation opendropdown() {
		/*
		 * ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
		 * anim.setDuration(400); return anim;
		 */

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		return inFromRight;
	}

	public Animation closedropdown() {
		/*
		 * ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 1);
		 * anim.setDuration(400); return anim;
		 */

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		return inFromRight;
	}

	public Animation blinkingAnim() {
		Animation mAnimation = new AlphaAnimation(1, 0);
		mAnimation.setDuration(400);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setRepeatCount(4);
		mAnimation.setRepeatMode(Animation.REVERSE);
		return mAnimation;
	}

}
