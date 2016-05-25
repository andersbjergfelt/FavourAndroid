package com.bjergfelt.himev5;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bjergfelt.himev5.Chat.ChatActivity;
import com.bjergfelt.himev5.Chat.ChatRoomActivity;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.addJob.addJobActivity;
import com.bjergfelt.himev5.jobData.DataProvider;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.JobDetailFragment;
import com.bjergfelt.himev5.jobData.JobFragment;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

/*
    For BottomBar we are using roughike's BottomBar
    https://github.com/roughike/BottomBar/blob/master/README.md#common-problems-and-solutions

    For FragNav we are using ncapdevi's FragNav: https://github.com/ncapdevi/FragNav
 */


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, JobFragment.OnListFragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOCATION_KEY = "Location";
    DataProvider dp = new DataProvider();
    Toolbar toolbar;
    Location location;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    private int[] tabIcons = {
            R.drawable.ic_view_list_white_48dp,
            R.drawable.ic_google_maps_white_48dp
    };
    HTTPManager httpManager;

    Job job;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //user interface layout for this Activity
        //To edit user interface go look for res/layout/activity_main.xml file.
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Initialize different components for use later.
       // tabLayout = (TabLayout) findViewById(R.id.tabs);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        getLocationFromPrefs();


        setupViewPager(viewPager,location);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout

        if (findViewById(R.id.sample_content_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                // Update the value of mCurrentLocation from the Bundle and update the UI to show the
                // correct latitude and longitude.
                if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                    // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                    // is not null.
                    location = savedInstanceState.getParcelable(LOCATION_KEY);
                }

                return;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    private void setupViewPager(ViewPager viewPager, Location location) {
        JobFragment jobFragment = JobFragment.newInstance(location);
       // getSupportFragmentManager().beginTransaction().add(R.id.sample_content_fragment, jobFragment).commit();
        adapter.addFragment(jobFragment, "JobList");

        //Log.d("MAINACTIVITY", "" + dp.getJobList().size());
        adapter.addFragment(JobMapActivity.newInstance(dp.getJobList()), "JobMap");

        viewPager.setAdapter(adapter);
    }

    //Adding icon to tab
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    public Location getLocationFromPrefs() {
        //Restoring preferences from Login
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        double latitude = Double.longBitsToDouble(prefs.getLong("Latitude", 0));
        double longitude = Double.longBitsToDouble(prefs.getLong("Longitude", 0));
        Log.d("PREFS", "" + latitude + "," + longitude);
        location = new Location("location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_job) {
            Intent intent = new Intent(this, addJobActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.view_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.view_messages) {
            Intent intent = new Intent(this, ChatRoomActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings){
           Intent intent = new Intent(this, UserSettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onListFragmentInteraction(Job item) {
        // Create fragment and give it an argument specifying the article it should show
        //mBottomBar.hide();
        String data = "data passing";
        this.job = item;
        JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(data, item);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.sample_content_fragment, jobDetailFragment).addToBackStack(null);
        fragmentTransaction.commit();
        tabLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (location == null) {

            location = getLocationFromPrefs();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (location == null) {
            location = getLocationFromPrefs();
        }
        
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the location
        savedInstanceState.putParcelable(LOCATION_KEY, getLocationFromPrefs());
//        mBottomBar.onSaveInstanceState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }




    ////INNER VIEWPAGEADAPTER

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);


        }


        @Override
        public Fragment getItem(int position) {
           // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, mFragmentList.get(position)).commit();
           /* switch (position) {
                case 0:
                    transaction.replace(R.id.sample_content_fragment, JobFragment.newInstance(location));
                    transaction.addToBackStack(null);
                    transaction.commit();
                    //return mFragmentList.get(position);

                case 1:
                    transaction.replace(R.id.sample_content_fragment, JobMapActivity.newInstance(dp.getJobList()));
                    transaction.commit();
                    //return mFragmentList.get(position);*//*
            }*/
        return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);

            titleList.add(title);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }


}
