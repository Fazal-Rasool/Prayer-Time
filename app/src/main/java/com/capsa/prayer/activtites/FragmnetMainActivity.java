package com.capsa.prayer.activtites;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.capsa.prayer.adaptors.PrayerListAdapter;
import com.capsa.prayer.makkah.QiblaDirectionCalculator;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.PrayerTime;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppUtils;
import com.capsa.prayer.utills.CallBack;
import com.capsa.prayer.utills.PrayTime;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmnetMainActivity extends Fragment implements OnClickListener,
		CallBack, OnItemClickListener, LocationListener {

	String[] islamicMonths;
	AppManager appManager;

	private ArrayList<PrayerTime> prayerTimesList;

	private TextView englishDateTextView;
	private TextView hijriDateTextView;
	private TextView currentTimeTextView;
	private TextView timeRemainingTextView;
	private TextView locationTextView;

	private static Timer updateTimer;
	private ListView prayerListView;
	private PrayerListAdapter adapter;
	private LocationManager locationManager;
	View v;
	private String provider;
	/*private AdView adView;*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.activity_main, null);

		appManager = new AppManager(getActivity().getApplicationContext());

		/*adView = new AdView(getActivity().getApplicationContext());
		adView = (AdView) v.findViewById(R.id.adlayout);AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);*/

		if (appManager.isCountryDialogRun() && appManager.isCityDialogRun()) {
			initMain();
		}
		if (!appManager.isCountryDialogRun()) {
			AppUtils.showCountryDialog(getActivity(), appManager, this, false);
			initMain();
		} else if (!appManager.isCityDialogRun()) {
			initMain();
			AppUtils.showCityDilog(getActivity(), this, false, false);
		}

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();

		provider = locationManager.getBestProvider(criteria, false);
//		updateViews();
		return v;
	}

	private void updateViews() {
		Calendar c = Calendar.getInstance(Locale.US);

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		String strDate = sdf.format(c.getTime());

//		currentTimeTextView.setText(strDate);

//		locationTextView.setText(appManager.getSelectedLocation().getCityName() + ", " + appManager.getSelectedCountry());
		getCurrentPrayerIndex();

		calculateRemainingTime();
	}

	@Override
	public void onResume() {
		super.onResume();

		int selectedLanguageIndex = appManager.getSelectedLanguage();
		AppUtils.updateLocale(getActivity().getApplicationContext(),
				selectedLanguageIndex);

		if (appManager.isCountryDialogRun() && appManager.isCityDialogRun()) {

			if (locationManager != null && appManager.isGPSSelected()) {
				if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				locationManager.requestLocationUpdates(provider, 300000, 100,
						this);
			} else {
				setValues();
			}

		}

	}

	@Override
	public void onPause() {
		super.onPause();

		if (updateTimer != null) {
			updateTimer.purge();
			updateTimer.cancel();

			updateTimer = null;
		}

		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.location:

			intent = new Intent(getActivity().getApplicationContext(),
					SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void initMain() {
		islamicMonths = getResources().getStringArray(R.array.islamic_months);

		/*englishDateTextView = (TextView) v
				.findViewById(R.id.englishDateTextView_main);
		hijriDateTextView = (TextView) v.findViewById(R.id.hijriDateTextView);
		currentTimeTextView = (TextView) v
				.findViewById(R.id.currentTimeTextView);
		timeRemainingTextView = (TextView) v
				.findViewById(R.id.timeRemainingTextView);*/
		/*locationTextView = (TextView) v
				.findViewById(R.id.locationTextView);
*/
		setValues();

	}

	private void setValues() {

		UserLocation userLocation = appManager.getSelectedLocation();

		prayerTimesList = new PrayTime(getActivity().getApplicationContext())
				.getPrayerTimes(userLocation, appManager);

		if (prayerListView == null) {
			prayerListView = (ListView) v.findViewById(R.id.prayerListView);
			prayerListView.setOnItemClickListener(this);
		}

		if (adapter == null) {
			adapter = new PrayerListAdapter(getActivity()
					.getApplicationContext(), R.layout.prayer_time_list_row,
					prayerTimesList);
			prayerListView.setAdapter(adapter);
		} else {
			adapter.clear();
			for (int index = 0; index < prayerTimesList.size(); index++) {
				adapter.add(prayerTimesList.get(index));
			}
			adapter.notifyDataSetChanged();
		}

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM, yyyy");
		String strDate = sdf.format(c.getTime());

		DateTime date = new DateTime(IslamicChronology.getInstance());

/*
		englishDateTextView.setText(strDate);
		hijriDateTextView.setText(date.getDayOfMonth() + " "
				+ islamicMonths[date.getMonthOfYear() - 1] + " "
				+ date.getYear());
*/

	}

	private void getCurrentPrayerIndex() {
		DateTime dateNow = new DateTime();
		DateTime date1;
		DateTime date2;

		PrayerTime firstPrayer;
		PrayerTime secondPrayer;

		try {
			for (int i = 0; i < prayerTimesList.size(); i++) {
				firstPrayer = prayerTimesList.get(i);

				if (i == 0) {
					secondPrayer = prayerTimesList.get(5);
				} else if (i == 5) {
					secondPrayer = prayerTimesList.get(0);
				} else {
					secondPrayer = prayerTimesList.get(i - 1);
				}

				String[] first = firstPrayer.getPrayerTime()
						.replaceAll(" am", "").replaceAll(" pm", "").split(":");
				String[] second = secondPrayer.getPrayerTime()
						.replaceAll(" am", "").replaceAll(" pm", "").split(":");

				date1 = new DateTime(dateNow.getYear(),
						dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
						getHourOfDay(first[0], firstPrayer),
						Integer.parseInt(first[1]));
				date2 = new DateTime(dateNow.getYear(),
						dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
						getHourOfDay(second[0], secondPrayer),
						Integer.parseInt(second[1]));

				date1.compareTo(date2);

				if ((dateNow.isEqual(date1) || dateNow.isBefore(date1))
						&& dateNow.isAfter(date2)) {
					adapter.mCurrentPrayerIndex = i;
					adapter.notifyDataSetChanged();

					return;

				} else if (i == 0
						&& (dateNow.isEqual(date1) || dateNow.isBefore(date1) || dateNow
								.isAfter(date2))) {
					adapter.mCurrentPrayerIndex = i;

					adapter.notifyDataSetChanged();
					return;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateRemainingTime() {
		PrayerTime prayer = prayerTimesList.get(adapter.mCurrentPrayerIndex);

		DateTime dateNow = new DateTime();
		String[] first = prayer.getPrayerTime().replaceAll(" am", "")
				.replaceAll(" pm", "").split(":");

		int minute = 0;
		if (first.length == 2) {
			minute = Integer.parseInt(first[1]);
		}

		DateTime prayerTime = new DateTime(dateNow.getYear(),
				dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
				getHourOfDay(first[0], prayer), minute);

		Period period = new Period(dateNow, prayerTime);
		PeriodFormatter HHMMSSFormater = new PeriodFormatterBuilder()
				.printZeroAlways().minimumPrintedDigits(2).appendHours()
				.appendSeparator("-").appendMinutes().appendSeparator("-")
				.appendSeconds().toFormatter(); // produce

		AppUtils.printLog("", HHMMSSFormater.print(period));

		String[] arrTime = HHMMSSFormater.print(period).split("-");

		int hours = 0;
		int minutes = 0;

		if (!TextUtils.isEmpty(arrTime[0])) {
			hours = Integer.parseInt(arrTime[0]);
		}

		if (!TextUtils.isEmpty(arrTime[1])) {
			minutes = Integer.parseInt(arrTime[1]);
		}

		// int seconds = Integer.parseInt(arrTime[2]);
/*
		String remainingTime = "";
		if (hours != 0) {
			remainingTime = hours + "  " + getString(R.string.hours) + " "
					+ minutes + " " + getString(R.string.minutes);
		} else {
			remainingTime = minutes + " " + getString(R.string.minutes);
		}

		if (minutes != 0) {
			timeRemainingTextView.setText(prayer.getPrayerName() + " "
					+ getString(R.string.after) + " " + remainingTime);
		} else {
			timeRemainingTextView.setText(getString(R.string.its) + " "
					+ prayer.getPrayerName() + " " + getString(R.string.time));
		}*/

	}

	/********************************** Exit Dialog ************************************/

	@Override
	public void onLocationChanged(Location location) {
		if (location != null && appManager.isGPSSelected()) {
			updateWithNewLocation(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	private void updateWithNewLocation(final Location location) {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Geocoder geo = new Geocoder(getActivity()
							.getApplicationContext(), Locale.getDefault());
					List<Address> addresses = geo.getFromLocation(
							location.getLatitude(), location.getLongitude(), 1);

					if (!addresses.isEmpty()) {

						if (addresses.size() > 0) {

							UserLocation userLocation = new UserLocation();
							userLocation.setCityName(addresses.get(0)
									.getLocality());
							userLocation.setCountryName(addresses.get(0)
									.getCountryName());
							userLocation.setLatitude(location.getLatitude());
							userLocation.setLongitude(location.getLongitude());
							userLocation.setAltitude(location.getAltitude());
							userLocation.setGPSLocation();

							Calendar calendar = Calendar.getInstance(
									TimeZone.getTimeZone("GMT"),
									Locale.getDefault());
							Date currentLocalTime = calendar.getTime();
							DateFormat date = new SimpleDateFormat("Z");
							String localTime = date.format(currentLocalTime);

							double timezone = 0;

							try {
								timezone = Double.parseDouble(localTime) / 100;
							} catch (Exception e) {
								e.printStackTrace();
							}

							userLocation.setTimeZone(timezone);

							userLocation.setQiblaAngle(QiblaDirectionCalculator
									.getQiblaDirectionFromNorth(
											userLocation.getLatitude(),
											userLocation.getLongitude()));
							appManager.setSelectedLocation(userLocation);

						}
					}
				} catch (Exception e) {
					e.printStackTrace(); // getFromLocation() may sometimes fail
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				setValues();
			}

		}.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position,
			long id) {
		PrayerTime prayer = prayerTimesList.get(position);

		if (prayer.isAlarmSet()) {
			appManager.cancelAlarm(position + 1);
			appManager.saveAlarmSetting(prayerTimesList.get(position)
					.getPrayerName(), false);
			prayer.setAlarm(false);
		} else {
			appManager.setAlarm(prayer.getPrayerTime(), (position + 1));
			appManager.saveAlarmSetting(prayerTimesList.get(position)
					.getPrayerName(), true);
			prayer.setAlarm(true);
		}

		adapter.objects.remove(position);
		adapter.objects.add(position, prayer);
		adapter.notifyDataSetChanged();
	}

	private int getHourOfDay(String hour, PrayerTime prayer) {
		int hourOfDay = 0;
		try {
			hourOfDay = Integer.parseInt(hour);

			if (prayer.getPrayerTime().contains("pm")) {
				if (hourOfDay != 12) {
					hourOfDay = hourOfDay + 12;
				}
			} else if (prayer.getPrayerTime().contains("am")) {
				if (hourOfDay == 12) {
					hourOfDay = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hourOfDay;
	}

	@Override
	public void notify(Object object, String type) {

	}

}
