package com.bjergfelt.himev5.Activities;

import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Adapters.OwnJobsAdapter;
import com.bjergfelt.himev5.Model.Job;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OwnJobs extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OwnJobsAdapter adapter;
    private List<Job> ownJobList;
    public RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_jobs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        ownJobList = new ArrayList<>();
        adapter = new OwnJobsAdapter(this, ownJobList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        OwnPreferenceManager preferenceManager = new OwnPreferenceManager(this);
        String userEmail = preferenceManager.getUser().getEmail();
        getAllOwnJobs(userEmail);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void getAllOwnJobs(String email) {
        requestQueue = Volley.newRequestQueue(this);

        //------Ajax------
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/jobs/getUsersJobs/"+email, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.w("response size: ", "" + response.length());
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = response.getJSONObject(i);
                                String jobName = jsonObject.getString("jobName");
                                String jobId = jsonObject.getString("jobId");
                                String description = jsonObject.getString("description");
                                int salary = jsonObject.getInt("salary");
                                int estimatedTime = jsonObject.getInt("estimatedTime");
                                String category = jsonObject.getString("category");
                                // lat and lng is saved inside "locationLatLng" as two seperate properties.

                                //double lat = (double) latLngArray.get(0);
                                //double lng = (double) latLngArray.get(1);
                                Location location = new Location("jobLocation");

                                location.setLatitude(jsonObject.getDouble("lat"));
                                location.setLongitude(jsonObject.getDouble("lng"));
                                // Photo is not working properly yet, therefor outcommented.
                                String photo = jsonObject.getString("photoData");
                                boolean jobAssigned = (Boolean) jsonObject.get("jobAssigned");
                                String assignedToUser = jsonObject.getString("assignedToUser");
                                String providedByUser = jsonObject.getString("providedByUser");

                                // Add new job with all the properties.
                                Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, location, photo, assignedToUser, jobAssigned, providedByUser);
                                // fx Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, latLngArray, jobAssigned, assignedToUser, providedByUser);
                                // All jobs fragment
                                // fx allJobsFragment.post.add(job);
                                ownJobList.add(job);
                                adapter.notifyDataSetChanged();
                            }
                            //recyclerView.invalidate();
                            //Vi refresher listen, da dataen først nu er kommet ind, og er klar til visning.
                            adapter = new OwnJobsAdapter(getApplicationContext(), ownJobList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.w("ajax error", e.getMessage().toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error, getAllJobs", "response error listener: " + error);
            }
        });
        requestQueue.add(jsonArrayRequest);
        Log.w("AJAX", "GET ALL POSTS, success");
        //------Ajax slut------
    }
}
