package com.bjergfelt.himev5.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjergfelt.himev5.Fragments.JobListFragment;
import com.bjergfelt.himev5.Model.Job;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Services.FetchAddressIntentService;
import com.bjergfelt.himev5.Util.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABjergfelt on 17-03-2016.
 */
public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.ViewHolder>{
    private List<Job> jobs;
    private final JobListFragment.OnListFragmentInteractionListener mListener;
    Location userLocation ;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected final static String LOCATION_KEY = "location-key";
    private Context context;


    public JobListAdapter(Context context, List<Job> items, JobListFragment.OnListFragmentInteractionListener listener, Location location) {
        jobs = new ArrayList<>(items);
        mListener = listener;
        userLocation = location;
        this.context = context;
        //super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_job_test, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        startIntentService(jobs.get(position).getLocationLatLong());
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        float distanceInKilometers = userLocation.distanceTo(jobs.get(position).getLocationLatLong()) / 1000;
        NumberFormat meterFormatter = NumberFormat.getInstance();
        meterFormatter.setMaximumFractionDigits(1);
        String kilometerFormatter = meterFormatter.format(distanceInKilometers);
        String price = formatter.format(jobs.get(position).getPrice());
        holder.mItem = jobs.get(position);
        holder.mIdView.setText(jobs.get(position).getName());
        holder.mContentView.setText(price);
        holder.mLocation.setText(""+jobs.get(position).getEstimatedTime() + " timer");
        //holder.mLocation.setText(""+jobs.get(position).getLocationLatLong());
        holder.kmText.setText(kilometerFormatter + " km");
        holder.bind(holder.mItem);

        //Right it takes a manually inserted bitmap or the picture that was captured.
        if(jobs.get(position).getPhoto() != null) {
            String coverImageString = jobs.get(position).getPhoto();
            byte[] imageBytes = Base64.decode(coverImageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
           holder.iv.setImageBitmap(bitmap);
        }else {
           // holder.iv.setImageResource(jobs.get(position).getPhotoId());
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


    //Use setModels() to change models in the Adapter. Then call notifyDatasetChanged() on the Adapter to update the RecyclerView based on search query
    public void setModels(List<Job> sortedJobs){
        jobs = new ArrayList<>(sortedJobs);
    }

    //Defining three helper methods which enable us to add, remove or move items around in the Adapter.
    //Those methods will automatically call the required notify method to trigger the item animation that goes along with it.

    public Job removeItem(int position){
        final Job job = jobs.remove(position);
        notifyItemRemoved(position);
        return job;
    }

    public void addItem(int position, Job job){
        jobs.add(position,job);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition){
        final Job job = jobs.remove(fromPosition);
        jobs.add(toPosition,job);
        notifyItemMoved(fromPosition,toPosition);
    }

    /*
    We just modify the internal list of objects in the Adapter by either removing, adding or moving objects and once we are done, call a notify method
    */

    //Creating a method which will animate between the List of objects currently displayed in the Adapter to the filtered List we are going to supply to the method from the parameter.
    public void animateTo(List<Job> jobs){
        applyAndAnimateRemovals(jobs);
        applyAndAnimateAdditions(jobs);
        applyAndAnimateMovedItems(jobs);
    }







    /*
    The most difficult part about animating multiple items is keepin track of indexes.
    Example: You add an item then all items below the item you added are moved down.
    Equally if you remove an item all items below it are move up.
    Huge problem as notify..() methods which trigger the item animations require the index of an item.
    Careful with add or remove items and the try to call the notify..
     */

    /*
    We can simplify it by using the three helper methods above
    They automatically call the correct notify method.. So we dont have to worry about the animations anymore.
    We also define a specific order of operations when modifying the internal List of the Adapter.
    */

    /*
    This method iterates through the internal List of the Adapter backwards and checks if each item is contained in the new filtered List. If it is not it calls removeItem().
    The reason we iterate backwards is to avoid having to keep track of an offset. If you remove an item all items below it move up.
     */
    private void applyAndAnimateRemovals(List<Job> filteredJobs) {
        for (int i = jobs.size() - 1; i >= 0; i--){
            final Job job = jobs.get(i);
            if (!filteredJobs.contains(job)){
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Job> filteredJobs) {
        for (int i = 0, count = filteredJobs.size() - 1; i < count; i++ ){
            final Job job = filteredJobs.get(i);
            if (!jobs.contains(job)){
                addItem(i,job);
            }
        }
    }

    /*
    It is essentially a combination of applyAndAnimateRemovals() and applyAndAnimateAdditions() but with a twist.
    At this point applyAndAnimateRemovals() and applyAndAnimateAdditions() have already been called. So we have removed all the items that need to be removed and we added all new items which need to be added.
    So the internal List of the Adapter and the filtered List contain the exactly same items, but they may be in a different order. What applyAndAnimateMovedItems() now does is it iterates through the filtered List backwards and looks up the index of each item in the internal List.
    If it detects a difference in the index it calls moveItem() to bring the internal List of the Adapter in line with the filtered List.
     */

    private void applyAndAnimateMovedItems(List<Job> filteredJobs) {
        for (int toPosition = filteredJobs.size() - 1; toPosition >=0; toPosition--){
            final Job job = filteredJobs.get(toPosition);
            final int fromPosition = jobs.indexOf(job);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition,toPosition);
            }
        }
    }

    /*
    simple approach to animating the items it is by far not the most efficient one
     */

    @Override
    public int getItemCount() {
        return jobs.size();
    }


    //Delares and instantiate and uses it with onBindViewHolder. When the view is created the holder ensures that our components will be shown
    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView iv;
        public final TextView mLocation;
        public final TextView kmText;
        public final ImageView imageView2;
        public Job mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.nameText);
            mContentView = (TextView) view.findViewById(R.id.priceText);
            iv = (ImageView) view.findViewById(R.id.imageView);
            mLocation = (TextView) view.findViewById(R.id.locationText);
            kmText = (TextView) view.findViewById(R.id.kmText);
            imageView2 = (ImageView) view.findViewById(R.id.imageView2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        public void bind(Job model) {
            mIdView.setText(model.getName());
        }
    }


    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();


        }
    }

    private String displayAddressOutput() {
        return mAddressOutput;
    }

    protected void startIntentService(Location location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        context.startService(intent);
    }

}