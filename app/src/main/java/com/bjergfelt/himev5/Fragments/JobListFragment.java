package com.bjergfelt.himev5.Fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Adapters.JobListAdapter;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Model.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Et fragment der holder styr på listen af jobs
 *
 */
public class JobListFragment extends Fragment implements SearchView.OnQueryTextListener {


    //Hvis der klikkes på listen, skal man bruge en listener
    private OnListFragmentInteractionListener mListener;
    public RequestQueue requestQueue;
    private TabLayout tabLayout;

    private List<Job> jobs = new ArrayList<>();

    JobListAdapter cAdapter;
    //private static RecyclerView.Adapter mAdapter;

    //private List<Job> filteredJobs;
     RecyclerView recyclerView;
    private static Location userLocation;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JobListFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
        public static JobListFragment newInstance(Location location) {
        JobListFragment fragment = new JobListFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating an instance of HTTPManager


        if (getArguments() != null) {
            userLocation = getArguments().getParcelable("location");
        }
        setHasOptionsMenu(true);


    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        cAdapter = new JobListAdapter(jobs, mListener, userLocation);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(cAdapter);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        //tabLayout.setVisibility(View.VISIBLE);


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new     SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshList();
                //HTTPManager httpManager = new HTTPManager(getContext());
               // httpManager.getAllJobs();

                //jobs = dt.getJobList();
            }
        });


        getAllJobs();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //tabLayout.setVisibility(View.VISIBLE);
        getUserLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //tabLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Job> filteredModelList = filter(jobs, newText);
        cAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Job> filter(List<Job> jobs, String query) {
        query = query.toLowerCase();

        final List<Job> filteredModelList = new ArrayList<>();
        for (Job model : jobs) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
    */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Job item);

    }

    public static Location getUserLocation(){
        return userLocation;
    }


    public  void refreshList () {
        recyclerView.removeAllViews();
        cAdapter.notifyDataSetChanged();
    }


    public void getAllJobs() {
        requestQueue = Volley.newRequestQueue(getContext());

        //------Ajax------
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/jobs/getAll", null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.w("response size: ", "" + response.length());
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = response.getJSONObject(i);
                                String jobName = jsonObject.getString("jobName");
                                String jobId = jsonObject.get("_id").toString();
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
                                jobs.add(job);
                                cAdapter.notifyDataSetChanged();
                            }
                                //recyclerView.invalidate();
                            //Vi refresher listen, da dataen først nu er kommet ind, og er klar til visning.
                            cAdapter = new JobListAdapter(jobs, mListener, userLocation);
                            recyclerView.setAdapter(cAdapter);
                            cAdapter.notifyDataSetChanged();

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
