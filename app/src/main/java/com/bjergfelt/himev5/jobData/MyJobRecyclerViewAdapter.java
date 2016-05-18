package com.bjergfelt.himev5.jobData;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.jobData.JobFragment.OnListFragmentInteractionListener;


import java.text.NumberFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyJobRecyclerViewAdapter extends RecyclerView.Adapter<MyJobRecyclerViewAdapter.ViewHolder> {

    private final List<Job> jobs;
    private final OnListFragmentInteractionListener mListener;

    public MyJobRecyclerViewAdapter(List<Job> items, OnListFragmentInteractionListener listener) {
        jobs = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_job_test, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String price = formatter.format(jobs.get(position).getPrice());
        holder.mItem = jobs.get(position);
        holder.mIdView.setText(jobs.get(position).getName());
        holder.mContentView.setText(price);
        holder.mLocation.setText(jobs.get(position).getLocationLatLong()+"");
            if(jobs.get(position).getPhoto() != null) {
                //holder.iv.setImageBitmap(jobs.get(position).getPhoto());

            }else {
               /// holder.iv.setImageResource(jobs.get(position).getPhotoId());
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView iv;
        public final TextView mLocation;
        public Job mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.nameText);
            mContentView = (TextView) view.findViewById(R.id.priceText);
            iv = (ImageView) view.findViewById(R.id.imageView);
            mLocation = (TextView) view.findViewById(R.id.locationText);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }


    }
}
