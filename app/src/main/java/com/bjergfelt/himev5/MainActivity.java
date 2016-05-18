package com.bjergfelt.himev5;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bjergfelt.himev5.Chat.ChatActivity;
import com.bjergfelt.himev5.Chat.ChatRoomActivity;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.addJob.addJobActivity;
import com.bjergfelt.himev5.jobData.DataProvider;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.JobDetailFragment;
import com.bjergfelt.himev5.jobData.JobFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

/*
    For BottomBar we are using roughike's BottomBar
    https://github.com/roughike/BottomBar/blob/master/README.md#common-problems-and-solutions

    For FragNav we are using ncapdevi's FragNav: https://github.com/ncapdevi/FragNav
 */


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, JobFragment.OnListFragmentInteractionListener, JobDetailFragment.OnFragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private BottomBar mBottomBar;
    private static final String LOCATION_KEY = "Location";
    DataProvider dp = new DataProvider();
    Toolbar toolbar;
    Location location;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SupportMapFragment supportMapFragment; // field
    SearchView searchView;
    private int[] tabIcons = {
            R.drawable.ic_view_list_white_48dp,
            R.drawable.ic_google_maps_white_48dp
    };
    FragNavController fragNavController;
    HTTPManager httpManager;

    List<Fragment> fragments = new ArrayList<>(2);
    Job job;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpManager = new HTTPManager(this);


        //user interface layout for this Activity
        //To edit user interface go look for res/layout/activity_main.xml file.
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize different components for use later.
       // tabLayout = (TabLayout) findViewById(R.id.tabs);
        //viewPager = (ViewPager) findViewById(R.id.viewpager);
        getLocationFromPrefs();
        httpManager.getAllJobs();
        fragments.add(JobFragment.newInstance(location));
        fragments.add(JobMapActivity.newInstance(httpManager.getJobList()));


        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.sample_content_fragment,fragments);


        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
                findViewById(R.id.sample_content_fragment), savedInstanceState);

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user selected item number one.
                    fragNavController.switchTab(FragNavController.TAB1);
                    //fragNavController.clearStack();
                }

                if (menuItemId == R.id.bottomBarItemTwo) {
                    // The user selected item number one.
                    fragNavController.switchTab(FragNavController.TAB2);
                }


            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user reselected item number one, scroll your content to top.
                    fragNavController.switchTab(FragNavController.TAB1);
                }

                if (menuItemId == R.id.bottomBarItemTwo) {
                    // The user reselected item number one, scroll your content to top.
                    fragNavController.switchTab(FragNavController.TAB2);
                }
            }
        });
        BottomBarBadge unreadMessages = mBottomBar.makeBadgeForTabAt(0, "#FF0000", 6);
        unreadMessages.show();
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

           // setupViewPager(viewPager, location);
            //tabLayout.setupWithViewPager(viewPager);

            //setupTabIcons();
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(JobFragment.newInstance(location), "JobList");
        Log.d("MAINACTIVITY", "" + dp.getJobList().size());
        adapter.addFragment(JobMapActivity.newInstance(dp.getJobList()), "JobMap");

        viewPager.setAdapter(adapter);
    }

    //Adding icon to tab
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    public Location getLocationFromPrefs() {
        //Restoring preferences from SplashScreen
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onListFragmentInteraction(Job item) {
        // Create fragment and give it an argument specifying the article it should show
        mBottomBar.hide();
        String data = "data passing";
        this.job = item;
        JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(data, item);
        //fragments.add(JobDetailFragment.newInstance("data", item));

       // fragNavController.switchTab(FragNavController.TAB3);
        getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, jobDetailFragment).addToBackStack(null).commit();

        //jobDetailFragment.update(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
        mBottomBar.onSaveInstanceState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }




    ////INNER VIEWPAGEADAPTER

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();
        int numOfTabs;

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);


        }


        @Override
        public Fragment getItem(int position) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            switch (position) {
                case 0:
                    transaction.replace(R.id.sample_content_fragment, JobFragment.newInstance(location));
                    transaction.addToBackStack(null);
                    transaction.commit();
                    //return mFragmentList.get(position);

                case 1:
                    transaction1.replace(R.id.sample_content_fragment, JobMapActivity.newInstance(dp.getJobList()));
                    transaction1.commit();
                    //return mFragmentList.get(position);


            }

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
