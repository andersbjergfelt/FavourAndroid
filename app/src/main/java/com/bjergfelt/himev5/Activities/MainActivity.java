package com.bjergfelt.himev5.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Fragments.JobDetailFragment;
import com.bjergfelt.himev5.Fragments.JobListFragment;
import com.bjergfelt.himev5.LocalDB.DBHandler;
import com.bjergfelt.himev5.Model.Job;
import com.bjergfelt.himev5.Model.UserProfile;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, JobListFragment.OnListFragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOCATION_KEY = "Location";

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
    OwnPreferenceManager preferenceManager;
    ArrayList<Job> jobs = new ArrayList<>();
    List<UserProfile> userProfiles = new ArrayList<>();
    DBHandler db = new DBHandler(this);
    Job job;
    private RequestQueue requestQueue;
    private List<UserProfile> userProfileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager = new OwnPreferenceManager(this);
        //Får brugerens profil oplysninger til senere brug. Ligger det ned i et array
        getProfile(preferenceManager.getUser().getEmail());

        //user interface layout for this Activity
        //To edit user interface go look for res/layout/activity_main.xml file.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        httpManager = new HTTPManager(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        jobs = httpManager.getAllJobs();
        //Får location som er nede i preferences.
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
        JobListFragment jobListFragment = JobListFragment.newInstance(location);

        adapter.addFragment(jobListFragment, "JobList");

        adapter.addFragment(JobMapActivity.newInstance(jobs), "JobMap");

        viewPager.setAdapter(adapter);
    }

    //Adding icon to tab
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    public Location getLocationFromPrefs() {
        //Restoring preferences from Login
        double latitude = preferenceManager.getUser().getLocation().getLatitude();
        double longitude = preferenceManager.getUser().getLocation().getLongitude();
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
            Intent intent = new Intent(this, ViewApplicantsActivity.class);
            startActivity(intent);

        } else if (id == R.id.view_profile) {
            UserProfile userProfile = userProfileList.get(0);
            Intent intent = new Intent(MainActivity.this, ViewOwnProfileActivity.class);
            intent.putExtra("profile", userProfile);

            startActivity(intent);


        }
        else if (id == R.id.nav_settings){

           Intent intent = new Intent(this, UserSettingsActivity.class);

            startActivity(intent);
        }
          else if (id == R.id.your_job){
            Intent intent = new Intent(this, OwnJobs.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onListFragmentInteraction(Job item) {
        // Create fragment and give it an argument specifying the article it should show
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

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }


    public void getProfile(String email) {
        requestQueue = Volley.newRequestQueue(this);
        //------Ajax------
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/profile/getProfile/" + email, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String firstName = response.getString("firstname");
                            String lastName = response.getString("lastname");
                            String city = response.getString("city");
                            int rating = response.getInt("rating");
                            String photo = response.getString("photo");

                            UserProfile userProfile = new UserProfile(firstName, lastName, city, rating, photo);
                            userProfileList.add(userProfile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.w("ajax error", e.getMessage().toString());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error, getProfile", "response error listener: " + error);
            }
        });
        requestQueue.add(jsonArrayRequest);
        Log.w("AJAX", "GET PROFILE, success");

    }

    ////INNER VIEWPAGEADAPTER DER STYRER FRAGMENTS

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);


        }
        @Override
        public Fragment getItem(int position) {

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






}
