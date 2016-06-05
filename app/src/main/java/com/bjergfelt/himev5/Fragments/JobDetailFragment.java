package com.bjergfelt.himev5.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Model.Job;


import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link interface
 * to handle interaction events.
 * Use the {@link JobDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_JOB_ID = "job_id";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private Job mParam2;
    private TextView tv;
    private TextView desc;
    private TextView priceText;
    private ImageView iv;
    private TextView locationText;
    private TextView estimatedText;
    private TabLayout tabLayout;
    private Button applyButton;

    public JobDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment JobDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobDetailFragment newInstance(String param1, Job job) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        args.putParcelable(ARG_PARAM2, job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //Getting the job we want to show in details from the params.
            //We are getting the data from MainActivity.
            mParam2 =  getArguments().getParcelable(ARG_PARAM2);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabLayout.setVisibility(View.VISIBLE);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_detail_head, container, false);
        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(),
                "font/Roboto-Thin.ttf"); //use this.getAssets if you are calling from an Activity
        view.setBackgroundColor(Color.WHITE);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tv = (TextView) view.findViewById(R.id.nameText1);
        applyButton = (Button) view.findViewById(R.id.apply_button);
        estimatedText = (TextView) view.findViewById(R.id.estimatedText);
        desc = (TextView) view.findViewById(R.id.descriptionText1);
        priceText = (TextView) view.findViewById(R.id.priceText1);
        locationText = (TextView) view.findViewById(R.id.locationTextDetail);
        iv = (ImageView) view.findViewById(R.id.imageView1);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String price = formatter.format(mParam2.getPrice());
        priceText.setText(price);
        locationText.setText(""+mParam2.getLocationLatLong());
        estimatedText.setText(String.valueOf(mParam2.getEstimatedTime()) + "hours");
        tv.setText(mParam2.getName());
        desc.setText(mParam2.getDescription());
        if (mParam2.getPhoto() != null){

            byte[] imageBytes = Base64.decode(mParam2.getPhoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            iv.setImageBitmap(bitmap);
        }else{
           // iv.setImageResource(mParam2.getPhotoId());
        }
        tv.setTypeface(roboto);
        estimatedText.setTypeface(roboto);
        desc.setTypeface(roboto);
        priceText.setTypeface(roboto);
        locationText.setTypeface(roboto);
        priceText.setTypeface(roboto);
        locationText.setTypeface(roboto);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        if (appBarLayout != null) {
            appBarLayout.setTitle(mParam2.getName());
        }

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        return view;

    }

    public void showDialog() {
        ApplyJobDialogFragment.newInstance(mParam1,mParam2).show(getFragmentManager(),"apply");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



}
