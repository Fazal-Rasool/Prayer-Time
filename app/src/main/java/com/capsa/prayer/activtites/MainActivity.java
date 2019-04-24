package com.capsa.prayer.activtites;

import java.util.ArrayList;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.capsa.prayer.time.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity  {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    AdRequest adRequest;
    private AdView adView;

    private InterstitialAd interstitialAd;

    private String[] tabs = {"Prayer Time", "Qibla Direction"};

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter NavDraweradapter;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.framget_menu);

        adRequest = new AdRequest.Builder().build();
        interstitialAd = new InterstitialAd(this);
        interstitialAd
                .setAdUnitId("ca-app-pub-2530669520505137/9237568801");
        interstitialAd.loadAd(adRequest);

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
                .getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
                .getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
                .getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
                .getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
                .getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
                .getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons
                .getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons
                .getResourceId(7, -1)));
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        NavDraweradapter = new NavDrawerListAdapter(MainActivity.this,
                navDrawerItems);
        mDrawerList.setAdapter(NavDraweradapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setIcon(R.drawable.transparent);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Adding Tabs
        for (String tab_name : tabs) {
            getSupportActionBar().addTab(getSupportActionBar().newTab().setText(tab_name)
                    .setTabListener(new android.support.v7.app.ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {
                            int position = tab.getPosition();
                            if (position == 0) {
                                /*interstitialgoogle.show();*/

                            } else {
                                Intent intent = new Intent(MainActivity.this, QiblaActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {

                        }

                        @Override
                        public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {

                        }
                    }));

        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {

            displayView(0);
        }
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }

    }

    /*
      Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {

            case 0:
                fragment = new FragmnetMainActivity();
                break;

            case 1:
                if (interstitialAd.isLoaded())
                    interstitialAd.show();
                else
                    interstitialAd.loadAd(adRequest);
                Intent intentStatus = new Intent(MainActivity.this,
                        TodayStatus.class);
                intentStatus.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentStatus);
                break;

            case 2:
                Intent intentCalendar = new Intent(MainActivity.this,
                        RamadanCalendar.class);
                intentCalendar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentCalendar);
                break;

            case 3:
                Intent intentAzan = new Intent(MainActivity.this,
                        AzanActivity.class);
                intentAzan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentAzan);
                break;

            case 4:
                Intent intentQalme = new Intent(MainActivity.this,
                        QalmaActivity.class);
                intentQalme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentQalme);
                break;

            case 5:
                Intent settingIntent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                settingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingIntent);
                break;

            case 6:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.capsa.prayer.time");
                intent.putExtra(android.content.Intent.EXTRA_STREAM, R.drawable.ic_launcher);
                startActivity(Intent.createChooser(intent, "Choose from..."));
                break;

            case 7:
                Intent moreintent = new Intent(Intent.ACTION_VIEW);
                moreintent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=CapsaApps"));
                startActivity(moreintent);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        getSupportActionBar().setSelectedNavigationItem(0);
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public void showExitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(getString(R.string.app_name));

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure, you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                MainActivity.this.finish();

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
