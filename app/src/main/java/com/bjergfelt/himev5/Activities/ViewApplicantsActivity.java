package com.bjergfelt.himev5.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Adapters.ApplicantsAdapter;
import com.bjergfelt.himev5.Model.Applicant;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;
import com.bjergfelt.himev5.Util.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ViewApplicantsActivity extends AppCompatActivity {
    private String TAG = ViewApplicantsActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<Applicant> listOfApplicantArrayList;
    private ApplicantsAdapter mAdapter;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OwnPreferenceManager ownPreferenceManager = new OwnPreferenceManager(this);
        /**
         * Check for login session. If not logged in launch
         * login activity
         * */

        setContentView(R.layout.activity_applicants);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_applicant_list);

        listOfApplicantArrayList = new ArrayList<>();
        Log.e(TAG, listOfApplicantArrayList.size()+"");
        mAdapter = new ApplicantsAdapter(this, listOfApplicantArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        Log.e(TAG, recyclerView.getVisibility() + "");

        recyclerView.addOnItemTouchListener(new ApplicantsAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ApplicantsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                Applicant applicant = listOfApplicantArrayList.get(position);
                Intent intent = new Intent(ViewApplicantsActivity.this, ProfileActivity.class);
                intent.putExtra("chat_room_id", applicant.getName());
                intent.putExtra("name", applicant.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAdapter.notifyDataSetChanged();
        /**
         * Updates the chat list unread count and the last message
         */
        getAllApplicants(ownPreferenceManager.getUser().getEmail());
    }



    public void getAllApplicants(String email) {
        requestQueue = Volley.newRequestQueue(this);

        //------Ajax------
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/jobs/getUsersJobs/"+email, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.w("response size: ", "" + response.length());
                            for (int i = 0; i < response.length(); i++) {
                                String applicant ="";
                                JSONObject jsonObject = response.getJSONObject(i);

                                //Er kun interesseret i applicants array. Vi kan få dem sorteret ved at finde userens job, og få alle applicants derfra

                                String jobName = jsonObject.getString("jobName");

                                JSONArray applicants = jsonObject.getJSONArray("applicants");

                                for (int j = 0; j <applicants.length();j++){
                                    applicant = applicants.getString(j);

                                    listOfApplicantArrayList.add(new Applicant(applicant,"Ansøgning:" + " " +jobName,new Date()));
                                }


                                // fx Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, latLngArray, jobAssigned, assignedToUser, providedByUser);
                                // All jobs fragment
                                // fx allJobsFragment.post.add(job);

                                mAdapter.notifyDataSetChanged();
                            }
                            //recyclerView.invalidate();
                            //Vi refresher listen, da dataen først nu er kommet ind, og er klar til visning.
                            mAdapter = new ApplicantsAdapter(getApplicationContext(), listOfApplicantArrayList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

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
