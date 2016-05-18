package com.bjergfelt.himev5.jobData;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.Util.httpListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class JobFragment extends Fragment implements SearchView.OnQueryTextListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public static List<Job> jobs;
    HTTPManager httpManager;

    CardAdapter cAdapter;

    public static List<Job> getJobs() {
        return jobs;
    }

    public static void setJobs(List<Job> jobs) {
        JobFragment.jobs = jobs;
    }

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
    public JobFragment() {
        jobs = getJobs();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
        public static JobFragment newInstance(Location location) {
        JobFragment fragment = new JobFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating an instance of HTTPManager
        httpManager = new HTTPManager(getContext());


        if (getArguments() != null) {
            userLocation = getArguments().getParcelable("location");
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new     SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshData();
                //jobs = dt.getJobList();
            }
        });
      /*  // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView  = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
            }

            jobs = new ArrayList<>();

            cAdapter  = new CardAdapter(jobs,mListener, userLocation);

            recyclerView.setAdapter(cAdapter);
            //recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }*/







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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //jobs = new ArrayList<>();


        cAdapter = new CardAdapter(getJobs(), mListener, userLocation);
        recyclerView.setAdapter(cAdapter);
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
        getUserLocation();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Job item);

    }

    public static Location getUserLocation(){
        return userLocation;
    }


    public void refreshList () {
        recyclerView.removeAllViews();
        cAdapter.notifyDataSetChanged();
    }


}
