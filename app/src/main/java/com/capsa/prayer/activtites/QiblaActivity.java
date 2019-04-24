package com.capsa.prayer.activtites;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.capsa.prayer.makkah.QiblaCompassManager;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppUtils;
import com.capsa.prayer.utills.ConcurrencyUtil;
import com.capsa.prayer.utills.ConstantUtilInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
/*import com.google.android.gms.ads.InterstitialAd;*/

public class QiblaActivity extends AppCompatActivity implements AnimationListener,
		OnSharedPreferenceChangeListener, ConstantUtilInterface {
	private boolean faceUp = true;
	private boolean gpsLocationFound = true;
	private String location_line2 = "";

	public Location currentLocation = null;

	private double lastQiblaAngle = 0;
	private double lastNorthAngle = 0;
	private double lastQiblaAngleFromN = 0;

	private RotateAnimation animation;

	private ImageView compassImageView;
	private ImageView qiblaArrowImageView;

	private FrameLayout qiblaFrameLayout;
	private LinearLayout noLocationLayout;

	private final QiblaCompassManager qiblaManager = new QiblaCompassManager(this);

	private boolean angleSignaled = false;
	private Timer timer = null;

	private SharedPreferences perfs;

	public boolean isRegistered = false;
	public boolean isGPSRegistered = false;
	/*private InterstitialAd interstitialgoogle;*/

	private AppManager appManager;
	private UserLocation userLocation;
	/*private AdView adView;*/
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message.what == ROTATE_IMAGES_MESSAGE) {
				Bundle bundle = message.getData();

				boolean isQiblaChanged = bundle.getBoolean(IS_QIBLA_CHANGED);
				boolean isCompassChanged = bundle
						.getBoolean(IS_COMPASS_CHANGED);

				double qiblaNewAngle = 0;
				double compassNewAngle = 0;

				if (isQiblaChanged)
					qiblaNewAngle = (Double) bundle.get(QIBLA_BUNDLE_DELTA_KEY);

				if (isCompassChanged) {
					compassNewAngle = (Double) bundle
							.get(COMPASS_BUNDLE_DELTA_KEY);
				}
				// This
				syncQiblaAndNorthArrow(compassNewAngle, qiblaNewAngle,
						isCompassChanged, isQiblaChanged);
				angleSignaled = false;
			}
		}

	};
	private TextView locationCountryText;
	private TextView locationCityText;

	public void setLocationText(String textToShow) {
		this.location_line2 = textToShow;
	}

	/*
	 * This is actually a loop task that check for new angles when no animation
	 * is in run and then provide a Message for QiblaActivity. Please note that
	 * this class is running in another thread.
	 */
	private TimerTask getTimerTask() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {

				if (angleSignaled && !ConcurrencyUtil.isAnyAnimationOnRun()) {

					// numAnimationOnRun += 2;
					Map<String, Double> newAnglesMap = qiblaManager
							.fetchDeltaAngles();
					Double newNorthAngle = newAnglesMap
							.get(QiblaCompassManager.NORTH_CHANGED_MAP_KEY);
					Double newQiblaAngle = newAnglesMap
							.get(QiblaCompassManager.QIBLA_CHANGED_MAP_KEY);

					Message message = mHandler.obtainMessage();
					message.what = ROTATE_IMAGES_MESSAGE;
					Bundle bundle = new Bundle();
					if (newNorthAngle == null) {
						bundle.putBoolean(IS_COMPASS_CHANGED, false);
					} else {
						ConcurrencyUtil.incrementAnimation();
						bundle.putBoolean(IS_COMPASS_CHANGED, true);

						bundle.putDouble(COMPASS_BUNDLE_DELTA_KEY,
								newNorthAngle);
					}
					if (newQiblaAngle == null) {
						bundle.putBoolean(IS_QIBLA_CHANGED, false);

					} else {
						ConcurrencyUtil.incrementAnimation();
						bundle.putBoolean(IS_QIBLA_CHANGED, true);
						bundle.putDouble(QIBLA_BUNDLE_DELTA_KEY, newQiblaAngle);
					}

					message.setData(bundle);
					mHandler.sendMessage(message);
				} else if (ConcurrencyUtil.getNumAimationsOnRun() < 0) {
					AppUtils.printLog(NAMAZ_LOG_TAG,
							" Number of animations are negetive numOfAnimation: "
									+ ConcurrencyUtil.getNumAimationsOnRun());
				}
			}
		};
		return timerTask;
	}

	/*
	 * Running the TimerTask. (for example when application is started or became
	 * back from pause mode.)
	 */
	private void schedule() {

		if (timer == null) {
			timer = new Timer();
			this.timer.schedule(getTimerTask(), 0, 200);
		} else {
			timer.cancel();
			timer = new Timer();
			timer.schedule(getTimerTask(), 0, 200);
		}
	}

	/*
	 * Stopping the timerTask (For example when activity is paused or stopped)
	 */
	private void cancelSchedule() {

		if (timer == null)
			return;
		// timer.cancel();

	}

	/*
	 * When user changes the gps status to on mode. The QiblaImages must became
	 * unvisible and some screen texts must be changed. These changes will
	 * became permanent until the GPS device recieves location, or user set GPS
	 * to off.
	 */
	private void onInvalidateQibla(String message) {

		qiblaArrowImageView.setVisibility(View.INVISIBLE);
		compassImageView.setVisibility(View.INVISIBLE);
		// frameImage.setVisibility(View.INVISIBLE);
		qiblaFrameLayout.setVisibility(View.INVISIBLE);

		TextView textView3 = (TextView) findViewById(R.id.noLocationText);
		textView3.setText(message);

		noLocationLayout.setVisibility(View.VISIBLE);
	}

	private void requestForValidationOfQibla() {

		if (faceUp && (gpsLocationFound || currentLocation != null)) {
			noLocationLayout.setVisibility(View.INVISIBLE);
			qiblaFrameLayout.setVisibility(View.VISIBLE);
			qiblaArrowImageView.setVisibility(View.VISIBLE);
			compassImageView.setVisibility(View.VISIBLE);
			// frameImage.setVisibility(View.VISIBLE);
		} else {
			if (!faceUp) {
				onScreenDown();
			} else if (!(gpsLocationFound || currentLocation != null)) {
				onGPSOn();
			}
		}
	}

	private void onGPSOn() {
		gpsLocationFound = false;
		onInvalidateQibla(getString(R.string.no_location_yet));
	}

	/*
	 * Qible direction is set with the assumption of horizontal and up to ceil
	 * screen orientation. If the user changes these aligns, we wil notify
	 * him/her with messages.
	 */
	public void onScreenDown() {
		faceUp = false;
		onInvalidateQibla(getString(R.string.screen_down_text));
	}

	/*
	 * when user changes align of screen to horizontal and up to sky. The
	 * previously set messages will changes
	 */
	public void onScreenUp() {
		faceUp = true;
		requestForValidationOfQibla();
	}

	/*
	 * QiblaManager will set new location of the device with this method. We
	 * will set appropriate me.ssages
	 */
	public void onNewLocationFromGPS(Location location) {
		gpsLocationFound = true;
		currentLocation = location;

		this.setLocationText(getLocationForPrint(location.getLatitude(),
				location.getLongitude()));

		requestForValidationOfQibla();
	}

	/*
	 * when user changes the GPS status off, any changes we must show the images
	 * and use last location for direction
	 */
	private void onGPSOff(Location defaultLocation) {
		currentLocation = defaultLocation;
		gpsLocationFound = false;
		requestForValidationOfQibla();
	}

	/*
	 * This method get us appropraite message string about latitude and
	 * longitude points
	 */
	private String getLocationForPrint(double latitude, double longitude) {
		int latDegree = (Double.valueOf(Math.floor(latitude))).intValue();
		int longDegree = (Double.valueOf(Math.floor(longitude))).intValue();

		String latEnd = getString(R.string.latitude_south);
		String longEnd = getString(R.string.longitude_west);

		if (latDegree > 0) {
			latEnd = getString(R.string.latitude_north);

		}
		if (longDegree > 0) {
			longEnd = getString(R.string.longitude_east);
		}

		double latSecond = (latitude - latDegree) * 100;
		double latMinDouble = (latSecond * 3d / 5d);

		int latMinute = Double.valueOf(Math.floor(latMinDouble)).intValue();

		double longSecond = (longitude - longDegree) * 100;
		double longMinDouble = (longSecond * 3d / 5d);

		int longMinute = Double.valueOf(Math.floor(longMinDouble)).intValue();

		return String.format(getString(R.string.geo_location_info), latDegree,
				latMinute, latEnd, longDegree, longMinute, longEnd);
		// return getString(R.string.geo_location_info);

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qibla_direction);
		// get action bar

		// Enabling Up / Back navigation
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
		getSupportActionBar().setTitle("Qibla Direction");
		/*adView = new AdView(this);
		adView = (AdView) this.findViewById(R.id.adlayout_qibla);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		interstitialgoogle = new InterstitialAd(this);
		interstitialgoogle
				.setAdUnitId("ca-app-pub-2530669520505137/9237568801");
		interstitialgoogle.loadAd(adRequest);*/

		this.qiblaArrowImageView = (ImageView) findViewById(R.id.arrowImage);
		this.compassImageView = (ImageView) findViewById(R.id.compassImage);
		userLocation = new UserLocation();
		appManager = new AppManager(QiblaActivity.this);
		userLocation = appManager.getSelectedLocation();

		locationCityText = (TextView) findViewById(R.id.locationCity);
		locationCountryText = (TextView) findViewById(R.id.locationCountry);

		locationCityText.setText(userLocation.getCityName());
		locationCountryText.setText(userLocation.getCountryName());

		// frameImage = (ImageView) findViewById(R.id.frameImage);
		qiblaFrameLayout = (FrameLayout) findViewById(R.id.qiblaLayout);
		noLocationLayout = (LinearLayout) findViewById(R.id.noLocationLayout);

		// registering for listeners
		registerListeners();

		// Checking if the GPS is on or off. If it was on the default location
		// will be set and if its on, appropriate
		Context context = getApplicationContext();
		perfs = PreferenceManager.getDefaultSharedPreferences(context);
		perfs.registerOnSharedPreferenceChangeListener(this);
		String gpsPerfKey = getString(R.string.gps_pref_key);

		boolean isGPS = false;
		try {
			isGPS = Boolean.parseBoolean(perfs.getString(gpsPerfKey, "false"));
		} catch (ClassCastException e) {
			isGPS = perfs.getBoolean(gpsPerfKey, false);
		}

		if (!isGPS && !appManager.isGPSSelected()) {
			unregisterForGPS();
			useDefaultLocation(perfs,
					getString(R.string.state_location_pref_key));
		} else {
			registerForGPS();
			onGPSOn();
		}

	}

	/*
	 * Unregistering every listeners
	 */
	private void unregisterListeners() {
		((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(qiblaManager);

		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor gsensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.unregisterListener(qiblaManager, gsensor);
		mSensorManager.unregisterListener(qiblaManager, msensor);

		cancelSchedule();
	}

	/*
	 * Registering for locationListener (When GPS is set on)
	 */
	private void registerForGPS() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);

		LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		String provider = locationManager.getBestProvider(criteria, true);

		if (provider != null) {
			locationManager.requestLocationUpdates(provider, MIN_LOCATION_TIME,
					MIN_LOCATION_DISTANCE, qiblaManager);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_LOCATION_TIME, MIN_LOCATION_DISTANCE, qiblaManager);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_LOCATION_TIME,
				MIN_LOCATION_DISTANCE, qiblaManager);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (location != null) {
			qiblaManager.onLocationChanged(location);
		} else {
			if (userLocation != null && userLocation.getGPSLocation() != null) {
				qiblaManager.onLocationChanged(userLocation.getGPSLocation());
			}

		}

	}

	/*
	 * Unregistering from Location Listener (When GPS is set off)
	 */
	private void unregisterForGPS() {
		((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(qiblaManager);

	}

	/*
	 * Registering for all Listeners. LocationListener will be registered if and
	 * only if GPS status is on.
	 */
	private void registerListeners() {
		SharedPreferences perfs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (perfs.getBoolean(getString(R.string.gps_pref_key), false)) {
			registerForGPS();
		} else {
			useDefaultLocation(perfs,
					getString(R.string.state_location_pref_key));
		}

		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor gsensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(qiblaManager, gsensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(qiblaManager, msensor,
				SensorManager.SENSOR_DELAY_GAME);
		schedule();

		isRegistered = true;

	}

	@Override
	protected void onResume() {
		super.onResume();

		userLocation = appManager.getSelectedLocation();

		int selectedLanguageIndex = appManager.getSelectedLanguage();
		AppUtils.updateLocale(this, selectedLanguageIndex);

		locationCityText.setText(userLocation.getCityName());
		locationCountryText.setText(userLocation.getCountryName());

		registerListeners();
	}

	@Override
	protected void onPause() {
		super.onPause();

		ConcurrencyUtil.setToZero();
		ConcurrencyUtil.directionChangedLock.readLock();
		unregisterListeners();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/*
	 * This method synchronizes the Qibla and North arrow rotation.
	 */
	public void syncQiblaAndNorthArrow(double northNewAngle,
			double qiblaNewAngle, boolean northChanged, boolean qiblaChanged) {
		if (northChanged) {
			lastNorthAngle = rotateImageView(northNewAngle, lastNorthAngle,
					compassImageView, false);
			// if North is changed and our location are not changed(Though qibla
			// direction is not changed). Still we need to rotated Qibla arrow
			// to have the same difference between north and Qibla.
			if (qiblaChanged == false && qiblaNewAngle != 0) {
				lastQiblaAngleFromN = qiblaNewAngle;
				lastQiblaAngle = rotateImageView(qiblaNewAngle + northNewAngle,
						lastQiblaAngle, qiblaArrowImageView, true);
			} else if (qiblaChanged == false && qiblaNewAngle == 0)

				lastQiblaAngle = rotateImageView(lastQiblaAngleFromN
						+ northNewAngle, lastQiblaAngle, qiblaArrowImageView,
						true);

		}
		if (qiblaChanged) {
			lastQiblaAngleFromN = qiblaNewAngle;
			lastQiblaAngle = rotateImageView(qiblaNewAngle + lastNorthAngle,
					lastQiblaAngle, qiblaArrowImageView, true);

			UserLocation selectedLocation = appManager.getSelectedLocation();
			selectedLocation.setQiblaAngle(lastQiblaAngleFromN);
			appManager.setSelectedLocation(selectedLocation);
		}
	}

	private double rotateImageView(double newAngle, double fromDegree,
			ImageView imageView, boolean isQiblaArrow) {

		newAngle = newAngle % 360;
		double rotationDegree = fromDegree - newAngle;
		rotationDegree = rotationDegree % 360;
		long duration = Double.valueOf(Math.abs(rotationDegree) * 2000 / 360)
				.longValue();
		if (rotationDegree > 180)
			rotationDegree -= 360;

		float toDegree = Double.valueOf(newAngle % 360).floatValue();
		final int width = Math.abs(qiblaFrameLayout.getRight()
				- qiblaFrameLayout.getLeft());
		final int height = Math.abs(qiblaFrameLayout.getBottom()
				- qiblaFrameLayout.getTop());

		float pivotX = width / 2f;
		float pivotY = height / 2f;

		animation = new RotateAnimation(
				Double.valueOf(fromDegree).floatValue(), toDegree, pivotX,
				pivotY);
		/*
		 * if (isQiblaArrow) { animation = new
		 * RotateAnimation(Double.valueOf(fromDegree).floatValue(), toDegree,
		 * Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		 * } else { animation = new
		 * RotateAnimation(Double.valueOf(fromDegree).floatValue(), toDegree,
		 * Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		 * }
		 */

		animation.setRepeatCount(0);
		animation.setDuration(duration);
		animation.setInterpolator(new LinearInterpolator());
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setAnimationListener(this);

		AppUtils.printLog(NAMAZ_LOG_TAG, "rotating image from " + fromDegree
				+ " degree to rotate: " + rotationDegree);

		imageView.startAnimation(animation);

		return toDegree;

	}

	public void signalForAngleChange(Double qiblaDirection,
			boolean isQiblaDirection) {
		this.angleSignaled = true;

		if (isQiblaDirection) {
			UserLocation selectedLocation = appManager.getSelectedLocation();
			selectedLocation.setQiblaAngle(qiblaDirection);
			appManager.setSelectedLocation(selectedLocation);
		}
	}

	public void onAnimationEnd(Animation animation) {
		if (ConcurrencyUtil.getNumAimationsOnRun() <= 0) {
			AppUtils.printLog(NAMAZ_LOG_TAG,
					"An animation ended but no animation was on run!!!!!!!!!");
		} else {
			ConcurrencyUtil.decrementAnimation();
		}
		schedule();
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
		cancelSchedule();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		String gpsPerfKey = getString(R.string.gps_pref_key);
		String defaultLocationPerfKey = getString(R.string.state_location_pref_key);
		if (gpsPerfKey.equals(key)) {
			boolean isGPS = false;
			try {
				isGPS = Boolean.parseBoolean(sharedPreferences.getString(key,
						"false"));
			} catch (ClassCastException e) {
				isGPS = sharedPreferences.getBoolean(key, false);
			}
			if (isGPS && appManager.isGPSSelected()) {
				registerForGPS();
				currentLocation = null;
				onGPSOn();
			} else {
				useDefaultLocation(sharedPreferences, defaultLocationPerfKey);
				unregisterForGPS();

			}
		} else if (defaultLocationPerfKey.equals(key)) {
			sharedPreferences.edit().putBoolean(gpsPerfKey, false);
			sharedPreferences.edit().commit();
			unregisterForGPS();
			useDefaultLocation(sharedPreferences, key);
		} else {
			AppUtils.printLog(NAMAZ_LOG_TAG, "preference with key:" + key
					+ " is changed and it is not handled properly");
		}

	}

	private void useDefaultLocation(SharedPreferences perfs, String key) {
		qiblaManager.onLocationChanged(userLocation.getGPSLocation());

		this.setLocationText(userLocation.getCityName() + "\n"
				+ userLocation.getCountryName());

		onGPSOff(userLocation.getGPSLocation());
	}

}