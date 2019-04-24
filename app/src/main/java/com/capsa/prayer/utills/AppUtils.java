package com.capsa.prayer.utills;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.capsa.prayer.adaptors.CitiesListAdapter;
import com.capsa.prayer.adaptors.CountryAdapter;
import com.capsa.prayer.adaptors.SettingsListAdapter;
import com.capsa.prayer.makkah.QiblaDirectionCalculator;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;

public class AppUtils {

	private static final boolean DEBUG = false;
	private static CountryAdapter adapter;
	private static CitiesListAdapter mCitiesListAdapter;

	private static String[] filteredArray;
	protected static ArrayList<UserLocation> filteredCities;

	protected static boolean selected;
	protected static boolean isSearching;

	public static void showCountryDialog(final Context context,
			final AppManager appManager, final CallBack callback,
			final boolean cancelable) {
		isSearching = false;
		try {
			final Dialog dialogCountry = new Dialog(context,
					R.style.customDialogStyle);

			dialogCountry.setContentView(R.layout.settings_country);
			dialogCountry.setCancelable(cancelable);
			dialogCountry.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialogCountry.setCanceledOnTouchOutside(false);

			final ListView listView = (ListView) dialogCountry
					.findViewById(R.id.listView);

			final String[] countries = context.getAssets().list("Countries");

			adapter = new CountryAdapter(context, R.layout.list_item_country,
					countries);

			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parentView, View view,
						int position, long id) {
					String selectedCountry = "";
					if (isSearching) {
						selectedCountry = filteredArray[position].replace(
								".txt", "");
					} else {
						selectedCountry = countries[position].replace(".txt",
								"");
					}
					appManager.setSelectedCountry(selectedCountry);
					appManager.setCountryDialogRun(true);

					dialogCountry.dismiss();

					showCityDilog(context, callback, cancelable, true);
				}
			});

			dialogCountry.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.notify("", "SHOW_DATA");
				}
			});

			EditText searchEditText = (EditText) dialogCountry
					.findViewById(R.id.searchEditText);
			searchEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					if (!cs.toString().equals("")) {
						isSearching = true;
						List<String> filteredTitles = new ArrayList<String>();
						for (int i = 0; i < countries.length; i++) {
							String country = countries[i].replace(".txt", "")
									.toLowerCase(Locale.US);
							AppUtils.printLog("", "" + country);
							if (country.contains(cs.toString().toLowerCase(
									Locale.US))) {
								filteredTitles.add(countries[i]);
							}
						}

						filteredArray = new String[filteredTitles.size()];
						filteredArray = filteredTitles.toArray(filteredArray);

						adapter = new CountryAdapter(context,
								R.layout.list_item_country, filteredArray);
						listView.setAdapter(adapter);
					} else {
						isSearching = true;
						adapter = new CountryAdapter(context,
								R.layout.list_item_country, countries);
						listView.setAdapter(adapter);
					}

				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
				}
			});

			dialogCountry.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showCityDilog(final Context context,
			final CallBack callback, boolean cancelable,
			final boolean fromCountry) {
		isSearching = false;
		selected = false;

		final AppManager appManager = new AppManager(context);

		final Dialog dialogCity = new Dialog(context, R.style.customDialogStyle);
		dialogCity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogCity.setContentView(R.layout.settings_country);
		dialogCity.setCancelable(cancelable);
		dialogCity.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogCity.setCanceledOnTouchOutside(false);

		final ArrayList<UserLocation> locationsList = appManager
				.getCitiesList(appManager.getSelectedCountry() + ".txt");

		mCitiesListAdapter = new CitiesListAdapter(context,
				R.layout.list_item_country, locationsList);

		final ListView listView = (ListView) dialogCity
				.findViewById(R.id.listView);
		listView.setItemsCanFocus(false);
		listView.setAdapter(mCitiesListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				selected = true;
				UserLocation location = locationsList.get(position);
				if (isSearching) {
					location = filteredCities.get(position);
				}

				location.setQiblaAngle(QiblaDirectionCalculator
						.getQiblaDirectionFromNorth(location.getLatitude(),
								location.getLongitude()));
				appManager.setSelectedLocation(location);
				appManager.setCityDialogRun(true);

				dialogCity.dismiss();

				callback.notify("", "SHOW_DATA");
			}
		});

		dialogCity.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				selected = false;
				callback.notify("", "SHOW_DATA");
			}
		});

		dialogCity.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (!selected && fromCountry) {
						UserLocation location = locationsList.get(0);
						location.setQiblaAngle(QiblaDirectionCalculator
								.getQiblaDirectionFromNorth(
										location.getLatitude(),
										location.getLongitude()));
						appManager.setSelectedLocation(location);
						appManager.setCityDialogRun(true);

						callback.notify("", "SHOW_DATA");
					}
				}
				return false;
			}
		});

		EditText searchEditText = (EditText) dialogCity
				.findViewById(R.id.searchEditText);
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (!cs.toString().equals("")) {
					isSearching = true;
					filteredCities = new ArrayList<UserLocation>();
					for (int i = 0; i < locationsList.size(); i++) {
						String city = locationsList.get(i).getCityName()
								.replace(".txt", "").toLowerCase(Locale.US);
						if (city.contains(cs.toString().toLowerCase(Locale.US))) {
							filteredCities.add(locationsList.get(i));
						}
					}
					mCitiesListAdapter = new CitiesListAdapter(context,
							R.layout.list_item_country, filteredCities);
					listView.setAdapter(mCitiesListAdapter);
				} else {
					isSearching = false;
					mCitiesListAdapter = new CitiesListAdapter(context,
							R.layout.list_item_country, locationsList);
					listView.setAdapter(mCitiesListAdapter);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		dialogCity.show();
	}

	public static void showJuristicDialog(final Context context,
			final CallBack callback) {
		final AppManager appManager = new AppManager(context);

		final Dialog dialog = new Dialog(context, R.style.customDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.settings_calc_methods);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		TextView headingTextView = (TextView) dialog
				.findViewById(R.id.headingTextView);
		headingTextView.setText(context
				.getString(R.string.title_select_juristic));

		Button submitJuristic = (Button) dialog
				.findViewById(R.id.submitJuristic);
		submitJuristic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		ListView listView = (ListView) dialog.findViewById(R.id.listView);
		listView.setItemsCanFocus(false);

		String[] arrMethods = context.getResources().getStringArray(
				R.array.juristics_array);

		final SettingsListAdapter adapter = new SettingsListAdapter(context,
				R.layout.settings_list_row_layout, arrMethods, appManager,
				AppConstants.LIST_TYPE_JURISTICS);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				appManager.setJuristic(position);
				adapter.notifyDataSetChanged();
			}

		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				callback.notify("", "SHOW_DATA");
			}
		});

		dialog.show();
	}

	public static void showCalcMethodDialog(final Context context,
			final CallBack callback) {

		final AppManager appManager = new AppManager(context);

		final Dialog dialog = new Dialog(context, R.style.customDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.settings_calc_methods);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		TextView headingTextView = (TextView) dialog
				.findViewById(R.id.headingTextView);
		headingTextView.setText(context
				.getString(R.string.title_select_calc_method));

		Button submitJuristic = (Button) dialog
				.findViewById(R.id.submitJuristic);
		submitJuristic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		ListView listView = (ListView) dialog.findViewById(R.id.listView);
		listView.setItemsCanFocus(false);

		String[] arrMethods = context.getResources().getStringArray(
				R.array.calc_methods_array);

		final SettingsListAdapter adapter = new SettingsListAdapter(context,
				R.layout.settings_list_row_layout, arrMethods, appManager,
				AppConstants.LIST_TYPE_CALC_METHOD);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				appManager.setCalcMethod(position);
				adapter.notifyDataSetChanged();
			}

		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				callback.notify("", "SHOW_DATA");
			}
		});

		dialog.show();
	}

	public static void showLanguageSelectionDialog(final Context context,
			final CallBack callback) {

		final AppManager appManager = new AppManager(context);

		final Dialog dialog = new Dialog(context, R.style.customDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.settings_calc_methods);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		TextView headingTextView = (TextView) dialog
				.findViewById(R.id.headingTextView);
		headingTextView.setText(context
				.getString(R.string.title_select_lannguage));

		Button submitJuristic = (Button) dialog
				.findViewById(R.id.submitJuristic);
		submitJuristic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		ListView listView = (ListView) dialog.findViewById(R.id.listView);
		listView.setItemsCanFocus(false);

		String[] arrMethods = context.getResources().getStringArray(
				R.array.language_array);

		final SettingsListAdapter adapter = new SettingsListAdapter(context,
				R.layout.settings_list_row_layout, arrMethods, appManager,
				AppConstants.LIST_TYPE_LANGUAGE);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				appManager.setLanguage(position);
				adapter.notifyDataSetChanged();
			}

		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				callback.notify("", "RELOAD_DATA");
			}
		});

		dialog.show();
	}

	public static void printLog(String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}

	}

	public static void showAppInGooglePlay(Context applicationContext,
			String appPackageName) {
		Intent intent = null;
		try {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName));
		} catch (android.content.ActivityNotFoundException anfe) {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName));
		}

		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(intent);
		}

	}

	public static void startOtherApp(Context applicationContext) {
		final String appPackageName = "com.vexxor.world.prayer.qibla.direction";

		Intent intent = null;
		try {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName));
		} catch (android.content.ActivityNotFoundException anfe) {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName));
		}

		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(intent);
		}

	}

	public static void startFacebookApp(Context applicationContext) {

		Intent intent;
		try {
			applicationContext.getPackageManager().getPackageInfo(
					"com.facebook.katana", 0);
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"
					+ AppConstants.FB_PAGE_ID));
		} catch (Exception e) {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(AppConstants.FB_PAGE_URL));
		}

		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(intent);
		}
	}

	public static Locale updateLocale(Context context, int selectedLanguageIndex) {
		String locale = "en";
		switch (selectedLanguageIndex) {
		case 0:
			locale = "en";
			break;
		case 1:
			locale = "es";
			break;
		case 2:
			locale = "ru";
			break;
		case 3:
			locale = "fr";
			break;
		case 4:
			locale = "tr";
			break;
		case 5:
			locale = "ar";
			break;
		case 6:
			locale = "zh";
			break;
		default:
			break;
		}

		Configuration config = new Configuration();

		Locale myLocale = new Locale(locale);
		Locale.setDefault(myLocale);
		config.locale = myLocale;
		context.getResources().updateConfiguration(config, null);

		return myLocale;
	}

}
