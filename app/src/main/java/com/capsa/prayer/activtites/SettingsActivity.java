package com.capsa.prayer.activtites;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.capsa.prayer.makkah.QiblaDirectionCalculator;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppUtils;
import com.capsa.prayer.utills.CallBack;

public class SettingsActivity extends AppCompatActivity implements OnClickListener,
		CallBack, LocationListener {

	private View countrySettings, citySettings, juristicSettings, daylight,
			adhan;

	AppManager appManager;

	private View calcMethodSettings;

	private TextView selectedCountry;
	private TextView selectedCity;
	private TextView selectedJuristic;
	private TextView selectedCalcMethod;
	private TextView selectedLanguage;

	String[] arrJuristics;
	String[] arrCalcMethods;
	String[] arrLanguages;

	private View gpsSettings;
	private TextView gpsButton;
	private View languageSettings;

	private TextView stringCountry;
	private TextView stringCity;
	private TextView stringJuristic;
	private TextView stringGPS;
	private TextView stringCalcMethod;
	private TextView stringLanguage;
	private TextView stringAdhan;

	protected boolean isStartingGPS;
	private LocationManager locationManager;

	private String provider;

	private AdView adView;
	AdRequest adRequest;
	private InterstitialAd interstitialgoogle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		// get action bar

		// Enabling Up / Back navigation
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
		getSupportActionBar().setTitle("Settings");

		appManager = new AppManager(SettingsActivity.this);



		adView = new AdView(this);
		adView = (AdView) this.findViewById(R.id.adlayout);
		adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		interstitialgoogle = new InterstitialAd(this);
		interstitialgoogle
				.setAdUnitId("ca-app-pub-2530669520505137/9237568801");
		interstitialgoogle.loadAd(adRequest);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		provider = locationManager.getBestProvider(criteria, false);

		initMain();
	}

	private void initMain() {

		stringCountry = (TextView) findViewById(R.id.country_string);
		stringCity = (TextView) findViewById(R.id.city_string);
		stringJuristic = (TextView) findViewById(R.id.juristic_string);
		stringCalcMethod = (TextView) findViewById(R.id.calc_method_string);
		stringGPS = (TextView) findViewById(R.id.gps_string);
		stringLanguage = (TextView) findViewById(R.id.language_string);
		stringAdhan = (TextView) findViewById(R.id.adhan_string);

		selectedCountry = (TextView) findViewById(R.id.selectedCountry);
		selectedCity = (TextView) findViewById(R.id.selectedCity);
		selectedJuristic = (TextView) findViewById(R.id.selectedJuristic);
		selectedCalcMethod = (TextView) findViewById(R.id.selectedCalcMethod);
		selectedLanguage = (TextView) findViewById(R.id.selectedLanguage);

		// gpsButton = (TextView) findViewById(R.id.gps_string);

		stringGPS.setSelected(appManager.isGPSSelected());

		countrySettings = (View) findViewById(R.id.settings_contry);
		citySettings = (View) findViewById(R.id.settings_city);
		juristicSettings = (View) findViewById(R.id.settings_juristic);
		calcMethodSettings = (View) findViewById(R.id.settings_calc_method);
		gpsSettings = (View) findViewById(R.id.settings_gps);
		daylight = (View) findViewById(R.id.settings_daylight);
		adhan = (View) findViewById(R.id.settings_adhan_audio);
		languageSettings = (View) findViewById(R.id.settings_language);

		citySettings.setOnClickListener(this);
		countrySettings.setOnClickListener(this);
		daylight.setOnClickListener(this);
		adhan.setOnClickListener(this);
		juristicSettings.setOnClickListener(this);
		calcMethodSettings.setOnClickListener(this);
		gpsSettings.setOnClickListener(this);
		languageSettings.setOnClickListener(this);

		notify("", "SET_VALUES");
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isStartingGPS) {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				AppUtils.printLog("", "provider enabled");
				stringGPS.setSelected(true);
				appManager.setGPSSettings(stringGPS.isSelected());
			} else {
				AppUtils.printLog("", "provider disabled");
				stringGPS.setSelected(false);
				appManager.setGPSSettings(stringGPS.isSelected());
			}
		}

		if (locationManager != null && appManager.isGPSSelected()) {
			locationManager.requestLocationUpdates(provider, 300000, 100, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		/*adView.pause();*/

		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
			switch (v.getId()) {
			case R.id.settings_adhan_audio:
				if(interstitialgoogle.isLoaded())
					interstitialgoogle.show();
				else
					interstitialgoogle.loadAd(adRequest);
				Intent intent = new Intent(SettingsActivity.this,
						AzanActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.settings_city:
				if(interstitialgoogle.isLoaded())
					interstitialgoogle.show();
				else
					interstitialgoogle.loadAd(adRequest);
				citySettings.setSelected(!citySettings.isSelected());
				if (!citySettings.isSelected()) {
					AppUtils.showCityDilog(SettingsActivity.this,
							SettingsActivity.this, true, false);
				}

				break;
			case R.id.settings_contry:
				if(interstitialgoogle.isLoaded())
					interstitialgoogle.show();
				else
					interstitialgoogle.loadAd(adRequest);
				countrySettings.setSelected(!countrySettings.isSelected());
				if (!countrySettings.isSelected()) {
					AppUtils.showCountryDialog(SettingsActivity.this, appManager,
							SettingsActivity.this, true);
				}
				break;
			case R.id.settings_juristic:
				juristicSettings.setSelected(!juristicSettings.isSelected());
				if (!juristicSettings.isSelected()) {
					AppUtils.showJuristicDialog(SettingsActivity.this,
							SettingsActivity.this);
				}
				break;
			case R.id.settings_calc_method:
				if(interstitialgoogle.isLoaded())
					interstitialgoogle.show();
				else
					interstitialgoogle.loadAd(adRequest);
				calcMethodSettings.setSelected(!calcMethodSettings.isSelected());
				if (!calcMethodSettings.isSelected()) {
					AppUtils.showCalcMethodDialog(SettingsActivity.this,
							SettingsActivity.this);
				}
				break;
			case R.id.settings_daylight:

				break;
			case R.id.settings_gps:
				checkGPSEnabled();
				break;
			case R.id.settings_language:
				languageSettings.setSelected(!languageSettings.isSelected());
				if (!languageSettings.isSelected()) {
					AppUtils.showLanguageSelectionDialog(SettingsActivity.this,
							SettingsActivity.this);
				}
				break;
			case R.id.qibla:
				if(interstitialgoogle.isLoaded())
					interstitialgoogle.show();
				else
					interstitialgoogle.loadAd(adRequest);
				intent = new Intent(SettingsActivity.this, QiblaActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void notify(Object object, String type) {

		int selectedLanguageIndex = appManager.getSelectedLanguage();
		AppUtils.updateLocale(this, selectedLanguageIndex);

		UserLocation userLocation = appManager.getSelectedLocation();

		arrJuristics = getResources().getStringArray(R.array.juristics_array);
		arrCalcMethods = getResources().getStringArray(
				R.array.calc_methods_array);
		arrLanguages = getResources().getStringArray(R.array.language_array);

		countrySettings.setSelected(true);
		citySettings.setSelected(true);
		juristicSettings.setSelected(true);
		calcMethodSettings.setSelected(true);
		languageSettings.setSelected(true);

		selectedCountry.setText(appManager.getSelectedCountry());
		selectedCity.setText(appManager.getSelectedLocation().getCityName());
		selectedJuristic.setText(arrJuristics[appManager.getJuristic()]);
		selectedCalcMethod.setText(arrCalcMethods[appManager.getCalcMethod()]);
		selectedLanguage.setText(arrLanguages[selectedLanguageIndex]);

		stringCountry.setText(getString(R.string.country_string));
		stringCity.setText(getString(R.string.city_string));
		stringJuristic.setText(getString(R.string.juristics_string));
		stringCalcMethod.setText(getString(R.string.calc_method_string));
		stringLanguage.setText(getString(R.string.language_string));
		stringAdhan.setText(getString(R.string.adhan_audio_string));
		stringGPS.setText(getString(R.string.gps_location_string));

	}

	private void checkGPSEnabled() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			stringGPS.setSelected(!stringGPS.isSelected());
			appManager.setGPSSettings(stringGPS.isSelected());

			if (locationManager != null) {
				locationManager.requestLocationUpdates(provider, 300000, 100,
						this);
			}

		} else {
			showGPSDisabledAlertToUser();
		}
	}

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled on your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Enable",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								isStartingGPS = true;
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						isStartingGPS = false;
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

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
					Geocoder geo = new Geocoder(getApplicationContext(),
							Locale.getDefault());
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

				SettingsActivity.this.notify("", "");
			}

		}.execute();

	}

}
