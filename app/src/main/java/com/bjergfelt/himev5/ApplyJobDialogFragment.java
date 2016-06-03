package com.bjergfelt.himev5;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.model.Applicant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ABjergfelt on 25-05-2016.
 */
public class ApplyJobDialogFragment extends DialogFragment {

    OwnPreferenceManager ownPreferenceManager;
    Job job;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_JOB_ID = "job_id";
    private String mParam1;
    private Job mParam2;
    JSONArray jsonArray = new JSONArray();
    Map<String, String> params = new HashMap<>();
    public static ApplyJobDialogFragment newInstance(String param1, Job job) {
        ApplyJobDialogFragment fragment = new ApplyJobDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        args.putParcelable(ARG_PARAM2, job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ownPreferenceManager = new OwnPreferenceManager(getActivity());

    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //Getting the job we want to show in details from the params.
            //We are getting the data from MainActivity.
            mParam2 =  getArguments().getParcelable(ARG_PARAM2);
        }
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Apply for job");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.apply_job, null))
                // Add action buttons
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String applicant = ownPreferenceManager.getUser().getEmail();
                        try {
                            editJob(mParam2,mParam2.getJobID(), applicant);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ApplyJobDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    public void editJob(Job job, String jobId, String applicant) throws JSONException {


        String putUrl = "http://apifavour-ab207.rhcloud.com/jobs/editJob/" + jobId;


        Log.d("JOBID",""+putUrl);

        jsonArray.put(applicant);
        //applicantsParams.put("applicants", applicant);
        params.put("jobName", job.getName());
        params.put("description", job.getDescription());
        String salaryString = String.valueOf(job.getPrice());
        params.put("salary", "2");
        params.put("jobId", job.getJobID());
        params.put("estimatedTime","2");
        params.put("category", job.getCategory());
        params.put("lat", ""+job.getLocationLatLong().getLatitude());
        params.put("lng", ""+job.getLocationLatLong().getLongitude());
        params.put("photoData", "");
        params.put("photoContent", "JPEG");
        String jobAssignedString = String.valueOf(job.isJobAssigned());
        params.put("jobAssigned", jobAssignedString);
        params.put("assignedToUser", job.getAssignedToUser());
        params.put("providedByUser", job.getProvidedByUser());



        JSONObject jsonObjectParams = new JSONObject(params);
        jsonObjectParams.put("applicants",jsonArray);
        Log.d("PUT", jsonObjectParams.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, putUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w("POST: ", "success!");
                Log.w("POST: ", ""+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("POST: ", "error! " + error.networkResponse);
            }
        });

        Volley.newRequestQueue(getActivity()).add(jsonRequest);


    }

}


