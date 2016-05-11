package com.bjergfelt.himev5;


import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.JobFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class JobMapActivity extends Fragment implements OnMapReadyCallback {
    ArrayList<Job> jobs;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(R.layout.activity_job_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }*/

    public JobMapActivity() {

    }

    public static JobMapActivity newInstance(ArrayList<Job> jobs){
        JobMapActivity fragment = new JobMapActivity();
        Bundle args = new Bundle();
        args.putParcelableArrayList("jobs", jobs);
         fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating an instance of HTTPManager

        if (getArguments() != null) {
            jobs = getArguments().getParcelableArrayList("jobs");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_job_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.sample_content_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng copenhagen = new LatLng(55.46325, 11.72150);
       // mMap.addMarker(new MarkerOptions().position(copenhagen).title("Marker in Sydney"));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(copenhagen,8f));

        for (int i = 0; i < jobs.size(); i++){
            LatLng location = new LatLng(jobs.get(i).getLocationLatLong().getLatitude(), jobs.get(i).getLocationLatLong().getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(jobs.get(i).getName())
                    .snippet(jobs.get(i).getCategory()));



        }

    }


 /*   @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (this.mapFragment != null
                && getFragmentManager().findFragmentById(
                this.mapFragment.getId()) != null) {

            getFragmentManager().beginTransaction().remove(this.mapFragment)
                    .commit();
            this.mapFragment = null;
        }
    }*/
/* @Override
 public void onDestroy() {

     SupportMapFragment f = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
     if (f.isResumed()){
         getFragmentManager().beginTransaction().remove(f).commit();
     }

     super.onDestroy();
 }*/

}
